package net.minecraft.client.gui.screen.inventory;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import dimasik.Load;
import dimasik.helpers.render.ColorHelpers;
import dimasik.managers.hook.main.ModuleManagers;
import dimasik.modules.misc.AhHelper;
import dimasik.modules.misc.ItemScroller;
import dimasik.utils.time.TimerUtils;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;

public abstract class ContainerScreen<T extends Container>
extends Screen
implements IHasContainer<T> {
    public static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation("textures/gui/container/inventory.png");
    protected int xSize = 176;
    protected int ySize = 166;
    protected int titleX;
    protected int titleY;
    protected int playerInventoryTitleX;
    protected int playerInventoryTitleY;
    protected final T container;
    protected final PlayerInventory playerInventory;
    @Nullable
    protected Slot hoveredSlot;
    @Nullable
    private Slot clickedSlot;
    @Nullable
    private Slot returningStackDestSlot;
    @Nullable
    private Slot currentDragTargetSlot;
    @Nullable
    private Slot lastClickSlot;
    protected int guiLeft;
    protected int guiTop;
    private boolean isRightMouseClick;
    private ItemStack draggedStack = ItemStack.EMPTY;
    private int touchUpX;
    private int touchUpY;
    private long returningStackTime;
    private ItemStack returningStack = ItemStack.EMPTY;
    private long dragItemDropDelay;
    protected final Set<Slot> dragSplittingSlots = Sets.newHashSet();
    protected boolean dragSplitting;
    private int dragSplittingLimit;
    private int dragSplittingButton;
    private boolean ignoreMouseUp;
    private int dragSplittingRemnant;
    private long lastClickTime;
    private int lastClickButton;
    private boolean doubleClick;
    private ItemStack shiftClickedSlot = ItemStack.EMPTY;
    private final TimerUtils timer = new TimerUtils();

    public ContainerScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(titleIn);
        this.container = screenContainer;
        this.playerInventory = inv;
        this.ignoreMouseUp = true;
        this.titleX = 8;
        this.titleY = 6;
        this.playerInventoryTitleX = 8;
        this.playerInventoryTitleY = this.ySize - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        ItemStack itemstack;
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(i, j, 0.0f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableRescaleNormal();
        this.hoveredSlot = null;
        int k = 240;
        int l = 240;
        RenderSystem.glMultiTexCoord2f(33986, 240.0f, 240.0f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        for (int i1 = 0; i1 < ((Container)this.container).inventorySlots.size(); ++i1) {
            Slot slot = ((Container)this.container).inventorySlots.get(i1);
            if (slot.isEnabled()) {
                this.moveItems(matrixStack, slot);
            }
            if (!this.isSlotSelected(slot, mouseX, mouseY) || !slot.isEnabled()) continue;
            ItemScroller itemScroller = Load.getInstance().getHooks().getModuleManagers().getItemScroller();
            if (itemScroller != null && itemScroller.isToggled() && GLFW.glfwGetMouseButton(Minecraft.getInstance().getMainWindow().getHandle(), 0) == 1 && GLFW.glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), 340) == 1 && Minecraft.getInstance().currentScreen != null && this.timer.hasTimeElapsed(((Float)itemScroller.getDelay().getValue()).longValue()) && slot.getHasStack()) {
                this.handleMouseClick(slot, slot.slotNumber, 1, ClickType.QUICK_MOVE);
                this.timer.reset();
            }
            this.hoveredSlot = slot;
            RenderSystem.disableDepthTest();
            int j1 = slot.xPos;
            int k1 = slot.yPos;
            RenderSystem.colorMask(true, true, true, false);
            this.fillGradient(matrixStack, j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
        }
        this.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        PlayerInventory playerinventory = this.minecraft.player.inventory;
        ItemStack itemStack = itemstack = this.draggedStack.isEmpty() ? playerinventory.getItemStack() : this.draggedStack;
        if (!itemstack.isEmpty()) {
            int j2 = 8;
            int k2 = this.draggedStack.isEmpty() ? 8 : 16;
            String s = null;
            if (!this.draggedStack.isEmpty() && this.isRightMouseClick) {
                itemstack = itemstack.copy();
                itemstack.setCount(MathHelper.ceil((float)itemstack.getCount() / 2.0f));
            } else if (this.dragSplitting && this.dragSplittingSlots.size() > 1) {
                itemstack = itemstack.copy();
                itemstack.setCount(this.dragSplittingRemnant);
                if (itemstack.isEmpty()) {
                    s = String.valueOf((Object)TextFormatting.YELLOW) + "0";
                }
            }
            this.drawItemStack(itemstack, mouseX - i - 8, mouseY - j - k2, s);
        }
        if (!this.returningStack.isEmpty()) {
            float f = (float)(Util.milliTime() - this.returningStackTime) / 100.0f;
            if (f >= 1.0f) {
                f = 1.0f;
                this.returningStack = ItemStack.EMPTY;
            }
            int l2 = this.returningStackDestSlot.xPos - this.touchUpX;
            int i3 = this.returningStackDestSlot.yPos - this.touchUpY;
            int l1 = this.touchUpX + (int)((float)l2 * f);
            int i2 = this.touchUpY + (int)((float)i3 * f);
            this.drawItemStack(this.returningStack, l1, i2, null);
        }
        RenderSystem.popMatrix();
        RenderSystem.enableDepthTest();
    }

    protected void renderHoveredTooltip(MatrixStack matrixStack, int x, int y) {
        if (this.minecraft.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
            this.renderTooltip(matrixStack, this.hoveredSlot.getStack(), x, y);
        }
    }

    private void drawItemStack(ItemStack stack, int x, int y, String altText) {
        RenderSystem.translatef(0.0f, 0.0f, 32.0f);
        this.setBlitOffset(200);
        this.itemRenderer.zLevel = 200.0f;
        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRenderer.renderItemOverlayIntoGUI(this.font, stack, x, y - (this.draggedStack.isEmpty() ? 0 : 8), altText);
        this.setBlitOffset(0);
        this.itemRenderer.zLevel = 0.0f;
    }

    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        this.font.func_243248_b(matrixStack, this.title, this.titleX, this.titleY, 0x404040);
        this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), this.playerInventoryTitleX, this.playerInventoryTitleY, 0x404040);
    }

    protected abstract void drawGuiContainerBackgroundLayer(MatrixStack var1, float var2, int var3, int var4);

    private void moveItems(MatrixStack matrixStack, Slot slot) {
        Pair<ResourceLocation, ResourceLocation> pair;
        int i = slot.xPos;
        int j = slot.yPos;
        ItemStack itemstack = slot.getStack();
        boolean flag = false;
        boolean flag1 = slot == this.clickedSlot && !this.draggedStack.isEmpty() && !this.isRightMouseClick;
        ItemStack itemstack1 = this.minecraft.player.inventory.getItemStack();
        String s = null;
        if (slot == this.clickedSlot && !this.draggedStack.isEmpty() && this.isRightMouseClick && !itemstack.isEmpty()) {
            itemstack = itemstack.copy();
            itemstack.setCount(itemstack.getCount() / 2);
        } else if (this.dragSplitting && this.dragSplittingSlots.contains(slot) && !itemstack1.isEmpty()) {
            if (this.dragSplittingSlots.size() == 1) {
                return;
            }
            if (Container.canAddItemToSlot(slot, itemstack1, true) && ((Container)this.container).canDragIntoSlot(slot)) {
                itemstack = itemstack1.copy();
                flag = true;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
                int k = Math.min(itemstack.getMaxStackSize(), slot.getItemStackLimit(itemstack));
                if (itemstack.getCount() > k) {
                    s = TextFormatting.YELLOW.toString() + k;
                    itemstack.setCount(k);
                }
            } else {
                this.dragSplittingSlots.remove(slot);
                this.updateDragSplitting();
            }
        }
        this.setBlitOffset(100);
        this.itemRenderer.zLevel = 100.0f;
        if (itemstack.isEmpty() && slot.isEnabled() && (pair = slot.getBackground()) != null) {
            TextureAtlasSprite textureatlassprite = this.minecraft.getAtlasSpriteGetter(pair.getFirst()).apply(pair.getSecond());
            this.minecraft.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
            ContainerScreen.blit(matrixStack, i, j, this.getBlitOffset(), 16, 16, textureatlassprite);
            flag1 = true;
        }
        if (!flag1) {
            if (flag) {
                ContainerScreen.fill(matrixStack, i, j, i + 16, j + 16, -2130706433);
            }
            RenderSystem.enableDepthTest();
            this.itemRenderer.renderItemAndEffectIntoGUI(this.minecraft.player, itemstack, i, j);
            this.itemRenderer.renderItemOverlayIntoGUI(this.font, itemstack, i, j, s);
            ModuleManagers moduleManager = Load.getInstance().getHooks().getModuleManagers();
            AhHelper ahHelper = moduleManager.getAhHelper();
            if (ahHelper.isToggled()) {
                float blinkFactor = (float)(Math.sin(System.currentTimeMillis() / 100L) * 0.5 + 0.5);
                if (ahHelper.getX() != 0.0f) {
                    int x = (int)ahHelper.getX();
                    int y = (int)ahHelper.getY();
                    ContainerScreen.fill(matrixStack, x, y, x + 16, y + 16, ColorHelpers.rgba(64, 255, 64, 3.0f * blinkFactor));
                    if (ahHelper.getX2() != 0.0f) {
                        int x2 = (int)ahHelper.getX2();
                        int y2 = (int)ahHelper.getY2();
                        ContainerScreen.fill(matrixStack, x2, y2, x2 + 16, y2 + 16, ColorHelpers.rgba(255, 255, 64, 3.0f * blinkFactor));
                    }
                    if (ahHelper.getX3() != 0.0f) {
                        int x3 = (int)ahHelper.getX3();
                        int y3 = (int)ahHelper.getY3();
                        ContainerScreen.fill(matrixStack, x3, y3, x3 + 16, y3 + 16, ColorHelpers.rgba(255, 64, 64, 3.0f * blinkFactor));
                    }
                }
            }
        }
        this.itemRenderer.zLevel = 0.0f;
        this.setBlitOffset(0);
    }

    private void updateDragSplitting() {
        ItemStack itemstack = this.minecraft.player.inventory.getItemStack();
        if (!itemstack.isEmpty() && this.dragSplitting) {
            if (this.dragSplittingLimit == 2) {
                this.dragSplittingRemnant = itemstack.getMaxStackSize();
            } else {
                this.dragSplittingRemnant = itemstack.getCount();
                for (Slot slot : this.dragSplittingSlots) {
                    ItemStack itemstack1 = itemstack.copy();
                    ItemStack itemstack2 = slot.getStack();
                    int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
                    Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack1, i);
                    int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));
                    if (itemstack1.getCount() > j) {
                        itemstack1.setCount(j);
                    }
                    this.dragSplittingRemnant -= itemstack1.getCount() - i;
                }
            }
        }
    }

    @Nullable
    private Slot getSelectedSlot(double mouseX, double mouseY) {
        for (int i = 0; i < ((Container)this.container).inventorySlots.size(); ++i) {
            Slot slot = ((Container)this.container).inventorySlots.get(i);
            if (!this.isSlotSelected(slot, mouseX, mouseY) || !slot.isEnabled()) continue;
            return slot;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        boolean flag = this.minecraft.gameSettings.keyBindPickBlock.matchesMouseKey(button);
        Slot slot = this.getSelectedSlot(mouseX, mouseY);
        long i = Util.milliTime();
        this.doubleClick = this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == button;
        this.ignoreMouseUp = false;
        if (button != 0 && button != 1 && !flag) {
            this.hotkeySwapItems(button);
        } else {
            int j = this.guiLeft;
            int k = this.guiTop;
            boolean flag1 = this.hasClickedOutside(mouseX, mouseY, j, k, button);
            int l = -1;
            if (slot != null) {
                l = slot.slotNumber;
            }
            if (flag1) {
                l = -999;
            }
            if (this.minecraft.gameSettings.touchscreen && flag1 && this.minecraft.player.inventory.getItemStack().isEmpty()) {
                this.minecraft.displayGuiScreen(null);
                return true;
            }
            if (l != -1) {
                if (this.minecraft.gameSettings.touchscreen) {
                    if (slot != null && slot.getHasStack()) {
                        this.clickedSlot = slot;
                        this.draggedStack = ItemStack.EMPTY;
                        this.isRightMouseClick = button == 1;
                    } else {
                        this.clickedSlot = null;
                    }
                } else if (!this.dragSplitting) {
                    if (this.minecraft.player.inventory.getItemStack().isEmpty()) {
                        if (this.minecraft.gameSettings.keyBindPickBlock.matchesMouseKey(button)) {
                            this.handleMouseClick(slot, l, button, ClickType.CLONE);
                        } else {
                            boolean flag2 = l != -999 && (InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344));
                            ClickType clicktype = ClickType.PICKUP;
                            if (flag2) {
                                this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                                clicktype = ClickType.QUICK_MOVE;
                            } else if (l == -999) {
                                clicktype = ClickType.THROW;
                            }
                            this.handleMouseClick(slot, l, button, clicktype);
                        }
                        this.ignoreMouseUp = true;
                    } else {
                        this.dragSplitting = true;
                        this.dragSplittingButton = button;
                        this.dragSplittingSlots.clear();
                        if (button == 0) {
                            this.dragSplittingLimit = 0;
                        } else if (button == 1) {
                            this.dragSplittingLimit = 1;
                        } else if (this.minecraft.gameSettings.keyBindPickBlock.matchesMouseKey(button)) {
                            this.dragSplittingLimit = 2;
                        }
                    }
                }
            }
        }
        this.lastClickSlot = slot;
        this.lastClickTime = i;
        this.lastClickButton = button;
        return true;
    }

    private void hotkeySwapItems(int keyCode) {
        if (this.hoveredSlot != null && this.minecraft.player.inventory.getItemStack().isEmpty()) {
            if (this.minecraft.gameSettings.keyBindSwapHands.matchesMouseKey(keyCode)) {
                this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, 40, ClickType.SWAP);
                return;
            }
            for (int i = 0; i < 9; ++i) {
                if (!this.minecraft.gameSettings.keyBindsHotbar[i].matchesMouseKey(keyCode)) continue;
                this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, i, ClickType.SWAP);
            }
        }
    }

    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
        return mouseX < (double)guiLeftIn || mouseY < (double)guiTopIn || mouseX >= (double)(guiLeftIn + this.xSize) || mouseY >= (double)(guiTopIn + this.ySize);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        Slot slot = this.getSelectedSlot(mouseX, mouseY);
        ItemStack itemstack = this.minecraft.player.inventory.getItemStack();
        if (this.clickedSlot != null && this.minecraft.gameSettings.touchscreen) {
            if (button == 0 || button == 1) {
                if (this.draggedStack.isEmpty()) {
                    if (slot != this.clickedSlot && !this.clickedSlot.getStack().isEmpty()) {
                        this.draggedStack = this.clickedSlot.getStack().copy();
                    }
                } else if (this.draggedStack.getCount() > 1 && slot != null && Container.canAddItemToSlot(slot, this.draggedStack, false)) {
                    long i = Util.milliTime();
                    if (this.currentDragTargetSlot == slot) {
                        if (i - this.dragItemDropDelay > 500L) {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
                            this.handleMouseClick(slot, slot.slotNumber, 1, ClickType.PICKUP);
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
                            this.dragItemDropDelay = i + 750L;
                            this.draggedStack.shrink(1);
                        }
                    } else {
                        this.currentDragTargetSlot = slot;
                        this.dragItemDropDelay = i;
                    }
                }
            }
        } else if (this.dragSplitting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > this.dragSplittingSlots.size() || this.dragSplittingLimit == 2) && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && ((Container)this.container).canDragIntoSlot(slot)) {
            this.dragSplittingSlots.add(slot);
            this.updateDragSplitting();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Slot slot = this.getSelectedSlot(mouseX, mouseY);
        int i = this.guiLeft;
        int j = this.guiTop;
        boolean flag = this.hasClickedOutside(mouseX, mouseY, i, j, button);
        int k = -1;
        if (slot != null) {
            k = slot.slotNumber;
        }
        if (flag) {
            k = -999;
        }
        if (this.doubleClick && slot != null && button == 0 && ((Container)this.container).canMergeSlot(ItemStack.EMPTY, slot)) {
            if (ContainerScreen.hasShiftDown()) {
                if (!this.shiftClickedSlot.isEmpty()) {
                    for (Slot slot2 : ((Container)this.container).inventorySlots) {
                        if (slot2 == null || !slot2.canTakeStack(this.minecraft.player) || !slot2.getHasStack() || slot2.inventory != slot.inventory || !Container.canAddItemToSlot(slot2, this.shiftClickedSlot, true)) continue;
                        this.handleMouseClick(slot2, slot2.slotNumber, button, ClickType.QUICK_MOVE);
                    }
                }
            } else {
                this.handleMouseClick(slot, k, button, ClickType.PICKUP_ALL);
            }
            this.doubleClick = false;
            this.lastClickTime = 0L;
        } else {
            if (this.dragSplitting && this.dragSplittingButton != button) {
                this.dragSplitting = false;
                this.dragSplittingSlots.clear();
                this.ignoreMouseUp = true;
                return true;
            }
            if (this.ignoreMouseUp) {
                this.ignoreMouseUp = false;
                return true;
            }
            if (this.clickedSlot != null && this.minecraft.gameSettings.touchscreen) {
                if (button == 0 || button == 1) {
                    if (this.draggedStack.isEmpty() && slot != this.clickedSlot) {
                        this.draggedStack = this.clickedSlot.getStack();
                    }
                    boolean flag2 = Container.canAddItemToSlot(slot, this.draggedStack, false);
                    if (k != -1 && !this.draggedStack.isEmpty() && flag2) {
                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, button, ClickType.PICKUP);
                        this.handleMouseClick(slot, k, 0, ClickType.PICKUP);
                        if (this.minecraft.player.inventory.getItemStack().isEmpty()) {
                            this.returningStack = ItemStack.EMPTY;
                        } else {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, button, ClickType.PICKUP);
                            this.touchUpX = MathHelper.floor(mouseX - (double)i);
                            this.touchUpY = MathHelper.floor(mouseY - (double)j);
                            this.returningStackDestSlot = this.clickedSlot;
                            this.returningStack = this.draggedStack;
                            this.returningStackTime = Util.milliTime();
                        }
                    } else if (!this.draggedStack.isEmpty()) {
                        this.touchUpX = MathHelper.floor(mouseX - (double)i);
                        this.touchUpY = MathHelper.floor(mouseY - (double)j);
                        this.returningStackDestSlot = this.clickedSlot;
                        this.returningStack = this.draggedStack;
                        this.returningStackTime = Util.milliTime();
                    }
                    this.draggedStack = ItemStack.EMPTY;
                    this.clickedSlot = null;
                }
            } else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
                this.handleMouseClick(null, -999, Container.getQuickcraftMask(0, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
                for (Slot slot1 : this.dragSplittingSlots) {
                    this.handleMouseClick(slot1, slot1.slotNumber, Container.getQuickcraftMask(1, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
                }
                this.handleMouseClick(null, -999, Container.getQuickcraftMask(2, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
            } else if (!this.minecraft.player.inventory.getItemStack().isEmpty()) {
                if (this.minecraft.gameSettings.keyBindPickBlock.matchesMouseKey(button)) {
                    this.handleMouseClick(slot, k, button, ClickType.CLONE);
                } else {
                    boolean flag1;
                    boolean bl = flag1 = k != -999 && (InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344));
                    if (flag1) {
                        this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                    }
                    this.handleMouseClick(slot, k, button, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                }
            }
        }
        if (this.minecraft.player.inventory.getItemStack().isEmpty()) {
            this.lastClickTime = 0L;
        }
        this.dragSplitting = false;
        return true;
    }

    private boolean isSlotSelected(Slot slotIn, double mouseX, double mouseY) {
        return this.isPointInRegion(slotIn.xPos, slotIn.yPos, 16, 16, mouseX, mouseY);
    }

    protected boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
        int i = this.guiLeft;
        int j = this.guiTop;
        return (mouseX -= (double)i) >= (double)(x - 1) && mouseX < (double)(x + width + 1) && (mouseY -= (double)j) >= (double)(y - 1) && mouseY < (double)(y + height + 1);
    }

    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if (slotIn != null) {
            slotId = slotIn.slotNumber;
        }
        this.minecraft.playerController.windowClick(((Container)this.container).windowId, slotId, mouseButton, type, this.minecraft.player);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.minecraft.gameSettings.keyBindInventory.matchesKey(keyCode, scanCode)) {
            this.closeScreen();
            return true;
        }
        this.itemStackMoved(keyCode, scanCode);
        if (this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
            if (this.minecraft.gameSettings.keyBindPickBlock.matchesKey(keyCode, scanCode)) {
                this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, 0, ClickType.CLONE);
            } else if (this.minecraft.gameSettings.keyBindDrop.matchesKey(keyCode, scanCode)) {
                this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, ContainerScreen.hasControlDown() ? 1 : 0, ClickType.THROW);
            }
        }
        return true;
    }

    protected boolean itemStackMoved(int keyCode, int scanCode) {
        if (this.minecraft.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null) {
            if (this.minecraft.gameSettings.keyBindSwapHands.matchesKey(keyCode, scanCode)) {
                this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, 40, ClickType.SWAP);
                return true;
            }
            for (int i = 0; i < 9; ++i) {
                if (!this.minecraft.gameSettings.keyBindsHotbar[i].matchesKey(keyCode, scanCode)) continue;
                this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, i, ClickType.SWAP);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClose() {
        if (this.minecraft.player != null) {
            ((Container)this.container).onContainerClosed(this.minecraft.player);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.minecraft.player.isAlive() || this.minecraft.player.removed) {
            this.minecraft.player.closeScreen();
        }
    }

    @Override
    public T getContainer() {
        return this.container;
    }

    @Override
    public void closeScreen() {
        this.minecraft.player.closeScreen();
        super.closeScreen();
    }
}

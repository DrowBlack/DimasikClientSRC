package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.managers.client.ClientManagers;
import dimasik.utils.time.TimerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ChestScreen
extends ContainerScreen<ChestContainer>
implements IHasContainer<ChestContainer> {
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final TimerUtils delayUtil = new TimerUtils();
    private final int inventoryRows;
    private int msX = 0;
    private int msY = 0;

    public ChestScreen(ChestContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.passEvents = false;
        int i = 222;
        int j = 114;
        this.inventoryRows = container.getNumRows();
        this.ySize = 114 + this.inventoryRows * 18;
        this.playerInventoryTitleY = this.ySize - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.drawButtons();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.msX = mouseX;
        this.msY = mouseY;
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    protected void drawButtons() {
        if (!ClientManagers.isUnHook()) {
            ChestContainer container = (ChestContainer)IFastAccess.mc.player.openContainer;
            int sizer = container.getNumRows() > 3 ? 30 : 3;
            this.addButton(new Button(this.width / 2 + 90, this.height / 2 - (80 + sizer), 70, 20, new StringTextComponent("\u0412\u0437\u044f\u0442\u044c"), button -> this.get()));
            this.addButton(new Button(this.width / 2 + 90, this.height / 2 - (60 + sizer), 70, 20, new StringTextComponent("\u0421\u043b\u043e\u0436\u0438\u0442\u044c"), button -> this.put()));
            this.addButton(new Button(this.width / 2 + 90, this.height / 2 - (40 + sizer), 70, 20, new StringTextComponent("\u0412\u044b\u043a\u0438\u043d\u0443\u0442\u044c"), button -> this.drop()));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.blit(matrixStack, i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }

    private void get() {
        if (IFastAccess.mc.player.openContainer instanceof ChestContainer) {
            new Thread(() -> {
                ChestContainer container = (ChestContainer)IFastAccess.mc.player.openContainer;
                for (int index = 0; index < container.inventorySlots.size(); ++index) {
                    if (container.getLowerChestInventory().getStackInSlot(index).getItem() != Item.getItemById(0) && this.delayUtil.hasTimeElapsed(30L)) {
                        try {
                            Minecraft.getInstance().playerController.windowClick(Minecraft.getInstance().player.openContainer.windowId, index, 1, ClickType.QUICK_MOVE, Minecraft.getInstance().player);
                            Thread.sleep(50L);
                        }
                        catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (!container.getLowerChestInventory().isEmpty()) continue;
                    this.delayUtil.reset();
                }
            }).start();
        }
    }

    private void drop() {
        if (IFastAccess.mc.player.openContainer instanceof ChestContainer) {
            new Thread(() -> {
                ChestContainer container = (ChestContainer)IFastAccess.mc.player.openContainer;
                for (int index = 0; index < container.inventorySlots.size(); ++index) {
                    if (container.getLowerChestInventory().getStackInSlot(index).getItem() != Item.getItemById(0) && this.delayUtil.hasTimeElapsed(30L)) {
                        try {
                            Minecraft.getInstance().playerController.windowClick(Minecraft.getInstance().player.openContainer.windowId, index, 1, ClickType.THROW, Minecraft.getInstance().player);
                            Thread.sleep(50L);
                        }
                        catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (!container.getLowerChestInventory().isEmpty()) continue;
                    this.delayUtil.reset();
                }
            }).start();
        }
    }

    private void put() {
        if (IFastAccess.mc.player.openContainer instanceof ChestContainer) {
            new Thread(() -> {
                int index;
                ChestContainer container = (ChestContainer)IFastAccess.mc.player.openContainer;
                boolean chestFull = true;
                for (index = 0; index < container.getLowerChestInventory().getSizeInventory(); ++index) {
                    if (!container.getLowerChestInventory().getStackInSlot(index).isEmpty()) continue;
                    chestFull = false;
                    break;
                }
                for (index = 27; index < container.inventorySlots.size(); ++index) {
                    if (!container.getInventory().get(index).isEmpty() && this.delayUtil.hasTimeElapsed(30L)) {
                        try {
                            IFastAccess.mc.playerController.windowClick(container.windowId, index, 0, ClickType.QUICK_MOVE, IFastAccess.mc.player);
                            Thread.sleep(50L);
                        }
                        catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (chestFull) break;
                }
                this.delayUtil.reset();
            }).start();
        }
    }
}

package dimasik.ui.autobuy;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.GLHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.ui.autobuy.Selector;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.AirItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SkullItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import ru.dreamix.class2native.CompileNativeCalls;

public class BuyScreen
extends Screen
implements IFastAccess {
    public String searchText = "";
    public boolean searching;
    float x;
    float y;
    float width;
    float height;
    boolean selecting;
    Item selectedItem;
    private float scrollingY = 0.0f;
    private float scrollingOutY;
    private boolean update = true;
    public ItemsCategory itemsCategory;
    private float catAnimX = 0.0f;
    private float catAnimY = 0.0f;
    private final Animation animation = new Animation();
    private final Animation settAnim = new Animation();
    private final Animation backgroundAnimation = new Animation();
    int itemX;
    int itemY;
    int categoryX;
    int categoryY;
    private Selector selector = new Selector(null);

    @CompileNativeCalls
    public BuyScreen() {
        super(new StringTextComponent("caxap_coder1337"));
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        this.selecting = false;
        this.searching = false;
        this.selector = new Selector(null);
        super.init(minecraft, width, height);
    }

    @Override
    public void tick() {
        this.animation.update(this.update);
        this.backgroundAnimation.update(this.update);
        this.settAnim.update(this.selecting);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
        this.backgroundAnimation.animate(0.0f, 1.0f, 0.125f, EasingList.NONE, BuyScreen.mc.getTimer().renderPartialTicks);
        GLHelpers.INSTANCE.rescale(1.0);
        this.animation.animate(0.0f, 1.0f, 0.125f, EasingList.BACK_OUT, BuyScreen.mc.getTimer().renderPartialTicks);
        GLHelpers.INSTANCE.scaleAnimation(0.0f, 0.0f, mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), this.animation.getAnimationValue());
        float scrollingOffsetY = this.scrollingOutY = Animation.animate(this.scrollingOutY, this.scrollingY);
        this.width = 1200.0f;
        this.height = 900.0f;
        this.x = (float)mc.getMainWindow().getWidth() / 2.0f - this.width / 2.0f;
        this.y = (float)mc.getMainWindow().getHeight() / 2.0f - this.height / 2.0f;
        VisualHelpers.drawRoundedRect(matrixStack, this.x, this.y, this.width, this.height, 10.0f, ColorHelpers.rgba(15, 15, 15, 255));
        VisualHelpers.drawRoundedRect(matrixStack, this.x + 15.0f, this.y + 50.0f, this.width / 3.0f, this.height - 65.0f, 10.0f, ColorHelpers.rgba(200, 200, 200, 50));
        VisualHelpers.drawRoundedRect(matrixStack, this.x + 20.0f + this.width / 3.0f, this.y + 50.0f, this.width / 3.0f, this.height - 65.0f, 10.0f, ColorHelpers.rgba(200, 200, 200, 50));
        sf_medium.drawCenteredText(matrixStack, "\u0412\u044b\u0431\u043e\u0440 \u043f\u0440\u0435\u0434\u043c\u0435\u043d\u0442\u043e\u0432", this.x + 15.0f + this.width / 3.0f / 2.0f, this.y + 15.0f, ColorHelpers.rgba(255, 255, 255, 255), 25.0f);
        sf_medium.drawCenteredText(matrixStack, "\u0414\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u043d\u044b\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b", this.x + 20.0f + this.width / 3.0f + this.width / 3.0f / 2.0f, this.y + 15.0f, ColorHelpers.rgba(255, 255, 255, 255), 25.0f);
        sf_medium.drawCenteredText(matrixStack, "\u041a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u0438", this.x + this.width / 1.5f + (this.width - this.width / 1.5f) / 2.0f, this.y + 15.0f, ColorHelpers.rgba(255, 255, 255, 255), 25.0f);
        this.itemX = (int)(this.x + 27.0f);
        this.itemY = (int)(this.y + 65.0f + scrollingOffsetY);
        this.categoryX = (int)(this.x + this.width / 1.5f + 40.0f);
        this.categoryY = (int)(this.y + 65.0f);
        float catHeight = 140.0f;
        float catY = this.y + 50.0f;
        VisualHelpers.drawRoundedRect(matrixStack, this.x + this.width / 1.5f + 30.0f, catY, this.width - this.width / 1.5f - 45.0f, catHeight, 10.0f, ColorHelpers.rgba(200, 200, 200, 50));
        GLHelpers.INSTANCE.drawItemStack(matrixStack, new ItemStack(Items.NETHERITE_SWORD, 1), this.categoryX, this.categoryY, true, true, 3.0f);
        GLHelpers.INSTANCE.drawItemStack(matrixStack, new ItemStack(Items.NETHERITE_CHESTPLATE, 1), this.categoryX + 70, this.categoryY, true, true, 3.0f);
        GLHelpers.INSTANCE.drawItemStack(matrixStack, new ItemStack(Items.GOLDEN_APPLE, 1), this.categoryX + 140, this.categoryY, true, true, 3.0f);
        GLHelpers.INSTANCE.drawItemStack(matrixStack, new ItemStack(Items.OAK_LOG, 1), this.categoryX + 210, this.categoryY, true, true, 3.0f);
        GLHelpers.INSTANCE.drawItemStack(matrixStack, new ItemStack(Items.POTION, 1), this.categoryX + 280, this.categoryY, true, true, 3.0f);
        GLHelpers.INSTANCE.drawItemStack(matrixStack, new ItemStack(Items.PLAYER_HEAD, 1), this.categoryX, this.categoryY + 60, true, true, 3.0f);
        GLHelpers.INSTANCE.drawItemStack(matrixStack, new ItemStack(Items.BARRIER, 1), this.categoryX + 70, this.categoryY + 60, true, true, 3.0f);
        float currentCatX = 0.0f;
        float currentCatY = 0.0f;
        if (this.itemsCategory == ItemsCategory.TOOL) {
            currentCatX = 0.0f;
        }
        if (this.itemsCategory == ItemsCategory.ARMOR) {
            currentCatX = 70.0f;
        }
        if (this.itemsCategory == ItemsCategory.FOOD) {
            currentCatX = 140.0f;
        }
        if (this.itemsCategory == ItemsCategory.BLOCK) {
            currentCatX = 210.0f;
        }
        if (this.itemsCategory == ItemsCategory.POTION) {
            currentCatX = 280.0f;
        }
        if (this.itemsCategory == ItemsCategory.SKULL) {
            currentCatX = 0.0f;
            currentCatY = 60.0f;
        }
        if (this.itemsCategory == ItemsCategory.ALL) {
            currentCatX = 70.0f;
            currentCatY = 60.0f;
        }
        this.catAnimX = Animation.animate(this.catAnimX, currentCatX);
        this.catAnimY = Animation.animate(this.catAnimY, currentCatY);
        VisualHelpers.drawRoundedRect(matrixStack, (float)(this.categoryX - 5) + this.catAnimX, this.y + 60.0f + this.catAnimY, 58.0f, 58.0f, 10.0f, ColorHelpers.rgba(200, 200, 200, 50));
        sf_medium.drawCenteredText(matrixStack, "\u041f\u043e\u0438\u0441\u043a", this.x + this.width / 1.5f + (this.width - this.width / 1.5f) / 2.0f, catY + 15.0f + catHeight, ColorHelpers.rgba(255, 255, 255, 255), 25.0f);
        VisualHelpers.drawRoundedRect(matrixStack, this.x + this.width / 1.5f + 30.0f, this.y + 60.0f + catHeight, 355.0f, 70.0f, 10.0f, ColorHelpers.rgba(200, 200, 200, 50));
        VisualHelpers.drawRoundedRect(matrixStack, this.x + this.width / 1.5f + 35.0f, this.y + 95.0f + catHeight, 345.0f, 30.0f, 10.0f, ColorHelpers.rgba(35, 35, 35, 250));
        sf_medium.drawCenteredText(matrixStack, (String)(this.searching ? this.searchText + (System.currentTimeMillis() % 1000L > 500L ? "|" : "") : (this.searchText.isEmpty() ? "\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430" : this.searchText)), this.x + this.width / 1.5f + (this.width - this.width / 1.5f) / 2.0f, catY + 50.0f + catHeight, ColorHelpers.rgba(150, 150, 150, 255), 18.0f);
        for (Item item : Registry.ITEM) {
            if (item instanceof AirItem) continue;
            if (ScreenHelpers.isHovered(this.itemX, this.itemY, this.x + 15.0f, this.y + 50.0f, this.width / 1.5f, this.height - 100.0f)) {
                if (this.itemsCategory == ItemsCategory.FOOD && !item.isFood() || this.itemsCategory == ItemsCategory.BLOCK && !(item instanceof BlockItem) || this.itemsCategory == ItemsCategory.TOOL && !(item instanceof SwordItem) && !(item instanceof ToolItem) && !(item instanceof BowItem) && !(item instanceof FishingRodItem) && !(item instanceof TridentItem) && item != Items.TOTEM_OF_UNDYING || this.itemsCategory == ItemsCategory.ARMOR && !(item instanceof ArmorItem) && !(item instanceof ElytraItem) && !(item instanceof FireworkRocketItem) || this.itemsCategory == ItemsCategory.ARMOR && !(item instanceof ArmorItem) || this.itemsCategory == ItemsCategory.SKULL && !(item instanceof SkullItem) || !item.getName().getString().toLowerCase().contains(this.searchText.toLowerCase())) continue;
                GLHelpers.INSTANCE.drawItemStack(matrixStack, new ItemStack(item, 1), this.itemX, this.itemY, true, true, 2.0f);
            }
            this.itemX += 35;
            if ((float)this.itemX >= this.x + this.width / 3.0f - 15.0f) {
                this.itemX = (int)this.x + 27;
                this.itemY += 35;
            }
            this.scrollingOutY = MathHelper.clamp(this.scrollingOutY, (float)(-this.itemY - 1275), 0.0f);
        }
        this.settAnim.animate(0.0f, 1.0f, 0.125f, EasingList.BACK_OUT, BuyScreen.mc.getTimer().renderPartialTicks);
        GLHelpers.INSTANCE.scaleAnimation(this.x + this.width / 1.5f + 30.0f, this.y + 60.0f + 85.0f + catHeight, 355.0f, this.selector.setH, this.settAnim.getAnimationValue());
        if (this.selecting) {
            this.selector.setCoord(this.x, this.y, this.width, this.height);
            this.selector.render(matrixStack, fixedMouse.x, fixedMouse.y, partialTicks);
        }
        GLHelpers.INSTANCE.rescaleMC();
        if (this.animation.getPrevValue() == 0.0f && this.animation.getValue() == 0.0f && !this.update) {
            this.update = true;
            super.closeScreen();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
        this.width = 1200.0f;
        this.height = 900.0f;
        this.x = (float)mc.getMainWindow().getWidth() / 2.0f - this.width / 2.0f;
        this.y = (float)mc.getMainWindow().getHeight() / 2.0f - this.height / 2.0f;
        if (this.selecting) {
            if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, this.x + 15.0f, this.y + 50.0f, this.width / 1.5f, this.height - 65.0f)) {
                this.scrollingY += (float)(delta * 15.0);
            }
        } else if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, this.x + 15.0f, this.y + 50.0f, this.width / 1.5f, this.height - 65.0f)) {
            this.scrollingY += (float)(delta * 15.0);
        }
        this.selector.scroll(delta, mouseX, mouseY);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.selector.release(mouseX, mouseY);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searching && keyCode == 259 && !this.searchText.isEmpty()) {
            this.searchText = this.searchText.substring(0, this.searchText.length() - 1);
        }
        if (this.searching && keyCode == 257) {
            this.searching = false;
        }
        this.selector.key(keyCode);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.searching) {
            this.searchText = this.searchText + codePoint;
        }
        this.selector.charTyped(codePoint);
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vector2f fixedMouse = GLHelpers.INSTANCE.normalizeCords(mouseX, mouseY, 1.0);
        this.selector.mouseClicked(mouseX, mouseY, button);
        this.itemX = (int)(this.x + 27.0f);
        this.itemY = (int)(this.y + 65.0f + this.scrollingOutY);
        this.categoryX = (int)(this.x + this.width / 1.5f + 40.0f);
        this.categoryY = (int)(this.y + 65.0f);
        float catHeight = 140.0f;
        float catY = this.y + 50.0f;
        if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, (float)this.categoryX, (float)this.categoryY, 55.0f, 55.0f)) {
            this.itemsCategory = ItemsCategory.TOOL;
        } else if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, (float)(this.categoryX + 70), (float)this.categoryY, 55.0f, 55.0f)) {
            this.itemsCategory = ItemsCategory.ARMOR;
        } else if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, (float)(this.categoryX + 140), (float)this.categoryY, 55.0f, 55.0f)) {
            this.itemsCategory = ItemsCategory.FOOD;
        } else if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, (float)(this.categoryX + 210), (float)this.categoryY, 55.0f, 55.0f)) {
            this.itemsCategory = ItemsCategory.BLOCK;
        } else if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, (float)(this.categoryX + 280), (float)this.categoryY, 55.0f, 55.0f)) {
            this.itemsCategory = ItemsCategory.POTION;
        } else if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, (float)this.categoryX, (float)(this.categoryY + 60), 55.0f, 55.0f)) {
            this.itemsCategory = ItemsCategory.SKULL;
        } else if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, (float)(this.categoryX + 70), (float)(this.categoryY + 60), 55.0f, 55.0f)) {
            this.itemsCategory = ItemsCategory.ALL;
        }
        if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, this.x + this.width / 1.5f + 35.0f, this.y + 95.0f + 140.0f, 345.0f, 30.0f)) {
            this.searching = !this.searching;
        }
        for (Item item : Registry.ITEM) {
            if (item instanceof AirItem || this.itemsCategory == ItemsCategory.FOOD && !item.isFood() || this.itemsCategory == ItemsCategory.BLOCK && !(item instanceof BlockItem) || this.itemsCategory == ItemsCategory.TOOL && !(item instanceof SwordItem) && !(item instanceof ToolItem) && !(item instanceof BowItem) && !(item instanceof FishingRodItem) && !(item instanceof TridentItem) && item != Items.TOTEM_OF_UNDYING || this.itemsCategory == ItemsCategory.ARMOR && !(item instanceof ArmorItem) && !(item instanceof ElytraItem) && !(item instanceof FireworkRocketItem) || this.itemsCategory == ItemsCategory.ARMOR && !(item instanceof PotionItem) || this.itemsCategory == ItemsCategory.SKULL && !(item instanceof SkullItem) || !item.getName().getString().toLowerCase().contains(this.searchText.toLowerCase())) continue;
            if (ScreenHelpers.isHovered(fixedMouse.x, fixedMouse.y, (float)this.itemX, (float)this.itemY, 30.0f, 35.0f)) {
                this.selecting = true;
                this.selector.item = item;
                break;
            }
            this.itemX += 35;
            if ((float)this.itemX >= this.x + this.width / 3.0f - 15.0f) {
                this.itemX = (int)this.x + 26;
                this.itemY += 35;
            }
            this.scrollingY = MathHelper.clamp(this.scrollingY, (float)(-this.itemY - 1275), 0.0f);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void closeScreen() {
        this.update = false;
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }

    @Generated
    public Animation getSettAnim() {
        return this.settAnim;
    }

    @Generated
    public Animation getBackgroundAnimation() {
        return this.backgroundAnimation;
    }

    public static enum ItemsCategory {
        TOOL,
        ARMOR,
        FOOD,
        BLOCK,
        POTION,
        SKULL,
        ALL;

    }
}

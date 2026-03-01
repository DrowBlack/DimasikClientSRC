package dimasik.helpers.module.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.draggable.api.Component;
import dimasik.utils.client.StringUtils;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;

public class NearList
extends Component {
    private float width = 0.0f;
    private float blockWidth = 0.0f;
    private final Pattern namePattern = Pattern.compile("^\\w{3,16}$");

    public NearList() {
        super("NearList", new Vector2f(250.0f, 46.0f), 145.0f, 66.0f);
    }

    @Override
    public void update(EventUpdate event) {
        for (PlayerEntity playerEntity : NearList.mc.world.getPlayers()) {
            playerEntity.getAnimation().update(true);
        }
        int list = 0;
        for (PlayerEntity playerEntity : NearList.mc.world.getPlayers()) {
            if (!this.namePattern.matcher(playerEntity.getName().getString()).matches() || playerEntity.getName().getString().equals(NearList.mc.player.getName().getString())) continue;
            ++list;
        }
        boolean bl = Load.getInstance().getHooks().getModuleManagers().getInterfaces().getElements().getSelected("NearList") && (list > 0 || NearList.mc.currentScreen instanceof ChatScreen);
        this.getShowAnimation().update(false);
    }

    @Override
    public void render(EventRender2D.Pre event) {
        MatrixStack matrixStack = event.getMatrixStack();
        float x = ((Vector2f)this.getDraggableOption().getValue()).x;
        float y = ((Vector2f)this.getDraggableOption().getValue()).y;
        float staticWidth = 140.0f;
        float staticBlock = 10.0f;
        float height = 32.0f;
        int list = 0;
        for (PlayerEntity playerEntity : NearList.mc.world.getPlayers()) {
            if (!this.namePattern.matcher(playerEntity.getName().getString()).matches() || playerEntity.getName().getString().equals(NearList.mc.player.getName().getString())) continue;
            ++list;
        }
        if (list < 12) {
            for (PlayerEntity playerEntity : NearList.mc.world.getPlayers()) {
                if (!this.namePattern.matcher(playerEntity.getName().getString()).matches() || playerEntity.getName().getString().equals(NearList.mc.player.getName().getString())) continue;
                height += 21.0f * playerEntity.getAnimation().getAnimationValue();
                staticWidth = Math.max(staticWidth, sf_medium.getWidth(playerEntity.getName().getString(), 14.0f) + sf_medium.getWidth(playerEntity.getPrefix(), 14.0f) + sf_medium.getWidth(String.format("%.0f", Float.valueOf(NearList.mc.player.getDistance(playerEntity))), 14.0f) + 35.0f);
                staticBlock = Math.max(staticBlock, sf_medium.getWidth(String.format("%.0f", Float.valueOf(NearList.mc.player.getDistance(playerEntity))), 14.0f));
            }
            this.width = Animation.animate(this.width, staticWidth);
            this.blockWidth = Animation.animate(this.blockWidth, staticBlock);
            this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, NearList.mc.getTimer().renderPartialTicks);
            VisualHelpers.drawRoundedRect(matrixStack, x, y, this.width, 30.0f, 7.0f, ColorHelpers.rgba(0, 0, 0, 120.0f * this.getShowAnimation().getAnimationValue()));
            VisualHelpers.drawRoundedRect(matrixStack, x + nursultan.getWidth("N", 17.0f) + 10.0f, y + 4.0f, 2.0f, 22.0f, 0.0f, ColorHelpers.rgba(100, 100, 100, (int)(255.0f * this.getShowAnimation().getAnimationValue())));
            sf_medium.drawText(matrixStack, "NearList", x + 33.5f, y + 8.0f, ColorHelpers.rgba(255, 255, 255, 255.0f * this.getShowAnimation().getAnimationValue()), 15.0f);
            nursultan.drawText(matrixStack, "N", x + 6.0f, y + 7.0f, ColorHelpers.rgba(255, 255, 255, (int)(255.0f * this.getShowAnimation().getAnimationValue())), 17.0f);
            float i = 32.0f;
            for (PlayerEntity playerEntity : NearList.mc.world.getPlayers()) {
                if (!this.namePattern.matcher(playerEntity.getName().getString()).matches() || playerEntity.getName().getString().equals(NearList.mc.player.getName().getString())) continue;
                playerEntity.getAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.NONE, NearList.mc.getTimer().renderPartialTicks);
                float animX = 20.0f - 20.0f * playerEntity.getAnimation().getAnimationValue();
                VisualHelpers.drawRoundedRect(matrixStack, x - animX, y + i, this.width, 20.0f, 7.0f, ColorHelpers.rgba(0, 0, 0, 120.0f * playerEntity.getAnimation().getAnimationValue() * this.getShowAnimation().getAnimationValue()));
                VisualHelpers.drawRoundedRect(matrixStack, x - animX + this.width - this.blockWidth - 10.0f, y + i + 4.0f, 2.0f, 12.0f, 0.0f, ColorHelpers.rgba(100, 100, 100, (int)(255.0f * this.getShowAnimation().getAnimationValue())));
                IFormattableTextComponent name = new StringTextComponent(Load.getInstance().getHooks().getFriendManagers().is(playerEntity.getName().getString()) ? "[F] " : "").setStyle(Style.EMPTY.setFormatting(TextFormatting.GREEN));
                for (ITextComponent component : playerEntity.getPrefix().getSiblings()) {
                    if (!StringUtils.smallCaps(component.getString().replace(" ", "")).contains("null")) {
                        ((TextComponent)name).append(new StringTextComponent(StringUtils.smallCaps(component.getString().replace(" ", ""))).setStyle(component.getStyle()));
                        continue;
                    }
                    ((TextComponent)name).append(new StringTextComponent(component.getString()).setStyle(component.getStyle()));
                }
                if (!playerEntity.getPrefix().getString().isEmpty()) {
                    ((TextComponent)name).appendString("  ");
                }
                ((TextComponent)name).append(new StringTextComponent(playerEntity.getName().getString()).setStyle(Style.EMPTY.setFormatting(TextFormatting.WHITE)));
                sf_medium.drawText(matrixStack, name, x - animX + 5.0f, y + i + 3.0f, 14.0f, 255.0f * playerEntity.getAnimation().getAnimationValue() * this.getShowAnimation().getAnimationValue());
                sf_medium.drawText(matrixStack, String.format("%.0f", Float.valueOf(NearList.mc.player.getDistance(playerEntity))), x - animX + this.width - sf_medium.getWidth(String.format("%.0f", Float.valueOf(NearList.mc.player.getDistance(playerEntity))), 14.0f) - 5.0f, y + i + 3.0f, ColorHelpers.rgba(255, 255, 255, 255.0f * playerEntity.getAnimation().getAnimationValue() * this.getShowAnimation().getAnimationValue()), 14.0f);
                i += 21.0f * playerEntity.getAnimation().getAnimationValue();
            }
        } else {
            staticWidth = sf_medium.getWidth("Too many players...", 14.0f) + 30.0f;
            this.width = Animation.animate(this.width, staticWidth);
            this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, NearList.mc.getTimer().renderPartialTicks);
            VisualHelpers.drawRoundedRect(matrixStack, x, y, this.width, 30.0f, 7.0f, ColorHelpers.rgba(0, 0, 0, 120.0f * this.getShowAnimation().getAnimationValue()));
            VisualHelpers.drawRoundedRect(matrixStack, x + nursultan.getWidth("E", 17.0f) + 10.0f, y + 4.0f, 2.0f, 22.0f, 0.0f, ColorHelpers.rgba(100, 100, 100, (int)(255.0f * this.getShowAnimation().getAnimationValue())));
            sf_medium.drawText(matrixStack, "NearList", x + 33.5f, y + 8.0f, ColorHelpers.rgba(255, 255, 255, 255.0f * this.getShowAnimation().getAnimationValue()), 15.0f);
            nursultan.drawText(matrixStack, "E", x + 6.0f, y + 7.0f, ColorHelpers.rgba(255, 255, 255, (int)(255.0f * this.getShowAnimation().getAnimationValue())), 17.0f);
            VisualHelpers.drawRoundedRect(matrixStack, x, y + 32.0f, this.width, 20.0f, 7.0f, ColorHelpers.rgba(0, 0, 0, 120.0f * this.getShowAnimation().getAnimationValue()));
            sf_medium.drawText(matrixStack, "Too many players...", x + 5.0f, y + 35.0f, ColorHelpers.rgba(255, 255, 255, 255.0f * this.getShowAnimation().getAnimationValue()), 14.0f);
        }
        this.getDraggableOption().setWidth(this.width);
        this.getDraggableOption().setHeight(height);
    }
}

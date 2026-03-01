package dimasik.managers.mods.voicechat.gui.group;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.gui.GameProfileUtils;
import dimasik.managers.mods.voicechat.gui.GroupType;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenBase;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenEntryBase;
import dimasik.managers.mods.voicechat.voice.common.ClientGroup;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

public class JoinGroupEntry
extends ListScreenEntryBase<JoinGroupEntry> {
    protected static final ResourceLocation LOCK = new ResourceLocation("main/textures/images/icons/lock.png");
    protected static final ITextComponent GROUP_MEMBERS = new TranslationTextComponent("message.voicechat.group_members").setStyle(Style.EMPTY.setColor(net.minecraft.util.text.Color.fromInt(Color.GRAY.getRGB())));
    protected static final ITextComponent NO_GROUP_MEMBERS = new TranslationTextComponent("message.voicechat.no_group_members").setStyle(Style.EMPTY.setColor(net.minecraft.util.text.Color.fromInt(Color.GRAY.getRGB())));
    protected static final int SKIN_SIZE = 12;
    protected static final int PADDING = 4;
    protected static final int BG_FILL = new Color(74, 74, 74).getRGB();
    protected static final int BG_FILL_SELECTED = new Color(90, 90, 90).getRGB();
    protected static final int PLAYER_NAME_COLOR = new Color(255, 255, 255).getRGB();
    protected final ListScreenBase parent;
    protected final Minecraft minecraft;
    protected final Group group;

    public JoinGroupEntry(ListScreenBase parent, Group group) {
        this.parent = parent;
        this.minecraft = Minecraft.getInstance();
        this.group = group;
    }

    @Override
    public void render(MatrixStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
        if (hovered) {
            AbstractGui.fill(poseStack, left, top, left + width, top + height, BG_FILL_SELECTED);
        } else {
            AbstractGui.fill(poseStack, left, top, left + width, top + height, BG_FILL);
        }
        boolean hasPassword = this.group.group.hasPassword();
        if (hasPassword) {
            poseStack.push();
            poseStack.translate(left + 4, (float)top + (float)height / 2.0f - 8.0f, 0.0);
            poseStack.scale(1.3333334f, 1.3333334f, 1.0f);
            this.minecraft.getTextureManager().bindTexture(LOCK);
            Screen.blit(poseStack, 0, 0, 0.0f, 0.0f, 12, 12, 16, 16);
            poseStack.pop();
        }
        StringTextComponent groupName = new StringTextComponent(this.group.group.getName());
        this.minecraft.fontRenderer.drawString(poseStack, groupName.getText(), left + 4 + (hasPassword ? 20 : 0), top + height / 2 - this.minecraft.fontRenderer.FONT_HEIGHT / 2, PLAYER_NAME_COLOR);
        int textWidth = this.minecraft.fontRenderer.getStringWidth(groupName.getText()) + (hasPassword ? 20 : 0);
        int headsPerRow = (width - (4 + textWidth + 4 + 4)) / 13;
        int rows = 2;
        for (int i = 0; i < this.group.members.size(); ++i) {
            PlayerState state = this.group.members.get(i);
            int headXIndex = i / rows;
            int headYIndex = i % rows;
            if (i >= headsPerRow * rows) break;
            int headPosX = left + width - 12 - 4 - headXIndex * 13;
            int headPosY = top + height / 2 - 13 + 13 * headYIndex;
            poseStack.push();
            this.minecraft.getTextureManager().bindTexture(GameProfileUtils.getSkin(state.getUuid()));
            poseStack.translate(headPosX, headPosY, 0.0);
            float scale = 1.5f;
            poseStack.scale(scale, scale, scale);
            Screen.blit(poseStack, 0, 0, 8.0f, 8.0f, 8, 8, 64, 64);
            RenderSystem.enableBlend();
            Screen.blit(poseStack, 0, 0, 40.0f, 8.0f, 8, 8, 64, 64);
            RenderSystem.disableBlend();
            poseStack.pop();
        }
        if (!hovered) {
            return;
        }
        ArrayList<IReorderingProcessor> tooltip = Lists.newArrayList();
        if (this.group.getGroup().getType().equals(Group.Type.NORMAL)) {
            tooltip.add((IReorderingProcessor)((Object)new TranslationTextComponent("message.voicechat.group_title", new StringTextComponent(this.group.getGroup().getName()))));
        } else {
            tooltip.add((IReorderingProcessor)((Object)new TranslationTextComponent("message.voicechat.group_type_title", new StringTextComponent(this.group.getGroup().getName()), GroupType.fromType(this.group.getGroup().getType()).getTranslation())));
        }
        if (this.group.getMembers().isEmpty()) {
            tooltip.add((IReorderingProcessor)((Object)NO_GROUP_MEMBERS));
        } else {
            tooltip.add((IReorderingProcessor)((Object)GROUP_MEMBERS));
            int maxMembers = 10;
            for (int i = 0; i < this.group.getMembers().size(); ++i) {
                if (i >= maxMembers) {
                    tooltip.add((IReorderingProcessor)((Object)new TranslationTextComponent("message.voicechat.more_members", this.group.getMembers().size() - maxMembers).setStyle(Style.EMPTY.setColor(net.minecraft.util.text.Color.fromInt(Color.GRAY.getRGB())))));
                    break;
                }
                PlayerState state = this.group.getMembers().get(i);
                tooltip.add((IReorderingProcessor)((Object)new StringTextComponent("  " + state.getName()).setStyle(Style.EMPTY.setColor(net.minecraft.util.text.Color.fromInt(Color.GRAY.getRGB())))));
            }
        }
        this.parent.postRender(() -> this.parent.renderTooltip(poseStack, tooltip, mouseX, mouseY));
    }

    public Group getGroup() {
        return this.group;
    }

    @Override
    public List<? extends IGuiEventListener> getEventListeners() {
        return List.of();
    }

    public static class Group {
        private final ClientGroup group;
        private final List<PlayerState> members;

        public Group(ClientGroup group) {
            this.group = group;
            this.members = new ArrayList<PlayerState>();
        }

        public ClientGroup getGroup() {
            return this.group;
        }

        public List<PlayerState> getMembers() {
            return this.members;
        }
    }
}

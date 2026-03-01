package dimasik.managers.mods.voicechat.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.gui.VoiceChatScreenBase;
import dimasik.managers.mods.voicechat.net.ClientServerNetManager;
import dimasik.managers.mods.voicechat.net.JoinGroupPacket;
import dimasik.managers.mods.voicechat.voice.common.ClientGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class EnterPasswordScreen
extends VoiceChatScreenBase {
    private static final ResourceLocation TEXTURE = new ResourceLocation("main/textures/images/gui/gui_enter_password.png");
    private static final ITextComponent TITLE = new TranslationTextComponent("gui.voicechat.enter_password.title");
    private static final ITextComponent JOIN_GROUP = new TranslationTextComponent("message.voicechat.join_group");
    private static final ITextComponent ENTER_GROUP_PASSWORD = new TranslationTextComponent("message.voicechat.enter_group_password");
    private static final ITextComponent PASSWORD = new TranslationTextComponent("message.voicechat.password");
    private TextFieldWidget password;
    private Button joinGroup;
    private ClientGroup group;

    public EnterPasswordScreen(ClientGroup group) {
        super(TITLE, 195, 74);
        this.group = group;
    }

    @Override
    protected void init() {
        super.init();
        this.hoverAreas.clear();
        this.children.clear();
        this.buttons.clear();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.password = new TextFieldWidget(this.font, this.guiLeft + 7, this.guiTop + 7 + (this.minecraft.fontRenderer.FONT_HEIGHT + 5) * 2 - 5 + 1, this.xSize - 14, 12, new StringTextComponent(""));
        this.password.setMaxStringLength(32);
        this.password.setTextFormatter((s, comp) -> {
            if (s.isEmpty() || Voicechat.GROUP_REGEX.matcher((CharSequence)s).matches()) {
                return (IReorderingProcessor)((Object)new StringTextComponent((String)s));
            }
            return (IReorderingProcessor)((Object)new StringTextComponent("Invalid input"));
        });
        this.addButton(this.password);
        this.joinGroup = new Button(this.guiLeft + 7, this.guiTop + this.ySize - 20 - 7, this.xSize - 14, 20, JOIN_GROUP, button -> this.joinGroup());
        this.addButton(this.joinGroup);
    }

    private void joinGroup() {
        if (!this.password.getText().isEmpty()) {
            ClientServerNetManager.sendToServer(new JoinGroupPacket(this.group.getId(), this.password.getText()));
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.password.tick();
        this.joinGroup.active = !this.password.getText().isEmpty();
    }

    @Override
    public void onClose() {
        super.onClose();
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public void renderBackground(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        this.blit(poseStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void renderForeground(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        this.minecraft.fontRenderer.drawString(poseStack, ENTER_GROUP_PASSWORD.getString(), this.guiLeft + this.xSize / 2 - this.font.getStringWidth(ENTER_GROUP_PASSWORD.getString()) / 2, this.guiTop + 7, 0x404040);
        this.minecraft.fontRenderer.drawString(poseStack, PASSWORD.getString(), this.guiLeft + 8, this.guiTop + 7 + this.minecraft.fontRenderer.FONT_HEIGHT + 5, 0x404040);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.minecraft.displayGuiScreen(null);
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 257) {
            this.joinGroup();
            return true;
        }
        return false;
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        String passwordText = this.password.getText();
        this.init(client, width, height);
        this.password.setText(passwordText);
    }
}

package dimasik.managers.mods.voicechat.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.gui.GroupType;
import dimasik.managers.mods.voicechat.gui.VoiceChatScreenBase;
import dimasik.managers.mods.voicechat.net.ClientServerNetManager;
import dimasik.managers.mods.voicechat.net.CreateGroupPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CreateGroupScreen
extends VoiceChatScreenBase {
    private static final ResourceLocation TEXTURE = new ResourceLocation("main/textures/images/gui/gui_create_group.png");
    private static final ITextComponent TITLE = new TranslationTextComponent("gui.voicechat.create_group.title");
    private static final ITextComponent CREATE = new TranslationTextComponent("message.voicechat.create");
    private static final ITextComponent CREATE_GROUP = new TranslationTextComponent("message.voicechat.create_group");
    private static final ITextComponent GROUP_NAME = new TranslationTextComponent("message.voicechat.group_name");
    private static final ITextComponent OPTIONAL_PASSWORD = new TranslationTextComponent("message.voicechat.optional_password");
    private static final ITextComponent GROUP_TYPE = new TranslationTextComponent("message.voicechat.group_type");
    private TextFieldWidget groupName;
    private TextFieldWidget password;
    private GroupType groupType = GroupType.NORMAL;
    private Button groupTypeButton;
    private Button createGroup;

    public CreateGroupScreen() {
        super(TITLE, 195, 124);
    }

    @Override
    protected void init() {
        super.init();
        this.hoverAreas.clear();
        this.children.clear();
        this.buttons.clear();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.groupName = new TextFieldWidget(this.font, this.guiLeft + 7, this.guiTop + 31, this.xSize - 14, 12, new StringTextComponent(""));
        this.groupName.setMaxStringLength(24);
        this.groupName.setTextFormatter((s, comp) -> {
            if (s.isEmpty() || Voicechat.GROUP_REGEX.matcher((CharSequence)s).matches()) {
                return (IReorderingProcessor)((Object)new StringTextComponent((String)s));
            }
            return (IReorderingProcessor)((Object)new StringTextComponent("Invalid input"));
        });
        this.addButton(this.groupName);
        this.password = new TextFieldWidget(this.font, this.guiLeft + 7, this.guiTop + 57, this.xSize - 14, 12, new StringTextComponent(""));
        this.password.setMaxStringLength(32);
        this.password.setTextFormatter((s, comp) -> {
            if (s.isEmpty() || Voicechat.GROUP_REGEX.matcher((CharSequence)s).matches()) {
                return (IReorderingProcessor)((Object)new StringTextComponent((String)s));
            }
            return (IReorderingProcessor)((Object)new StringTextComponent("Invalid input"));
        });
        this.addButton(this.password);
        this.groupTypeButton = new Button(this.guiLeft + 6, this.guiTop + 74, this.xSize - 12, 20, GROUP_TYPE, button -> {
            this.groupType = GroupType.values()[(this.groupType.ordinal() + 1) % GroupType.values().length];
        }){

            @Override
            public ITextComponent getMessage() {
                return new TranslationTextComponent("message.voicechat.group_type").append(new StringTextComponent(": ")).append(CreateGroupScreen.this.groupType.getTranslation());
            }
        };
        this.addButton(this.groupTypeButton);
        this.createGroup = new Button(this.guiLeft + 6, this.guiTop + this.ySize - 27, this.xSize - 12, 20, CREATE, button -> this.createGroup());
        this.addButton(this.createGroup);
    }

    private void createGroup() {
        if (!this.groupName.getText().isEmpty()) {
            ClientServerNetManager.sendToServer(new CreateGroupPacket(this.groupName.getText(), this.password.getText().isEmpty() ? null : this.password.getText(), this.groupType.getType()));
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.groupName.tick();
        this.password.tick();
        this.createGroup.active = !this.groupName.getText().isEmpty();
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
        this.minecraft.fontRenderer.drawString(poseStack, CREATE_GROUP.getString(), this.guiLeft + this.xSize / 2 - this.font.getStringWidth(CREATE_GROUP.getString()) / 2, this.guiTop + 7, 0x404040);
        this.minecraft.fontRenderer.drawString(poseStack, GROUP_NAME.getString(), this.guiLeft + 8, this.guiTop + 7 + this.minecraft.fontRenderer.FONT_HEIGHT + 5, 0x404040);
        this.minecraft.fontRenderer.drawString(poseStack, OPTIONAL_PASSWORD.getString(), this.guiLeft + 8, this.guiTop + 7 + (this.minecraft.fontRenderer.FONT_HEIGHT + 5) * 2 + 10 + 2, 0x404040);
        if (mouseX >= this.groupTypeButton.x && mouseY >= this.groupTypeButton.y && mouseX < this.groupTypeButton.x + this.groupTypeButton.getWidth() && mouseY < this.groupTypeButton.y + this.groupTypeButton.getHeight()) {
            this.renderTooltip(poseStack, this.groupType.getDescription(), mouseX, mouseY);
        }
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
            this.createGroup();
            return true;
        }
        return false;
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        String groupNameText = this.groupName.getText();
        String passwordText = this.password.getText();
        this.init(client, width, height);
        this.groupName.setText(groupNameText);
        this.password.setText(passwordText);
    }
}

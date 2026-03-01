package dimasik.managers.mods.voicechat.gui;

import dimasik.managers.mods.voicechat.api.Group;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum GroupType {
    NORMAL(new TranslationTextComponent("message.voicechat.group_type.normal"), new TranslationTextComponent("message.voicechat.group_type.normal.description"), Group.Type.NORMAL),
    OPEN(new TranslationTextComponent("message.voicechat.group_type.open"), new TranslationTextComponent("message.voicechat.group_type.open.description"), Group.Type.OPEN),
    ISOLATED(new TranslationTextComponent("message.voicechat.group_type.isolated"), new TranslationTextComponent("message.voicechat.group_type.isolated.description"), Group.Type.ISOLATED);

    private final ITextComponent translation;
    private final ITextComponent description;
    private final Group.Type type;

    private GroupType(ITextComponent translation, ITextComponent description, Group.Type type) {
        this.translation = translation;
        this.description = description;
        this.type = type;
    }

    public ITextComponent getTranslation() {
        return this.translation;
    }

    public ITextComponent getDescription() {
        return this.description;
    }

    public Group.Type getType() {
        return this.type;
    }

    public static GroupType fromType(Group.Type type) {
        for (GroupType groupType : GroupType.values()) {
            if (groupType.getType() != type) continue;
            return groupType;
        }
        return NORMAL;
    }
}

package net.minecraft.util.text;

public enum ChatType {
    CHAT(0, false),
    SYSTEM(1, true),
    GAME_INFO(2, true);

    private final byte id;
    private final boolean interrupts;

    private ChatType(byte id, boolean interrupts) {
        this.id = id;
        this.interrupts = interrupts;
    }

    public byte getId() {
        return this.id;
    }

    public static ChatType byId(byte idIn) {
        for (ChatType chattype : ChatType.values()) {
            if (idIn != chattype.id) continue;
            return chattype;
        }
        return CHAT;
    }

    public boolean getInterrupts() {
        return this.interrupts;
    }
}

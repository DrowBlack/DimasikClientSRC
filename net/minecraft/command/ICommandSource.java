package net.minecraft.command;

import java.util.UUID;
import net.minecraft.util.text.ITextComponent;

public interface ICommandSource {
    public static final ICommandSource DUMMY = new ICommandSource(){

        @Override
        public void sendMessage(ITextComponent component, UUID senderUUID) {
        }

        @Override
        public boolean shouldReceiveFeedback() {
            return false;
        }

        @Override
        public boolean shouldReceiveErrors() {
            return false;
        }

        @Override
        public boolean allowLogging() {
            return false;
        }
    };

    public void sendMessage(ITextComponent var1, UUID var2);

    public boolean shouldReceiveFeedback();

    public boolean shouldReceiveErrors();

    public boolean allowLogging();
}

package dimasik.itemics.api.command;

import java.util.UUID;

public interface IItemicsChatControl {
    public static final String FORCE_COMMAND_PREFIX = String.format("<<%s>>", UUID.randomUUID().toString());
}

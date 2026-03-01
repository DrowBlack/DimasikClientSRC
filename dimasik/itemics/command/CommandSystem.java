package dimasik.itemics.command;

import dimasik.itemics.api.command.ICommandSystem;
import dimasik.itemics.api.command.argparser.IArgParserManager;
import dimasik.itemics.command.argparser.ArgParserManager;

public enum CommandSystem implements ICommandSystem
{
    INSTANCE;


    @Override
    public IArgParserManager getParserManager() {
        return ArgParserManager.INSTANCE;
    }
}

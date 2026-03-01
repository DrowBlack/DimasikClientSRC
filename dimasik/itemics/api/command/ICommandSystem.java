package dimasik.itemics.api.command;

import dimasik.itemics.api.command.argparser.IArgParserManager;

public interface ICommandSystem {
    public IArgParserManager getParserManager();
}

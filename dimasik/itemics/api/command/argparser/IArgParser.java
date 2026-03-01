package dimasik.itemics.api.command.argparser;

import dimasik.itemics.api.command.argument.ICommandArgument;

public interface IArgParser<T> {
    public Class<T> getTarget();

    public static interface Stated<T, S>
    extends IArgParser<T> {
        public Class<S> getStateType();

        public T parseArg(ICommandArgument var1, S var2) throws Exception;
    }

    public static interface Stateless<T>
    extends IArgParser<T> {
        public T parseArg(ICommandArgument var1) throws Exception;
    }
}

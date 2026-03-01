package cpw.mods.modlauncher;

import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import java.util.Set;
import javax.annotation.Nonnull;

public class TransformerHolder<T>
implements ITransformer<T> {
    private final ITransformer<T> wrapped;
    private final ITransformationService owner;

    public TransformerHolder(ITransformer<T> wrapped, ITransformationService owner) {
        this.wrapped = wrapped;
        this.owner = owner;
    }

    @Override
    @Nonnull
    public T transform(T input, ITransformerVotingContext context) {
        return this.wrapped.transform(input, context);
    }

    @Override
    @Nonnull
    public TransformerVoteResult castVote(ITransformerVotingContext context) {
        return this.wrapped.castVote(context);
    }

    @Override
    @Nonnull
    public Set<ITransformer.Target> targets() {
        return this.wrapped.targets();
    }

    @Override
    public String[] labels() {
        return this.wrapped.labels();
    }

    public ITransformationService owner() {
        return this.owner;
    }
}

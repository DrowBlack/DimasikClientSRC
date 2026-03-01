package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.CommentedPropertyConfig;
import de.maxhenkel.configbuilder.entry.AbstractConfigEntry;
import de.maxhenkel.configbuilder.entry.RangedConfigEntry;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractRangedConfigEntry<T>
extends AbstractConfigEntry<T>
implements RangedConfigEntry<T> {
    @Nonnull
    protected final T min;
    @Nonnull
    protected final T max;

    public AbstractRangedConfigEntry(CommentedPropertyConfig config, ValueSerializer<T> serializer, String[] comments, String key, T def, @Nullable T min, @Nullable T max) {
        super(config, serializer, comments, key, def);
        this.min = min != null ? min : this.minimumPossibleValue();
        this.max = max != null ? max : this.maximumPossibleValue();
    }

    @Override
    @Nonnull
    public T getMin() {
        return this.min;
    }

    @Override
    @Nonnull
    public T getMax() {
        return this.max;
    }

    @Nonnull
    abstract T minimumPossibleValue();

    @Nonnull
    abstract T maximumPossibleValue();
}

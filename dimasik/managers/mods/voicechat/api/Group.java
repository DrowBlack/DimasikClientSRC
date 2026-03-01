package dimasik.managers.mods.voicechat.api;

import java.util.UUID;
import javax.annotation.Nullable;

public interface Group {
    public String getName();

    public boolean hasPassword();

    public UUID getId();

    public boolean isPersistent();

    public boolean isHidden();

    public Type getType();

    public static interface Builder {
        public Builder setId(@Nullable UUID var1);

        public Builder setName(String var1);

        public Builder setPassword(@Nullable String var1);

        public Builder setPersistent(boolean var1);

        public Builder setHidden(boolean var1);

        public Builder setType(Type var1);

        public Group build();
    }

    public static interface Type {
        public static final Type NORMAL = new Type(){};
        public static final Type OPEN = new Type(){};
        public static final Type ISOLATED = new Type(){};
    }
}

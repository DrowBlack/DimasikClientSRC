package dimasik.managers.mods.voicechat.api;

import javax.annotation.Nullable;

public interface VolumeCategory {
    public String getId();

    public String getName();

    @Nullable
    public String getDescription();

    @Nullable
    public int[][] getIcon();

    public static interface Builder {
        public Builder setId(String var1);

        public Builder setName(String var1);

        public Builder setDescription(@Nullable String var1);

        public Builder setIcon(@Nullable int[][] var1);

        public VolumeCategory build();
    }
}

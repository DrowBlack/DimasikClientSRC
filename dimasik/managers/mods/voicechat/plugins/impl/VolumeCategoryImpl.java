package dimasik.managers.mods.voicechat.plugins.impl;

import dimasik.managers.mods.voicechat.api.VolumeCategory;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;

public class VolumeCategoryImpl
implements VolumeCategory {
    public static final Pattern ID_REGEX = Pattern.compile("^[a-z_]{1,16}$");
    private final String id;
    private final String name;
    @Nullable
    private final String description;
    @Nullable
    private final int[][] icon;

    public VolumeCategoryImpl(String id, String name, @Nullable String description, @Nullable int[][] icon) {
        if (!ID_REGEX.matcher(id).matches()) {
            throw new IllegalArgumentException("Volume category ID can only contain a-z and _ with a maximum amount of 16 characters");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @Nullable
    public String getDescription() {
        return this.description;
    }

    @Override
    @Nullable
    public int[][] getIcon() {
        return this.icon;
    }

    public static VolumeCategoryImpl fromBytes(PacketBuffer buf) {
        String id = buf.readString(16);
        String name = buf.readString(16);
        String description = null;
        if (buf.readBoolean()) {
            description = buf.readString(Short.MAX_VALUE);
        }
        int[][] icon = null;
        if (buf.readBoolean()) {
            icon = new int[16][16];
            for (int x = 0; x < icon.length; ++x) {
                for (int y = 0; y < icon.length; ++y) {
                    icon[x][y] = buf.readInt();
                }
            }
        }
        return new VolumeCategoryImpl(id, name, description, icon);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeString(this.id, 16);
        buf.writeString(this.name, 16);
        buf.writeBoolean(this.description != null);
        if (this.description != null) {
            buf.writeString(this.description, Short.MAX_VALUE);
        }
        buf.writeBoolean(this.icon != null);
        if (this.icon != null) {
            if (this.icon.length != 16) {
                throw new IllegalStateException("Icon is not 16x16");
            }
            for (int x = 0; x < this.icon.length; ++x) {
                if (this.icon[x].length != 16) {
                    throw new IllegalStateException("Icon is not 16x16");
                }
                for (int y = 0; y < this.icon.length; ++y) {
                    buf.writeInt(this.icon[x][y]);
                }
            }
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        VolumeCategoryImpl that = (VolumeCategoryImpl)object;
        return this.id.equals(that.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public static class BuilderImpl
    implements VolumeCategory.Builder {
        private String id;
        private String name;
        @Nullable
        private String description;
        @Nullable
        private int[][] icon;

        @Override
        public VolumeCategory.Builder setId(String id) {
            this.id = id;
            return this;
        }

        @Override
        public VolumeCategory.Builder setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public VolumeCategory.Builder setDescription(@Nullable String description) {
            this.description = description;
            return this;
        }

        @Override
        public VolumeCategory.Builder setIcon(@Nullable int[][] icon) {
            this.icon = icon;
            return this;
        }

        @Override
        public VolumeCategory build() {
            if (this.id == null) {
                throw new IllegalStateException("id missing");
            }
            if (this.name == null) {
                throw new IllegalStateException("name missing");
            }
            return new VolumeCategoryImpl(this.id, this.name, this.description, this.icon);
        }
    }
}

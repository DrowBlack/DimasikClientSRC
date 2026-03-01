package net.minecraft.resources;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resources.IResource;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class SimpleResource
implements IResource {
    private final String packName;
    private final ResourceLocation location;
    private final InputStream inputStream;
    private final InputStream metadataInputStream;
    private boolean wasMetadataRead;
    private JsonObject metadataJson;

    public SimpleResource(String packNameIn, ResourceLocation locationIn, InputStream inputStreamIn, @Nullable InputStream metadataInputStreamIn) {
        this.packName = packNameIn;
        this.location = locationIn;
        this.inputStream = inputStreamIn;
        this.metadataInputStream = metadataInputStreamIn;
    }

    @Override
    public ResourceLocation getLocation() {
        return this.location;
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }

    public boolean hasMetadata() {
        return this.metadataInputStream != null;
    }

    @Override
    @Nullable
    public <T> T getMetadata(IMetadataSectionSerializer<T> serializer) {
        if (!this.hasMetadata()) {
            return null;
        }
        if (this.metadataJson == null && !this.wasMetadataRead) {
            this.wasMetadataRead = true;
            BufferedReader bufferedreader = null;
            try {
                bufferedreader = new BufferedReader(new InputStreamReader(this.metadataInputStream, StandardCharsets.UTF_8));
                this.metadataJson = JSONUtils.fromJson(bufferedreader);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(bufferedreader);
                throw throwable;
            }
            IOUtils.closeQuietly(bufferedreader);
        }
        if (this.metadataJson == null) {
            return null;
        }
        String s = serializer.getSectionName();
        return this.metadataJson.has(s) ? (T)serializer.deserialize(JSONUtils.getJsonObject(this.metadataJson, s)) : null;
    }

    @Override
    public String getPackName() {
        return this.packName;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof SimpleResource)) {
            return false;
        }
        SimpleResource simpleresource = (SimpleResource)p_equals_1_;
        if (this.location != null ? !this.location.equals(simpleresource.location) : simpleresource.location != null) {
            return false;
        }
        return !(this.packName != null ? !this.packName.equals(simpleresource.packName) : simpleresource.packName != null);
    }

    public int hashCode() {
        int i = this.packName != null ? this.packName.hashCode() : 0;
        return 31 * i + (this.location != null ? this.location.hashCode() : 0);
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
        if (this.metadataInputStream != null) {
            this.metadataInputStream.close();
        }
    }
}

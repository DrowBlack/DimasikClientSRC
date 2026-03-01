package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.client.shader.IShaderManager;
import net.minecraft.client.shader.ShaderDefault;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.client.shader.ShaderLoader;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.client.util.JSONBlendingMode;
import net.minecraft.client.util.JSONException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShaderInstance
implements IShaderManager,
AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ShaderDefault field_216546_b = new ShaderDefault();
    private static ShaderInstance field_216547_c;
    private static int field_216548_d;
    private final Map<String, IntSupplier> field_216549_e = Maps.newHashMap();
    private final List<String> field_216550_f = Lists.newArrayList();
    private final List<Integer> field_216551_g = Lists.newArrayList();
    private final List<ShaderUniform> field_216552_h = Lists.newArrayList();
    private final List<Integer> field_216553_i = Lists.newArrayList();
    private final Map<String, ShaderUniform> field_216554_j = Maps.newHashMap();
    private final int field_216555_k;
    private final String field_216556_l;
    private boolean field_216558_n;
    private final JSONBlendingMode field_216559_o;
    private final List<Integer> field_216560_p;
    private final List<String> field_216561_q;
    private final ShaderLoader field_216562_r;
    private final ShaderLoader field_216563_s;

    public ShaderInstance(IResourceManager p_i50988_1_, String p_i50988_2_) throws IOException {
        ResourceLocation resourcelocation = new ResourceLocation("shaders/program/" + p_i50988_2_ + ".json");
        this.field_216556_l = p_i50988_2_;
        IResource iresource = null;
        try {
            JsonArray jsonarray2;
            JsonArray jsonarray1;
            iresource = p_i50988_1_.getResource(resourcelocation);
            JsonObject jsonobject = JSONUtils.fromJson(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
            String s = JSONUtils.getString(jsonobject, "vertex");
            String s2 = JSONUtils.getString(jsonobject, "fragment");
            JsonArray jsonarray = JSONUtils.getJsonArray(jsonobject, "samplers", null);
            if (jsonarray != null) {
                int i = 0;
                for (Object jsonelement : jsonarray) {
                    try {
                        this.func_216541_a((JsonElement)jsonelement);
                    }
                    catch (Exception exception2) {
                        JSONException jsonexception1 = JSONException.forException(exception2);
                        jsonexception1.prependJsonKey("samplers[" + i + "]");
                        throw jsonexception1;
                    }
                    ++i;
                }
            }
            if ((jsonarray1 = JSONUtils.getJsonArray(jsonobject, "attributes", null)) != null) {
                int j = 0;
                this.field_216560_p = Lists.newArrayListWithCapacity(jsonarray1.size());
                this.field_216561_q = Lists.newArrayListWithCapacity(jsonarray1.size());
                for (Object jsonelement1 : jsonarray1) {
                    try {
                        this.field_216561_q.add(JSONUtils.getString((JsonElement)jsonelement1, "attribute"));
                    }
                    catch (Exception exception1) {
                        JSONException jsonexception2 = JSONException.forException(exception1);
                        jsonexception2.prependJsonKey("attributes[" + j + "]");
                        throw jsonexception2;
                    }
                    ++j;
                }
            } else {
                this.field_216560_p = null;
                this.field_216561_q = null;
            }
            if ((jsonarray2 = JSONUtils.getJsonArray(jsonobject, "uniforms", null)) != null) {
                int k = 0;
                for (JsonElement jsonelement2 : jsonarray2) {
                    try {
                        this.func_216540_b(jsonelement2);
                    }
                    catch (Exception exception) {
                        JSONException jsonexception3 = JSONException.forException(exception);
                        jsonexception3.prependJsonKey("uniforms[" + k + "]");
                        throw jsonexception3;
                    }
                    ++k;
                }
            }
            this.field_216559_o = ShaderInstance.func_216543_a(JSONUtils.getJsonObject(jsonobject, "blend", null));
            this.field_216562_r = ShaderInstance.func_216542_a(p_i50988_1_, ShaderLoader.ShaderType.VERTEX, s);
            this.field_216563_s = ShaderInstance.func_216542_a(p_i50988_1_, ShaderLoader.ShaderType.FRAGMENT, s2);
            this.field_216555_k = ShaderLinkHelper.createProgram();
            ShaderLinkHelper.linkProgram(this);
            this.func_216536_h();
            if (this.field_216561_q != null) {
                for (String s3 : this.field_216561_q) {
                    int l = ShaderUniform.func_227807_b_(this.field_216555_k, s3);
                    this.field_216560_p.add(l);
                }
            }
        }
        catch (Exception exception3) {
            Object s1 = iresource != null ? " (" + iresource.getPackName() + ")" : "";
            JSONException jsonexception = JSONException.forException(exception3);
            jsonexception.setFilenameAndFlush(resourcelocation.getPath() + (String)s1);
            throw jsonexception;
        }
        finally {
            IOUtils.closeQuietly((Closeable)iresource);
        }
        this.markDirty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ShaderLoader func_216542_a(IResourceManager p_216542_0_, ShaderLoader.ShaderType p_216542_1_, String p_216542_2_) throws IOException {
        ShaderLoader shaderloader = p_216542_1_.getLoadedShaders().get(p_216542_2_);
        if (shaderloader == null) {
            ResourceLocation resourcelocation = new ResourceLocation("shaders/program/" + p_216542_2_ + p_216542_1_.getShaderExtension());
            IResource iresource = p_216542_0_.getResource(resourcelocation);
            try {
                shaderloader = ShaderLoader.func_216534_a(p_216542_1_, p_216542_2_, iresource.getInputStream(), iresource.getPackName());
            }
            finally {
                IOUtils.closeQuietly((Closeable)iresource);
            }
        }
        return shaderloader;
    }

    public static JSONBlendingMode func_216543_a(JsonObject p_216543_0_) {
        if (p_216543_0_ == null) {
            return new JSONBlendingMode();
        }
        int i = 32774;
        int j = 1;
        int k = 0;
        int l = 1;
        int i1 = 0;
        boolean flag = true;
        boolean flag1 = false;
        if (JSONUtils.isString(p_216543_0_, "func") && (i = JSONBlendingMode.stringToBlendFunction(p_216543_0_.get("func").getAsString())) != 32774) {
            flag = false;
        }
        if (JSONUtils.isString(p_216543_0_, "srcrgb") && (j = JSONBlendingMode.stringToBlendFactor(p_216543_0_.get("srcrgb").getAsString())) != 1) {
            flag = false;
        }
        if (JSONUtils.isString(p_216543_0_, "dstrgb") && (k = JSONBlendingMode.stringToBlendFactor(p_216543_0_.get("dstrgb").getAsString())) != 0) {
            flag = false;
        }
        if (JSONUtils.isString(p_216543_0_, "srcalpha")) {
            l = JSONBlendingMode.stringToBlendFactor(p_216543_0_.get("srcalpha").getAsString());
            if (l != 1) {
                flag = false;
            }
            flag1 = true;
        }
        if (JSONUtils.isString(p_216543_0_, "dstalpha")) {
            i1 = JSONBlendingMode.stringToBlendFactor(p_216543_0_.get("dstalpha").getAsString());
            if (i1 != 0) {
                flag = false;
            }
            flag1 = true;
        }
        if (flag) {
            return new JSONBlendingMode();
        }
        return flag1 ? new JSONBlendingMode(j, k, l, i1, i) : new JSONBlendingMode(j, k, i);
    }

    @Override
    public void close() {
        for (ShaderUniform shaderuniform : this.field_216552_h) {
            shaderuniform.close();
        }
        ShaderLinkHelper.deleteShader(this);
    }

    public void func_216544_e() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        ShaderLinkHelper.func_227804_a_(0);
        field_216548_d = -1;
        field_216547_c = null;
        for (int i = 0; i < this.field_216551_g.size(); ++i) {
            if (this.field_216549_e.get(this.field_216550_f.get(i)) == null) continue;
            GlStateManager.activeTexture(33984 + i);
            GlStateManager.disableTexture();
            GlStateManager.bindTexture(0);
        }
    }

    public void func_216535_f() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        this.field_216558_n = false;
        field_216547_c = this;
        this.field_216559_o.apply();
        if (this.field_216555_k != field_216548_d) {
            ShaderLinkHelper.func_227804_a_(this.field_216555_k);
            field_216548_d = this.field_216555_k;
        }
        for (int i = 0; i < this.field_216551_g.size(); ++i) {
            String s = this.field_216550_f.get(i);
            IntSupplier intsupplier = this.field_216549_e.get(s);
            if (intsupplier == null) continue;
            RenderSystem.activeTexture(33984 + i);
            RenderSystem.enableTexture();
            int j = intsupplier.getAsInt();
            if (j == -1) continue;
            RenderSystem.bindTexture(j);
            ShaderUniform.func_227805_a_(this.field_216551_g.get(i), i);
        }
        for (ShaderUniform shaderuniform : this.field_216552_h) {
            shaderuniform.upload();
        }
    }

    @Override
    public void markDirty() {
        this.field_216558_n = true;
    }

    @Nullable
    public ShaderUniform func_216539_a(String p_216539_1_) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return this.field_216554_j.get(p_216539_1_);
    }

    public ShaderDefault getShaderUniform(String p_216538_1_) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        ShaderUniform shaderuniform = this.func_216539_a(p_216538_1_);
        return shaderuniform == null ? field_216546_b : shaderuniform;
    }

    private void func_216536_h() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        IntArrayList intlist = new IntArrayList();
        for (int i = 0; i < this.field_216550_f.size(); ++i) {
            String s = this.field_216550_f.get(i);
            int j = ShaderUniform.func_227806_a_(this.field_216555_k, s);
            if (j == -1) {
                LOGGER.warn("Shader {} could not find sampler named {} in the specified shader program.", (Object)this.field_216556_l, (Object)s);
                this.field_216549_e.remove(s);
                intlist.add(i);
                continue;
            }
            this.field_216551_g.add(j);
        }
        for (int l = intlist.size() - 1; l >= 0; --l) {
            this.field_216550_f.remove(intlist.getInt(l));
        }
        for (ShaderUniform shaderuniform : this.field_216552_h) {
            String s1 = shaderuniform.getShaderName();
            int k = ShaderUniform.func_227806_a_(this.field_216555_k, s1);
            if (k == -1) {
                LOGGER.warn("Could not find uniform named {} in the specified shader program.", (Object)s1);
                continue;
            }
            this.field_216553_i.add(k);
            shaderuniform.setUniformLocation(k);
            this.field_216554_j.put(s1, shaderuniform);
        }
    }

    private void func_216541_a(JsonElement p_216541_1_) {
        JsonObject jsonobject = JSONUtils.getJsonObject(p_216541_1_, "sampler");
        String s = JSONUtils.getString(jsonobject, "name");
        if (!JSONUtils.isString(jsonobject, "file")) {
            this.field_216549_e.put(s, null);
            this.field_216550_f.add(s);
        } else {
            this.field_216550_f.add(s);
        }
    }

    public void func_216537_a(String p_216537_1_, IntSupplier p_216537_2_) {
        if (this.field_216549_e.containsKey(p_216537_1_)) {
            this.field_216549_e.remove(p_216537_1_);
        }
        this.field_216549_e.put(p_216537_1_, p_216537_2_);
        this.markDirty();
    }

    private void func_216540_b(JsonElement p_216540_1_) throws JSONException {
        JsonObject jsonobject = JSONUtils.getJsonObject(p_216540_1_, "uniform");
        String s = JSONUtils.getString(jsonobject, "name");
        int i = ShaderUniform.parseType(JSONUtils.getString(jsonobject, "type"));
        int j = JSONUtils.getInt(jsonobject, "count");
        float[] afloat = new float[Math.max(j, 16)];
        JsonArray jsonarray = JSONUtils.getJsonArray(jsonobject, "values");
        if (jsonarray.size() != j && jsonarray.size() > 1) {
            throw new JSONException("Invalid amount of values specified (expected " + j + ", found " + jsonarray.size() + ")");
        }
        int k = 0;
        for (JsonElement jsonelement : jsonarray) {
            try {
                afloat[k] = JSONUtils.getFloat(jsonelement, "value");
            }
            catch (Exception exception) {
                JSONException jsonexception = JSONException.forException(exception);
                jsonexception.prependJsonKey("values[" + k + "]");
                throw jsonexception;
            }
            ++k;
        }
        if (j > 1 && jsonarray.size() == 1) {
            while (k < j) {
                afloat[k] = afloat[0];
                ++k;
            }
        }
        int l = j > 1 && j <= 4 && i < 8 ? j - 1 : 0;
        ShaderUniform shaderuniform = new ShaderUniform(s, i + l, j, this);
        if (i <= 3) {
            shaderuniform.set((int)afloat[0], (int)afloat[1], (int)afloat[2], (int)afloat[3]);
        } else if (i <= 7) {
            shaderuniform.setSafe(afloat[0], afloat[1], afloat[2], afloat[3]);
        } else {
            shaderuniform.set(afloat);
        }
        this.field_216552_h.add(shaderuniform);
    }

    @Override
    public ShaderLoader getVertexShaderLoader() {
        return this.field_216562_r;
    }

    @Override
    public ShaderLoader getFragmentShaderLoader() {
        return this.field_216563_s;
    }

    @Override
    public int getProgram() {
        return this.field_216555_k;
    }

    static {
        field_216548_d = -1;
    }
}

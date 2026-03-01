package net.minecraft.client.gui.screen;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.PackLoadingManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ResourcePackList;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PackScreen
extends Screen {
    private static final Logger field_238883_a_ = LogManager.getLogger();
    private static final ITextComponent field_238884_b_ = new TranslationTextComponent("pack.dropInfo").mergeStyle(TextFormatting.GRAY);
    private static final ITextComponent field_238885_c_ = new TranslationTextComponent("pack.folderInfo");
    private static final ResourceLocation field_243391_p = new ResourceLocation("textures/misc/unknown_pack.png");
    private final PackLoadingManager field_238887_q_;
    private final Screen field_238888_r_;
    @Nullable
    private PackDirectoryWatcher field_243392_s;
    private long field_243393_t;
    private ResourcePackList field_238891_u_;
    private ResourcePackList field_238892_v_;
    private final File field_241817_w_;
    private Button field_238894_x_;
    private final Map<String, ResourceLocation> field_243394_y = Maps.newHashMap();

    public PackScreen(Screen p_i242060_1_, net.minecraft.resources.ResourcePackList p_i242060_2_, Consumer<net.minecraft.resources.ResourcePackList> p_i242060_3_, File p_i242060_4_, ITextComponent p_i242060_5_) {
        super(p_i242060_5_);
        this.field_238888_r_ = p_i242060_1_;
        this.field_238887_q_ = new PackLoadingManager(this::func_238904_g_, this::func_243395_a, p_i242060_2_, p_i242060_3_);
        this.field_241817_w_ = p_i242060_4_;
        this.field_243392_s = PackDirectoryWatcher.func_243403_a(p_i242060_4_);
    }

    @Override
    public void closeScreen() {
        this.field_238887_q_.func_241618_c_();
        this.minecraft.displayGuiScreen(this.field_238888_r_);
        this.func_243399_k();
    }

    private void func_243399_k() {
        if (this.field_243392_s != null) {
            try {
                this.field_243392_s.close();
                this.field_243392_s = null;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    protected void init() {
        this.field_238894_x_ = this.addButton(new Button(this.width / 2 + 4, this.height - 48, 150, 20, DialogTexts.GUI_DONE, p_238903_1_ -> this.closeScreen()));
        this.addButton(new Button(this.width / 2 - 154, this.height - 48, 150, 20, new TranslationTextComponent("pack.openFolder"), p_238896_1_ -> Util.getOSType().openFile(this.field_241817_w_), (p_238897_1_, p_238897_2_, p_238897_3_, p_238897_4_) -> this.renderTooltip(p_238897_2_, field_238885_c_, p_238897_3_, p_238897_4_)));
        this.field_238891_u_ = new ResourcePackList(this.minecraft, 200, this.height, new TranslationTextComponent("pack.available.title"));
        this.field_238891_u_.setLeftPos(this.width / 2 - 4 - 200);
        this.children.add(this.field_238891_u_);
        this.field_238892_v_ = new ResourcePackList(this.minecraft, 200, this.height, new TranslationTextComponent("pack.selected.title"));
        this.field_238892_v_.setLeftPos(this.width / 2 + 4);
        this.children.add(this.field_238892_v_);
        this.func_238906_l_();
    }

    @Override
    public void tick() {
        if (this.field_243392_s != null) {
            try {
                if (this.field_243392_s.func_243402_a()) {
                    this.field_243393_t = 20L;
                }
            }
            catch (IOException ioexception) {
                field_238883_a_.warn("Failed to poll for directory {} changes, stopping", (Object)this.field_241817_w_);
                this.func_243399_k();
            }
        }
        if (this.field_243393_t > 0L && --this.field_243393_t == 0L) {
            this.func_238906_l_();
        }
    }

    private void func_238904_g_() {
        this.func_238899_a_(this.field_238892_v_, this.field_238887_q_.func_238869_b_());
        this.func_238899_a_(this.field_238891_u_, this.field_238887_q_.func_238865_a_());
        this.field_238894_x_.active = !this.field_238892_v_.getEventListeners().isEmpty();
    }

    private void func_238899_a_(ResourcePackList p_238899_1_, Stream<PackLoadingManager.IPack> p_238899_2_) {
        p_238899_1_.getEventListeners().clear();
        p_238899_2_.forEach(p_238898_2_ -> p_238899_1_.getEventListeners().add(new ResourcePackList.ResourcePackEntry(this.minecraft, p_238899_1_, this, (PackLoadingManager.IPack)p_238898_2_)));
    }

    private void func_238906_l_() {
        this.field_238887_q_.func_241619_d_();
        this.func_238904_g_();
        this.field_243393_t = 0L;
        this.field_243394_y.clear();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderDirtBackground(0);
        this.field_238891_u_.render(matrixStack, mouseX, mouseY, partialTicks);
        this.field_238892_v_.render(matrixStack, mouseX, mouseY, partialTicks);
        PackScreen.drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        PackScreen.drawCenteredString(matrixStack, this.font, field_238884_b_, this.width / 2, 20, 0xFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    protected static void func_238895_a_(Minecraft p_238895_0_, List<Path> p_238895_1_, Path p_238895_2_) {
        MutableBoolean mutableboolean = new MutableBoolean();
        p_238895_1_.forEach(p_238901_2_ -> {
            try (Stream<Path> stream = Files.walk(p_238901_2_, new FileVisitOption[0]);){
                stream.forEach(p_238900_3_ -> {
                    try {
                        Util.func_240984_a_(p_238901_2_.getParent(), p_238895_2_, p_238900_3_);
                    }
                    catch (IOException ioexception1) {
                        field_238883_a_.warn("Failed to copy datapack file  from {} to {}", p_238900_3_, (Object)p_238895_2_, (Object)ioexception1);
                        mutableboolean.setTrue();
                    }
                });
            }
            catch (IOException ioexception) {
                field_238883_a_.warn("Failed to copy datapack file from {} to {}", p_238901_2_, (Object)p_238895_2_);
                mutableboolean.setTrue();
            }
        });
        if (mutableboolean.isTrue()) {
            SystemToast.func_238539_c_(p_238895_0_, p_238895_2_.toString());
        }
    }

    @Override
    public void addPacks(List<Path> packs) {
        String s = packs.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.joining(", "));
        this.minecraft.displayGuiScreen(new ConfirmScreen(p_238902_2_ -> {
            if (p_238902_2_) {
                PackScreen.func_238895_a_(this.minecraft, packs, this.field_241817_w_.toPath());
                this.func_238906_l_();
            }
            this.minecraft.displayGuiScreen(this);
        }, new TranslationTextComponent("pack.dropConfirm"), new StringTextComponent(s)));
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private ResourceLocation func_243397_a(TextureManager p_243397_1_, ResourcePackInfo p_243397_2_) {
        try (IResourcePack iresourcepack2 = p_243397_2_.getResourcePack();){
            ResourceLocation resourceLocation;
            block15: {
                InputStream inputstream = iresourcepack2.getRootResourceStream("pack.png");
                try {
                    String s = p_243397_2_.getName();
                    ResourceLocation resourcelocation = new ResourceLocation("minecraft", "pack/" + Util.func_244361_a(s, ResourceLocation::validatePathChar) + "/" + String.valueOf(Hashing.sha1().hashUnencodedChars(s)) + "/icon");
                    NativeImage nativeimage = NativeImage.read(inputstream);
                    p_243397_1_.loadTexture(resourcelocation, new DynamicTexture(nativeimage));
                    resourceLocation = resourcelocation;
                    if (inputstream == null) break block15;
                }
                catch (Throwable throwable) {
                    if (inputstream != null) {
                        try {
                            inputstream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                inputstream.close();
            }
            return resourceLocation;
        }
        catch (FileNotFoundException iresourcepack2) {
        }
        catch (Exception exception) {
            field_238883_a_.warn("Failed to load icon from pack {}", (Object)p_243397_2_.getName(), (Object)exception);
        }
        return field_243391_p;
    }

    private ResourceLocation func_243395_a(ResourcePackInfo p_243395_1_) {
        return this.field_243394_y.computeIfAbsent(p_243395_1_.getName(), p_243396_2_ -> this.func_243397_a(this.minecraft.getTextureManager(), p_243395_1_));
    }

    static class PackDirectoryWatcher
    implements AutoCloseable {
        private final WatchService field_243400_a;
        private final Path field_243401_b;

        public PackDirectoryWatcher(File p_i242061_1_) throws IOException {
            this.field_243401_b = p_i242061_1_.toPath();
            this.field_243400_a = this.field_243401_b.getFileSystem().newWatchService();
            try {
                this.func_243404_a(this.field_243401_b);
                try (DirectoryStream<Path> directorystream = Files.newDirectoryStream(this.field_243401_b);){
                    for (Path path : directorystream) {
                        if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) continue;
                        this.func_243404_a(path);
                    }
                }
            }
            catch (Exception exception) {
                this.field_243400_a.close();
                throw exception;
            }
        }

        @Nullable
        public static PackDirectoryWatcher func_243403_a(File p_243403_0_) {
            try {
                return new PackDirectoryWatcher(p_243403_0_);
            }
            catch (IOException ioexception) {
                field_238883_a_.warn("Failed to initialize pack directory {} monitoring", (Object)p_243403_0_, (Object)ioexception);
                return null;
            }
        }

        private void func_243404_a(Path p_243404_1_) throws IOException {
            p_243404_1_.register(this.field_243400_a, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        }

        public boolean func_243402_a() throws IOException {
            WatchKey watchkey;
            boolean flag = false;
            while ((watchkey = this.field_243400_a.poll()) != null) {
                for (WatchEvent<?> watchevent : watchkey.pollEvents()) {
                    Path path;
                    flag = true;
                    if (watchkey.watchable() != this.field_243401_b || watchevent.kind() != StandardWatchEventKinds.ENTRY_CREATE || !Files.isDirectory(path = this.field_243401_b.resolve((Path)watchevent.context()), LinkOption.NOFOLLOW_LINKS)) continue;
                    this.func_243404_a(path);
                }
                watchkey.reset();
            }
            return flag;
        }

        @Override
        public void close() throws IOException {
            this.field_243400_a.close();
        }
    }
}

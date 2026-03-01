package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.Empty3i;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.Load;
import java.io.File;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.util.UndeclaredException;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.Session;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Bootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dreamix.class2native.CompileNativeCalls;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtection;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtectionType;

@AdditionalReferenceProtection(value=AdditionalReferenceProtectionType.ONLY_GENERATIVE)
public class Main {
    private static final Logger LOGGER = LogManager.getLogger();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @CompileNativeCalls
    @AdditionalReferenceProtection(value=AdditionalReferenceProtectionType.GENERATIVE_CONFUSION)
    public static void main(String[] p_main_0_) {
        Thread thread1;
        Minecraft minecraft;
        OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("disableMultiplayer");
        optionparser.accepts("disableChat");
        optionparser.accepts("fullscreen");
        optionparser.accepts("checkGlErrors");
        ArgumentAcceptingOptionSpec<String> optionspec = optionparser.accepts("server").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> optionspec1 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565, (Integer[])new Integer[0]);
        ArgumentAcceptingOptionSpec<File> optionspec2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), (File[])new File[0]);
        ArgumentAcceptingOptionSpec<File> optionspec3 = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<File> optionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<File> optionspec5 = optionparser.accepts("dataPackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<String> optionspec6 = optionparser.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> optionspec7 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", (String[])new String[0]).ofType(Integer.class);
        ArgumentAcceptingOptionSpec<String> optionspec8 = optionparser.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> optionspec9 = optionparser.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> optionspec10 = optionparser.accepts("username").withRequiredArg().defaultsTo("NeRagerik", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> optionspec11 = optionparser.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> optionspec12 = optionparser.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<String> optionspec13 = optionparser.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<Integer> optionspec14 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, (Integer[])new Integer[0]);
        ArgumentAcceptingOptionSpec<Integer> optionspec15 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, (Integer[])new Integer[0]);
        ArgumentAcceptingOptionSpec<Integer> optionspec16 = optionparser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec<Integer> optionspec17 = optionparser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec<String> optionspec18 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> optionspec19 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> optionspec20 = optionparser.accepts("assetIndex").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> optionspec21 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> optionspec22 = optionparser.accepts("versionType").withRequiredArg().defaultsTo("release", (String[])new String[0]);
        NonOptionArgumentSpec<String> optionspec23 = optionparser.nonOptions();
        OptionSet optionset = optionparser.parse(p_main_0_);
        List<String> list = optionset.valuesOf(optionspec23);
        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + String.valueOf(list));
        }
        String s = Main.getValue(optionset, optionspec6);
        Proxy proxy = Proxy.NO_PROXY;
        if (s != null) {
            try {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(s, (int)Main.getValue(optionset, optionspec7)));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        final String s1 = Main.getValue(optionset, optionspec8);
        final String s2 = Main.getValue(optionset, optionspec9);
        if (!proxy.equals(Proxy.NO_PROXY) && Main.isNotEmpty(s1) && Main.isNotEmpty(s2)) {
            Authenticator.setDefault(new Authenticator(){

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(s1, s2.toCharArray());
                }
            });
        }
        int i = Main.getValue(optionset, optionspec14);
        int j = Main.getValue(optionset, optionspec15);
        OptionalInt optionalint = Main.toOptionalInt(Main.getValue(optionset, optionspec16));
        OptionalInt optionalint1 = Main.toOptionalInt(Main.getValue(optionset, optionspec17));
        boolean flag = optionset.has("fullscreen");
        boolean flag1 = optionset.has("demo");
        boolean flag2 = optionset.has("disableMultiplayer");
        boolean flag3 = optionset.has("disableChat");
        String s3 = Main.getValue(optionset, optionspec13);
        Gson gson = new GsonBuilder().registerTypeAdapter((Type)((Object)PropertyMap.class), new PropertyMap.Serializer()).create();
        PropertyMap propertymap = JSONUtils.fromJson(gson, Main.getValue(optionset, optionspec18), PropertyMap.class);
        PropertyMap propertymap1 = JSONUtils.fromJson(gson, Main.getValue(optionset, optionspec19), PropertyMap.class);
        String s4 = Main.getValue(optionset, optionspec22);
        File file1 = Main.getValue(optionset, optionspec2);
        File file2 = optionset.has(optionspec3) ? Main.getValue(optionset, optionspec3) : new File(file1, "assets/");
        File file3 = optionset.has(optionspec4) ? Main.getValue(optionset, optionspec4) : new File(file1, "resourcepacks/");
        String s5 = optionset.has(optionspec11) ? (String)optionspec11.value(optionset) : PlayerEntity.getOfflineUUID((String)optionspec10.value(optionset)).toString();
        String s6 = optionset.has(optionspec20) ? (String)optionspec20.value(optionset) : null;
        String s7 = Main.getValue(optionset, optionspec);
        Integer integer = Main.getValue(optionset, optionspec1);
        CrashReport.crash();
        Bootstrap.register();
        Bootstrap.checkTranslations();
        Util.func_240994_l_();
        Session session = new Session((String)optionspec10.value(optionset), s5, (String)optionspec12.value(optionset), (String)optionspec21.value(optionset));
        GameConfiguration gameconfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, propertymap, propertymap1, proxy), new ScreenSize(i, j, optionalint, optionalint1, flag), new GameConfiguration.FolderInformation(file1, file3, file2, s6), new GameConfiguration.GameInformation(flag1, s3, s4, flag2, flag3), new GameConfiguration.ServerInformation(s7, integer));
        Thread thread = new Thread("Client Shutdown Thread"){

            @Override
            public void run() {
                IntegratedServer integratedserver;
                try {
                    Load.getInstance().shutDown();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                Minecraft minecraft1 = Minecraft.getInstance();
                if (minecraft1 != null && (integratedserver = minecraft1.getIntegratedServer()) != null) {
                    integratedserver.initiateShutdown(true);
                }
            }
        };
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        Runtime.getRuntime().addShutdownHook(thread);
        new Empty3i();
        try {
            Thread.currentThread().setName("Render thread");
            RenderSystem.initRenderThread();
            RenderSystem.beginInitialization();
            minecraft = new Minecraft(gameconfiguration);
            RenderSystem.finishInitialization();
        }
        catch (UndeclaredException undeclaredexception) {
            LOGGER.warn("Failed to create window: ", (Throwable)undeclaredexception);
            return;
        }
        catch (Throwable throwable1) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Initializing game");
            crashreport.makeCategory("Initialization");
            Minecraft.fillCrashReport(null, gameconfiguration.gameInfo.version, null, crashreport);
            Minecraft.displayCrashReport(crashreport);
            return;
        }
        if (minecraft.isRenderOnThread()) {
            thread1 = new Thread("Game thread"){

                @Override
                public void run() {
                    try {
                        RenderSystem.initGameThread(true);
                        minecraft.run();
                    }
                    catch (Throwable throwable2) {
                        LOGGER.error("Exception in client thread", throwable2);
                    }
                }
            };
            thread1.start();
            while (minecraft.isRunning()) {
            }
        } else {
            thread1 = null;
            try {
                RenderSystem.initGameThread(false);
                minecraft.run();
            }
            catch (Throwable throwable) {
                LOGGER.error("Unhandled game exception", throwable);
            }
        }
        try {
            minecraft.shutdown();
            if (thread1 != null) {
                thread1.join();
            }
        }
        catch (InterruptedException interruptedexception) {
            LOGGER.error("Exception during client thread shutdown", (Throwable)interruptedexception);
        }
        finally {
            minecraft.shutdownMinecraftApplet();
        }
    }

    private static OptionalInt toOptionalInt(@Nullable Integer value) {
        return value != null ? OptionalInt.of(value) : OptionalInt.empty();
    }

    @Nullable
    private static <T> T getValue(OptionSet set, OptionSpec<T> option) {
        try {
            return set.valueOf(option);
        }
        catch (Throwable throwable) {
            ArgumentAcceptingOptionSpec argumentacceptingoptionspec;
            List list;
            if (option instanceof ArgumentAcceptingOptionSpec && !(list = (argumentacceptingoptionspec = (ArgumentAcceptingOptionSpec)option).defaultValues()).isEmpty()) {
                return (T)list.get(0);
            }
            throw throwable;
        }
    }

    private static boolean isNotEmpty(@Nullable String str) {
        return str != null && !str.isEmpty();
    }

    static {
        System.setProperty("java.awt.headless", "true");
    }
}

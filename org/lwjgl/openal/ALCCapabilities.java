package org.lwjgl.openal;

import java.util.Set;
import java.util.function.IntFunction;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.Checks;
import org.lwjgl.system.FunctionProviderLocal;
import org.lwjgl.system.ThreadLocalUtil;

public final class ALCCapabilities {
    public final long alcOpenDevice;
    public final long alcCloseDevice;
    public final long alcCreateContext;
    public final long alcMakeContextCurrent;
    public final long alcProcessContext;
    public final long alcSuspendContext;
    public final long alcDestroyContext;
    public final long alcGetCurrentContext;
    public final long alcGetContextsDevice;
    public final long alcIsExtensionPresent;
    public final long alcGetProcAddress;
    public final long alcGetEnumValue;
    public final long alcGetError;
    public final long alcGetString;
    public final long alcGetIntegerv;
    public final long alcCaptureOpenDevice;
    public final long alcCaptureCloseDevice;
    public final long alcCaptureStart;
    public final long alcCaptureStop;
    public final long alcCaptureSamples;
    public final long alcSetThreadContext;
    public final long alcGetThreadContext;
    public final long alcGetInteger64vSOFT;
    public final long alcGetStringiSOFT;
    public final long alcResetDeviceSOFT;
    public final long alcLoopbackOpenDeviceSOFT;
    public final long alcIsRenderFormatSupportedSOFT;
    public final long alcRenderSamplesSOFT;
    public final long alcDevicePauseSOFT;
    public final long alcDeviceResumeSOFT;
    public final long alcReopenDeviceSOFT;
    public final boolean OpenALC10;
    public final boolean OpenALC11;
    public final boolean OpenALC_SOFT_loopback_bformat;
    public final boolean ALC_ENUMERATE_ALL_EXT;
    public final boolean ALC_ENUMERATION_EXT;
    public final boolean ALC_EXT_CAPTURE;
    public final boolean ALC_EXT_DEDICATED;
    public final boolean ALC_EXT_DEFAULT_FILTER_ORDER;
    public final boolean ALC_EXT_disconnect;
    public final boolean ALC_EXT_EFX;
    public final boolean ALC_EXT_thread_local_context;
    public final boolean ALC_LOKI_audio_channel;
    public final boolean ALC_SOFT_device_clock;
    public final boolean ALC_SOFT_HRTF;
    public final boolean ALC_SOFT_loopback;
    public final boolean ALC_SOFT_output_limiter;
    public final boolean ALC_SOFT_output_mode;
    public final boolean ALC_SOFT_pause_device;
    public final boolean ALC_SOFT_reopen_device;
    final long device;
    final PointerBuffer addresses;

    ALCCapabilities(FunctionProviderLocal provider, long device, Set<String> ext, IntFunction<PointerBuffer> bufferFactory) {
        this.device = device;
        PointerBuffer caps = bufferFactory.apply(31);
        this.OpenALC10 = ALCCapabilities.check_ALC10(provider, device, caps, ext);
        this.OpenALC11 = ALCCapabilities.check_ALC11(provider, device, caps, ext);
        this.OpenALC_SOFT_loopback_bformat = ext.contains("OpenALC_SOFT_loopback_bformat");
        this.ALC_ENUMERATE_ALL_EXT = ext.contains("ALC_ENUMERATE_ALL_EXT");
        this.ALC_ENUMERATION_EXT = ext.contains("ALC_ENUMERATION_EXT");
        this.ALC_EXT_CAPTURE = ALCCapabilities.check_EXT_CAPTURE(provider, device, caps, ext);
        this.ALC_EXT_DEDICATED = ext.contains("ALC_EXT_DEDICATED");
        this.ALC_EXT_DEFAULT_FILTER_ORDER = ext.contains("ALC_EXT_DEFAULT_FILTER_ORDER");
        this.ALC_EXT_disconnect = ext.contains("ALC_EXT_disconnect");
        this.ALC_EXT_EFX = ext.contains("ALC_EXT_EFX");
        this.ALC_EXT_thread_local_context = ALCCapabilities.check_EXT_thread_local_context(provider, device, caps, ext);
        this.ALC_LOKI_audio_channel = ext.contains("ALC_LOKI_audio_channel");
        this.ALC_SOFT_device_clock = ALCCapabilities.check_SOFT_device_clock(provider, device, caps, ext);
        this.ALC_SOFT_HRTF = ALCCapabilities.check_SOFT_HRTF(provider, device, caps, ext);
        this.ALC_SOFT_loopback = ALCCapabilities.check_SOFT_loopback(provider, device, caps, ext);
        this.ALC_SOFT_output_limiter = ext.contains("ALC_SOFT_output_limiter");
        this.ALC_SOFT_output_mode = ext.contains("ALC_SOFT_output_mode");
        this.ALC_SOFT_pause_device = ALCCapabilities.check_SOFT_pause_device(provider, device, caps, ext);
        this.ALC_SOFT_reopen_device = ALCCapabilities.check_SOFT_reopen_device(provider, device, caps, ext);
        this.alcOpenDevice = caps.get(0);
        this.alcCloseDevice = caps.get(1);
        this.alcCreateContext = caps.get(2);
        this.alcMakeContextCurrent = caps.get(3);
        this.alcProcessContext = caps.get(4);
        this.alcSuspendContext = caps.get(5);
        this.alcDestroyContext = caps.get(6);
        this.alcGetCurrentContext = caps.get(7);
        this.alcGetContextsDevice = caps.get(8);
        this.alcIsExtensionPresent = caps.get(9);
        this.alcGetProcAddress = caps.get(10);
        this.alcGetEnumValue = caps.get(11);
        this.alcGetError = caps.get(12);
        this.alcGetString = caps.get(13);
        this.alcGetIntegerv = caps.get(14);
        this.alcCaptureOpenDevice = caps.get(15);
        this.alcCaptureCloseDevice = caps.get(16);
        this.alcCaptureStart = caps.get(17);
        this.alcCaptureStop = caps.get(18);
        this.alcCaptureSamples = caps.get(19);
        this.alcSetThreadContext = caps.get(20);
        this.alcGetThreadContext = caps.get(21);
        this.alcGetInteger64vSOFT = caps.get(22);
        this.alcGetStringiSOFT = caps.get(23);
        this.alcResetDeviceSOFT = caps.get(24);
        this.alcLoopbackOpenDeviceSOFT = caps.get(25);
        this.alcIsRenderFormatSupportedSOFT = caps.get(26);
        this.alcRenderSamplesSOFT = caps.get(27);
        this.alcDevicePauseSOFT = caps.get(28);
        this.alcDeviceResumeSOFT = caps.get(29);
        this.alcReopenDeviceSOFT = caps.get(30);
        this.addresses = ThreadLocalUtil.setupAddressBuffer(caps);
    }

    public PointerBuffer getAddressBuffer() {
        return this.addresses;
    }

    private static boolean check_ALC10(FunctionProviderLocal provider, long device, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("OpenALC10")) {
            return false;
        }
        return Checks.checkFunctions(provider, device, caps, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14}, "alcOpenDevice", "alcCloseDevice", "alcCreateContext", "alcMakeContextCurrent", "alcProcessContext", "alcSuspendContext", "alcDestroyContext", "alcGetCurrentContext", "alcGetContextsDevice", "alcIsExtensionPresent", "alcGetProcAddress", "alcGetEnumValue", "alcGetError", "alcGetString", "alcGetIntegerv") || Checks.reportMissing("ALC", "OpenALC10");
    }

    private static boolean check_ALC11(FunctionProviderLocal provider, long device, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("OpenALC11")) {
            return false;
        }
        return Checks.checkFunctions(provider, device, caps, new int[]{15, 16, 17, 18, 19}, "alcCaptureOpenDevice", "alcCaptureCloseDevice", "alcCaptureStart", "alcCaptureStop", "alcCaptureSamples") || Checks.reportMissing("ALC", "OpenALC11");
    }

    private static boolean check_EXT_CAPTURE(FunctionProviderLocal provider, long device, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("ALC_EXT_CAPTURE")) {
            return false;
        }
        return Checks.checkFunctions(provider, device, caps, new int[]{15, 16, 17, 18, 19}, "alcCaptureOpenDevice", "alcCaptureCloseDevice", "alcCaptureStart", "alcCaptureStop", "alcCaptureSamples") || Checks.reportMissing("ALC", "ALC_EXT_CAPTURE");
    }

    private static boolean check_EXT_thread_local_context(FunctionProviderLocal provider, long device, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("ALC_EXT_thread_local_context")) {
            return false;
        }
        return Checks.checkFunctions(provider, device, caps, new int[]{20, 21}, "alcSetThreadContext", "alcGetThreadContext") || Checks.reportMissing("ALC", "ALC_EXT_thread_local_context");
    }

    private static boolean check_SOFT_device_clock(FunctionProviderLocal provider, long device, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("ALC_SOFT_device_clock")) {
            return false;
        }
        return Checks.checkFunctions(provider, device, caps, new int[]{22}, "alcGetInteger64vSOFT") || Checks.reportMissing("ALC", "ALC_SOFT_device_clock");
    }

    private static boolean check_SOFT_HRTF(FunctionProviderLocal provider, long device, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("ALC_SOFT_HRTF")) {
            return false;
        }
        return Checks.checkFunctions(provider, device, caps, new int[]{23, 24}, "alcGetStringiSOFT", "alcResetDeviceSOFT") || Checks.reportMissing("ALC", "ALC_SOFT_HRTF");
    }

    private static boolean check_SOFT_loopback(FunctionProviderLocal provider, long device, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("ALC_SOFT_loopback")) {
            return false;
        }
        return Checks.checkFunctions(provider, device, caps, new int[]{25, 26, 27}, "alcLoopbackOpenDeviceSOFT", "alcIsRenderFormatSupportedSOFT", "alcRenderSamplesSOFT") || Checks.reportMissing("ALC", "ALC_SOFT_loopback");
    }

    private static boolean check_SOFT_pause_device(FunctionProviderLocal provider, long device, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("ALC_SOFT_pause_device")) {
            return false;
        }
        return Checks.checkFunctions(provider, device, caps, new int[]{28, 29}, "alcDevicePauseSOFT", "alcDeviceResumeSOFT") || Checks.reportMissing("ALC", "ALC_SOFT_pause_device");
    }

    private static boolean check_SOFT_reopen_device(FunctionProviderLocal provider, long device, PointerBuffer caps, Set<String> ext) {
        if (!ext.contains("ALC_SOFT_reopen_device")) {
            return false;
        }
        return Checks.checkFunctions(provider, device, caps, new int[]{30}, "alcReopenDeviceSOFT") || Checks.reportMissing("ALC", "ALC_SOFT_reopen_device");
    }
}

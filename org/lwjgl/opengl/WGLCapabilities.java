package org.lwjgl.opengl;

import java.util.Set;
import org.lwjgl.system.Checks;
import org.lwjgl.system.FunctionProvider;

public final class WGLCapabilities {
    public final long wglGetGPUIDsAMD;
    public final long wglGetGPUInfoAMD;
    public final long wglGetContextGPUIDAMD;
    public final long wglCreateAssociatedContextAMD;
    public final long wglCreateAssociatedContextAttribsAMD;
    public final long wglDeleteAssociatedContextAMD;
    public final long wglMakeAssociatedContextCurrentAMD;
    public final long wglGetCurrentAssociatedContextAMD;
    public final long wglBlitContextFramebufferAMD;
    public final long wglCreateBufferRegionARB;
    public final long wglDeleteBufferRegionARB;
    public final long wglSaveBufferRegionARB;
    public final long wglRestoreBufferRegionARB;
    public final long wglCreateContextAttribsARB;
    public final long wglGetExtensionsStringARB;
    public final long wglMakeContextCurrentARB;
    public final long wglGetCurrentReadDCARB;
    public final long wglCreatePbufferARB;
    public final long wglGetPbufferDCARB;
    public final long wglReleasePbufferDCARB;
    public final long wglDestroyPbufferARB;
    public final long wglQueryPbufferARB;
    public final long wglGetPixelFormatAttribivARB;
    public final long wglGetPixelFormatAttribfvARB;
    public final long wglChoosePixelFormatARB;
    public final long wglBindTexImageARB;
    public final long wglReleaseTexImageARB;
    public final long wglSetPbufferAttribARB;
    public final long wglGetExtensionsStringEXT;
    public final long wglSwapIntervalEXT;
    public final long wglGetSwapIntervalEXT;
    public final long wglCopyImageSubDataNV;
    public final long wglDelayBeforeSwapNV;
    public final long wglDXSetResourceShareHandleNV;
    public final long wglDXOpenDeviceNV;
    public final long wglDXCloseDeviceNV;
    public final long wglDXRegisterObjectNV;
    public final long wglDXUnregisterObjectNV;
    public final long wglDXObjectAccessNV;
    public final long wglDXLockObjectsNV;
    public final long wglDXUnlockObjectsNV;
    public final long wglEnumGpusNV;
    public final long wglEnumGpuDevicesNV;
    public final long wglCreateAffinityDCNV;
    public final long wglEnumGpusFromAffinityDCNV;
    public final long wglDeleteDCNV;
    public final long wglJoinSwapGroupNV;
    public final long wglBindSwapBarrierNV;
    public final long wglQuerySwapGroupNV;
    public final long wglQueryMaxSwapGroupsNV;
    public final long wglQueryFrameCountNV;
    public final long wglResetFrameCountNV;
    public final long wglAllocateMemoryNV;
    public final long wglFreeMemoryNV;
    public final boolean WGL_AMD_gpu_association;
    public final boolean WGL_ARB_buffer_region;
    public final boolean WGL_ARB_context_flush_control;
    public final boolean WGL_ARB_create_context;
    public final boolean WGL_ARB_create_context_no_error;
    public final boolean WGL_ARB_create_context_profile;
    public final boolean WGL_ARB_create_context_robustness;
    public final boolean WGL_ARB_extensions_string;
    public final boolean WGL_ARB_framebuffer_sRGB;
    public final boolean WGL_ARB_make_current_read;
    public final boolean WGL_ARB_multisample;
    public final boolean WGL_ARB_pbuffer;
    public final boolean WGL_ARB_pixel_format;
    public final boolean WGL_ARB_pixel_format_float;
    public final boolean WGL_ARB_render_texture;
    public final boolean WGL_ARB_robustness_application_isolation;
    public final boolean WGL_ARB_robustness_share_group_isolation;
    public final boolean WGL_ATI_pixel_format_float;
    public final boolean WGL_ATI_render_texture_rectangle;
    public final boolean WGL_EXT_colorspace;
    public final boolean WGL_EXT_create_context_es2_profile;
    public final boolean WGL_EXT_create_context_es_profile;
    public final boolean WGL_EXT_depth_float;
    public final boolean WGL_EXT_extensions_string;
    public final boolean WGL_EXT_framebuffer_sRGB;
    public final boolean WGL_EXT_pixel_format_packed_float;
    public final boolean WGL_EXT_swap_control;
    public final boolean WGL_EXT_swap_control_tear;
    public final boolean WGL_NV_copy_image;
    public final boolean WGL_NV_delay_before_swap;
    public final boolean WGL_NV_DX_interop;
    public final boolean WGL_NV_DX_interop2;
    public final boolean WGL_NV_float_buffer;
    public final boolean WGL_NV_gpu_affinity;
    public final boolean WGL_NV_multigpu_context;
    public final boolean WGL_NV_multisample_coverage;
    public final boolean WGL_NV_render_depth_texture;
    public final boolean WGL_NV_render_texture_rectangle;
    public final boolean WGL_NV_swap_group;
    public final boolean WGL_NV_vertex_array_range;

    WGLCapabilities(FunctionProvider provider, Set<String> ext) {
        long[] caps = new long[54];
        this.WGL_AMD_gpu_association = WGLCapabilities.check_WGL_AMD_gpu_association(provider, caps, ext);
        this.WGL_ARB_buffer_region = WGLCapabilities.check_WGL_ARB_buffer_region(provider, caps, ext);
        this.WGL_ARB_context_flush_control = ext.contains("WGL_ARB_context_flush_control");
        this.WGL_ARB_create_context = WGLCapabilities.check_WGL_ARB_create_context(provider, caps, ext);
        this.WGL_ARB_create_context_no_error = ext.contains("WGL_ARB_create_context_no_error");
        this.WGL_ARB_create_context_profile = ext.contains("WGL_ARB_create_context_profile");
        this.WGL_ARB_create_context_robustness = ext.contains("WGL_ARB_create_context_robustness");
        this.WGL_ARB_extensions_string = WGLCapabilities.check_WGL_ARB_extensions_string(provider, caps, ext);
        this.WGL_ARB_framebuffer_sRGB = ext.contains("WGL_ARB_framebuffer_sRGB");
        this.WGL_ARB_make_current_read = WGLCapabilities.check_WGL_ARB_make_current_read(provider, caps, ext);
        this.WGL_ARB_multisample = ext.contains("WGL_ARB_multisample");
        this.WGL_ARB_pbuffer = WGLCapabilities.check_WGL_ARB_pbuffer(provider, caps, ext);
        this.WGL_ARB_pixel_format = WGLCapabilities.check_WGL_ARB_pixel_format(provider, caps, ext);
        this.WGL_ARB_pixel_format_float = ext.contains("WGL_ARB_pixel_format_float");
        this.WGL_ARB_render_texture = WGLCapabilities.check_WGL_ARB_render_texture(provider, caps, ext);
        this.WGL_ARB_robustness_application_isolation = ext.contains("WGL_ARB_robustness_application_isolation");
        this.WGL_ARB_robustness_share_group_isolation = ext.contains("WGL_ARB_robustness_share_group_isolation");
        this.WGL_ATI_pixel_format_float = ext.contains("WGL_ATI_pixel_format_float");
        this.WGL_ATI_render_texture_rectangle = ext.contains("WGL_ATI_render_texture_rectangle");
        this.WGL_EXT_colorspace = ext.contains("WGL_EXT_colorspace");
        this.WGL_EXT_create_context_es2_profile = ext.contains("WGL_EXT_create_context_es2_profile");
        this.WGL_EXT_create_context_es_profile = ext.contains("WGL_EXT_create_context_es_profile");
        this.WGL_EXT_depth_float = ext.contains("WGL_EXT_depth_float");
        this.WGL_EXT_extensions_string = WGLCapabilities.check_WGL_EXT_extensions_string(provider, caps, ext);
        this.WGL_EXT_framebuffer_sRGB = ext.contains("WGL_EXT_framebuffer_sRGB");
        this.WGL_EXT_pixel_format_packed_float = ext.contains("WGL_EXT_pixel_format_packed_float");
        this.WGL_EXT_swap_control = WGLCapabilities.check_WGL_EXT_swap_control(provider, caps, ext);
        this.WGL_EXT_swap_control_tear = ext.contains("WGL_EXT_swap_control_tear");
        this.WGL_NV_copy_image = WGLCapabilities.check_WGL_NV_copy_image(provider, caps, ext);
        this.WGL_NV_delay_before_swap = WGLCapabilities.check_WGL_NV_delay_before_swap(provider, caps, ext);
        this.WGL_NV_DX_interop = WGLCapabilities.check_WGL_NV_DX_interop(provider, caps, ext);
        this.WGL_NV_DX_interop2 = ext.contains("WGL_NV_DX_interop2");
        this.WGL_NV_float_buffer = ext.contains("WGL_NV_float_buffer");
        this.WGL_NV_gpu_affinity = WGLCapabilities.check_WGL_NV_gpu_affinity(provider, caps, ext);
        this.WGL_NV_multigpu_context = ext.contains("WGL_NV_multigpu_context");
        this.WGL_NV_multisample_coverage = ext.contains("WGL_NV_multisample_coverage");
        this.WGL_NV_render_depth_texture = ext.contains("WGL_NV_render_depth_texture");
        this.WGL_NV_render_texture_rectangle = ext.contains("WGL_NV_render_texture_rectangle");
        this.WGL_NV_swap_group = WGLCapabilities.check_WGL_NV_swap_group(provider, caps, ext);
        this.WGL_NV_vertex_array_range = WGLCapabilities.check_WGL_NV_vertex_array_range(provider, caps, ext);
        this.wglGetGPUIDsAMD = caps[0];
        this.wglGetGPUInfoAMD = caps[1];
        this.wglGetContextGPUIDAMD = caps[2];
        this.wglCreateAssociatedContextAMD = caps[3];
        this.wglCreateAssociatedContextAttribsAMD = caps[4];
        this.wglDeleteAssociatedContextAMD = caps[5];
        this.wglMakeAssociatedContextCurrentAMD = caps[6];
        this.wglGetCurrentAssociatedContextAMD = caps[7];
        this.wglBlitContextFramebufferAMD = caps[8];
        this.wglCreateBufferRegionARB = caps[9];
        this.wglDeleteBufferRegionARB = caps[10];
        this.wglSaveBufferRegionARB = caps[11];
        this.wglRestoreBufferRegionARB = caps[12];
        this.wglCreateContextAttribsARB = caps[13];
        this.wglGetExtensionsStringARB = caps[14];
        this.wglMakeContextCurrentARB = caps[15];
        this.wglGetCurrentReadDCARB = caps[16];
        this.wglCreatePbufferARB = caps[17];
        this.wglGetPbufferDCARB = caps[18];
        this.wglReleasePbufferDCARB = caps[19];
        this.wglDestroyPbufferARB = caps[20];
        this.wglQueryPbufferARB = caps[21];
        this.wglGetPixelFormatAttribivARB = caps[22];
        this.wglGetPixelFormatAttribfvARB = caps[23];
        this.wglChoosePixelFormatARB = caps[24];
        this.wglBindTexImageARB = caps[25];
        this.wglReleaseTexImageARB = caps[26];
        this.wglSetPbufferAttribARB = caps[27];
        this.wglGetExtensionsStringEXT = caps[28];
        this.wglSwapIntervalEXT = caps[29];
        this.wglGetSwapIntervalEXT = caps[30];
        this.wglCopyImageSubDataNV = caps[31];
        this.wglDelayBeforeSwapNV = caps[32];
        this.wglDXSetResourceShareHandleNV = caps[33];
        this.wglDXOpenDeviceNV = caps[34];
        this.wglDXCloseDeviceNV = caps[35];
        this.wglDXRegisterObjectNV = caps[36];
        this.wglDXUnregisterObjectNV = caps[37];
        this.wglDXObjectAccessNV = caps[38];
        this.wglDXLockObjectsNV = caps[39];
        this.wglDXUnlockObjectsNV = caps[40];
        this.wglEnumGpusNV = caps[41];
        this.wglEnumGpuDevicesNV = caps[42];
        this.wglCreateAffinityDCNV = caps[43];
        this.wglEnumGpusFromAffinityDCNV = caps[44];
        this.wglDeleteDCNV = caps[45];
        this.wglJoinSwapGroupNV = caps[46];
        this.wglBindSwapBarrierNV = caps[47];
        this.wglQuerySwapGroupNV = caps[48];
        this.wglQueryMaxSwapGroupsNV = caps[49];
        this.wglQueryFrameCountNV = caps[50];
        this.wglResetFrameCountNV = caps[51];
        this.wglAllocateMemoryNV = caps[52];
        this.wglFreeMemoryNV = caps[53];
    }

    private static boolean check_WGL_AMD_gpu_association(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_AMD_gpu_association")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{0, 1, 2, 3, 4, 5, 6, 7}, "wglGetGPUIDsAMD", "wglGetGPUInfoAMD", "wglGetContextGPUIDAMD", "wglCreateAssociatedContextAMD", "wglCreateAssociatedContextAttribsAMD", "wglDeleteAssociatedContextAMD", "wglMakeAssociatedContextCurrentAMD", "wglGetCurrentAssociatedContextAMD") || Checks.reportMissing("WGL", "WGL_AMD_gpu_association");
    }

    private static boolean check_WGL_ARB_buffer_region(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_ARB_buffer_region")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{9, 10, 11, 12}, "wglCreateBufferRegionARB", "wglDeleteBufferRegionARB", "wglSaveBufferRegionARB", "wglRestoreBufferRegionARB") || Checks.reportMissing("WGL", "WGL_ARB_buffer_region");
    }

    private static boolean check_WGL_ARB_create_context(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_ARB_create_context")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{13}, "wglCreateContextAttribsARB") || Checks.reportMissing("WGL", "WGL_ARB_create_context");
    }

    private static boolean check_WGL_ARB_extensions_string(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_ARB_extensions_string")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{14}, "wglGetExtensionsStringARB") || Checks.reportMissing("WGL", "WGL_ARB_extensions_string");
    }

    private static boolean check_WGL_ARB_make_current_read(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_ARB_make_current_read")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{15, 16}, "wglMakeContextCurrentARB", "wglGetCurrentReadDCARB") || Checks.reportMissing("WGL", "WGL_ARB_make_current_read");
    }

    private static boolean check_WGL_ARB_pbuffer(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_ARB_pbuffer")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{17, 18, 19, 20, 21}, "wglCreatePbufferARB", "wglGetPbufferDCARB", "wglReleasePbufferDCARB", "wglDestroyPbufferARB", "wglQueryPbufferARB") || Checks.reportMissing("WGL", "WGL_ARB_pbuffer");
    }

    private static boolean check_WGL_ARB_pixel_format(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_ARB_pixel_format")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{22, 23, 24}, "wglGetPixelFormatAttribivARB", "wglGetPixelFormatAttribfvARB", "wglChoosePixelFormatARB") || Checks.reportMissing("WGL", "WGL_ARB_pixel_format");
    }

    private static boolean check_WGL_ARB_render_texture(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_ARB_render_texture")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{25, 26, 27}, "wglBindTexImageARB", "wglReleaseTexImageARB", "wglSetPbufferAttribARB") || Checks.reportMissing("WGL", "WGL_ARB_render_texture");
    }

    private static boolean check_WGL_EXT_extensions_string(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_EXT_extensions_string")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{28}, "wglGetExtensionsStringEXT") || Checks.reportMissing("WGL", "WGL_EXT_extensions_string");
    }

    private static boolean check_WGL_EXT_swap_control(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_EXT_swap_control")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{29, 30}, "wglSwapIntervalEXT", "wglGetSwapIntervalEXT") || Checks.reportMissing("WGL", "WGL_EXT_swap_control");
    }

    private static boolean check_WGL_NV_copy_image(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_NV_copy_image")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{31}, "wglCopyImageSubDataNV") || Checks.reportMissing("WGL", "WGL_NV_copy_image");
    }

    private static boolean check_WGL_NV_delay_before_swap(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_NV_delay_before_swap")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{32}, "wglDelayBeforeSwapNV") || Checks.reportMissing("WGL", "WGL_NV_delay_before_swap");
    }

    private static boolean check_WGL_NV_DX_interop(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_NV_DX_interop")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{33, 34, 35, 36, 37, 38, 39, 40}, "wglDXSetResourceShareHandleNV", "wglDXOpenDeviceNV", "wglDXCloseDeviceNV", "wglDXRegisterObjectNV", "wglDXUnregisterObjectNV", "wglDXObjectAccessNV", "wglDXLockObjectsNV", "wglDXUnlockObjectsNV") || Checks.reportMissing("WGL", "WGL_NV_DX_interop");
    }

    private static boolean check_WGL_NV_gpu_affinity(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_NV_gpu_affinity")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{41, 42, 43, 44, 45}, "wglEnumGpusNV", "wglEnumGpuDevicesNV", "wglCreateAffinityDCNV", "wglEnumGpusFromAffinityDCNV", "wglDeleteDCNV") || Checks.reportMissing("WGL", "WGL_NV_gpu_affinity");
    }

    private static boolean check_WGL_NV_swap_group(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_NV_swap_group")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{46, 47, 48, 49, 50, 51}, "wglJoinSwapGroupNV", "wglBindSwapBarrierNV", "wglQuerySwapGroupNV", "wglQueryMaxSwapGroupsNV", "wglQueryFrameCountNV", "wglResetFrameCountNV") || Checks.reportMissing("WGL", "WGL_NV_swap_group");
    }

    private static boolean check_WGL_NV_vertex_array_range(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("WGL_NV_vertex_array_range")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{52, 53}, "wglAllocateMemoryNV", "wglFreeMemoryNV") || Checks.reportMissing("WGL", "WGL_NV_vertex_array_range");
    }
}

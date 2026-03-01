package org.lwjgl.opengl;

import java.util.Set;
import org.lwjgl.system.Checks;
import org.lwjgl.system.FunctionProvider;

public final class GLXCapabilities {
    public final long glXQueryExtensionsString;
    public final long glXGetClientString;
    public final long glXQueryServerString;
    public final long glXGetCurrentDisplay;
    public final long glXGetFBConfigs;
    public final long glXChooseFBConfig;
    public final long glXGetFBConfigAttrib;
    public final long glXGetVisualFromFBConfig;
    public final long glXCreateWindow;
    public final long glXCreatePixmap;
    public final long glXDestroyPixmap;
    public final long glXCreatePbuffer;
    public final long glXDestroyPbuffer;
    public final long glXQueryDrawable;
    public final long glXCreateNewContext;
    public final long glXMakeContextCurrent;
    public final long glXGetCurrentReadDrawable;
    public final long glXQueryContext;
    public final long glXSelectEvent;
    public final long glXGetSelectedEvent;
    public final long glXGetProcAddress;
    public final long glXBlitContextFramebufferAMD;
    public final long glXCreateAssociatedContextAMD;
    public final long glXCreateAssociatedContextAttribsAMD;
    public final long glXDeleteAssociatedContextAMD;
    public final long glXGetContextGPUIDAMD;
    public final long glXGetCurrentAssociatedContextAMD;
    public final long glXGetGPUIDsAMD;
    public final long glXGetGPUInfoAMD;
    public final long glXMakeAssociatedContextCurrentAMD;
    public final long glXCreateContextAttribsARB;
    public final long glXGetProcAddressARB;
    public final long glXGetCurrentDisplayEXT;
    public final long glXQueryContextInfoEXT;
    public final long glXGetContextIDEXT;
    public final long glXImportContextEXT;
    public final long glXFreeContextEXT;
    public final long glXSwapIntervalEXT;
    public final long glXBindTexImageEXT;
    public final long glXReleaseTexImageEXT;
    public final long glXCopyBufferSubDataNV;
    public final long glXNamedCopyBufferSubDataNV;
    public final long glXCopyImageSubDataNV;
    public final long glXDelayBeforeSwapNV;
    public final long glXJoinSwapGroupNV;
    public final long glXBindSwapBarrierNV;
    public final long glXQuerySwapGroupNV;
    public final long glXQueryMaxSwapGroupsNV;
    public final long glXQueryFrameCountNV;
    public final long glXResetFrameCountNV;
    public final long glXMakeCurrentReadSGI;
    public final long glXGetCurrentReadDrawableSGI;
    public final long glXSwapIntervalSGI;
    public final long glXGetVideoSyncSGI;
    public final long glXWaitVideoSyncSGI;
    public final long glXGetFBConfigAttribSGIX;
    public final long glXChooseFBConfigSGIX;
    public final long glXCreateGLXPixmapWithConfigSGIX;
    public final long glXCreateContextWithConfigSGIX;
    public final long glXGetVisualFromFBConfigSGIX;
    public final long glXGetFBConfigFromVisualSGIX;
    public final long glXCreateGLXPbufferSGIX;
    public final long glXDestroyGLXPbufferSGIX;
    public final long glXQueryGLXPbufferSGIX;
    public final long glXSelectEventSGIX;
    public final long glXGetSelectedEventSGIX;
    public final long glXBindSwapBarrierSGIX;
    public final long glXQueryMaxSwapBarriersSGIX;
    public final long glXJoinSwapGroupSGIX;
    public final boolean GLX11;
    public final boolean GLX12;
    public final boolean GLX13;
    public final boolean GLX14;
    public final boolean GLX_AMD_gpu_association;
    public final boolean GLX_ARB_context_flush_control;
    public final boolean GLX_ARB_create_context;
    public final boolean GLX_ARB_create_context_no_error;
    public final boolean GLX_ARB_create_context_profile;
    public final boolean GLX_ARB_create_context_robustness;
    public final boolean GLX_ARB_fbconfig_float;
    public final boolean GLX_ARB_framebuffer_sRGB;
    public final boolean GLX_ARB_get_proc_address;
    public final boolean GLX_ARB_multisample;
    public final boolean GLX_ARB_robustness_application_isolation;
    public final boolean GLX_ARB_robustness_share_group_isolation;
    public final boolean GLX_ARB_vertex_buffer_object;
    public final boolean GLX_EXT_buffer_age;
    public final boolean GLX_EXT_context_priority;
    public final boolean GLX_EXT_create_context_es2_profile;
    public final boolean GLX_EXT_create_context_es_profile;
    public final boolean GLX_EXT_fbconfig_packed_float;
    public final boolean GLX_EXT_framebuffer_sRGB;
    public final boolean GLX_EXT_get_drawable_type;
    public final boolean GLX_EXT_import_context;
    public final boolean GLX_EXT_no_config_context;
    public final boolean GLX_EXT_stereo_tree;
    public final boolean GLX_EXT_swap_control;
    public final boolean GLX_EXT_swap_control_tear;
    public final boolean GLX_EXT_texture_from_pixmap;
    public final boolean GLX_EXT_visual_info;
    public final boolean GLX_EXT_visual_rating;
    public final boolean GLX_INTEL_swap_event;
    public final boolean GLX_NV_copy_buffer;
    public final boolean GLX_NV_copy_image;
    public final boolean GLX_NV_delay_before_swap;
    public final boolean GLX_NV_float_buffer;
    public final boolean GLX_NV_multigpu_context;
    public final boolean GLX_NV_multisample_coverage;
    public final boolean GLX_NV_robustness_video_memory_purge;
    public final boolean GLX_NV_swap_group;
    public final boolean GLX_SGI_make_current_read;
    public final boolean GLX_SGI_swap_control;
    public final boolean GLX_SGI_video_sync;
    public final boolean GLX_SGIX_fbconfig;
    public final boolean GLX_SGIX_pbuffer;
    public final boolean GLX_SGIX_swap_barrier;
    public final boolean GLX_SGIX_swap_group;

    GLXCapabilities(FunctionProvider provider, Set<String> ext) {
        long[] caps = new long[69];
        this.GLX11 = GLXCapabilities.check_GLX11(provider, caps, ext);
        this.GLX12 = GLXCapabilities.check_GLX12(provider, caps, ext);
        this.GLX13 = GLXCapabilities.check_GLX13(provider, caps, ext);
        this.GLX14 = GLXCapabilities.check_GLX14(provider, caps, ext);
        this.GLX_AMD_gpu_association = GLXCapabilities.check_GLX_AMD_gpu_association(provider, caps, ext);
        this.GLX_ARB_context_flush_control = ext.contains("GLX_ARB_context_flush_control");
        this.GLX_ARB_create_context = GLXCapabilities.check_GLX_ARB_create_context(provider, caps, ext);
        this.GLX_ARB_create_context_no_error = ext.contains("GLX_ARB_create_context_no_error");
        this.GLX_ARB_create_context_profile = ext.contains("GLX_ARB_create_context_profile");
        this.GLX_ARB_create_context_robustness = ext.contains("GLX_ARB_create_context_robustness");
        this.GLX_ARB_fbconfig_float = ext.contains("GLX_ARB_fbconfig_float");
        this.GLX_ARB_framebuffer_sRGB = ext.contains("GLX_ARB_framebuffer_sRGB");
        this.GLX_ARB_get_proc_address = GLXCapabilities.check_GLX_ARB_get_proc_address(provider, caps, ext);
        this.GLX_ARB_multisample = ext.contains("GLX_ARB_multisample");
        this.GLX_ARB_robustness_application_isolation = ext.contains("GLX_ARB_robustness_application_isolation");
        this.GLX_ARB_robustness_share_group_isolation = ext.contains("GLX_ARB_robustness_share_group_isolation");
        this.GLX_ARB_vertex_buffer_object = ext.contains("GLX_ARB_vertex_buffer_object");
        this.GLX_EXT_buffer_age = ext.contains("GLX_EXT_buffer_age");
        this.GLX_EXT_context_priority = ext.contains("GLX_EXT_context_priority");
        this.GLX_EXT_create_context_es2_profile = ext.contains("GLX_EXT_create_context_es2_profile");
        this.GLX_EXT_create_context_es_profile = ext.contains("GLX_EXT_create_context_es_profile");
        this.GLX_EXT_fbconfig_packed_float = ext.contains("GLX_EXT_fbconfig_packed_float");
        this.GLX_EXT_framebuffer_sRGB = ext.contains("GLX_EXT_framebuffer_sRGB");
        this.GLX_EXT_get_drawable_type = ext.contains("GLX_EXT_get_drawable_type");
        this.GLX_EXT_import_context = GLXCapabilities.check_GLX_EXT_import_context(provider, caps, ext);
        this.GLX_EXT_no_config_context = ext.contains("GLX_EXT_no_config_context");
        this.GLX_EXT_stereo_tree = ext.contains("GLX_EXT_stereo_tree");
        this.GLX_EXT_swap_control = GLXCapabilities.check_GLX_EXT_swap_control(provider, caps, ext);
        this.GLX_EXT_swap_control_tear = ext.contains("GLX_EXT_swap_control_tear");
        this.GLX_EXT_texture_from_pixmap = GLXCapabilities.check_GLX_EXT_texture_from_pixmap(provider, caps, ext);
        this.GLX_EXT_visual_info = ext.contains("GLX_EXT_visual_info");
        this.GLX_EXT_visual_rating = ext.contains("GLX_EXT_visual_rating");
        this.GLX_INTEL_swap_event = ext.contains("GLX_INTEL_swap_event");
        this.GLX_NV_copy_buffer = GLXCapabilities.check_GLX_NV_copy_buffer(provider, caps, ext);
        this.GLX_NV_copy_image = GLXCapabilities.check_GLX_NV_copy_image(provider, caps, ext);
        this.GLX_NV_delay_before_swap = GLXCapabilities.check_GLX_NV_delay_before_swap(provider, caps, ext);
        this.GLX_NV_float_buffer = ext.contains("GLX_NV_float_buffer");
        this.GLX_NV_multigpu_context = ext.contains("GLX_NV_multigpu_context");
        this.GLX_NV_multisample_coverage = ext.contains("GLX_NV_multisample_coverage");
        this.GLX_NV_robustness_video_memory_purge = ext.contains("GLX_NV_robustness_video_memory_purge");
        this.GLX_NV_swap_group = GLXCapabilities.check_GLX_NV_swap_group(provider, caps, ext);
        this.GLX_SGI_make_current_read = GLXCapabilities.check_GLX_SGI_make_current_read(provider, caps, ext);
        this.GLX_SGI_swap_control = GLXCapabilities.check_GLX_SGI_swap_control(provider, caps, ext);
        this.GLX_SGI_video_sync = GLXCapabilities.check_GLX_SGI_video_sync(provider, caps, ext);
        this.GLX_SGIX_fbconfig = GLXCapabilities.check_GLX_SGIX_fbconfig(provider, caps, ext);
        this.GLX_SGIX_pbuffer = GLXCapabilities.check_GLX_SGIX_pbuffer(provider, caps, ext);
        this.GLX_SGIX_swap_barrier = GLXCapabilities.check_GLX_SGIX_swap_barrier(provider, caps, ext);
        this.GLX_SGIX_swap_group = GLXCapabilities.check_GLX_SGIX_swap_group(provider, caps, ext);
        this.glXQueryExtensionsString = caps[0];
        this.glXGetClientString = caps[1];
        this.glXQueryServerString = caps[2];
        this.glXGetCurrentDisplay = caps[3];
        this.glXGetFBConfigs = caps[4];
        this.glXChooseFBConfig = caps[5];
        this.glXGetFBConfigAttrib = caps[6];
        this.glXGetVisualFromFBConfig = caps[7];
        this.glXCreateWindow = caps[8];
        this.glXCreatePixmap = caps[9];
        this.glXDestroyPixmap = caps[10];
        this.glXCreatePbuffer = caps[11];
        this.glXDestroyPbuffer = caps[12];
        this.glXQueryDrawable = caps[13];
        this.glXCreateNewContext = caps[14];
        this.glXMakeContextCurrent = caps[15];
        this.glXGetCurrentReadDrawable = caps[16];
        this.glXQueryContext = caps[17];
        this.glXSelectEvent = caps[18];
        this.glXGetSelectedEvent = caps[19];
        this.glXGetProcAddress = caps[20];
        this.glXBlitContextFramebufferAMD = caps[21];
        this.glXCreateAssociatedContextAMD = caps[22];
        this.glXCreateAssociatedContextAttribsAMD = caps[23];
        this.glXDeleteAssociatedContextAMD = caps[24];
        this.glXGetContextGPUIDAMD = caps[25];
        this.glXGetCurrentAssociatedContextAMD = caps[26];
        this.glXGetGPUIDsAMD = caps[27];
        this.glXGetGPUInfoAMD = caps[28];
        this.glXMakeAssociatedContextCurrentAMD = caps[29];
        this.glXCreateContextAttribsARB = caps[30];
        this.glXGetProcAddressARB = caps[31];
        this.glXGetCurrentDisplayEXT = caps[32];
        this.glXQueryContextInfoEXT = caps[33];
        this.glXGetContextIDEXT = caps[34];
        this.glXImportContextEXT = caps[35];
        this.glXFreeContextEXT = caps[36];
        this.glXSwapIntervalEXT = caps[37];
        this.glXBindTexImageEXT = caps[38];
        this.glXReleaseTexImageEXT = caps[39];
        this.glXCopyBufferSubDataNV = caps[40];
        this.glXNamedCopyBufferSubDataNV = caps[41];
        this.glXCopyImageSubDataNV = caps[42];
        this.glXDelayBeforeSwapNV = caps[43];
        this.glXJoinSwapGroupNV = caps[44];
        this.glXBindSwapBarrierNV = caps[45];
        this.glXQuerySwapGroupNV = caps[46];
        this.glXQueryMaxSwapGroupsNV = caps[47];
        this.glXQueryFrameCountNV = caps[48];
        this.glXResetFrameCountNV = caps[49];
        this.glXMakeCurrentReadSGI = caps[50];
        this.glXGetCurrentReadDrawableSGI = caps[51];
        this.glXSwapIntervalSGI = caps[52];
        this.glXGetVideoSyncSGI = caps[53];
        this.glXWaitVideoSyncSGI = caps[54];
        this.glXGetFBConfigAttribSGIX = caps[55];
        this.glXChooseFBConfigSGIX = caps[56];
        this.glXCreateGLXPixmapWithConfigSGIX = caps[57];
        this.glXCreateContextWithConfigSGIX = caps[58];
        this.glXGetVisualFromFBConfigSGIX = caps[59];
        this.glXGetFBConfigFromVisualSGIX = caps[60];
        this.glXCreateGLXPbufferSGIX = caps[61];
        this.glXDestroyGLXPbufferSGIX = caps[62];
        this.glXQueryGLXPbufferSGIX = caps[63];
        this.glXSelectEventSGIX = caps[64];
        this.glXGetSelectedEventSGIX = caps[65];
        this.glXBindSwapBarrierSGIX = caps[66];
        this.glXQueryMaxSwapBarriersSGIX = caps[67];
        this.glXJoinSwapGroupSGIX = caps[68];
    }

    private static boolean check_GLX11(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX11")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{0, 1, 2}, "glXQueryExtensionsString", "glXGetClientString", "glXQueryServerString") || Checks.reportMissing("GLX", "GLX11");
    }

    private static boolean check_GLX12(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX12")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{3}, "glXGetCurrentDisplay") || Checks.reportMissing("GLX", "GLX12");
    }

    private static boolean check_GLX13(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX13")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19}, "glXGetFBConfigs", "glXChooseFBConfig", "glXGetFBConfigAttrib", "glXGetVisualFromFBConfig", "glXCreateWindow", "glXCreatePixmap", "glXDestroyPixmap", "glXCreatePbuffer", "glXDestroyPbuffer", "glXQueryDrawable", "glXCreateNewContext", "glXMakeContextCurrent", "glXGetCurrentReadDrawable", "glXQueryContext", "glXSelectEvent", "glXGetSelectedEvent") || Checks.reportMissing("GLX", "GLX13");
    }

    private static boolean check_GLX14(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX14")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{20}, "glXGetProcAddress") || Checks.reportMissing("GLX", "GLX14");
    }

    private static boolean check_GLX_AMD_gpu_association(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_AMD_gpu_association")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{21, 22, 23, 24, 25, 26, 27, 28, 29}, "glXBlitContextFramebufferAMD", "glXCreateAssociatedContextAMD", "glXCreateAssociatedContextAttribsAMD", "glXDeleteAssociatedContextAMD", "glXGetContextGPUIDAMD", "glXGetCurrentAssociatedContextAMD", "glXGetGPUIDsAMD", "glXGetGPUInfoAMD", "glXMakeAssociatedContextCurrentAMD") || Checks.reportMissing("GLX", "GLX_AMD_gpu_association");
    }

    private static boolean check_GLX_ARB_create_context(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_ARB_create_context")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{30}, "glXCreateContextAttribsARB") || Checks.reportMissing("GLX", "GLX_ARB_create_context");
    }

    private static boolean check_GLX_ARB_get_proc_address(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_ARB_get_proc_address")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{31}, "glXGetProcAddressARB") || Checks.reportMissing("GLX", "GLX_ARB_get_proc_address");
    }

    private static boolean check_GLX_EXT_import_context(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_EXT_import_context")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{32, 33, 34, 35, 36}, "glXGetCurrentDisplayEXT", "glXQueryContextInfoEXT", "glXGetContextIDEXT", "glXImportContextEXT", "glXFreeContextEXT") || Checks.reportMissing("GLX", "GLX_EXT_import_context");
    }

    private static boolean check_GLX_EXT_swap_control(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_EXT_swap_control")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{37}, "glXSwapIntervalEXT") || Checks.reportMissing("GLX", "GLX_EXT_swap_control");
    }

    private static boolean check_GLX_EXT_texture_from_pixmap(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_EXT_texture_from_pixmap")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{38, 39}, "glXBindTexImageEXT", "glXReleaseTexImageEXT") || Checks.reportMissing("GLX", "GLX_EXT_texture_from_pixmap");
    }

    private static boolean check_GLX_NV_copy_buffer(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_NV_copy_buffer")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{40, 41}, "glXCopyBufferSubDataNV", "glXNamedCopyBufferSubDataNV") || Checks.reportMissing("GLX", "GLX_NV_copy_buffer");
    }

    private static boolean check_GLX_NV_copy_image(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_NV_copy_image")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{42}, "glXCopyImageSubDataNV") || Checks.reportMissing("GLX", "GLX_NV_copy_image");
    }

    private static boolean check_GLX_NV_delay_before_swap(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_NV_delay_before_swap")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{43}, "glXDelayBeforeSwapNV") || Checks.reportMissing("GLX", "GLX_NV_delay_before_swap");
    }

    private static boolean check_GLX_NV_swap_group(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_NV_swap_group")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{44, 45, 46, 47, 48, 49}, "glXJoinSwapGroupNV", "glXBindSwapBarrierNV", "glXQuerySwapGroupNV", "glXQueryMaxSwapGroupsNV", "glXQueryFrameCountNV", "glXResetFrameCountNV") || Checks.reportMissing("GLX", "GLX_NV_swap_group");
    }

    private static boolean check_GLX_SGI_make_current_read(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_SGI_make_current_read")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{50, 51}, "glXMakeCurrentReadSGI", "glXGetCurrentReadDrawableSGI") || Checks.reportMissing("GLX", "GLX_SGI_make_current_read");
    }

    private static boolean check_GLX_SGI_swap_control(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_SGI_swap_control")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{52}, "glXSwapIntervalSGI") || Checks.reportMissing("GLX", "GLX_SGI_swap_control");
    }

    private static boolean check_GLX_SGI_video_sync(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_SGI_video_sync")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{53, 54}, "glXGetVideoSyncSGI", "glXWaitVideoSyncSGI") || Checks.reportMissing("GLX", "GLX_SGI_video_sync");
    }

    private static boolean check_GLX_SGIX_fbconfig(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_SGIX_fbconfig")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{55, 56, 57, 58, 59, 60}, "glXGetFBConfigAttribSGIX", "glXChooseFBConfigSGIX", "glXCreateGLXPixmapWithConfigSGIX", "glXCreateContextWithConfigSGIX", "glXGetVisualFromFBConfigSGIX", "glXGetFBConfigFromVisualSGIX") || Checks.reportMissing("GLX", "GLX_SGIX_fbconfig");
    }

    private static boolean check_GLX_SGIX_pbuffer(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_SGIX_pbuffer")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{61, 62, 63, 64, 65}, "glXCreateGLXPbufferSGIX", "glXDestroyGLXPbufferSGIX", "glXQueryGLXPbufferSGIX", "glXSelectEventSGIX", "glXGetSelectedEventSGIX") || Checks.reportMissing("GLX", "GLX_SGIX_pbuffer");
    }

    private static boolean check_GLX_SGIX_swap_barrier(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_SGIX_swap_barrier")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{66, 67}, "glXBindSwapBarrierSGIX", "glXQueryMaxSwapBarriersSGIX") || Checks.reportMissing("GLX", "GLX_SGIX_swap_barrier");
    }

    private static boolean check_GLX_SGIX_swap_group(FunctionProvider provider, long[] caps, Set<String> ext) {
        if (!ext.contains("GLX_SGIX_swap_group")) {
            return false;
        }
        return Checks.checkFunctions(provider, caps, new int[]{68}, "glXJoinSwapGroupSGIX") || Checks.reportMissing("GLX", "GLX_SGIX_swap_group");
    }
}

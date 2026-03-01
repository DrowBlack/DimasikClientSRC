package org.lwjgl.system.linux;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.system.linux.XAnyEvent;
import org.lwjgl.system.linux.XButtonEvent;
import org.lwjgl.system.linux.XCirculateEvent;
import org.lwjgl.system.linux.XCirculateRequestEvent;
import org.lwjgl.system.linux.XClientMessageEvent;
import org.lwjgl.system.linux.XColormapEvent;
import org.lwjgl.system.linux.XConfigureEvent;
import org.lwjgl.system.linux.XConfigureRequestEvent;
import org.lwjgl.system.linux.XCreateWindowEvent;
import org.lwjgl.system.linux.XCrossingEvent;
import org.lwjgl.system.linux.XDestroyWindowEvent;
import org.lwjgl.system.linux.XErrorEvent;
import org.lwjgl.system.linux.XExposeEvent;
import org.lwjgl.system.linux.XFocusChangeEvent;
import org.lwjgl.system.linux.XGenericEvent;
import org.lwjgl.system.linux.XGenericEventCookie;
import org.lwjgl.system.linux.XGraphicsExposeEvent;
import org.lwjgl.system.linux.XGravityEvent;
import org.lwjgl.system.linux.XKeyEvent;
import org.lwjgl.system.linux.XKeymapEvent;
import org.lwjgl.system.linux.XMapEvent;
import org.lwjgl.system.linux.XMapRequestEvent;
import org.lwjgl.system.linux.XMappingEvent;
import org.lwjgl.system.linux.XMotionEvent;
import org.lwjgl.system.linux.XNoExposeEvent;
import org.lwjgl.system.linux.XPropertyEvent;
import org.lwjgl.system.linux.XReparentEvent;
import org.lwjgl.system.linux.XResizeRequestEvent;
import org.lwjgl.system.linux.XSelectionClearEvent;
import org.lwjgl.system.linux.XSelectionEvent;
import org.lwjgl.system.linux.XSelectionRequestEvent;
import org.lwjgl.system.linux.XUnmapEvent;
import org.lwjgl.system.linux.XVisibilityEvent;

public class XEvent
extends Struct<XEvent>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int TYPE;
    public static final int XANY;
    public static final int XKEY;
    public static final int XBUTTON;
    public static final int XMOTION;
    public static final int XCROSSING;
    public static final int XFOCUS;
    public static final int XEXPOSE;
    public static final int XGRAPHICSEXPOSE;
    public static final int XNOEXPOSE;
    public static final int XVISIBILITY;
    public static final int XCREATEWINDOW;
    public static final int XDESTROYWINDOW;
    public static final int XUNMAP;
    public static final int XMAP;
    public static final int XMAPREQUEST;
    public static final int XREPARENT;
    public static final int XCONFIGURE;
    public static final int XGRAVITY;
    public static final int XRESIZEREQUEST;
    public static final int XCONFIGUREREQUEST;
    public static final int XCIRCULATE;
    public static final int XCIRCULATEREQUEST;
    public static final int XPROPERTY;
    public static final int XSELECTIONCLEAR;
    public static final int XSELECTIONREQUEST;
    public static final int XSELECTION;
    public static final int XCOLORMAP;
    public static final int XCLIENT;
    public static final int XMAPPING;
    public static final int XERROR;
    public static final int XKEYMAP;
    public static final int XGENERIC;
    public static final int XCOOKIE;

    protected XEvent(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected XEvent create(long address, @Nullable ByteBuffer container) {
        return new XEvent(address, container);
    }

    public XEvent(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), XEvent.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public int type() {
        return XEvent.ntype(this.address());
    }

    public XAnyEvent xany() {
        return XEvent.nxany(this.address());
    }

    public XKeyEvent xkey() {
        return XEvent.nxkey(this.address());
    }

    public XButtonEvent xbutton() {
        return XEvent.nxbutton(this.address());
    }

    public XMotionEvent xmotion() {
        return XEvent.nxmotion(this.address());
    }

    public XCrossingEvent xcrossing() {
        return XEvent.nxcrossing(this.address());
    }

    public XFocusChangeEvent xfocus() {
        return XEvent.nxfocus(this.address());
    }

    public XExposeEvent xexpose() {
        return XEvent.nxexpose(this.address());
    }

    public XGraphicsExposeEvent xgraphicsexpose() {
        return XEvent.nxgraphicsexpose(this.address());
    }

    public XNoExposeEvent xnoexpose() {
        return XEvent.nxnoexpose(this.address());
    }

    public XVisibilityEvent xvisibility() {
        return XEvent.nxvisibility(this.address());
    }

    public XCreateWindowEvent xcreatewindow() {
        return XEvent.nxcreatewindow(this.address());
    }

    public XDestroyWindowEvent xdestroywindow() {
        return XEvent.nxdestroywindow(this.address());
    }

    public XUnmapEvent xunmap() {
        return XEvent.nxunmap(this.address());
    }

    public XMapEvent xmap() {
        return XEvent.nxmap(this.address());
    }

    public XMapRequestEvent xmaprequest() {
        return XEvent.nxmaprequest(this.address());
    }

    public XReparentEvent xreparent() {
        return XEvent.nxreparent(this.address());
    }

    public XConfigureEvent xconfigure() {
        return XEvent.nxconfigure(this.address());
    }

    public XGravityEvent xgravity() {
        return XEvent.nxgravity(this.address());
    }

    public XResizeRequestEvent xresizerequest() {
        return XEvent.nxresizerequest(this.address());
    }

    public XConfigureRequestEvent xconfigurerequest() {
        return XEvent.nxconfigurerequest(this.address());
    }

    public XCirculateEvent xcirculate() {
        return XEvent.nxcirculate(this.address());
    }

    public XCirculateRequestEvent xcirculaterequest() {
        return XEvent.nxcirculaterequest(this.address());
    }

    public XPropertyEvent xproperty() {
        return XEvent.nxproperty(this.address());
    }

    public XSelectionClearEvent xselectionclear() {
        return XEvent.nxselectionclear(this.address());
    }

    public XSelectionRequestEvent xselectionrequest() {
        return XEvent.nxselectionrequest(this.address());
    }

    public XSelectionEvent xselection() {
        return XEvent.nxselection(this.address());
    }

    public XColormapEvent xcolormap() {
        return XEvent.nxcolormap(this.address());
    }

    public XClientMessageEvent xclient() {
        return XEvent.nxclient(this.address());
    }

    public XMappingEvent xmapping() {
        return XEvent.nxmapping(this.address());
    }

    public XErrorEvent xerror() {
        return XEvent.nxerror(this.address());
    }

    public XKeymapEvent xkeymap() {
        return XEvent.nxkeymap(this.address());
    }

    public XGenericEvent xgeneric() {
        return XEvent.nxgeneric(this.address());
    }

    public XGenericEventCookie xcookie() {
        return XEvent.nxcookie(this.address());
    }

    public static XEvent malloc() {
        return new XEvent(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static XEvent calloc() {
        return new XEvent(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static XEvent create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new XEvent(MemoryUtil.memAddress(container), container);
    }

    public static XEvent create(long address) {
        return new XEvent(address, null);
    }

    @Nullable
    public static XEvent createSafe(long address) {
        return address == 0L ? null : new XEvent(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(XEvent.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = XEvent.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    @Deprecated
    public static XEvent mallocStack() {
        return XEvent.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XEvent callocStack() {
        return XEvent.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XEvent mallocStack(MemoryStack stack) {
        return XEvent.malloc(stack);
    }

    @Deprecated
    public static XEvent callocStack(MemoryStack stack) {
        return XEvent.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return XEvent.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return XEvent.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return XEvent.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return XEvent.calloc(capacity, stack);
    }

    public static XEvent malloc(MemoryStack stack) {
        return new XEvent(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static XEvent calloc(MemoryStack stack) {
        return new XEvent(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int ntype(long struct) {
        return UNSAFE.getInt(null, struct + (long)TYPE);
    }

    public static XAnyEvent nxany(long struct) {
        return XAnyEvent.create(struct + (long)XANY);
    }

    public static XKeyEvent nxkey(long struct) {
        return XKeyEvent.create(struct + (long)XKEY);
    }

    public static XButtonEvent nxbutton(long struct) {
        return XButtonEvent.create(struct + (long)XBUTTON);
    }

    public static XMotionEvent nxmotion(long struct) {
        return XMotionEvent.create(struct + (long)XMOTION);
    }

    public static XCrossingEvent nxcrossing(long struct) {
        return XCrossingEvent.create(struct + (long)XCROSSING);
    }

    public static XFocusChangeEvent nxfocus(long struct) {
        return XFocusChangeEvent.create(struct + (long)XFOCUS);
    }

    public static XExposeEvent nxexpose(long struct) {
        return XExposeEvent.create(struct + (long)XEXPOSE);
    }

    public static XGraphicsExposeEvent nxgraphicsexpose(long struct) {
        return XGraphicsExposeEvent.create(struct + (long)XGRAPHICSEXPOSE);
    }

    public static XNoExposeEvent nxnoexpose(long struct) {
        return XNoExposeEvent.create(struct + (long)XNOEXPOSE);
    }

    public static XVisibilityEvent nxvisibility(long struct) {
        return XVisibilityEvent.create(struct + (long)XVISIBILITY);
    }

    public static XCreateWindowEvent nxcreatewindow(long struct) {
        return XCreateWindowEvent.create(struct + (long)XCREATEWINDOW);
    }

    public static XDestroyWindowEvent nxdestroywindow(long struct) {
        return XDestroyWindowEvent.create(struct + (long)XDESTROYWINDOW);
    }

    public static XUnmapEvent nxunmap(long struct) {
        return XUnmapEvent.create(struct + (long)XUNMAP);
    }

    public static XMapEvent nxmap(long struct) {
        return XMapEvent.create(struct + (long)XMAP);
    }

    public static XMapRequestEvent nxmaprequest(long struct) {
        return XMapRequestEvent.create(struct + (long)XMAPREQUEST);
    }

    public static XReparentEvent nxreparent(long struct) {
        return XReparentEvent.create(struct + (long)XREPARENT);
    }

    public static XConfigureEvent nxconfigure(long struct) {
        return XConfigureEvent.create(struct + (long)XCONFIGURE);
    }

    public static XGravityEvent nxgravity(long struct) {
        return XGravityEvent.create(struct + (long)XGRAVITY);
    }

    public static XResizeRequestEvent nxresizerequest(long struct) {
        return XResizeRequestEvent.create(struct + (long)XRESIZEREQUEST);
    }

    public static XConfigureRequestEvent nxconfigurerequest(long struct) {
        return XConfigureRequestEvent.create(struct + (long)XCONFIGUREREQUEST);
    }

    public static XCirculateEvent nxcirculate(long struct) {
        return XCirculateEvent.create(struct + (long)XCIRCULATE);
    }

    public static XCirculateRequestEvent nxcirculaterequest(long struct) {
        return XCirculateRequestEvent.create(struct + (long)XCIRCULATEREQUEST);
    }

    public static XPropertyEvent nxproperty(long struct) {
        return XPropertyEvent.create(struct + (long)XPROPERTY);
    }

    public static XSelectionClearEvent nxselectionclear(long struct) {
        return XSelectionClearEvent.create(struct + (long)XSELECTIONCLEAR);
    }

    public static XSelectionRequestEvent nxselectionrequest(long struct) {
        return XSelectionRequestEvent.create(struct + (long)XSELECTIONREQUEST);
    }

    public static XSelectionEvent nxselection(long struct) {
        return XSelectionEvent.create(struct + (long)XSELECTION);
    }

    public static XColormapEvent nxcolormap(long struct) {
        return XColormapEvent.create(struct + (long)XCOLORMAP);
    }

    public static XClientMessageEvent nxclient(long struct) {
        return XClientMessageEvent.create(struct + (long)XCLIENT);
    }

    public static XMappingEvent nxmapping(long struct) {
        return XMappingEvent.create(struct + (long)XMAPPING);
    }

    public static XErrorEvent nxerror(long struct) {
        return XErrorEvent.create(struct + (long)XERROR);
    }

    public static XKeymapEvent nxkeymap(long struct) {
        return XKeymapEvent.create(struct + (long)XKEYMAP);
    }

    public static XGenericEvent nxgeneric(long struct) {
        return XGenericEvent.create(struct + (long)XGENERIC);
    }

    public static XGenericEventCookie nxcookie(long struct) {
        return XGenericEventCookie.create(struct + (long)XCOOKIE);
    }

    static {
        Struct.Layout layout = XEvent.__union(XEvent.__member(4), XEvent.__member(XAnyEvent.SIZEOF, XAnyEvent.ALIGNOF), XEvent.__member(XKeyEvent.SIZEOF, XKeyEvent.ALIGNOF), XEvent.__member(XButtonEvent.SIZEOF, XButtonEvent.ALIGNOF), XEvent.__member(XMotionEvent.SIZEOF, XMotionEvent.ALIGNOF), XEvent.__member(XCrossingEvent.SIZEOF, XCrossingEvent.ALIGNOF), XEvent.__member(XFocusChangeEvent.SIZEOF, XFocusChangeEvent.ALIGNOF), XEvent.__member(XExposeEvent.SIZEOF, XExposeEvent.ALIGNOF), XEvent.__member(XGraphicsExposeEvent.SIZEOF, XGraphicsExposeEvent.ALIGNOF), XEvent.__member(XNoExposeEvent.SIZEOF, XNoExposeEvent.ALIGNOF), XEvent.__member(XVisibilityEvent.SIZEOF, XVisibilityEvent.ALIGNOF), XEvent.__member(XCreateWindowEvent.SIZEOF, XCreateWindowEvent.ALIGNOF), XEvent.__member(XDestroyWindowEvent.SIZEOF, XDestroyWindowEvent.ALIGNOF), XEvent.__member(XUnmapEvent.SIZEOF, XUnmapEvent.ALIGNOF), XEvent.__member(XMapEvent.SIZEOF, XMapEvent.ALIGNOF), XEvent.__member(XMapRequestEvent.SIZEOF, XMapRequestEvent.ALIGNOF), XEvent.__member(XReparentEvent.SIZEOF, XReparentEvent.ALIGNOF), XEvent.__member(XConfigureEvent.SIZEOF, XConfigureEvent.ALIGNOF), XEvent.__member(XGravityEvent.SIZEOF, XGravityEvent.ALIGNOF), XEvent.__member(XResizeRequestEvent.SIZEOF, XResizeRequestEvent.ALIGNOF), XEvent.__member(XConfigureRequestEvent.SIZEOF, XConfigureRequestEvent.ALIGNOF), XEvent.__member(XCirculateEvent.SIZEOF, XCirculateEvent.ALIGNOF), XEvent.__member(XCirculateRequestEvent.SIZEOF, XCirculateRequestEvent.ALIGNOF), XEvent.__member(XPropertyEvent.SIZEOF, XPropertyEvent.ALIGNOF), XEvent.__member(XSelectionClearEvent.SIZEOF, XSelectionClearEvent.ALIGNOF), XEvent.__member(XSelectionRequestEvent.SIZEOF, XSelectionRequestEvent.ALIGNOF), XEvent.__member(XSelectionEvent.SIZEOF, XSelectionEvent.ALIGNOF), XEvent.__member(XColormapEvent.SIZEOF, XColormapEvent.ALIGNOF), XEvent.__member(XClientMessageEvent.SIZEOF, XClientMessageEvent.ALIGNOF), XEvent.__member(XMappingEvent.SIZEOF, XMappingEvent.ALIGNOF), XEvent.__member(XErrorEvent.SIZEOF, XErrorEvent.ALIGNOF), XEvent.__member(XKeymapEvent.SIZEOF, XKeymapEvent.ALIGNOF), XEvent.__member(XGenericEvent.SIZEOF, XGenericEvent.ALIGNOF), XEvent.__member(XGenericEventCookie.SIZEOF, XGenericEventCookie.ALIGNOF), XEvent.__padding(24, CLONG_SIZE, true));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        TYPE = layout.offsetof(0);
        XANY = layout.offsetof(1);
        XKEY = layout.offsetof(2);
        XBUTTON = layout.offsetof(3);
        XMOTION = layout.offsetof(4);
        XCROSSING = layout.offsetof(5);
        XFOCUS = layout.offsetof(6);
        XEXPOSE = layout.offsetof(7);
        XGRAPHICSEXPOSE = layout.offsetof(8);
        XNOEXPOSE = layout.offsetof(9);
        XVISIBILITY = layout.offsetof(10);
        XCREATEWINDOW = layout.offsetof(11);
        XDESTROYWINDOW = layout.offsetof(12);
        XUNMAP = layout.offsetof(13);
        XMAP = layout.offsetof(14);
        XMAPREQUEST = layout.offsetof(15);
        XREPARENT = layout.offsetof(16);
        XCONFIGURE = layout.offsetof(17);
        XGRAVITY = layout.offsetof(18);
        XRESIZEREQUEST = layout.offsetof(19);
        XCONFIGUREREQUEST = layout.offsetof(20);
        XCIRCULATE = layout.offsetof(21);
        XCIRCULATEREQUEST = layout.offsetof(22);
        XPROPERTY = layout.offsetof(23);
        XSELECTIONCLEAR = layout.offsetof(24);
        XSELECTIONREQUEST = layout.offsetof(25);
        XSELECTION = layout.offsetof(26);
        XCOLORMAP = layout.offsetof(27);
        XCLIENT = layout.offsetof(28);
        XMAPPING = layout.offsetof(29);
        XERROR = layout.offsetof(30);
        XKEYMAP = layout.offsetof(31);
        XGENERIC = layout.offsetof(32);
        XCOOKIE = layout.offsetof(33);
    }

    public static class Buffer
    extends StructBuffer<XEvent, Buffer>
    implements NativeResource {
        private static final XEvent ELEMENT_FACTORY = XEvent.create(-1L);

        public Buffer(ByteBuffer container) {
            super(container, container.remaining() / SIZEOF);
        }

        public Buffer(long address, int cap) {
            super(address, null, -1, 0, cap, cap);
        }

        Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
            super(address, container, mark, pos, lim, cap);
        }

        @Override
        protected Buffer self() {
            return this;
        }

        @Override
        protected XEvent getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public int type() {
            return XEvent.ntype(this.address());
        }

        public XAnyEvent xany() {
            return XEvent.nxany(this.address());
        }

        public XKeyEvent xkey() {
            return XEvent.nxkey(this.address());
        }

        public XButtonEvent xbutton() {
            return XEvent.nxbutton(this.address());
        }

        public XMotionEvent xmotion() {
            return XEvent.nxmotion(this.address());
        }

        public XCrossingEvent xcrossing() {
            return XEvent.nxcrossing(this.address());
        }

        public XFocusChangeEvent xfocus() {
            return XEvent.nxfocus(this.address());
        }

        public XExposeEvent xexpose() {
            return XEvent.nxexpose(this.address());
        }

        public XGraphicsExposeEvent xgraphicsexpose() {
            return XEvent.nxgraphicsexpose(this.address());
        }

        public XNoExposeEvent xnoexpose() {
            return XEvent.nxnoexpose(this.address());
        }

        public XVisibilityEvent xvisibility() {
            return XEvent.nxvisibility(this.address());
        }

        public XCreateWindowEvent xcreatewindow() {
            return XEvent.nxcreatewindow(this.address());
        }

        public XDestroyWindowEvent xdestroywindow() {
            return XEvent.nxdestroywindow(this.address());
        }

        public XUnmapEvent xunmap() {
            return XEvent.nxunmap(this.address());
        }

        public XMapEvent xmap() {
            return XEvent.nxmap(this.address());
        }

        public XMapRequestEvent xmaprequest() {
            return XEvent.nxmaprequest(this.address());
        }

        public XReparentEvent xreparent() {
            return XEvent.nxreparent(this.address());
        }

        public XConfigureEvent xconfigure() {
            return XEvent.nxconfigure(this.address());
        }

        public XGravityEvent xgravity() {
            return XEvent.nxgravity(this.address());
        }

        public XResizeRequestEvent xresizerequest() {
            return XEvent.nxresizerequest(this.address());
        }

        public XConfigureRequestEvent xconfigurerequest() {
            return XEvent.nxconfigurerequest(this.address());
        }

        public XCirculateEvent xcirculate() {
            return XEvent.nxcirculate(this.address());
        }

        public XCirculateRequestEvent xcirculaterequest() {
            return XEvent.nxcirculaterequest(this.address());
        }

        public XPropertyEvent xproperty() {
            return XEvent.nxproperty(this.address());
        }

        public XSelectionClearEvent xselectionclear() {
            return XEvent.nxselectionclear(this.address());
        }

        public XSelectionRequestEvent xselectionrequest() {
            return XEvent.nxselectionrequest(this.address());
        }

        public XSelectionEvent xselection() {
            return XEvent.nxselection(this.address());
        }

        public XColormapEvent xcolormap() {
            return XEvent.nxcolormap(this.address());
        }

        public XClientMessageEvent xclient() {
            return XEvent.nxclient(this.address());
        }

        public XMappingEvent xmapping() {
            return XEvent.nxmapping(this.address());
        }

        public XErrorEvent xerror() {
            return XEvent.nxerror(this.address());
        }

        public XKeymapEvent xkeymap() {
            return XEvent.nxkeymap(this.address());
        }

        public XGenericEvent xgeneric() {
            return XEvent.nxgeneric(this.address());
        }

        public XGenericEventCookie xcookie() {
            return XEvent.nxcookie(this.address());
        }
    }
}

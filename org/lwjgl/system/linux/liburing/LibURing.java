package org.lwjgl.system.linux.liburing;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.Checks;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.linux.CMsghdr;
import org.lwjgl.system.linux.EpollEvent;
import org.lwjgl.system.linux.IOVec;
import org.lwjgl.system.linux.KernelTimespec;
import org.lwjgl.system.linux.Msghdr;
import org.lwjgl.system.linux.OpenHow;
import org.lwjgl.system.linux.Sockaddr;
import org.lwjgl.system.linux.Statx;
import org.lwjgl.system.linux.liburing.IOURing;
import org.lwjgl.system.linux.liburing.IOURingBuf;
import org.lwjgl.system.linux.liburing.IOURingBufReg;
import org.lwjgl.system.linux.liburing.IOURingBufRing;
import org.lwjgl.system.linux.liburing.IOURingCQE;
import org.lwjgl.system.linux.liburing.IOURingNAPI;
import org.lwjgl.system.linux.liburing.IOURingParams;
import org.lwjgl.system.linux.liburing.IOURingProbe;
import org.lwjgl.system.linux.liburing.IOURingRecvmsgOut;
import org.lwjgl.system.linux.liburing.IOURingRestriction;
import org.lwjgl.system.linux.liburing.IOURingSQE;
import org.lwjgl.system.linux.liburing.IOURingSyncCancelReg;

public class LibURing {
    public static final long LIBURING_UDATA_TIMEOUT = -1L;

    protected LibURing() {
        throw new UnsupportedOperationException();
    }

    public static native long nio_uring_get_probe_ring(long var0);

    @Nullable
    @NativeType(value="struct io_uring_probe *")
    public static IOURingProbe io_uring_get_probe_ring(@NativeType(value="struct io_uring *") IOURing ring) {
        long __result = LibURing.nio_uring_get_probe_ring(ring.address());
        return IOURingProbe.createSafe(__result);
    }

    public static native long nio_uring_get_probe();

    @Nullable
    @NativeType(value="struct io_uring_probe *")
    public static IOURingProbe io_uring_get_probe() {
        long __result = LibURing.nio_uring_get_probe();
        return IOURingProbe.createSafe(__result);
    }

    public static native void nio_uring_free_probe(long var0);

    public static void io_uring_free_probe(@NativeType(value="struct io_uring_probe *") IOURingProbe probe) {
        LibURing.nio_uring_free_probe(probe.address());
    }

    public static native int nio_uring_opcode_supported(long var0, int var2);

    public static int io_uring_opcode_supported(@NativeType(value="struct io_uring_probe const *") IOURingProbe p, int op) {
        return LibURing.nio_uring_opcode_supported(p.address(), op);
    }

    public static native int nio_uring_queue_init_mem(int var0, long var1, long var3, long var5, long var7);

    public static int io_uring_queue_init_mem(@NativeType(value="unsigned") int entries, @NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_params *") IOURingParams p, @NativeType(value="void *") ByteBuffer buf) {
        return LibURing.nio_uring_queue_init_mem(entries, ring.address(), p.address(), MemoryUtil.memAddress(buf), buf.remaining());
    }

    public static native int nio_uring_queue_init_params(int var0, long var1, long var3);

    public static int io_uring_queue_init_params(@NativeType(value="unsigned") int entries, @NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_params *") IOURingParams p) {
        return LibURing.nio_uring_queue_init_params(entries, ring.address(), p.address());
    }

    public static native int nio_uring_queue_init(int var0, long var1, int var3);

    public static int io_uring_queue_init(@NativeType(value="unsigned") int entries, @NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="unsigned") int flags) {
        return LibURing.nio_uring_queue_init(entries, ring.address(), flags);
    }

    public static native int nio_uring_queue_mmap(int var0, long var1, long var3);

    public static int io_uring_queue_mmap(int fd, @NativeType(value="struct io_uring_params *") IOURingParams p, @NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_queue_mmap(fd, p.address(), ring.address());
    }

    public static native int nio_uring_ring_dontfork(long var0);

    public static int io_uring_ring_dontfork(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_ring_dontfork(ring.address());
    }

    public static native void nio_uring_queue_exit(long var0);

    public static void io_uring_queue_exit(@NativeType(value="struct io_uring *") IOURing ring) {
        LibURing.nio_uring_queue_exit(ring.address());
    }

    public static native int nio_uring_peek_batch_cqe(long var0, long var2, int var4);

    @NativeType(value="unsigned")
    public static int io_uring_peek_batch_cqe(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_cqe **") PointerBuffer cqes) {
        return LibURing.nio_uring_peek_batch_cqe(ring.address(), MemoryUtil.memAddress(cqes), cqes.remaining());
    }

    public static native int nio_uring_wait_cqes(long var0, long var2, int var4, long var5, long var7);

    public static int io_uring_wait_cqes(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_cqe **") PointerBuffer cqe_ptr, @Nullable @NativeType(value="struct __kernel_timespec *") KernelTimespec ts, @NativeType(value="sigset_t *") long sigmask) {
        return LibURing.nio_uring_wait_cqes(ring.address(), MemoryUtil.memAddress(cqe_ptr), cqe_ptr.remaining(), MemoryUtil.memAddressSafe(ts), sigmask);
    }

    public static native int nio_uring_wait_cqe_timeout(long var0, long var2, long var4);

    public static int io_uring_wait_cqe_timeout(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_cqe **") PointerBuffer cqe_ptr, @Nullable @NativeType(value="struct __kernel_timespec *") KernelTimespec ts) {
        if (Checks.CHECKS) {
            Checks.check(cqe_ptr, 1);
        }
        return LibURing.nio_uring_wait_cqe_timeout(ring.address(), MemoryUtil.memAddress(cqe_ptr), MemoryUtil.memAddressSafe(ts));
    }

    public static native int nio_uring_submit(long var0);

    public static int io_uring_submit(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_submit(ring.address());
    }

    public static native int nio_uring_submit_and_wait(long var0, int var2);

    public static int io_uring_submit_and_wait(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="unsigned") int wait_nr) {
        return LibURing.nio_uring_submit_and_wait(ring.address(), wait_nr);
    }

    public static native int nio_uring_submit_and_wait_timeout(long var0, long var2, int var4, long var5, long var7);

    public static int io_uring_submit_and_wait_timeout(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_cqe **") PointerBuffer cqe_ptr, @Nullable @NativeType(value="struct __kernel_timespec *") KernelTimespec ts, @NativeType(value="sigset_t *") long sigmask) {
        return LibURing.nio_uring_submit_and_wait_timeout(ring.address(), MemoryUtil.memAddress(cqe_ptr), cqe_ptr.remaining(), MemoryUtil.memAddressSafe(ts), sigmask);
    }

    public static native int nio_uring_register_buffers(long var0, long var2, int var4);

    public static int io_uring_register_buffers(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct iovec const *") IOVec.Buffer iovecs) {
        return LibURing.nio_uring_register_buffers(ring.address(), iovecs.address(), iovecs.remaining());
    }

    public static native int nio_uring_register_buffers_tags(long var0, long var2, long var4, int var6);

    public static int io_uring_register_buffers_tags(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct iovec const *") IOVec.Buffer iovecs, @NativeType(value="__u64 const *") LongBuffer tags) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)tags, iovecs.remaining());
        }
        return LibURing.nio_uring_register_buffers_tags(ring.address(), iovecs.address(), MemoryUtil.memAddress(tags), iovecs.remaining());
    }

    public static native int nio_uring_register_buffers_sparse(long var0, int var2);

    public static int io_uring_register_buffers_sparse(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="unsigned") int nr_iovecs) {
        return LibURing.nio_uring_register_buffers_sparse(ring.address(), nr_iovecs);
    }

    public static native int nio_uring_register_buffers_update_tag(long var0, int var2, long var3, long var5, int var7);

    public static int io_uring_register_buffers_update_tag(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="unsigned") int off, @NativeType(value="struct iovec const *") IOVec.Buffer iovecs, @NativeType(value="__u64 const *") LongBuffer tags) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)tags, iovecs.remaining());
        }
        return LibURing.nio_uring_register_buffers_update_tag(ring.address(), off, iovecs.address(), MemoryUtil.memAddress(tags), iovecs.remaining());
    }

    public static native int nio_uring_unregister_buffers(long var0);

    public static int io_uring_unregister_buffers(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_unregister_buffers(ring.address());
    }

    public static native int nio_uring_register_files(long var0, long var2, int var4);

    public static int io_uring_register_files(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="int const *") IntBuffer files) {
        return LibURing.nio_uring_register_files(ring.address(), MemoryUtil.memAddress(files), files.remaining());
    }

    public static native int nio_uring_register_files_tags(long var0, long var2, long var4, int var6);

    public static int io_uring_register_files_tags(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="int const *") IntBuffer files, @NativeType(value="__u64 const *") LongBuffer tags) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)tags, files.remaining());
        }
        return LibURing.nio_uring_register_files_tags(ring.address(), MemoryUtil.memAddress(files), MemoryUtil.memAddress(tags), files.remaining());
    }

    public static native int nio_uring_register_files_sparse(long var0, int var2);

    public static int io_uring_register_files_sparse(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="unsigned") int nr_files) {
        return LibURing.nio_uring_register_files_sparse(ring.address(), nr_files);
    }

    public static native int nio_uring_register_files_update_tag(long var0, int var2, long var3, long var5, int var7);

    public static int io_uring_register_files_update_tag(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="unsigned") int off, @NativeType(value="int const *") IntBuffer files, @NativeType(value="__u64 const *") LongBuffer tags) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)tags, files.remaining());
        }
        return LibURing.nio_uring_register_files_update_tag(ring.address(), off, MemoryUtil.memAddress(files), MemoryUtil.memAddress(tags), files.remaining());
    }

    public static native int nio_uring_unregister_files(long var0);

    public static int io_uring_unregister_files(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_unregister_files(ring.address());
    }

    public static native int nio_uring_register_files_update(long var0, int var2, long var3, int var5);

    public static int io_uring_register_files_update(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="unsigned") int off, @NativeType(value="int const *") IntBuffer files) {
        return LibURing.nio_uring_register_files_update(ring.address(), off, MemoryUtil.memAddress(files), files.remaining());
    }

    public static native int nio_uring_register_eventfd(long var0, int var2);

    public static int io_uring_register_eventfd(@NativeType(value="struct io_uring *") IOURing ring, int fd) {
        return LibURing.nio_uring_register_eventfd(ring.address(), fd);
    }

    public static native int nio_uring_register_eventfd_async(long var0, int var2);

    public static int io_uring_register_eventfd_async(@NativeType(value="struct io_uring *") IOURing ring, int fd) {
        return LibURing.nio_uring_register_eventfd_async(ring.address(), fd);
    }

    public static native int nio_uring_unregister_eventfd(long var0);

    public static int io_uring_unregister_eventfd(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_unregister_eventfd(ring.address());
    }

    public static native int nio_uring_register_probe(long var0, long var2, int var4);

    public static int io_uring_register_probe(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_probe *") IOURingProbe p, @NativeType(value="unsigned") int nr) {
        return LibURing.nio_uring_register_probe(ring.address(), p.address(), nr);
    }

    public static native int nio_uring_register_personality(long var0);

    public static int io_uring_register_personality(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_register_personality(ring.address());
    }

    public static native int nio_uring_unregister_personality(long var0, int var2);

    public static int io_uring_unregister_personality(@NativeType(value="struct io_uring *") IOURing ring, int id) {
        return LibURing.nio_uring_unregister_personality(ring.address(), id);
    }

    public static native int nio_uring_register_restrictions(long var0, long var2, int var4);

    public static int io_uring_register_restrictions(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_restriction *") IOURingRestriction.Buffer res) {
        return LibURing.nio_uring_register_restrictions(ring.address(), res.address(), res.remaining());
    }

    public static native int nio_uring_enable_rings(long var0);

    public static int io_uring_enable_rings(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_enable_rings(ring.address());
    }

    public static native int n__io_uring_sqring_wait(long var0);

    public static int __io_uring_sqring_wait(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.n__io_uring_sqring_wait(ring.address());
    }

    public static native int nio_uring_register_iowq_aff(long var0, long var2, long var4);

    public static int io_uring_register_iowq_aff(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="size_t") long cpusz, @NativeType(value="cpu_set_t const *") long mask) {
        if (Checks.CHECKS) {
            Checks.check(mask);
        }
        return LibURing.nio_uring_register_iowq_aff(ring.address(), cpusz, mask);
    }

    public static native int nio_uring_unregister_iowq_aff(long var0);

    public static int io_uring_unregister_iowq_aff(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_unregister_iowq_aff(ring.address());
    }

    public static native int nio_uring_register_iowq_max_workers(long var0, long var2);

    public static int io_uring_register_iowq_max_workers(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="unsigned int *") IntBuffer values) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)values, 2);
        }
        return LibURing.nio_uring_register_iowq_max_workers(ring.address(), MemoryUtil.memAddress(values));
    }

    public static native int nio_uring_register_ring_fd(long var0);

    public static int io_uring_register_ring_fd(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_register_ring_fd(ring.address());
    }

    public static native int nio_uring_unregister_ring_fd(long var0);

    public static int io_uring_unregister_ring_fd(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_unregister_ring_fd(ring.address());
    }

    public static native int nio_uring_close_ring_fd(long var0);

    public static int io_uring_close_ring_fd(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_close_ring_fd(ring.address());
    }

    public static native int nio_uring_register_buf_ring(long var0, long var2, int var4);

    public static int io_uring_register_buf_ring(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_buf_reg *") IOURingBufReg reg, @NativeType(value="unsigned int") int flags) {
        return LibURing.nio_uring_register_buf_ring(ring.address(), reg.address(), flags);
    }

    public static native int nio_uring_unregister_buf_ring(long var0, int var2);

    public static int io_uring_unregister_buf_ring(@NativeType(value="struct io_uring *") IOURing ring, int bgid) {
        return LibURing.nio_uring_unregister_buf_ring(ring.address(), bgid);
    }

    public static native int nio_uring_buf_ring_head(long var0, int var2, long var3);

    public static int io_uring_buf_ring_head(@NativeType(value="struct io_uring *") IOURing ring, int buf_group, @NativeType(value="unsigned short *") ShortBuffer head) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)head, 1);
        }
        return LibURing.nio_uring_buf_ring_head(ring.address(), buf_group, MemoryUtil.memAddress(head));
    }

    public static native int nio_uring_register_sync_cancel(long var0, long var2);

    public static int io_uring_register_sync_cancel(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_sync_cancel_reg *") IOURingSyncCancelReg reg) {
        return LibURing.nio_uring_register_sync_cancel(ring.address(), reg.address());
    }

    public static native int nio_uring_register_file_alloc_range(long var0, int var2, int var3);

    public static int io_uring_register_file_alloc_range(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="unsigned") int off, @NativeType(value="unsigned") int len) {
        return LibURing.nio_uring_register_file_alloc_range(ring.address(), off, len);
    }

    public static native int nio_uring_register_napi(long var0, long var2);

    public static int io_uring_register_napi(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_napi *") IOURingNAPI napi) {
        return LibURing.nio_uring_register_napi(ring.address(), napi.address());
    }

    public static native int nio_uring_unregister_napi(long var0, long var2);

    public static int io_uring_unregister_napi(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_napi *") IOURingNAPI napi) {
        return LibURing.nio_uring_unregister_napi(ring.address(), napi.address());
    }

    public static native int nio_uring_get_events(long var0);

    public static int io_uring_get_events(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_get_events(ring.address());
    }

    public static native int nio_uring_submit_and_get_events(long var0);

    public static int io_uring_submit_and_get_events(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_submit_and_get_events(ring.address());
    }

    public static native int nio_uring_enter(int var0, int var1, int var2, int var3, long var4);

    public static int io_uring_enter(@NativeType(value="unsigned int") int fd, @NativeType(value="unsigned int") int to_submit, @NativeType(value="unsigned int") int min_complete, @NativeType(value="unsigned int") int flags, @NativeType(value="sigset_t *") long sig) {
        if (Checks.CHECKS) {
            Checks.check(sig);
        }
        return LibURing.nio_uring_enter(fd, to_submit, min_complete, flags, sig);
    }

    public static native int nio_uring_enter2(int var0, int var1, int var2, int var3, long var4, long var6);

    public static int io_uring_enter2(@NativeType(value="unsigned int") int fd, @NativeType(value="unsigned int") int to_submit, @NativeType(value="unsigned int") int min_complete, @NativeType(value="unsigned int") int flags, @NativeType(value="sigset_t *") long sig, @NativeType(value="size_t") long sz) {
        if (Checks.CHECKS) {
            Checks.check(sig);
        }
        return LibURing.nio_uring_enter2(fd, to_submit, min_complete, flags, sig, sz);
    }

    public static native int nio_uring_setup(int var0, long var1);

    public static int io_uring_setup(@NativeType(value="unsigned int") int entries, @NativeType(value="struct io_uring_params *") IOURingParams p) {
        return LibURing.nio_uring_setup(entries, p.address());
    }

    public static native int io_uring_register(@NativeType(value="unsigned int") int var0, @NativeType(value="unsigned int") int var1, @NativeType(value="void *") long var2, @NativeType(value="unsigned int") int var4);

    public static native long nio_uring_setup_buf_ring(long var0, int var2, int var3, int var4, long var5);

    @Nullable
    @NativeType(value="struct io_uring_buf_ring *")
    public static IOURingBufRing io_uring_setup_buf_ring(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="unsigned int") int nentries, int bgid, @NativeType(value="unsigned int") int flags, @NativeType(value="int *") IntBuffer ret) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)ret, 1);
        }
        long __result = LibURing.nio_uring_setup_buf_ring(ring.address(), nentries, bgid, flags, MemoryUtil.memAddress(ret));
        return IOURingBufRing.createSafe(__result);
    }

    public static native int nio_uring_free_buf_ring(long var0, long var2, int var4, int var5);

    public static int io_uring_free_buf_ring(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_buf_ring *") IOURingBufRing br, @NativeType(value="unsigned int") int nentries, int bgid) {
        return LibURing.nio_uring_free_buf_ring(ring.address(), br.address(), nentries, bgid);
    }

    public static native void nio_uring_cqe_seen(long var0, long var2);

    public static void io_uring_cqe_seen(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_cqe *") IOURingCQE cqe) {
        LibURing.nio_uring_cqe_seen(ring.address(), cqe.address());
    }

    public static native void nio_uring_sqe_set_data(long var0, long var2);

    public static void io_uring_sqe_set_data(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="void *") long data) {
        if (Checks.CHECKS) {
            Checks.check(data);
        }
        LibURing.nio_uring_sqe_set_data(sqe.address(), data);
    }

    public static native long nio_uring_cqe_get_data(long var0);

    @NativeType(value="void *")
    public static long io_uring_cqe_get_data(@NativeType(value="struct io_uring_cqe const *") IOURingCQE cqe) {
        return LibURing.nio_uring_cqe_get_data(cqe.address());
    }

    public static native void nio_uring_sqe_set_data64(long var0, long var2);

    public static void io_uring_sqe_set_data64(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="__u64") long data) {
        LibURing.nio_uring_sqe_set_data64(sqe.address(), data);
    }

    public static native long nio_uring_cqe_get_data64(long var0);

    @NativeType(value="__u64")
    public static long io_uring_cqe_get_data64(@NativeType(value="struct io_uring_cqe const *") IOURingCQE cqe) {
        return LibURing.nio_uring_cqe_get_data64(cqe.address());
    }

    public static native void nio_uring_sqe_set_flags(long var0, int var2);

    public static void io_uring_sqe_set_flags(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_sqe_set_flags(sqe.address(), flags);
    }

    public static native void nio_uring_prep_splice(long var0, int var2, long var3, int var5, long var6, int var8, int var9);

    public static void io_uring_prep_splice(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd_in, @NativeType(value="int64_t") long off_in, int fd_out, @NativeType(value="int64_t") long off_out, @NativeType(value="unsigned int") int nbytes, @NativeType(value="unsigned int") int splice_flags) {
        LibURing.nio_uring_prep_splice(sqe.address(), fd_in, off_in, fd_out, off_out, nbytes, splice_flags);
    }

    public static native void nio_uring_prep_tee(long var0, int var2, int var3, int var4, int var5);

    public static void io_uring_prep_tee(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd_in, int fd_out, @NativeType(value="unsigned int") int nbytes, @NativeType(value="unsigned int") int splice_flags) {
        LibURing.nio_uring_prep_tee(sqe.address(), fd_in, fd_out, nbytes, splice_flags);
    }

    public static native void nio_uring_prep_readv(long var0, int var2, long var3, int var5, int var6);

    public static void io_uring_prep_readv(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="struct iovec const *") IOVec.Buffer iovecs, int offset) {
        LibURing.nio_uring_prep_readv(sqe.address(), fd, iovecs.address(), iovecs.remaining(), offset);
    }

    public static native void nio_uring_prep_readv2(long var0, int var2, long var3, int var5, int var6, int var7);

    public static void io_uring_prep_readv2(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="struct iovec const *") IOVec.Buffer iovecs, int offset, int flags) {
        LibURing.nio_uring_prep_readv2(sqe.address(), fd, iovecs.address(), iovecs.remaining(), offset, flags);
    }

    public static native void nio_uring_prep_read_fixed(long var0, int var2, long var3, int var5, int var6, int var7);

    public static void io_uring_prep_read_fixed(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="void *") ByteBuffer buf, int offset, int buf_index) {
        LibURing.nio_uring_prep_read_fixed(sqe.address(), fd, MemoryUtil.memAddress(buf), buf.remaining(), offset, buf_index);
    }

    public static native void nio_uring_prep_writev(long var0, int var2, long var3, int var5, int var6);

    public static void io_uring_prep_writev(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="struct iovec const *") IOVec.Buffer iovecs, int offset) {
        LibURing.nio_uring_prep_writev(sqe.address(), fd, iovecs.address(), iovecs.remaining(), offset);
    }

    public static native void nio_uring_prep_writev2(long var0, int var2, long var3, int var5, int var6, int var7);

    public static void io_uring_prep_writev2(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="struct iovec const *") IOVec.Buffer iovecs, int offset, int flags) {
        LibURing.nio_uring_prep_writev2(sqe.address(), fd, iovecs.address(), iovecs.remaining(), offset, flags);
    }

    public static native void nio_uring_prep_write_fixed(long var0, int var2, long var3, int var5, int var6, int var7);

    public static void io_uring_prep_write_fixed(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="void const *") ByteBuffer buf, int offset, int buf_index) {
        LibURing.nio_uring_prep_write_fixed(sqe.address(), fd, MemoryUtil.memAddress(buf), buf.remaining(), offset, buf_index);
    }

    public static native void nio_uring_prep_recvmsg(long var0, int var2, long var3, int var5);

    public static void io_uring_prep_recvmsg(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="struct msghdr *") Msghdr msg, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_recvmsg(sqe.address(), fd, msg.address(), flags);
    }

    public static native void nio_uring_prep_recvmsg_multishot(long var0, int var2, long var3, int var5);

    public static void io_uring_prep_recvmsg_multishot(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="struct msghdr *") Msghdr msg, @NativeType(value="unsigned") int flags) {
        LibURing.nio_uring_prep_recvmsg_multishot(sqe.address(), fd, msg.address(), flags);
    }

    public static native void nio_uring_prep_sendmsg(long var0, int var2, long var3, int var5);

    public static void io_uring_prep_sendmsg(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="struct msghdr const *") Msghdr msg, @NativeType(value="unsigned int") int flags) {
        if (Checks.CHECKS) {
            Msghdr.validate(msg.address());
        }
        LibURing.nio_uring_prep_sendmsg(sqe.address(), fd, msg.address(), flags);
    }

    public static native void nio_uring_prep_poll_add(long var0, int var2, int var3);

    public static void io_uring_prep_poll_add(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="unsigned int") int poll_mask) {
        LibURing.nio_uring_prep_poll_add(sqe.address(), fd, poll_mask);
    }

    public static native void nio_uring_prep_poll_multishot(long var0, int var2, int var3);

    public static void io_uring_prep_poll_multishot(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="unsigned int") int poll_mask) {
        LibURing.nio_uring_prep_poll_multishot(sqe.address(), fd, poll_mask);
    }

    public static native void nio_uring_prep_poll_remove(long var0, long var2);

    public static void io_uring_prep_poll_remove(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="__u64") long user_data) {
        LibURing.nio_uring_prep_poll_remove(sqe.address(), user_data);
    }

    public static native void nio_uring_prep_poll_update(long var0, long var2, long var4, int var6, int var7);

    public static void io_uring_prep_poll_update(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="__u64") long old_user_data, @NativeType(value="__u64") long new_user_data, @NativeType(value="unsigned int") int poll_mask, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_poll_update(sqe.address(), old_user_data, new_user_data, poll_mask, flags);
    }

    public static native void nio_uring_prep_fsync(long var0, int var2, int var3);

    public static void io_uring_prep_fsync(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="unsigned int") int fsync_flags) {
        LibURing.nio_uring_prep_fsync(sqe.address(), fd, fsync_flags);
    }

    public static native void nio_uring_prep_nop(long var0);

    public static void io_uring_prep_nop(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe) {
        LibURing.nio_uring_prep_nop(sqe.address());
    }

    public static native void nio_uring_prep_timeout(long var0, long var2, int var4, int var5);

    public static void io_uring_prep_timeout(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="struct __kernel_timespec *") KernelTimespec ts, @NativeType(value="unsigned int") int count, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_timeout(sqe.address(), ts.address(), count, flags);
    }

    public static native void nio_uring_prep_timeout_remove(long var0, long var2, int var4);

    public static void io_uring_prep_timeout_remove(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="__u64") long user_data, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_timeout_remove(sqe.address(), user_data, flags);
    }

    public static native void nio_uring_prep_timeout_update(long var0, long var2, long var4, int var6);

    public static void io_uring_prep_timeout_update(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="struct __kernel_timespec *") KernelTimespec ts, @NativeType(value="__u64") long user_data, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_timeout_update(sqe.address(), ts.address(), user_data, flags);
    }

    public static native void nio_uring_prep_accept(long var0, int var2, long var3, long var5, int var7);

    public static void io_uring_prep_accept(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="struct sockaddr *") Sockaddr addr, @NativeType(value="socklen_t *") IntBuffer addrlen, int flags) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)addrlen, 1);
        }
        LibURing.nio_uring_prep_accept(sqe.address(), fd, addr.address(), MemoryUtil.memAddress(addrlen), flags);
    }

    public static native void nio_uring_prep_accept_direct(long var0, int var2, long var3, long var5, int var7, int var8);

    public static void io_uring_prep_accept_direct(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="struct sockaddr *") Sockaddr addr, @NativeType(value="socklen_t *") IntBuffer addrlen, int flags, @NativeType(value="unsigned int") int file_index) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)addrlen, 1);
        }
        LibURing.nio_uring_prep_accept_direct(sqe.address(), fd, addr.address(), MemoryUtil.memAddress(addrlen), flags, file_index);
    }

    public static native void nio_uring_prep_multishot_accept(long var0, int var2, long var3, long var5, int var7);

    public static void io_uring_prep_multishot_accept(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="struct sockaddr *") Sockaddr addr, @NativeType(value="socklen_t *") IntBuffer addrlen, int flags) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)addrlen, 1);
        }
        LibURing.nio_uring_prep_multishot_accept(sqe.address(), fd, addr.address(), MemoryUtil.memAddress(addrlen), flags);
    }

    public static native void nio_uring_prep_multishot_accept_direct(long var0, int var2, long var3, long var5, int var7);

    public static void io_uring_prep_multishot_accept_direct(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="struct sockaddr *") Sockaddr addr, @NativeType(value="socklen_t *") IntBuffer addrlen, int flags) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)addrlen, 1);
        }
        LibURing.nio_uring_prep_multishot_accept_direct(sqe.address(), fd, addr.address(), MemoryUtil.memAddress(addrlen), flags);
    }

    public static native void nio_uring_prep_cancel64(long var0, long var2, int var4);

    public static void io_uring_prep_cancel64(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="__u64") long user_data, int flags) {
        LibURing.nio_uring_prep_cancel64(sqe.address(), user_data, flags);
    }

    public static native void nio_uring_prep_cancel(long var0, long var2, int var4);

    public static void io_uring_prep_cancel(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="void *") long user_data, int flags) {
        if (Checks.CHECKS) {
            Checks.check(user_data);
        }
        LibURing.nio_uring_prep_cancel(sqe.address(), user_data, flags);
    }

    public static native void nio_uring_prep_cancel_fd(long var0, int var2, int var3);

    public static void io_uring_prep_cancel_fd(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_cancel_fd(sqe.address(), fd, flags);
    }

    public static native void nio_uring_prep_link_timeout(long var0, long var2, int var4);

    public static void io_uring_prep_link_timeout(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="struct __kernel_timespec *") KernelTimespec ts, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_link_timeout(sqe.address(), ts.address(), flags);
    }

    public static native void nio_uring_prep_connect(long var0, int var2, long var3, int var5);

    public static void io_uring_prep_connect(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="struct sockaddr const *") Sockaddr addr, @NativeType(value="socklen_t") int addrlen) {
        LibURing.nio_uring_prep_connect(sqe.address(), fd, addr.address(), addrlen);
    }

    public static native void nio_uring_prep_files_update(long var0, long var2, int var4, int var5);

    public static void io_uring_prep_files_update(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="int *") IntBuffer fds, int offset) {
        LibURing.nio_uring_prep_files_update(sqe.address(), MemoryUtil.memAddress(fds), fds.remaining(), offset);
    }

    public static native void nio_uring_prep_fallocate(long var0, int var2, int var3, long var4, long var6);

    public static void io_uring_prep_fallocate(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, int mode, @NativeType(value="off_t") long offset, @NativeType(value="off_t") long len) {
        LibURing.nio_uring_prep_fallocate(sqe.address(), fd, mode, offset, len);
    }

    public static native void nio_uring_prep_openat(long var0, int var2, long var3, int var5, int var6);

    public static void io_uring_prep_openat(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType(value="char const *") ByteBuffer path, int flags, int mode) {
        if (Checks.CHECKS) {
            Checks.checkNT1(path);
        }
        LibURing.nio_uring_prep_openat(sqe.address(), dfd, MemoryUtil.memAddress(path), flags, mode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_openat(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType(value="char const *") CharSequence path, int flags, int mode) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_openat(sqe.address(), dfd, pathEncoded, flags, mode);
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_openat_direct(long var0, int var2, long var3, int var5, int var6, int var7);

    public static void io_uring_prep_openat_direct(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType(value="char const *") ByteBuffer path, int flags, int mode, @NativeType(value="unsigned int") int file_index) {
        if (Checks.CHECKS) {
            Checks.checkNT1(path);
        }
        LibURing.nio_uring_prep_openat_direct(sqe.address(), dfd, MemoryUtil.memAddress(path), flags, mode, file_index);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_openat_direct(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType(value="char const *") CharSequence path, int flags, int mode, @NativeType(value="unsigned int") int file_index) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_openat_direct(sqe.address(), dfd, pathEncoded, flags, mode, file_index);
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_close(long var0, int var2);

    public static void io_uring_prep_close(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd) {
        LibURing.nio_uring_prep_close(sqe.address(), fd);
    }

    public static native void nio_uring_prep_close_direct(long var0, int var2);

    public static void io_uring_prep_close_direct(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="unsigned int") int file_index) {
        LibURing.nio_uring_prep_close_direct(sqe.address(), file_index);
    }

    public static native void nio_uring_prep_read(long var0, int var2, long var3, int var5, int var6);

    public static void io_uring_prep_read(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="void *") ByteBuffer buf, int offset) {
        LibURing.nio_uring_prep_read(sqe.address(), fd, MemoryUtil.memAddress(buf), buf.remaining(), offset);
    }

    public static native void nio_uring_prep_read_multishot(long var0, int var2, int var3, long var4, int var6);

    public static void io_uring_prep_read_multishot(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="unsigned int") int nbytes, @NativeType(value="__u64") long offset, int buf_group) {
        LibURing.nio_uring_prep_read_multishot(sqe.address(), fd, nbytes, offset, buf_group);
    }

    public static native void nio_uring_prep_write(long var0, int var2, long var3, int var5, int var6);

    public static void io_uring_prep_write(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="void const *") ByteBuffer buf, int offset) {
        LibURing.nio_uring_prep_write(sqe.address(), fd, MemoryUtil.memAddress(buf), buf.remaining(), offset);
    }

    public static native void nio_uring_prep_statx(long var0, int var2, long var3, int var5, int var6, long var7);

    public static void io_uring_prep_statx(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType(value="char const *") ByteBuffer path, int flags, @NativeType(value="unsigned int") int mask, @NativeType(value="struct statx *") Statx statxbuf) {
        if (Checks.CHECKS) {
            Checks.checkNT1(path);
        }
        LibURing.nio_uring_prep_statx(sqe.address(), dfd, MemoryUtil.memAddress(path), flags, mask, statxbuf.address());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_statx(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType(value="char const *") CharSequence path, int flags, @NativeType(value="unsigned int") int mask, @NativeType(value="struct statx *") Statx statxbuf) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_statx(sqe.address(), dfd, pathEncoded, flags, mask, statxbuf.address());
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_fadvise(long var0, int var2, int var3, long var4, int var6);

    public static void io_uring_prep_fadvise(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, int offset, @NativeType(value="off_t") long len, int advice) {
        LibURing.nio_uring_prep_fadvise(sqe.address(), fd, offset, len, advice);
    }

    public static native void nio_uring_prep_madvise(long var0, long var2, long var4, int var6);

    public static void io_uring_prep_madvise(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="void *") ByteBuffer addr, int advice) {
        LibURing.nio_uring_prep_madvise(sqe.address(), MemoryUtil.memAddress(addr), addr.remaining(), advice);
    }

    public static native void nio_uring_prep_send(long var0, int var2, long var3, long var5, int var7);

    public static void io_uring_prep_send(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int sockfd, @NativeType(value="void const *") ByteBuffer buf, int flags) {
        LibURing.nio_uring_prep_send(sqe.address(), sockfd, MemoryUtil.memAddress(buf), buf.remaining(), flags);
    }

    public static native void nio_uring_prep_send_set_addr(long var0, long var2, short var4);

    public static void io_uring_prep_send_set_addr(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="struct sockaddr const *") Sockaddr dest_addr, @NativeType(value="__u16") short addr_len) {
        LibURing.nio_uring_prep_send_set_addr(sqe.address(), dest_addr.address(), addr_len);
    }

    public static native void nio_uring_prep_sendto(long var0, int var2, long var3, long var5, int var7, long var8, int var10);

    public static void io_uring_prep_sendto(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int sockfd, @NativeType(value="void const *") ByteBuffer buf, int flags, @NativeType(value="struct sockaddr const *") Sockaddr addr, @NativeType(value="socklen_t") int addrlen) {
        LibURing.nio_uring_prep_sendto(sqe.address(), sockfd, MemoryUtil.memAddress(buf), buf.remaining(), flags, addr.address(), addrlen);
    }

    public static native void nio_uring_prep_send_zc(long var0, int var2, long var3, long var5, int var7, int var8);

    public static void io_uring_prep_send_zc(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int sockfd, @NativeType(value="void const *") ByteBuffer buf, int flags, @NativeType(value="unsigned") int zc_flags) {
        LibURing.nio_uring_prep_send_zc(sqe.address(), sockfd, MemoryUtil.memAddress(buf), buf.remaining(), flags, zc_flags);
    }

    public static native void nio_uring_prep_send_zc_fixed(long var0, int var2, long var3, long var5, int var7, int var8, int var9);

    public static void io_uring_prep_send_zc_fixed(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int sockfd, @NativeType(value="void const *") ByteBuffer buf, int flags, @NativeType(value="unsigned") int zc_flags, @NativeType(value="unsigned") int buf_index) {
        LibURing.nio_uring_prep_send_zc_fixed(sqe.address(), sockfd, MemoryUtil.memAddress(buf), buf.remaining(), flags, zc_flags, buf_index);
    }

    public static native void nio_uring_prep_sendmsg_zc(long var0, int var2, long var3, int var5);

    public static void io_uring_prep_sendmsg_zc(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="struct msghdr const *") Msghdr msg, @NativeType(value="unsigned") int flags) {
        if (Checks.CHECKS) {
            Msghdr.validate(msg.address());
        }
        LibURing.nio_uring_prep_sendmsg_zc(sqe.address(), fd, msg.address(), flags);
    }

    public static native void nio_uring_prep_recv(long var0, int var2, long var3, long var5, int var7);

    public static void io_uring_prep_recv(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int sockfd, @NativeType(value="void *") ByteBuffer buf, int flags) {
        LibURing.nio_uring_prep_recv(sqe.address(), sockfd, MemoryUtil.memAddress(buf), buf.remaining(), flags);
    }

    public static native void nio_uring_prep_recv_multishot(long var0, int var2, long var3, long var5, int var7);

    public static void io_uring_prep_recv_multishot(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int sockfd, @NativeType(value="void *") ByteBuffer buf, int flags) {
        LibURing.nio_uring_prep_recv_multishot(sqe.address(), sockfd, MemoryUtil.memAddress(buf), buf.remaining(), flags);
    }

    public static native long nio_uring_recvmsg_validate(long var0, int var2, long var3);

    @Nullable
    @NativeType(value="struct io_uring_recvmsg_out *")
    public static IOURingRecvmsgOut io_uring_recvmsg_validate(@NativeType(value="void *") ByteBuffer buf, @NativeType(value="struct msghdr *") Msghdr msgh) {
        long __result = LibURing.nio_uring_recvmsg_validate(MemoryUtil.memAddress(buf), buf.remaining(), msgh.address());
        return IOURingRecvmsgOut.createSafe(__result);
    }

    public static native long nio_uring_recvmsg_name(long var0);

    @NativeType(value="void *")
    public static long io_uring_recvmsg_name(@NativeType(value="struct io_uring_recvmsg_out *") IOURingRecvmsgOut o) {
        return LibURing.nio_uring_recvmsg_name(o.address());
    }

    public static native long nio_uring_recvmsg_cmsg_firsthdr(long var0, long var2);

    @Nullable
    @NativeType(value="struct cmsghdr *")
    public static CMsghdr io_uring_recvmsg_cmsg_firsthdr(@NativeType(value="struct io_uring_recvmsg_out *") IOURingRecvmsgOut o, @NativeType(value="struct msghdr *") Msghdr msgh) {
        long __result = LibURing.nio_uring_recvmsg_cmsg_firsthdr(o.address(), msgh.address());
        return CMsghdr.createSafe(__result);
    }

    public static native long nio_uring_recvmsg_cmsg_nexthdr(long var0, long var2, long var4);

    @Nullable
    @NativeType(value="struct cmsghdr *")
    public static CMsghdr io_uring_recvmsg_cmsg_nexthdr(@NativeType(value="struct io_uring_recvmsg_out *") IOURingRecvmsgOut o, @NativeType(value="struct msghdr *") Msghdr msgh, @NativeType(value="struct cmsghdr *") CMsghdr cmsg) {
        long __result = LibURing.nio_uring_recvmsg_cmsg_nexthdr(o.address(), msgh.address(), cmsg.address());
        return CMsghdr.createSafe(__result);
    }

    public static native long nio_uring_recvmsg_payload(long var0, long var2);

    @NativeType(value="void *")
    public static long io_uring_recvmsg_payload(@NativeType(value="struct io_uring_recvmsg_out *") IOURingRecvmsgOut o, @NativeType(value="struct msghdr *") Msghdr msgh) {
        return LibURing.nio_uring_recvmsg_payload(o.address(), msgh.address());
    }

    public static native int nio_uring_recvmsg_payload_length(long var0, int var2, long var3);

    @NativeType(value="unsigned int")
    public static int io_uring_recvmsg_payload_length(@NativeType(value="struct io_uring_recvmsg_out *") IOURingRecvmsgOut o, int buf_len, @NativeType(value="struct msghdr *") Msghdr msgh) {
        return LibURing.nio_uring_recvmsg_payload_length(o.address(), buf_len, msgh.address());
    }

    public static native void nio_uring_prep_openat2(long var0, int var2, long var3, long var5);

    public static void io_uring_prep_openat2(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType(value="char const *") ByteBuffer path, @NativeType(value="struct open_how *") OpenHow how) {
        if (Checks.CHECKS) {
            Checks.checkNT1(path);
        }
        LibURing.nio_uring_prep_openat2(sqe.address(), dfd, MemoryUtil.memAddress(path), how.address());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_openat2(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType(value="char const *") CharSequence path, @NativeType(value="struct open_how *") OpenHow how) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_openat2(sqe.address(), dfd, pathEncoded, how.address());
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_openat2_direct(long var0, int var2, long var3, long var5, int var7);

    public static void io_uring_prep_openat2_direct(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType(value="char const *") ByteBuffer path, @NativeType(value="struct open_how *") OpenHow how, @NativeType(value="unsigned int") int file_index) {
        if (Checks.CHECKS) {
            Checks.checkNT1(path);
        }
        LibURing.nio_uring_prep_openat2_direct(sqe.address(), dfd, MemoryUtil.memAddress(path), how.address(), file_index);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_openat2_direct(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType(value="char const *") CharSequence path, @NativeType(value="struct open_how *") OpenHow how, @NativeType(value="unsigned int") int file_index) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_openat2_direct(sqe.address(), dfd, pathEncoded, how.address(), file_index);
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_epoll_ctl(long var0, int var2, int var3, int var4, long var5);

    public static void io_uring_prep_epoll_ctl(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int epfd, int fd, int op, @NativeType(value="struct epoll_event *") EpollEvent ev) {
        LibURing.nio_uring_prep_epoll_ctl(sqe.address(), epfd, fd, op, ev.address());
    }

    public static native void nio_uring_prep_provide_buffers(long var0, long var2, int var4, int var5, int var6, int var7);

    public static void io_uring_prep_provide_buffers(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="void *") ByteBuffer addr, int nr, int bgid, int bid) {
        LibURing.nio_uring_prep_provide_buffers(sqe.address(), MemoryUtil.memAddress(addr), addr.remaining(), nr, bgid, bid);
    }

    public static native void nio_uring_prep_remove_buffers(long var0, int var2, int var3);

    public static void io_uring_prep_remove_buffers(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int nr, int bgid) {
        LibURing.nio_uring_prep_remove_buffers(sqe.address(), nr, bgid);
    }

    public static native void nio_uring_prep_shutdown(long var0, int var2, int var3);

    public static void io_uring_prep_shutdown(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, int how) {
        LibURing.nio_uring_prep_shutdown(sqe.address(), fd, how);
    }

    public static native void nio_uring_prep_unlinkat(long var0, int var2, long var3, int var5);

    public static void io_uring_prep_unlinkat(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType(value="char const *") ByteBuffer path, int flags) {
        if (Checks.CHECKS) {
            Checks.checkNT1(path);
        }
        LibURing.nio_uring_prep_unlinkat(sqe.address(), dfd, MemoryUtil.memAddress(path), flags);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_unlinkat(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType(value="char const *") CharSequence path, int flags) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_unlinkat(sqe.address(), dfd, pathEncoded, flags);
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_unlink(long var0, long var2, int var4);

    public static void io_uring_prep_unlink(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") ByteBuffer path, int flags) {
        if (Checks.CHECKS) {
            Checks.checkNT1(path);
        }
        LibURing.nio_uring_prep_unlink(sqe.address(), MemoryUtil.memAddress(path), flags);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_unlink(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") CharSequence path, int flags) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_unlink(sqe.address(), pathEncoded, flags);
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_renameat(long var0, int var2, long var3, int var5, long var6, int var8);

    public static void io_uring_prep_renameat(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int olddfd, @NativeType(value="char const *") ByteBuffer oldpath, int newdfd, @NativeType(value="char const *") ByteBuffer newpath, @NativeType(value="unsigned int") int flags) {
        if (Checks.CHECKS) {
            Checks.checkNT1(oldpath);
            Checks.checkNT1(newpath);
        }
        LibURing.nio_uring_prep_renameat(sqe.address(), olddfd, MemoryUtil.memAddress(oldpath), newdfd, MemoryUtil.memAddress(newpath), flags);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_renameat(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int olddfd, @NativeType(value="char const *") CharSequence oldpath, int newdfd, @NativeType(value="char const *") CharSequence newpath, @NativeType(value="unsigned int") int flags) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(oldpath, true);
            long oldpathEncoded = stack.getPointerAddress();
            stack.nUTF8(newpath, true);
            long newpathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_renameat(sqe.address(), olddfd, oldpathEncoded, newdfd, newpathEncoded, flags);
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_rename(long var0, long var2, long var4);

    public static void io_uring_prep_rename(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") ByteBuffer oldpath, @NativeType(value="char const *") ByteBuffer newpath) {
        if (Checks.CHECKS) {
            Checks.checkNT1(oldpath);
            Checks.checkNT1(newpath);
        }
        LibURing.nio_uring_prep_rename(sqe.address(), MemoryUtil.memAddress(oldpath), MemoryUtil.memAddress(newpath));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_rename(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") CharSequence oldpath, @NativeType(value="char const *") CharSequence newpath) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(oldpath, true);
            long oldpathEncoded = stack.getPointerAddress();
            stack.nUTF8(newpath, true);
            long newpathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_rename(sqe.address(), oldpathEncoded, newpathEncoded);
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_sync_file_range(long var0, int var2, int var3, int var4, int var5);

    public static void io_uring_prep_sync_file_range(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="unsigned int") int len, int offset, int flags) {
        LibURing.nio_uring_prep_sync_file_range(sqe.address(), fd, len, offset, flags);
    }

    public static native void nio_uring_prep_mkdirat(long var0, int var2, long var3, int var5);

    public static void io_uring_prep_mkdirat(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType(value="char const *") ByteBuffer path, int mode) {
        if (Checks.CHECKS) {
            Checks.checkNT1(path);
        }
        LibURing.nio_uring_prep_mkdirat(sqe.address(), dfd, MemoryUtil.memAddress(path), mode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_mkdirat(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int dfd, @NativeType(value="char const *") CharSequence path, int mode) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_mkdirat(sqe.address(), dfd, pathEncoded, mode);
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_mkdir(long var0, long var2, int var4);

    public static void io_uring_prep_mkdir(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") ByteBuffer path, int mode) {
        if (Checks.CHECKS) {
            Checks.checkNT1(path);
        }
        LibURing.nio_uring_prep_mkdir(sqe.address(), MemoryUtil.memAddress(path), mode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_mkdir(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") CharSequence path, int mode) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_mkdir(sqe.address(), pathEncoded, mode);
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_symlinkat(long var0, long var2, int var4, long var5);

    public static void io_uring_prep_symlinkat(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") ByteBuffer target, int newdirfd, @NativeType(value="char const *") ByteBuffer linkpath) {
        if (Checks.CHECKS) {
            Checks.checkNT1(target);
            Checks.checkNT1(linkpath);
        }
        LibURing.nio_uring_prep_symlinkat(sqe.address(), MemoryUtil.memAddress(target), newdirfd, MemoryUtil.memAddress(linkpath));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_symlinkat(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") CharSequence target, int newdirfd, @NativeType(value="char const *") CharSequence linkpath) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(target, true);
            long targetEncoded = stack.getPointerAddress();
            stack.nUTF8(linkpath, true);
            long linkpathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_symlinkat(sqe.address(), targetEncoded, newdirfd, linkpathEncoded);
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_symlink(long var0, long var2, long var4);

    public static void io_uring_prep_symlink(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") ByteBuffer target, @NativeType(value="char const *") ByteBuffer linkpath) {
        if (Checks.CHECKS) {
            Checks.checkNT1(target);
            Checks.checkNT1(linkpath);
        }
        LibURing.nio_uring_prep_symlink(sqe.address(), MemoryUtil.memAddress(target), MemoryUtil.memAddress(linkpath));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_symlink(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") CharSequence target, @NativeType(value="char const *") CharSequence linkpath) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(target, true);
            long targetEncoded = stack.getPointerAddress();
            stack.nUTF8(linkpath, true);
            long linkpathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_symlink(sqe.address(), targetEncoded, linkpathEncoded);
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_linkat(long var0, int var2, long var3, int var5, long var6, int var8);

    public static void io_uring_prep_linkat(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int olddfd, @NativeType(value="char const *") ByteBuffer oldpath, int newdfd, @NativeType(value="char const *") ByteBuffer newpath, int flags) {
        if (Checks.CHECKS) {
            Checks.checkNT1(oldpath);
            Checks.checkNT1(newpath);
        }
        LibURing.nio_uring_prep_linkat(sqe.address(), olddfd, MemoryUtil.memAddress(oldpath), newdfd, MemoryUtil.memAddress(newpath), flags);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_linkat(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int olddfd, @NativeType(value="char const *") CharSequence oldpath, int newdfd, @NativeType(value="char const *") CharSequence newpath, int flags) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(oldpath, true);
            long oldpathEncoded = stack.getPointerAddress();
            stack.nUTF8(newpath, true);
            long newpathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_linkat(sqe.address(), olddfd, oldpathEncoded, newdfd, newpathEncoded, flags);
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_link(long var0, long var2, long var4, int var6);

    public static void io_uring_prep_link(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") ByteBuffer oldpath, @NativeType(value="char const *") ByteBuffer newpath, int flags) {
        if (Checks.CHECKS) {
            Checks.checkNT1(oldpath);
            Checks.checkNT1(newpath);
        }
        LibURing.nio_uring_prep_link(sqe.address(), MemoryUtil.memAddress(oldpath), MemoryUtil.memAddress(newpath), flags);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_link(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") CharSequence oldpath, @NativeType(value="char const *") CharSequence newpath, int flags) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(oldpath, true);
            long oldpathEncoded = stack.getPointerAddress();
            stack.nUTF8(newpath, true);
            long newpathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_link(sqe.address(), oldpathEncoded, newpathEncoded, flags);
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_msg_ring_cqe_flags(long var0, int var2, int var3, long var4, int var6, int var7);

    public static void io_uring_prep_msg_ring_cqe_flags(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="unsigned int") int len, @NativeType(value="__u64") long data, @NativeType(value="unsigned int") int flags, @NativeType(value="unsigned int") int cqe_flags) {
        LibURing.nio_uring_prep_msg_ring_cqe_flags(sqe.address(), fd, len, data, flags, cqe_flags);
    }

    public static native void nio_uring_prep_msg_ring(long var0, int var2, int var3, long var4, int var6);

    public static void io_uring_prep_msg_ring(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="unsigned int") int len, @NativeType(value="__u64") long data, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_msg_ring(sqe.address(), fd, len, data, flags);
    }

    public static native void nio_uring_prep_msg_ring_fd(long var0, int var2, int var3, int var4, long var5, int var7);

    public static void io_uring_prep_msg_ring_fd(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, int source_fd, int target_fd, @NativeType(value="__u64") long data, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_msg_ring_fd(sqe.address(), fd, source_fd, target_fd, data, flags);
    }

    public static native void nio_uring_prep_msg_ring_fd_alloc(long var0, int var2, int var3, long var4, int var6);

    public static void io_uring_prep_msg_ring_fd_alloc(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, int source_fd, @NativeType(value="__u64") long data, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_msg_ring_fd_alloc(sqe.address(), fd, source_fd, data, flags);
    }

    public static native void nio_uring_prep_getxattr(long var0, long var2, long var4, long var6, int var8);

    public static void io_uring_prep_getxattr(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") ByteBuffer name, @NativeType(value="char *") ByteBuffer value, @NativeType(value="char const *") ByteBuffer path) {
        if (Checks.CHECKS) {
            Checks.checkNT1(name);
            Checks.checkNT1(path);
        }
        LibURing.nio_uring_prep_getxattr(sqe.address(), MemoryUtil.memAddress(name), MemoryUtil.memAddress(value), MemoryUtil.memAddress(path), value.remaining());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_getxattr(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") CharSequence name, @NativeType(value="char *") ByteBuffer value, @NativeType(value="char const *") CharSequence path) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(name, true);
            long nameEncoded = stack.getPointerAddress();
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_getxattr(sqe.address(), nameEncoded, MemoryUtil.memAddress(value), pathEncoded, value.remaining());
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_setxattr(long var0, long var2, long var4, long var6, int var8, int var9);

    public static void io_uring_prep_setxattr(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") ByteBuffer name, @NativeType(value="char const *") ByteBuffer value, @NativeType(value="char const *") ByteBuffer path, int flags) {
        if (Checks.CHECKS) {
            Checks.checkNT1(name);
            Checks.checkNT1(path);
        }
        LibURing.nio_uring_prep_setxattr(sqe.address(), MemoryUtil.memAddress(name), MemoryUtil.memAddress(value), MemoryUtil.memAddress(path), flags, value.remaining());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_setxattr(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="char const *") CharSequence name, @NativeType(value="char const *") ByteBuffer value, @NativeType(value="char const *") CharSequence path, int flags) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(name, true);
            long nameEncoded = stack.getPointerAddress();
            stack.nUTF8(path, true);
            long pathEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_setxattr(sqe.address(), nameEncoded, MemoryUtil.memAddress(value), pathEncoded, flags, value.remaining());
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_fgetxattr(long var0, int var2, long var3, long var5, int var7);

    public static void io_uring_prep_fgetxattr(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="char const *") ByteBuffer name, @NativeType(value="char *") ByteBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkNT1(name);
        }
        LibURing.nio_uring_prep_fgetxattr(sqe.address(), fd, MemoryUtil.memAddress(name), MemoryUtil.memAddress(value), value.remaining());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_fgetxattr(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="char const *") CharSequence name, @NativeType(value="char *") ByteBuffer value) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(name, true);
            long nameEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_fgetxattr(sqe.address(), fd, nameEncoded, MemoryUtil.memAddress(value), value.remaining());
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_fsetxattr(long var0, int var2, long var3, long var5, int var7, int var8);

    public static void io_uring_prep_fsetxattr(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="char const *") ByteBuffer name, @NativeType(value="char const *") ByteBuffer value, int flags) {
        if (Checks.CHECKS) {
            Checks.checkNT1(name);
        }
        LibURing.nio_uring_prep_fsetxattr(sqe.address(), fd, MemoryUtil.memAddress(name), MemoryUtil.memAddress(value), flags, value.remaining());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void io_uring_prep_fsetxattr(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="char const *") CharSequence name, @NativeType(value="char const *") ByteBuffer value, int flags) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nUTF8(name, true);
            long nameEncoded = stack.getPointerAddress();
            LibURing.nio_uring_prep_fsetxattr(sqe.address(), fd, nameEncoded, MemoryUtil.memAddress(value), flags, value.remaining());
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native void nio_uring_prep_socket(long var0, int var2, int var3, int var4, int var5);

    public static void io_uring_prep_socket(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int domain, int type, int protocol, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_socket(sqe.address(), domain, type, protocol, flags);
    }

    public static native void nio_uring_prep_socket_direct(long var0, int var2, int var3, int var4, int var5, int var6);

    public static void io_uring_prep_socket_direct(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int domain, int type, int protocol, @NativeType(value="unsigned int") int file_index, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_socket_direct(sqe.address(), domain, type, protocol, file_index, flags);
    }

    public static native void nio_uring_prep_socket_direct_alloc(long var0, int var2, int var3, int var4, int var5);

    public static void io_uring_prep_socket_direct_alloc(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int domain, int type, int protocol, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_socket_direct_alloc(sqe.address(), domain, type, protocol, flags);
    }

    public static native void nio_uring_prep_cmd_sock(long var0, int var2, int var3, int var4, int var5, long var6, int var8);

    public static void io_uring_prep_cmd_sock(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int cmd_op, int fd, int level, int optname, @NativeType(value="void *") ByteBuffer optval) {
        LibURing.nio_uring_prep_cmd_sock(sqe.address(), cmd_op, fd, level, optname, MemoryUtil.memAddress(optval), optval.remaining());
    }

    public static native void nio_uring_prep_waitid(long var0, int var2, int var3, long var4, int var6, int var7);

    public static void io_uring_prep_waitid(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="idtype_t") int idtype, @NativeType(value="id_t") int id, @NativeType(value="siginfo_t *") long infop, int options, @NativeType(value="unsigned int") int flags) {
        if (Checks.CHECKS) {
            Checks.check(infop);
        }
        LibURing.nio_uring_prep_waitid(sqe.address(), idtype, id, infop, options, flags);
    }

    public static native void nio_uring_prep_futex_wake(long var0, long var2, long var4, long var6, int var8, int var9);

    public static void io_uring_prep_futex_wake(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="uint32_t *") IntBuffer futex, @NativeType(value="uint64_t") long val, @NativeType(value="uint64_t") long mask, @NativeType(value="uint32_t") int futex_flags, @NativeType(value="unsigned int") int flags) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)futex, 1);
        }
        LibURing.nio_uring_prep_futex_wake(sqe.address(), MemoryUtil.memAddress(futex), val, mask, futex_flags, flags);
    }

    public static native void nio_uring_prep_futex_wait(long var0, long var2, long var4, long var6, int var8, int var9);

    public static void io_uring_prep_futex_wait(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="uint32_t *") IntBuffer futex, @NativeType(value="uint64_t") long val, @NativeType(value="uint64_t") long mask, @NativeType(value="uint32_t") int futex_flags, @NativeType(value="unsigned int") int flags) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)futex, 1);
        }
        LibURing.nio_uring_prep_futex_wait(sqe.address(), MemoryUtil.memAddress(futex), val, mask, futex_flags, flags);
    }

    public static native void nio_uring_prep_futex_waitv(long var0, long var2, int var4, int var5);

    public static void io_uring_prep_futex_waitv(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, @NativeType(value="struct futex_waitv *") PointerBuffer futex, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_futex_waitv(sqe.address(), MemoryUtil.memAddress(futex), futex.remaining(), flags);
    }

    public static native void nio_uring_prep_fixed_fd_install(long var0, int var2, int var3);

    public static void io_uring_prep_fixed_fd_install(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="unsigned int") int flags) {
        LibURing.nio_uring_prep_fixed_fd_install(sqe.address(), fd, flags);
    }

    public static native void nio_uring_prep_ftruncate(long var0, int var2, long var3);

    public static void io_uring_prep_ftruncate(@NativeType(value="struct io_uring_sqe *") IOURingSQE sqe, int fd, @NativeType(value="loff_t") long len) {
        LibURing.nio_uring_prep_ftruncate(sqe.address(), fd, len);
    }

    public static native int nio_uring_sq_ready(long var0);

    @NativeType(value="unsigned int")
    public static int io_uring_sq_ready(@NativeType(value="struct io_uring const *") IOURing ring) {
        if (Checks.CHECKS) {
            IOURing.validate(ring.address());
        }
        return LibURing.nio_uring_sq_ready(ring.address());
    }

    public static native int nio_uring_sq_space_left(long var0);

    @NativeType(value="unsigned int")
    public static int io_uring_sq_space_left(@NativeType(value="struct io_uring const *") IOURing ring) {
        if (Checks.CHECKS) {
            IOURing.validate(ring.address());
        }
        return LibURing.nio_uring_sq_space_left(ring.address());
    }

    public static native int nio_uring_sqring_wait(long var0);

    public static int io_uring_sqring_wait(@NativeType(value="struct io_uring *") IOURing ring) {
        return LibURing.nio_uring_sqring_wait(ring.address());
    }

    public static native int nio_uring_cq_ready(long var0);

    @NativeType(value="unsigned int")
    public static int io_uring_cq_ready(@NativeType(value="struct io_uring const *") IOURing ring) {
        if (Checks.CHECKS) {
            IOURing.validate(ring.address());
        }
        return LibURing.nio_uring_cq_ready(ring.address());
    }

    public static native boolean nio_uring_cq_has_overflow(long var0);

    @NativeType(value="bool")
    public static boolean io_uring_cq_has_overflow(@NativeType(value="struct io_uring const *") IOURing ring) {
        if (Checks.CHECKS) {
            IOURing.validate(ring.address());
        }
        return LibURing.nio_uring_cq_has_overflow(ring.address());
    }

    public static native boolean nio_uring_cq_eventfd_enabled(long var0);

    @NativeType(value="bool")
    public static boolean io_uring_cq_eventfd_enabled(@NativeType(value="struct io_uring const *") IOURing ring) {
        if (Checks.CHECKS) {
            IOURing.validate(ring.address());
        }
        return LibURing.nio_uring_cq_eventfd_enabled(ring.address());
    }

    public static native int nio_uring_cq_eventfd_toggle(long var0, boolean var2);

    public static int io_uring_cq_eventfd_toggle(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="bool") boolean enabled) {
        return LibURing.nio_uring_cq_eventfd_toggle(ring.address(), enabled);
    }

    public static native int nio_uring_wait_cqe_nr(long var0, long var2, int var4);

    public static int io_uring_wait_cqe_nr(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_cqe **") PointerBuffer cqe_ptr) {
        return LibURing.nio_uring_wait_cqe_nr(ring.address(), MemoryUtil.memAddress(cqe_ptr), cqe_ptr.remaining());
    }

    public static native int nio_uring_peek_cqe(long var0, long var2);

    public static int io_uring_peek_cqe(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_cqe **") PointerBuffer cqe_ptr) {
        if (Checks.CHECKS) {
            Checks.check(cqe_ptr, 1);
        }
        return LibURing.nio_uring_peek_cqe(ring.address(), MemoryUtil.memAddress(cqe_ptr));
    }

    public static native int nio_uring_wait_cqe(long var0, long var2);

    public static int io_uring_wait_cqe(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_cqe **") PointerBuffer cqe_ptr) {
        if (Checks.CHECKS) {
            Checks.check(cqe_ptr, 1);
        }
        return LibURing.nio_uring_wait_cqe(ring.address(), MemoryUtil.memAddress(cqe_ptr));
    }

    public static native void nio_uring_buf_ring_advance(long var0, int var2);

    public static void io_uring_buf_ring_advance(@NativeType(value="struct io_uring_buf_ring *") IOURingBufRing br, int count) {
        LibURing.nio_uring_buf_ring_advance(br.address(), count);
    }

    public static native void nio_uring_buf_ring_cq_advance(long var0, long var2, int var4);

    public static void io_uring_buf_ring_cq_advance(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_buf_ring *") IOURingBufRing br, int count) {
        LibURing.nio_uring_buf_ring_cq_advance(ring.address(), br.address(), count);
    }

    public static native int nio_uring_buf_ring_available(long var0, long var2, short var4);

    public static int io_uring_buf_ring_available(@NativeType(value="struct io_uring *") IOURing ring, @NativeType(value="struct io_uring_buf_ring *") IOURingBufRing br, @NativeType(value="unsigned short") short bgid) {
        return LibURing.nio_uring_buf_ring_available(ring.address(), br.address(), bgid);
    }

    public static native long nio_uring_get_sqe(long var0);

    @Nullable
    @NativeType(value="struct io_uring_sqe *")
    public static IOURingSQE io_uring_get_sqe(@NativeType(value="struct io_uring *") IOURing ring) {
        long __result = LibURing.nio_uring_get_sqe(ring.address());
        return IOURingSQE.createSafe(__result);
    }

    public static native int io_uring_mlock_size(@NativeType(value="unsigned") int var0, @NativeType(value="unsigned") int var1);

    public static native int nio_uring_mlock_size_params(int var0, long var1);

    public static int io_uring_mlock_size_params(@NativeType(value="unsigned") int entries, @NativeType(value="struct io_uring_params *") IOURingParams p) {
        return LibURing.nio_uring_mlock_size_params(entries, p.address());
    }

    public static native int io_uring_major_version();

    public static native int io_uring_minor_version();

    @NativeType(value="bool")
    public static native boolean io_uring_check_version(int var0, int var1);

    public static int io_uring_buf_ring_mask(@NativeType(value="__u32") int ring_entries) {
        return ring_entries - 1;
    }

    public static void io_uring_buf_ring_init(@NativeType(value="struct io_uring_buf_ring *") IOURingBufRing br) {
        br.tail((short)0);
    }

    public static void io_uring_buf_ring_add(@NativeType(value="struct io_uring_buf_ring *") IOURingBufRing br, @NativeType(value="void *") ByteBuffer addr, @NativeType(value="unsigned short") short bid, int mask, int buf_offset) {
        IOURingBuf buf = br.bufs(br.tail() + buf_offset & mask);
        buf.addr(MemoryUtil.memAddress(addr));
        buf.len(addr.remaining());
        buf.bid(bid);
    }

    static {
        Library.initialize();
    }
}

package org.lwjgl.system.linux.liburing;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct io_uring_sqe")
public class IOURingSQE
extends Struct<IOURingSQE>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int OPCODE;
    public static final int FLAGS;
    public static final int IOPRIO;
    public static final int FD;
    public static final int OFF;
    public static final int ADDR2;
    public static final int CMD_OP;
    public static final int __PAD1;
    public static final int ADDR;
    public static final int SPLICE_OFF_IN;
    public static final int LEVEL;
    public static final int OPTNAME;
    public static final int LEN;
    public static final int RW_FLAGS;
    public static final int FSYNC_FLAGS;
    public static final int POLL_EVENTS;
    public static final int POLL32_EVENTS;
    public static final int SYNC_RANGE_FLAGS;
    public static final int MSG_FLAGS;
    public static final int TIMEOUT_FLAGS;
    public static final int ACCEPT_FLAGS;
    public static final int CANCEL_FLAGS;
    public static final int OPEN_FLAGS;
    public static final int STATX_FLAGS;
    public static final int FADVISE_ADVICE;
    public static final int SPLICE_FLAGS;
    public static final int RENAME_FLAGS;
    public static final int UNLINK_FLAGS;
    public static final int HARDLINK_FLAGS;
    public static final int XATTR_FLAGS;
    public static final int MSG_RING_FLAGS;
    public static final int URING_CMD_FLAGS;
    public static final int WAITID_FLAGS;
    public static final int FUTEX_FLAGS;
    public static final int INSTALL_FD_FLAGS;
    public static final int USER_DATA;
    public static final int BUF_INDEX;
    public static final int BUF_GROUP;
    public static final int PERSONALITY;
    public static final int SPLICE_FD_IN;
    public static final int FILE_INDEX;
    public static final int OPTLEN;
    public static final int ADDR_LEN;
    public static final int __PAD3;
    public static final int ADDR3;
    public static final int __PAD2;
    public static final int OPTVAL;
    public static final int CMD;

    protected IOURingSQE(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected IOURingSQE create(long address, @Nullable ByteBuffer container) {
        return new IOURingSQE(address, container);
    }

    public IOURingSQE(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), IOURingSQE.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="__u8")
    public byte opcode() {
        return IOURingSQE.nopcode(this.address());
    }

    @NativeType(value="__u8")
    public byte flags() {
        return IOURingSQE.nflags(this.address());
    }

    @NativeType(value="__u16")
    public short ioprio() {
        return IOURingSQE.nioprio(this.address());
    }

    @NativeType(value="__s32")
    public int fd() {
        return IOURingSQE.nfd(this.address());
    }

    @NativeType(value="__u64")
    public long off() {
        return IOURingSQE.noff(this.address());
    }

    @NativeType(value="__u64")
    public long addr2() {
        return IOURingSQE.naddr2(this.address());
    }

    @NativeType(value="__u32")
    public int cmd_op() {
        return IOURingSQE.ncmd_op(this.address());
    }

    @NativeType(value="__u32")
    public int __pad1() {
        return IOURingSQE.n__pad1(this.address());
    }

    @NativeType(value="__u64")
    public long addr() {
        return IOURingSQE.naddr(this.address());
    }

    @NativeType(value="__u64")
    public long splice_off_in() {
        return IOURingSQE.nsplice_off_in(this.address());
    }

    @NativeType(value="__u32")
    public int level() {
        return IOURingSQE.nlevel(this.address());
    }

    @NativeType(value="__u32")
    public int optname() {
        return IOURingSQE.noptname(this.address());
    }

    @NativeType(value="__u32")
    public int len() {
        return IOURingSQE.nlen(this.address());
    }

    @NativeType(value="__kernel_rwf_t")
    public int rw_flags() {
        return IOURingSQE.nrw_flags(this.address());
    }

    @NativeType(value="__u32")
    public int fsync_flags() {
        return IOURingSQE.nfsync_flags(this.address());
    }

    @NativeType(value="__u16")
    public short poll_events() {
        return IOURingSQE.npoll_events(this.address());
    }

    @NativeType(value="__u32")
    public int poll32_events() {
        return IOURingSQE.npoll32_events(this.address());
    }

    @NativeType(value="__u32")
    public int sync_range_flags() {
        return IOURingSQE.nsync_range_flags(this.address());
    }

    @NativeType(value="__u32")
    public int msg_flags() {
        return IOURingSQE.nmsg_flags(this.address());
    }

    @NativeType(value="__u32")
    public int timeout_flags() {
        return IOURingSQE.ntimeout_flags(this.address());
    }

    @NativeType(value="__u32")
    public int accept_flags() {
        return IOURingSQE.naccept_flags(this.address());
    }

    @NativeType(value="__u32")
    public int cancel_flags() {
        return IOURingSQE.ncancel_flags(this.address());
    }

    @NativeType(value="__u32")
    public int open_flags() {
        return IOURingSQE.nopen_flags(this.address());
    }

    @NativeType(value="__u32")
    public int statx_flags() {
        return IOURingSQE.nstatx_flags(this.address());
    }

    @NativeType(value="__u32")
    public int fadvise_advice() {
        return IOURingSQE.nfadvise_advice(this.address());
    }

    @NativeType(value="__u32")
    public int splice_flags() {
        return IOURingSQE.nsplice_flags(this.address());
    }

    @NativeType(value="__u32")
    public int rename_flags() {
        return IOURingSQE.nrename_flags(this.address());
    }

    @NativeType(value="__u32")
    public int unlink_flags() {
        return IOURingSQE.nunlink_flags(this.address());
    }

    @NativeType(value="__u32")
    public int hardlink_flags() {
        return IOURingSQE.nhardlink_flags(this.address());
    }

    @NativeType(value="__u32")
    public int xattr_flags() {
        return IOURingSQE.nxattr_flags(this.address());
    }

    @NativeType(value="__u32")
    public int msg_ring_flags() {
        return IOURingSQE.nmsg_ring_flags(this.address());
    }

    @NativeType(value="__u32")
    public int uring_cmd_flags() {
        return IOURingSQE.nuring_cmd_flags(this.address());
    }

    @NativeType(value="__u32")
    public int waitid_flags() {
        return IOURingSQE.nwaitid_flags(this.address());
    }

    @NativeType(value="__u32")
    public int futex_flags() {
        return IOURingSQE.nfutex_flags(this.address());
    }

    @NativeType(value="__u32")
    public int install_fd_flags() {
        return IOURingSQE.ninstall_fd_flags(this.address());
    }

    @NativeType(value="__u64")
    public long user_data() {
        return IOURingSQE.nuser_data(this.address());
    }

    @NativeType(value="__u16")
    public short buf_index() {
        return IOURingSQE.nbuf_index(this.address());
    }

    @NativeType(value="__u16")
    public short buf_group() {
        return IOURingSQE.nbuf_group(this.address());
    }

    @NativeType(value="__u16")
    public short personality() {
        return IOURingSQE.npersonality(this.address());
    }

    @NativeType(value="__s32")
    public int splice_fd_in() {
        return IOURingSQE.nsplice_fd_in(this.address());
    }

    @NativeType(value="__u32")
    public int file_index() {
        return IOURingSQE.nfile_index(this.address());
    }

    @NativeType(value="__u32")
    public int optlen() {
        return IOURingSQE.noptlen(this.address());
    }

    @NativeType(value="__u16")
    public short addr_len() {
        return IOURingSQE.naddr_len(this.address());
    }

    @NativeType(value="__u16[1]")
    public ShortBuffer __pad3() {
        return IOURingSQE.n__pad3(this.address());
    }

    @NativeType(value="__u16")
    public short __pad3(int index) {
        return IOURingSQE.n__pad3(this.address(), index);
    }

    @NativeType(value="__u64")
    public long addr3() {
        return IOURingSQE.naddr3(this.address());
    }

    @NativeType(value="__u64[1]")
    public LongBuffer __pad2() {
        return IOURingSQE.n__pad2(this.address());
    }

    @NativeType(value="__u64")
    public long __pad2(int index) {
        return IOURingSQE.n__pad2(this.address(), index);
    }

    @NativeType(value="__u64")
    public long optval() {
        return IOURingSQE.noptval(this.address());
    }

    @NativeType(value="__u8[0]")
    public ByteBuffer cmd() {
        return IOURingSQE.ncmd(this.address());
    }

    @NativeType(value="__u8")
    public byte cmd(int index) {
        return IOURingSQE.ncmd(this.address(), index);
    }

    public IOURingSQE opcode(@NativeType(value="__u8") byte value) {
        IOURingSQE.nopcode(this.address(), value);
        return this;
    }

    public IOURingSQE flags(@NativeType(value="__u8") byte value) {
        IOURingSQE.nflags(this.address(), value);
        return this;
    }

    public IOURingSQE ioprio(@NativeType(value="__u16") short value) {
        IOURingSQE.nioprio(this.address(), value);
        return this;
    }

    public IOURingSQE fd(@NativeType(value="__s32") int value) {
        IOURingSQE.nfd(this.address(), value);
        return this;
    }

    public IOURingSQE off(@NativeType(value="__u64") long value) {
        IOURingSQE.noff(this.address(), value);
        return this;
    }

    public IOURingSQE addr2(@NativeType(value="__u64") long value) {
        IOURingSQE.naddr2(this.address(), value);
        return this;
    }

    public IOURingSQE cmd_op(@NativeType(value="__u32") int value) {
        IOURingSQE.ncmd_op(this.address(), value);
        return this;
    }

    public IOURingSQE __pad1(@NativeType(value="__u32") int value) {
        IOURingSQE.n__pad1(this.address(), value);
        return this;
    }

    public IOURingSQE addr(@NativeType(value="__u64") long value) {
        IOURingSQE.naddr(this.address(), value);
        return this;
    }

    public IOURingSQE splice_off_in(@NativeType(value="__u64") long value) {
        IOURingSQE.nsplice_off_in(this.address(), value);
        return this;
    }

    public IOURingSQE level(@NativeType(value="__u32") int value) {
        IOURingSQE.nlevel(this.address(), value);
        return this;
    }

    public IOURingSQE optname(@NativeType(value="__u32") int value) {
        IOURingSQE.noptname(this.address(), value);
        return this;
    }

    public IOURingSQE len(@NativeType(value="__u32") int value) {
        IOURingSQE.nlen(this.address(), value);
        return this;
    }

    public IOURingSQE rw_flags(@NativeType(value="__kernel_rwf_t") int value) {
        IOURingSQE.nrw_flags(this.address(), value);
        return this;
    }

    public IOURingSQE fsync_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.nfsync_flags(this.address(), value);
        return this;
    }

    public IOURingSQE poll_events(@NativeType(value="__u16") short value) {
        IOURingSQE.npoll_events(this.address(), value);
        return this;
    }

    public IOURingSQE poll32_events(@NativeType(value="__u32") int value) {
        IOURingSQE.npoll32_events(this.address(), value);
        return this;
    }

    public IOURingSQE sync_range_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.nsync_range_flags(this.address(), value);
        return this;
    }

    public IOURingSQE msg_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.nmsg_flags(this.address(), value);
        return this;
    }

    public IOURingSQE timeout_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.ntimeout_flags(this.address(), value);
        return this;
    }

    public IOURingSQE accept_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.naccept_flags(this.address(), value);
        return this;
    }

    public IOURingSQE cancel_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.ncancel_flags(this.address(), value);
        return this;
    }

    public IOURingSQE open_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.nopen_flags(this.address(), value);
        return this;
    }

    public IOURingSQE statx_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.nstatx_flags(this.address(), value);
        return this;
    }

    public IOURingSQE fadvise_advice(@NativeType(value="__u32") int value) {
        IOURingSQE.nfadvise_advice(this.address(), value);
        return this;
    }

    public IOURingSQE splice_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.nsplice_flags(this.address(), value);
        return this;
    }

    public IOURingSQE rename_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.nrename_flags(this.address(), value);
        return this;
    }

    public IOURingSQE unlink_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.nunlink_flags(this.address(), value);
        return this;
    }

    public IOURingSQE hardlink_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.nhardlink_flags(this.address(), value);
        return this;
    }

    public IOURingSQE xattr_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.nxattr_flags(this.address(), value);
        return this;
    }

    public IOURingSQE msg_ring_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.nmsg_ring_flags(this.address(), value);
        return this;
    }

    public IOURingSQE uring_cmd_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.nuring_cmd_flags(this.address(), value);
        return this;
    }

    public IOURingSQE waitid_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.nwaitid_flags(this.address(), value);
        return this;
    }

    public IOURingSQE futex_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.nfutex_flags(this.address(), value);
        return this;
    }

    public IOURingSQE install_fd_flags(@NativeType(value="__u32") int value) {
        IOURingSQE.ninstall_fd_flags(this.address(), value);
        return this;
    }

    public IOURingSQE user_data(@NativeType(value="__u64") long value) {
        IOURingSQE.nuser_data(this.address(), value);
        return this;
    }

    public IOURingSQE buf_index(@NativeType(value="__u16") short value) {
        IOURingSQE.nbuf_index(this.address(), value);
        return this;
    }

    public IOURingSQE buf_group(@NativeType(value="__u16") short value) {
        IOURingSQE.nbuf_group(this.address(), value);
        return this;
    }

    public IOURingSQE personality(@NativeType(value="__u16") short value) {
        IOURingSQE.npersonality(this.address(), value);
        return this;
    }

    public IOURingSQE splice_fd_in(@NativeType(value="__s32") int value) {
        IOURingSQE.nsplice_fd_in(this.address(), value);
        return this;
    }

    public IOURingSQE file_index(@NativeType(value="__u32") int value) {
        IOURingSQE.nfile_index(this.address(), value);
        return this;
    }

    public IOURingSQE optlen(@NativeType(value="__u32") int value) {
        IOURingSQE.noptlen(this.address(), value);
        return this;
    }

    public IOURingSQE addr_len(@NativeType(value="__u16") short value) {
        IOURingSQE.naddr_len(this.address(), value);
        return this;
    }

    public IOURingSQE __pad3(@NativeType(value="__u16[1]") ShortBuffer value) {
        IOURingSQE.n__pad3(this.address(), value);
        return this;
    }

    public IOURingSQE __pad3(int index, @NativeType(value="__u16") short value) {
        IOURingSQE.n__pad3(this.address(), index, value);
        return this;
    }

    public IOURingSQE addr3(@NativeType(value="__u64") long value) {
        IOURingSQE.naddr3(this.address(), value);
        return this;
    }

    public IOURingSQE __pad2(@NativeType(value="__u64[1]") LongBuffer value) {
        IOURingSQE.n__pad2(this.address(), value);
        return this;
    }

    public IOURingSQE __pad2(int index, @NativeType(value="__u64") long value) {
        IOURingSQE.n__pad2(this.address(), index, value);
        return this;
    }

    public IOURingSQE optval(@NativeType(value="__u64") long value) {
        IOURingSQE.noptval(this.address(), value);
        return this;
    }

    public IOURingSQE cmd(@NativeType(value="__u8[0]") ByteBuffer value) {
        IOURingSQE.ncmd(this.address(), value);
        return this;
    }

    public IOURingSQE cmd(int index, @NativeType(value="__u8") byte value) {
        IOURingSQE.ncmd(this.address(), index, value);
        return this;
    }

    public IOURingSQE set(IOURingSQE src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static IOURingSQE malloc() {
        return new IOURingSQE(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static IOURingSQE calloc() {
        return new IOURingSQE(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static IOURingSQE create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new IOURingSQE(MemoryUtil.memAddress(container), container);
    }

    public static IOURingSQE create(long address) {
        return new IOURingSQE(address, null);
    }

    @Nullable
    public static IOURingSQE createSafe(long address) {
        return address == 0L ? null : new IOURingSQE(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(IOURingSQE.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = IOURingSQE.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static IOURingSQE malloc(MemoryStack stack) {
        return new IOURingSQE(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static IOURingSQE calloc(MemoryStack stack) {
        return new IOURingSQE(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static byte nopcode(long struct) {
        return UNSAFE.getByte(null, struct + (long)OPCODE);
    }

    public static byte nflags(long struct) {
        return UNSAFE.getByte(null, struct + (long)FLAGS);
    }

    public static short nioprio(long struct) {
        return UNSAFE.getShort(null, struct + (long)IOPRIO);
    }

    public static int nfd(long struct) {
        return UNSAFE.getInt(null, struct + (long)FD);
    }

    public static long noff(long struct) {
        return UNSAFE.getLong(null, struct + (long)OFF);
    }

    public static long naddr2(long struct) {
        return UNSAFE.getLong(null, struct + (long)ADDR2);
    }

    public static int ncmd_op(long struct) {
        return UNSAFE.getInt(null, struct + (long)CMD_OP);
    }

    public static int n__pad1(long struct) {
        return UNSAFE.getInt(null, struct + (long)__PAD1);
    }

    public static long naddr(long struct) {
        return UNSAFE.getLong(null, struct + (long)ADDR);
    }

    public static long nsplice_off_in(long struct) {
        return UNSAFE.getLong(null, struct + (long)SPLICE_OFF_IN);
    }

    public static int nlevel(long struct) {
        return UNSAFE.getInt(null, struct + (long)LEVEL);
    }

    public static int noptname(long struct) {
        return UNSAFE.getInt(null, struct + (long)OPTNAME);
    }

    public static int nlen(long struct) {
        return UNSAFE.getInt(null, struct + (long)LEN);
    }

    public static int nrw_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)RW_FLAGS);
    }

    public static int nfsync_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FSYNC_FLAGS);
    }

    public static short npoll_events(long struct) {
        return UNSAFE.getShort(null, struct + (long)POLL_EVENTS);
    }

    public static int npoll32_events(long struct) {
        return UNSAFE.getInt(null, struct + (long)POLL32_EVENTS);
    }

    public static int nsync_range_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)SYNC_RANGE_FLAGS);
    }

    public static int nmsg_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)MSG_FLAGS);
    }

    public static int ntimeout_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)TIMEOUT_FLAGS);
    }

    public static int naccept_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)ACCEPT_FLAGS);
    }

    public static int ncancel_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)CANCEL_FLAGS);
    }

    public static int nopen_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)OPEN_FLAGS);
    }

    public static int nstatx_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)STATX_FLAGS);
    }

    public static int nfadvise_advice(long struct) {
        return UNSAFE.getInt(null, struct + (long)FADVISE_ADVICE);
    }

    public static int nsplice_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)SPLICE_FLAGS);
    }

    public static int nrename_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)RENAME_FLAGS);
    }

    public static int nunlink_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)UNLINK_FLAGS);
    }

    public static int nhardlink_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)HARDLINK_FLAGS);
    }

    public static int nxattr_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)XATTR_FLAGS);
    }

    public static int nmsg_ring_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)MSG_RING_FLAGS);
    }

    public static int nuring_cmd_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)URING_CMD_FLAGS);
    }

    public static int nwaitid_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)WAITID_FLAGS);
    }

    public static int nfutex_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FUTEX_FLAGS);
    }

    public static int ninstall_fd_flags(long struct) {
        return UNSAFE.getInt(null, struct + (long)INSTALL_FD_FLAGS);
    }

    public static long nuser_data(long struct) {
        return UNSAFE.getLong(null, struct + (long)USER_DATA);
    }

    public static short nbuf_index(long struct) {
        return UNSAFE.getShort(null, struct + (long)BUF_INDEX);
    }

    public static short nbuf_group(long struct) {
        return UNSAFE.getShort(null, struct + (long)BUF_GROUP);
    }

    public static short npersonality(long struct) {
        return UNSAFE.getShort(null, struct + (long)PERSONALITY);
    }

    public static int nsplice_fd_in(long struct) {
        return UNSAFE.getInt(null, struct + (long)SPLICE_FD_IN);
    }

    public static int nfile_index(long struct) {
        return UNSAFE.getInt(null, struct + (long)FILE_INDEX);
    }

    public static int noptlen(long struct) {
        return UNSAFE.getInt(null, struct + (long)OPTLEN);
    }

    public static short naddr_len(long struct) {
        return UNSAFE.getShort(null, struct + (long)ADDR_LEN);
    }

    public static ShortBuffer n__pad3(long struct) {
        return MemoryUtil.memShortBuffer(struct + (long)__PAD3, 1);
    }

    public static short n__pad3(long struct, int index) {
        return UNSAFE.getShort(null, struct + (long)__PAD3 + Checks.check(index, 1) * 2L);
    }

    public static long naddr3(long struct) {
        return UNSAFE.getLong(null, struct + (long)ADDR3);
    }

    public static LongBuffer n__pad2(long struct) {
        return MemoryUtil.memLongBuffer(struct + (long)__PAD2, 1);
    }

    public static long n__pad2(long struct, int index) {
        return UNSAFE.getLong(null, struct + (long)__PAD2 + Checks.check(index, 1) * 8L);
    }

    public static long noptval(long struct) {
        return UNSAFE.getLong(null, struct + (long)OPTVAL);
    }

    public static ByteBuffer ncmd(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)CMD, 0);
    }

    public static byte ncmd(long struct, int index) {
        return UNSAFE.getByte(null, struct + (long)CMD + Checks.check(index, 0) * 1L);
    }

    public static void nopcode(long struct, byte value) {
        UNSAFE.putByte(null, struct + (long)OPCODE, value);
    }

    public static void nflags(long struct, byte value) {
        UNSAFE.putByte(null, struct + (long)FLAGS, value);
    }

    public static void nioprio(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)IOPRIO, value);
    }

    public static void nfd(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FD, value);
    }

    public static void noff(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)OFF, value);
    }

    public static void naddr2(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)ADDR2, value);
    }

    public static void ncmd_op(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)CMD_OP, value);
    }

    public static void n__pad1(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)__PAD1, value);
    }

    public static void naddr(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)ADDR, value);
    }

    public static void nsplice_off_in(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)SPLICE_OFF_IN, value);
    }

    public static void nlevel(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)LEVEL, value);
    }

    public static void noptname(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OPTNAME, value);
    }

    public static void nlen(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)LEN, value);
    }

    public static void nrw_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RW_FLAGS, value);
    }

    public static void nfsync_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FSYNC_FLAGS, value);
    }

    public static void npoll_events(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)POLL_EVENTS, value);
    }

    public static void npoll32_events(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)POLL32_EVENTS, value);
    }

    public static void nsync_range_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)SYNC_RANGE_FLAGS, value);
    }

    public static void nmsg_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)MSG_FLAGS, value);
    }

    public static void ntimeout_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)TIMEOUT_FLAGS, value);
    }

    public static void naccept_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)ACCEPT_FLAGS, value);
    }

    public static void ncancel_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)CANCEL_FLAGS, value);
    }

    public static void nopen_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OPEN_FLAGS, value);
    }

    public static void nstatx_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)STATX_FLAGS, value);
    }

    public static void nfadvise_advice(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FADVISE_ADVICE, value);
    }

    public static void nsplice_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)SPLICE_FLAGS, value);
    }

    public static void nrename_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)RENAME_FLAGS, value);
    }

    public static void nunlink_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)UNLINK_FLAGS, value);
    }

    public static void nhardlink_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)HARDLINK_FLAGS, value);
    }

    public static void nxattr_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)XATTR_FLAGS, value);
    }

    public static void nmsg_ring_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)MSG_RING_FLAGS, value);
    }

    public static void nuring_cmd_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)URING_CMD_FLAGS, value);
    }

    public static void nwaitid_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)WAITID_FLAGS, value);
    }

    public static void nfutex_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FUTEX_FLAGS, value);
    }

    public static void ninstall_fd_flags(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)INSTALL_FD_FLAGS, value);
    }

    public static void nuser_data(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)USER_DATA, value);
    }

    public static void nbuf_index(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)BUF_INDEX, value);
    }

    public static void nbuf_group(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)BUF_GROUP, value);
    }

    public static void npersonality(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)PERSONALITY, value);
    }

    public static void nsplice_fd_in(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)SPLICE_FD_IN, value);
    }

    public static void nfile_index(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FILE_INDEX, value);
    }

    public static void noptlen(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OPTLEN, value);
    }

    public static void naddr_len(long struct, short value) {
        UNSAFE.putShort(null, struct + (long)ADDR_LEN, value);
    }

    public static void n__pad3(long struct, ShortBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 1);
        }
        MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + (long)__PAD3, value.remaining() * 2);
    }

    public static void n__pad3(long struct, int index, short value) {
        UNSAFE.putShort(null, struct + (long)__PAD3 + Checks.check(index, 1) * 2L, value);
    }

    public static void naddr3(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)ADDR3, value);
    }

    public static void n__pad2(long struct, LongBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 1);
        }
        MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + (long)__PAD2, value.remaining() * 8);
    }

    public static void n__pad2(long struct, int index, long value) {
        UNSAFE.putLong(null, struct + (long)__PAD2 + Checks.check(index, 1) * 8L, value);
    }

    public static void noptval(long struct, long value) {
        UNSAFE.putLong(null, struct + (long)OPTVAL, value);
    }

    public static void ncmd(long struct, ByteBuffer value) {
        if (Checks.CHECKS) {
            Checks.checkGT(value, 0);
        }
        MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + (long)CMD, value.remaining() * 1);
    }

    public static void ncmd(long struct, int index, byte value) {
        UNSAFE.putByte(null, struct + (long)CMD + Checks.check(index, 0) * 1L, value);
    }

    static {
        Struct.Layout layout = IOURingSQE.__struct(IOURingSQE.__member(1), IOURingSQE.__member(1), IOURingSQE.__member(2), IOURingSQE.__member(4), IOURingSQE.__union(IOURingSQE.__member(8), IOURingSQE.__member(8), IOURingSQE.__struct(IOURingSQE.__member(4), IOURingSQE.__member(4))), IOURingSQE.__union(IOURingSQE.__member(8), IOURingSQE.__member(8), IOURingSQE.__struct(IOURingSQE.__member(4), IOURingSQE.__member(4))), IOURingSQE.__member(4), IOURingSQE.__union(IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(2), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4)), IOURingSQE.__member(8), IOURingSQE.__union(IOURingSQE.__member(2), IOURingSQE.__member(2)), IOURingSQE.__member(2), IOURingSQE.__union(IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__member(4), IOURingSQE.__struct(IOURingSQE.__member(2), IOURingSQE.__array(2, 1))), IOURingSQE.__union(IOURingSQE.__struct(IOURingSQE.__member(8), IOURingSQE.__array(8, 1)), IOURingSQE.__member(8), IOURingSQE.__array(1, 0)));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        OPCODE = layout.offsetof(0);
        FLAGS = layout.offsetof(1);
        IOPRIO = layout.offsetof(2);
        FD = layout.offsetof(3);
        OFF = layout.offsetof(5);
        ADDR2 = layout.offsetof(6);
        CMD_OP = layout.offsetof(8);
        __PAD1 = layout.offsetof(9);
        ADDR = layout.offsetof(11);
        SPLICE_OFF_IN = layout.offsetof(12);
        LEVEL = layout.offsetof(14);
        OPTNAME = layout.offsetof(15);
        LEN = layout.offsetof(16);
        RW_FLAGS = layout.offsetof(18);
        FSYNC_FLAGS = layout.offsetof(19);
        POLL_EVENTS = layout.offsetof(20);
        POLL32_EVENTS = layout.offsetof(21);
        SYNC_RANGE_FLAGS = layout.offsetof(22);
        MSG_FLAGS = layout.offsetof(23);
        TIMEOUT_FLAGS = layout.offsetof(24);
        ACCEPT_FLAGS = layout.offsetof(25);
        CANCEL_FLAGS = layout.offsetof(26);
        OPEN_FLAGS = layout.offsetof(27);
        STATX_FLAGS = layout.offsetof(28);
        FADVISE_ADVICE = layout.offsetof(29);
        SPLICE_FLAGS = layout.offsetof(30);
        RENAME_FLAGS = layout.offsetof(31);
        UNLINK_FLAGS = layout.offsetof(32);
        HARDLINK_FLAGS = layout.offsetof(33);
        XATTR_FLAGS = layout.offsetof(34);
        MSG_RING_FLAGS = layout.offsetof(35);
        URING_CMD_FLAGS = layout.offsetof(36);
        WAITID_FLAGS = layout.offsetof(37);
        FUTEX_FLAGS = layout.offsetof(38);
        INSTALL_FD_FLAGS = layout.offsetof(39);
        USER_DATA = layout.offsetof(40);
        BUF_INDEX = layout.offsetof(42);
        BUF_GROUP = layout.offsetof(43);
        PERSONALITY = layout.offsetof(44);
        SPLICE_FD_IN = layout.offsetof(46);
        FILE_INDEX = layout.offsetof(47);
        OPTLEN = layout.offsetof(48);
        ADDR_LEN = layout.offsetof(50);
        __PAD3 = layout.offsetof(51);
        ADDR3 = layout.offsetof(54);
        __PAD2 = layout.offsetof(55);
        OPTVAL = layout.offsetof(56);
        CMD = layout.offsetof(57);
    }

    public static class Buffer
    extends StructBuffer<IOURingSQE, Buffer>
    implements NativeResource {
        private static final IOURingSQE ELEMENT_FACTORY = IOURingSQE.create(-1L);

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
        protected IOURingSQE getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="__u8")
        public byte opcode() {
            return IOURingSQE.nopcode(this.address());
        }

        @NativeType(value="__u8")
        public byte flags() {
            return IOURingSQE.nflags(this.address());
        }

        @NativeType(value="__u16")
        public short ioprio() {
            return IOURingSQE.nioprio(this.address());
        }

        @NativeType(value="__s32")
        public int fd() {
            return IOURingSQE.nfd(this.address());
        }

        @NativeType(value="__u64")
        public long off() {
            return IOURingSQE.noff(this.address());
        }

        @NativeType(value="__u64")
        public long addr2() {
            return IOURingSQE.naddr2(this.address());
        }

        @NativeType(value="__u32")
        public int cmd_op() {
            return IOURingSQE.ncmd_op(this.address());
        }

        @NativeType(value="__u32")
        public int __pad1() {
            return IOURingSQE.n__pad1(this.address());
        }

        @NativeType(value="__u64")
        public long addr() {
            return IOURingSQE.naddr(this.address());
        }

        @NativeType(value="__u64")
        public long splice_off_in() {
            return IOURingSQE.nsplice_off_in(this.address());
        }

        @NativeType(value="__u32")
        public int level() {
            return IOURingSQE.nlevel(this.address());
        }

        @NativeType(value="__u32")
        public int optname() {
            return IOURingSQE.noptname(this.address());
        }

        @NativeType(value="__u32")
        public int len() {
            return IOURingSQE.nlen(this.address());
        }

        @NativeType(value="__kernel_rwf_t")
        public int rw_flags() {
            return IOURingSQE.nrw_flags(this.address());
        }

        @NativeType(value="__u32")
        public int fsync_flags() {
            return IOURingSQE.nfsync_flags(this.address());
        }

        @NativeType(value="__u16")
        public short poll_events() {
            return IOURingSQE.npoll_events(this.address());
        }

        @NativeType(value="__u32")
        public int poll32_events() {
            return IOURingSQE.npoll32_events(this.address());
        }

        @NativeType(value="__u32")
        public int sync_range_flags() {
            return IOURingSQE.nsync_range_flags(this.address());
        }

        @NativeType(value="__u32")
        public int msg_flags() {
            return IOURingSQE.nmsg_flags(this.address());
        }

        @NativeType(value="__u32")
        public int timeout_flags() {
            return IOURingSQE.ntimeout_flags(this.address());
        }

        @NativeType(value="__u32")
        public int accept_flags() {
            return IOURingSQE.naccept_flags(this.address());
        }

        @NativeType(value="__u32")
        public int cancel_flags() {
            return IOURingSQE.ncancel_flags(this.address());
        }

        @NativeType(value="__u32")
        public int open_flags() {
            return IOURingSQE.nopen_flags(this.address());
        }

        @NativeType(value="__u32")
        public int statx_flags() {
            return IOURingSQE.nstatx_flags(this.address());
        }

        @NativeType(value="__u32")
        public int fadvise_advice() {
            return IOURingSQE.nfadvise_advice(this.address());
        }

        @NativeType(value="__u32")
        public int splice_flags() {
            return IOURingSQE.nsplice_flags(this.address());
        }

        @NativeType(value="__u32")
        public int rename_flags() {
            return IOURingSQE.nrename_flags(this.address());
        }

        @NativeType(value="__u32")
        public int unlink_flags() {
            return IOURingSQE.nunlink_flags(this.address());
        }

        @NativeType(value="__u32")
        public int hardlink_flags() {
            return IOURingSQE.nhardlink_flags(this.address());
        }

        @NativeType(value="__u32")
        public int xattr_flags() {
            return IOURingSQE.nxattr_flags(this.address());
        }

        @NativeType(value="__u32")
        public int msg_ring_flags() {
            return IOURingSQE.nmsg_ring_flags(this.address());
        }

        @NativeType(value="__u32")
        public int uring_cmd_flags() {
            return IOURingSQE.nuring_cmd_flags(this.address());
        }

        @NativeType(value="__u32")
        public int waitid_flags() {
            return IOURingSQE.nwaitid_flags(this.address());
        }

        @NativeType(value="__u32")
        public int futex_flags() {
            return IOURingSQE.nfutex_flags(this.address());
        }

        @NativeType(value="__u32")
        public int install_fd_flags() {
            return IOURingSQE.ninstall_fd_flags(this.address());
        }

        @NativeType(value="__u64")
        public long user_data() {
            return IOURingSQE.nuser_data(this.address());
        }

        @NativeType(value="__u16")
        public short buf_index() {
            return IOURingSQE.nbuf_index(this.address());
        }

        @NativeType(value="__u16")
        public short buf_group() {
            return IOURingSQE.nbuf_group(this.address());
        }

        @NativeType(value="__u16")
        public short personality() {
            return IOURingSQE.npersonality(this.address());
        }

        @NativeType(value="__s32")
        public int splice_fd_in() {
            return IOURingSQE.nsplice_fd_in(this.address());
        }

        @NativeType(value="__u32")
        public int file_index() {
            return IOURingSQE.nfile_index(this.address());
        }

        @NativeType(value="__u32")
        public int optlen() {
            return IOURingSQE.noptlen(this.address());
        }

        @NativeType(value="__u16")
        public short addr_len() {
            return IOURingSQE.naddr_len(this.address());
        }

        @NativeType(value="__u16[1]")
        public ShortBuffer __pad3() {
            return IOURingSQE.n__pad3(this.address());
        }

        @NativeType(value="__u16")
        public short __pad3(int index) {
            return IOURingSQE.n__pad3(this.address(), index);
        }

        @NativeType(value="__u64")
        public long addr3() {
            return IOURingSQE.naddr3(this.address());
        }

        @NativeType(value="__u64[1]")
        public LongBuffer __pad2() {
            return IOURingSQE.n__pad2(this.address());
        }

        @NativeType(value="__u64")
        public long __pad2(int index) {
            return IOURingSQE.n__pad2(this.address(), index);
        }

        @NativeType(value="__u64")
        public long optval() {
            return IOURingSQE.noptval(this.address());
        }

        @NativeType(value="__u8[0]")
        public ByteBuffer cmd() {
            return IOURingSQE.ncmd(this.address());
        }

        @NativeType(value="__u8")
        public byte cmd(int index) {
            return IOURingSQE.ncmd(this.address(), index);
        }

        public Buffer opcode(@NativeType(value="__u8") byte value) {
            IOURingSQE.nopcode(this.address(), value);
            return this;
        }

        public Buffer flags(@NativeType(value="__u8") byte value) {
            IOURingSQE.nflags(this.address(), value);
            return this;
        }

        public Buffer ioprio(@NativeType(value="__u16") short value) {
            IOURingSQE.nioprio(this.address(), value);
            return this;
        }

        public Buffer fd(@NativeType(value="__s32") int value) {
            IOURingSQE.nfd(this.address(), value);
            return this;
        }

        public Buffer off(@NativeType(value="__u64") long value) {
            IOURingSQE.noff(this.address(), value);
            return this;
        }

        public Buffer addr2(@NativeType(value="__u64") long value) {
            IOURingSQE.naddr2(this.address(), value);
            return this;
        }

        public Buffer cmd_op(@NativeType(value="__u32") int value) {
            IOURingSQE.ncmd_op(this.address(), value);
            return this;
        }

        public Buffer __pad1(@NativeType(value="__u32") int value) {
            IOURingSQE.n__pad1(this.address(), value);
            return this;
        }

        public Buffer addr(@NativeType(value="__u64") long value) {
            IOURingSQE.naddr(this.address(), value);
            return this;
        }

        public Buffer splice_off_in(@NativeType(value="__u64") long value) {
            IOURingSQE.nsplice_off_in(this.address(), value);
            return this;
        }

        public Buffer level(@NativeType(value="__u32") int value) {
            IOURingSQE.nlevel(this.address(), value);
            return this;
        }

        public Buffer optname(@NativeType(value="__u32") int value) {
            IOURingSQE.noptname(this.address(), value);
            return this;
        }

        public Buffer len(@NativeType(value="__u32") int value) {
            IOURingSQE.nlen(this.address(), value);
            return this;
        }

        public Buffer rw_flags(@NativeType(value="__kernel_rwf_t") int value) {
            IOURingSQE.nrw_flags(this.address(), value);
            return this;
        }

        public Buffer fsync_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.nfsync_flags(this.address(), value);
            return this;
        }

        public Buffer poll_events(@NativeType(value="__u16") short value) {
            IOURingSQE.npoll_events(this.address(), value);
            return this;
        }

        public Buffer poll32_events(@NativeType(value="__u32") int value) {
            IOURingSQE.npoll32_events(this.address(), value);
            return this;
        }

        public Buffer sync_range_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.nsync_range_flags(this.address(), value);
            return this;
        }

        public Buffer msg_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.nmsg_flags(this.address(), value);
            return this;
        }

        public Buffer timeout_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.ntimeout_flags(this.address(), value);
            return this;
        }

        public Buffer accept_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.naccept_flags(this.address(), value);
            return this;
        }

        public Buffer cancel_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.ncancel_flags(this.address(), value);
            return this;
        }

        public Buffer open_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.nopen_flags(this.address(), value);
            return this;
        }

        public Buffer statx_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.nstatx_flags(this.address(), value);
            return this;
        }

        public Buffer fadvise_advice(@NativeType(value="__u32") int value) {
            IOURingSQE.nfadvise_advice(this.address(), value);
            return this;
        }

        public Buffer splice_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.nsplice_flags(this.address(), value);
            return this;
        }

        public Buffer rename_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.nrename_flags(this.address(), value);
            return this;
        }

        public Buffer unlink_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.nunlink_flags(this.address(), value);
            return this;
        }

        public Buffer hardlink_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.nhardlink_flags(this.address(), value);
            return this;
        }

        public Buffer xattr_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.nxattr_flags(this.address(), value);
            return this;
        }

        public Buffer msg_ring_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.nmsg_ring_flags(this.address(), value);
            return this;
        }

        public Buffer uring_cmd_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.nuring_cmd_flags(this.address(), value);
            return this;
        }

        public Buffer waitid_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.nwaitid_flags(this.address(), value);
            return this;
        }

        public Buffer futex_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.nfutex_flags(this.address(), value);
            return this;
        }

        public Buffer install_fd_flags(@NativeType(value="__u32") int value) {
            IOURingSQE.ninstall_fd_flags(this.address(), value);
            return this;
        }

        public Buffer user_data(@NativeType(value="__u64") long value) {
            IOURingSQE.nuser_data(this.address(), value);
            return this;
        }

        public Buffer buf_index(@NativeType(value="__u16") short value) {
            IOURingSQE.nbuf_index(this.address(), value);
            return this;
        }

        public Buffer buf_group(@NativeType(value="__u16") short value) {
            IOURingSQE.nbuf_group(this.address(), value);
            return this;
        }

        public Buffer personality(@NativeType(value="__u16") short value) {
            IOURingSQE.npersonality(this.address(), value);
            return this;
        }

        public Buffer splice_fd_in(@NativeType(value="__s32") int value) {
            IOURingSQE.nsplice_fd_in(this.address(), value);
            return this;
        }

        public Buffer file_index(@NativeType(value="__u32") int value) {
            IOURingSQE.nfile_index(this.address(), value);
            return this;
        }

        public Buffer optlen(@NativeType(value="__u32") int value) {
            IOURingSQE.noptlen(this.address(), value);
            return this;
        }

        public Buffer addr_len(@NativeType(value="__u16") short value) {
            IOURingSQE.naddr_len(this.address(), value);
            return this;
        }

        public Buffer __pad3(@NativeType(value="__u16[1]") ShortBuffer value) {
            IOURingSQE.n__pad3(this.address(), value);
            return this;
        }

        public Buffer __pad3(int index, @NativeType(value="__u16") short value) {
            IOURingSQE.n__pad3(this.address(), index, value);
            return this;
        }

        public Buffer addr3(@NativeType(value="__u64") long value) {
            IOURingSQE.naddr3(this.address(), value);
            return this;
        }

        public Buffer __pad2(@NativeType(value="__u64[1]") LongBuffer value) {
            IOURingSQE.n__pad2(this.address(), value);
            return this;
        }

        public Buffer __pad2(int index, @NativeType(value="__u64") long value) {
            IOURingSQE.n__pad2(this.address(), index, value);
            return this;
        }

        public Buffer optval(@NativeType(value="__u64") long value) {
            IOURingSQE.noptval(this.address(), value);
            return this;
        }

        public Buffer cmd(@NativeType(value="__u8[0]") ByteBuffer value) {
            IOURingSQE.ncmd(this.address(), value);
            return this;
        }

        public Buffer cmd(int index, @NativeType(value="__u8") byte value) {
            IOURingSQE.ncmd(this.address(), index, value);
            return this;
        }
    }
}

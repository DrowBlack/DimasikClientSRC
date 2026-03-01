package net.minecraft.server;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;

public class SessionLockManager
implements AutoCloseable {
    private final FileChannel field_232994_a_;
    private final FileLock field_232995_b_;
    private static final ByteBuffer field_232996_c_;

    public static SessionLockManager func_232998_a_(Path p_232998_0_) throws IOException {
        Path path = p_232998_0_.resolve("session.lock");
        if (!Files.isDirectory(p_232998_0_, new LinkOption[0])) {
            Files.createDirectories(p_232998_0_, new FileAttribute[0]);
        }
        FileChannel filechannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        try {
            filechannel.write(field_232996_c_.duplicate());
            filechannel.force(true);
            FileLock filelock = filechannel.tryLock();
            if (filelock == null) {
                throw AlreadyLockedException.func_233000_a_(path);
            }
            return new SessionLockManager(filechannel, filelock);
        }
        catch (IOException ioexception1) {
            try {
                filechannel.close();
            }
            catch (IOException ioexception) {
                ioexception1.addSuppressed(ioexception);
            }
            throw ioexception1;
        }
    }

    private SessionLockManager(FileChannel p_i231437_1_, FileLock p_i231437_2_) {
        this.field_232994_a_ = p_i231437_1_;
        this.field_232995_b_ = p_i231437_2_;
    }

    @Override
    public void close() throws IOException {
        try {
            if (this.field_232995_b_.isValid()) {
                this.field_232995_b_.release();
            }
        }
        finally {
            if (this.field_232994_a_.isOpen()) {
                this.field_232994_a_.close();
            }
        }
    }

    public boolean func_232997_a_() {
        return this.field_232995_b_.isValid();
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static boolean func_232999_b_(Path p_232999_0_) throws IOException {
        Path path = p_232999_0_.resolve("session.lock");
        try (FileChannel filechannel = FileChannel.open(path, StandardOpenOption.WRITE);){
            boolean bl;
            block15: {
                FileLock filelock = filechannel.tryLock();
                try {
                    boolean bl2 = bl = filelock == null;
                    if (filelock == null) break block15;
                }
                catch (Throwable throwable) {
                    if (filelock != null) {
                        try {
                            filelock.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                filelock.close();
            }
            return bl;
        }
        catch (AccessDeniedException accessdeniedexception) {
            return true;
        }
        catch (NoSuchFileException nosuchfileexception) {
            return false;
        }
    }

    static {
        byte[] abyte = "\u2603".getBytes(Charsets.UTF_8);
        field_232996_c_ = ByteBuffer.allocateDirect(abyte.length);
        field_232996_c_.put(abyte);
        ((Buffer)field_232996_c_).flip();
    }

    public static class AlreadyLockedException
    extends IOException {
        private AlreadyLockedException(Path p_i231438_1_, String p_i231438_2_) {
            super(String.valueOf(p_i231438_1_.toAbsolutePath()) + ": " + p_i231438_2_);
        }

        public static AlreadyLockedException func_233000_a_(Path p_233000_0_) {
            return new AlreadyLockedException(p_233000_0_, "already locked (possibly by other Minecraft instance?)");
        }
    }
}

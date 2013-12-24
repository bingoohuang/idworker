package org.n3r.idworker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;

/**
 * A file lock a la flock/funlock
 * <p/>
 * The given path will be created and opened if it doesn't exist.
 */
public class FileServerLock {
    private final File file;
    private FileChannel channel;
    private java.nio.channels.FileLock flock = null;
    Logger logger = LoggerFactory.getLogger(FileServerLock.class);

    public FileServerLock(File file) {
        this.file = file;

        try {
            file.createNewFile(); // create the file if it doesn't exist
            channel = new RandomAccessFile(file, "rw").getChannel();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setIndex(long index) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putLong(index).flip();
        try {
            channel.position(0);
            channel.write(buf);
            channel.force(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long getIndex() {
        ByteBuffer buf = ByteBuffer.allocate(8);
        try {
            if (channel.size() >= 8) {
                channel.read(buf);
                return buf.getLong(0);
            } else {
                buf.putLong(0L).flip();
                channel.write(buf);
                channel.force(false); // Force them out to the diskA
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }


    /**
     * Try to lock the file and return true if the locking succeeds
     */
    public boolean tryLock() {
        synchronized (this) {
            logger.trace("Acquiring lock on {}", file.getAbsolutePath());
            try {
                // weirdly this method will return null if the lock is held by another
                // process, but will throw an exception if the lock is held by this process
                // so we have to handle both cases
                flock = channel.tryLock();
                return flock != null;
            } catch (OverlappingFileLockException e) {
                return false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

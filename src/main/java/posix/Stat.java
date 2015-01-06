package posix;
import java.io.IOException;

/** File status record for posix systems.  The cross-platform features
  of java.io.File do not cover everything available on posix systems.
  @author Stuart D. Gathman
  Copyright 2002 Business Management Systems, Inc
 */
public class Stat {
  private static native void init();
  static {
    System.loadLibrary("posix");
    init();
  }

  /** Create a blank Stat record. */
  public Stat() { }

  /** Create a Stat record for the named file.
    @param path  a posix compliant path name for the file
   */
  public Stat(String path) throws IOException {
    this(path,true);
  }
  /** Create a Stat record for the named file.
    @param path  a posix compliant path name for the file
    @param follow  follow symlinks if true
   */
  public Stat(String path,boolean follow) throws IOException {
    int rc = follow ? stat(path) : lstat(path);
    if (rc != 0)
      throw new IOException(path+": "+Errno.getErrdesc(rc));
  }
  /** ID of device containing a directory entry for this file. */
  public int dev;
  /** File serial number. */
  public int ino;
  /** File mode. */
  public int mode;
  /** Number of links. */
  public int nlink;
  /** User ID of the file's owner */
  public int uid;
  /** Group ID of the file's group */
  public int gid;
  /** ID of device if special file. */
  public int rdev;
  /** File size in bytes. */
  public long size;
  /** Time of last access */
  public long atime;
  /** Time of last data modification */
  public long mtime;
  /** Time of last file status change */
  public long ctime;
  /** Optimal blocksize for filesystem. */
  public int	blksize;
  /** Actual number of blocks allocated. */
  public long blocks;
  /** Fill in fields from a file path.  The fields are filled with
   information about the file.
   @return 0 on success or errno on failure
   */
  public native int stat(String path);
  /** Fill in fields from a file path.  Like {@link posix.Stat#stat}, except
   that if the path refers to a symbolic link, the information for the link
   is retrieved instead of the file it points to.
   @return 0 on success or errno on failure
   */
  public native int lstat(String path);

  /** Set the access and modification times of a file.  This calls
   the posix <code>utimes()</code> function to set the times to 
   the millisecond if available, otherwise it calls <code>utime()</code>.
   @param path	the pathname of the file
   @param mtime	the modification time in Java milliseconds
   @param atime the access time in Java milliseconds
   @since 1.2.1
   @return 0 on success or errno on failure
   */
  public static native int utime(String path,long mtime,long atime);

  /** Set the user and group of a file.  This is probably not
   that useful in Java since it requires a privileged process.
   @param path  the pathname of the file
   @param uid	the new user id
   @param gid	the new group id
   @return 0 on success or errno on failure
   @since 1.2.1
   */
  public static native int chown(String path,int uid, int gid);

  /** Set the user and group of a file.
   @param path  the pathname of the file
   @param mode	the new posix permission mask
   @return 0 on success or errno on failure
   @since 1.2.1
   */
  public static native int chmod(String path,int mode);

  /** Test posix file modes.
    @param mode	the posix file mode
    @param what	one of LNK,REG,DIR,CHR,BLK,FIFO,SOCK
    @since 1.1.9
   */
  public static native boolean S_IS(int what,int mode);
  public static final int 
    LNK = 0, REG = 1, DIR = 2, CHR = 3, BLK = 4, FIFO = 5, SOCK = 6;

  /** True if Stat is for a symbolic link. @since 1.1.9 */
  public final boolean isLNK() { return S_IS(LNK,mode); }
  /** True if Stat is for a regular file. @since 1.1.9 */
  public final boolean isREG() { return S_IS(REG,mode); }
  /** True if Stat is for a directory. @since 1.1.9 */
  public final boolean isDIR() { return S_IS(DIR,mode); }
  /** True if Stat is for a character device. @since 1.1.9 */
  public final boolean isCHR() { return S_IS(CHR,mode); }
  /** True if Stat is for a block device. @since 1.1.9 */
  public final boolean isBLK() { return S_IS(BLK,mode); }
  /** True if Stat is for a unix pipe. @since 1.1.9 */
  public final boolean isFIFO() { return S_IS(FIFO,mode); }
  /** True if Stat is for a unix socket. @since 1.1.9 */
  public final boolean isSOCK() { return S_IS(SOCK,mode); }

  /** Set the process file creation mask.  Bits in this mask clear
    corresponding unix permissions when creating a new file.  This
    unix concept is not very Thread friendly, of course, so you'll need to 
    do something like:
<pre>
    synchronized (Stat.class) {
      int oldmask = Stat.umask(newmask);
      createFile();
      Stat.umask(oldmask);
    }
</pre>
   @return the previous value
  */
  public static native int umask(int mask);

  /** Return the current process file creation mask. */
  public synchronized static native int umask();

}

package posix;
import java.io.IOException;

/** Extend java.io.File with posix features.
  @author Stuart D. Gathman
  Copyright 2002 Business Management Systems, Inc
 */
public class File extends java.io.File {
  public File(String name) { super(name); }
  public File(String dir,String name) { super(dir,name); }
  public File(java.io.File dir,String name) { super(dir,name); }
  private final Stat s = new Stat();

  private Stat tryStat() {
    /* Fields remain 0 if stat fails. */
    s.stat(getAbsolutePath());
    return s;
  }

  /** Return the posix last accessed time (atime).
   @return the accessed time in milliseconds since 1970
   */
  public long lastAccessed() { return tryStat().atime; }

  /** Set the posix last accessed time (atime).
   @param atime the new last accessed time as Java milliseconds since 1970
   @return true iff the operation succeeded
   @since 1.2.1
   */
  public boolean setLastAccessed(long atime) {
    final String path = getAbsolutePath();
    s.stat(path);
    return Stat.utime(path,atime,s.mtime) == 0;
  }

  /** Set last accessed and last modified times.
   @throws IOException on failure
   @since 1.2.1
   */
  public void setTimes(long atime,long mtime) throws IOException {
    String path = getAbsolutePath();
    int rc = Stat.utime(path,atime,mtime);
    if (rc != 0) throw new IOException(String.format(
      "utime(%s): %s",path,Errno.getErrdesc(rc)));
  }

  /** Return the posix last changed time (ctime).
   @return the changed time in milliseconds since 1970
   */
  public long lastChanged() { return tryStat().ctime; }
  /** Return the posix file mode.
   @return a bitmask of posix file permissions and type
   */
  public int getMode() { return tryStat().mode; }

  /** Set the posix file permission bits.  Only the low order 12 bits
    corresponding to classic posix permisions are set.
   @param mode the new file permission bits
   @throws IOException on failure
   @since 1.2.1
   */
  public void setMode(int mode) throws IOException {
    String path = getAbsolutePath();
    int rc = Stat.chmod(path,mode);
    if (rc != 0) throw new IOException(String.format(
      "chmod(%s): %s",path,Errno.getErrdesc(rc)));
  }

  /** Set the posix file owner and group.  
   @param uid the new file owner
   @param gid the new file group
   @throws IOException on failure
   @since 1.2.1
   */
  public void setOwner(int uid,int gid) throws IOException {
    String path = getAbsolutePath();
    int rc = Stat.chown(path,uid,gid);
    if (rc != 0) throw new IOException(String.format(
      "chown(%s): %s",path,Errno.getErrdesc(rc)));
  }

  /** Return the posix Stat record for the file.
   @return the posix Stat record or null 
   @throws IOException on failure
   */
  public Stat getStat() throws IOException {
    return new Stat(getAbsolutePath());
  }
}

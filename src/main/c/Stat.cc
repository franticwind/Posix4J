#include "posix_Stat.h"
#include <errno.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <utime.h>
#include <unistd.h>

class jFile_stat {
  jfieldID dev,ino,mode,nlink,uid,gid,rdev,size,atime,mtime,ctime,
  	blksize,blocks;
public:
  void init(JNIEnv *env,jclass c) {
    dev = env->GetFieldID(c,"dev","I");
    ino = env->GetFieldID(c,"ino","I");
    mode = env->GetFieldID(c,"mode","I");
    nlink = env->GetFieldID(c,"nlink","I");
    uid = env->GetFieldID(c,"uid","I");
    gid = env->GetFieldID(c,"gid","I");
    rdev = env->GetFieldID(c,"rdev","I");
    size = env->GetFieldID(c,"size","J");
    atime = env->GetFieldID(c,"atime","J");
    mtime = env->GetFieldID(c,"mtime","J");
    ctime = env->GetFieldID(c,"ctime","J");
    blksize = env->GetFieldID(c,"blksize","I");
    blocks = env->GetFieldID(c,"blocks","J");
  }
  void setstat(JNIEnv *env,const struct stat *p,jobject statbuf) {
    env->SetIntField(statbuf,dev,p->st_dev);
    env->SetIntField(statbuf,ino,p->st_ino);
    env->SetIntField(statbuf,mode,p->st_mode);
    env->SetIntField(statbuf,nlink,p->st_nlink);
    env->SetIntField(statbuf,uid,p->st_uid);
    env->SetIntField(statbuf,gid,p->st_gid);
    env->SetIntField(statbuf,rdev,p->st_rdev);
    env->SetLongField(statbuf,size,p->st_size);
    env->SetLongField(statbuf,atime,(long long)p->st_atime * 1000);
    env->SetLongField(statbuf,mtime,(long long)p->st_mtime * 1000);
    env->SetLongField(statbuf,ctime,(long long)p->st_ctime * 1000);
    env->SetIntField(statbuf,blksize,p->st_blksize);
    env->SetLongField(statbuf,blocks,p->st_blocks);
  }
};

static jFile_stat File_stat;

JNIEXPORT void JNICALL Java_posix_Stat_init
  (JNIEnv *env, jclass c) {
  File_stat.init(env,c);
}

JNIEXPORT jint JNICALL Java_posix_Stat_stat
  (JNIEnv *env, jobject statbuf, jstring s) {
  struct stat buf;
  const char *path = env->GetStringUTFChars(s,0);
  int rc = 0;
  if (stat(path,&buf))
    rc = errno;
  else
    File_stat.setstat(env,&buf,statbuf);
  env->ReleaseStringUTFChars(s,path);
  return rc;
}

JNIEXPORT jint JNICALL Java_posix_Stat_lstat
  (JNIEnv *env, jobject statbuf, jstring s)
{
  struct stat buf;
  const char *path = env->GetStringUTFChars(s,0);
  int rc = 0;
  if (lstat(path,&buf))
    rc = errno;
  else
    File_stat.setstat(env,&buf,statbuf);
  env->ReleaseStringUTFChars(s,path);
  return rc;
}

JNIEXPORT jint JNICALL Java_posix_Stat_utime
  (JNIEnv *env, jclass, jstring s, jlong mtime, jlong atime) {
  const char *path = env->GetStringUTFChars(s,0);
  int rc = 0;
#ifdef HAVE_UTIMES
  struct timeval u[2] = {
    { atime / 1000, (atime % 1000) * 1000 },
    { mtime / 1000, (mtime % 1000) * 1000 }
  };
  if (utimes(path,u))
    rc = errno;
#else
  struct utimbuf u = { atime/1000, mtime/1000 };
  if (utime(path,&u))
    rc = errno;
#endif
  env->ReleaseStringUTFChars(s,path);
  return rc;
}

JNIEXPORT jint JNICALL Java_posix_Stat_chown
  (JNIEnv *env, jclass, jstring s, jint uid, jint gid) {
  const char *path = env->GetStringUTFChars(s,0);
  int rc = chown(path,uid,gid) ? errno : 0;
  env->ReleaseStringUTFChars(s,path);
  return rc;
}

JNIEXPORT jint JNICALL Java_posix_Stat_chmod
  (JNIEnv *env, jclass, jstring s, jint mode) {
  const char *path = env->GetStringUTFChars(s,0);
  int rc = chmod(path,mode) ? errno : 0;
  env->ReleaseStringUTFChars(s,path);
  return rc;
}

JNIEXPORT jboolean JNICALL Java_posix_Stat_S_1IS
  (JNIEnv *env, jclass, jint what, jint mode) {
  switch (what) {
    case posix_Stat_LNK: return S_ISLNK(mode) != 0;
    case posix_Stat_REG: return S_ISREG(mode) != 0;
    case posix_Stat_DIR: return S_ISDIR(mode) != 0;
    case posix_Stat_CHR: return S_ISCHR(mode) != 0;
    case posix_Stat_BLK: return S_ISBLK(mode) != 0;
    case posix_Stat_FIFO: return S_ISFIFO(mode) != 0;
    case posix_Stat_SOCK: return S_ISSOCK(mode) != 0;
  }
  jclass exclass = env->FindClass("java/lang/IllegalArgumentException");
  if (exclass != 0) {
    char buf[80];
    sprintf(buf,"Invalid mode predicate: %d", what);
    env->ThrowNew(exclass,buf);
    // not much we can do if ThrowNew fails...
  }
  return false;
}

JNIEXPORT jint JNICALL Java_posix_IPC_umask__I
  (JNIEnv *, jclass, jint mask) { return umask(mask); }

JNIEXPORT jint JNICALL Java_posix_IPC_umask__
  (JNIEnv *, jclass) {
#if 0 
    /* GNU_C extension not available within JVM because it uses
      thread locking that doesn't play with JVM threads. */
    return getumask();	
#else
    /* This old fashioned unix version is not thread safe, but we
       mark the Java wrapper as synchronized. */
    int old = umask(0);
    umask(old);
    return old;
#endif
  }


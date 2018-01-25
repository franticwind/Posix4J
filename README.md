# Posix4J
Project based on Posix for Java http://www.bmsi.com/java/posix/
###2、C++库编译
&nbsp;&nbsp;&nbsp;&nbsp;下面介绍下具体的用法（github上没有readme，没有任何说明文档，估计大家看着都不敢试了）。
&nbsp;&nbsp;&nbsp;&nbsp;首先是编译C++库，把代码上传到linux目标机器，执行make，生成posix.so。

###3、Java环境应用
&nbsp;&nbsp;&nbsp;&nbsp;posix.so重命名libposix.so，放到java依赖lib库下面（也可以放到jre库目录），启动脚本加载进去。
如下：
```
export LD_LIBRARY_PATH=$APP_HOME/lib:$LD_LIBRARY_PATH
```
&nbsp;&nbsp;&nbsp;&nbsp;集成java API代码，直接拷贝xx目录到项目源代码中（也可以自己编译成jar包引用进去），注意package路径不要变，固定posix，后面就可以直接调用API进行共享内存操作了，核心类SharedMem、CPtr。
&nbsp;&nbsp;&nbsp;&nbsp;主要API如下：
```
/** Copy bytes out of C memory into a Java byte array. */
public native void copyOut(int off,byte[] ba,int pos,int cnt);
/** Copy a Java byte array into C memory. */
public native void copyIn(int off,byte[] ba,int pos,int cnt);
public native byte getByte(int off);
public native void setByte(int off,byte val);
public native short getShort(int off);
public native void setShort(int off,short val);
public native int getInt(int off);
public native void setInt(int off,int val);
public native short getCShort(int off,int idx);
public native void setCShort(int off,int idx,short val);
public native int getCInt(int off,int idx);
public native void setCInt(int off,int idx,int val);
public short getCShort(int off) { return getCShort(off,0); }
public void setCShort(int off,short val ) { setCShort(off,0,val); }
public int getCInt(int off) { return getCInt(off,0); }
public void setCInt(int off,int val) { setCInt(off,0,val); }
```

Java测试Demo：
```
import posix.CPtr;
import posix.IPC;
import posix.IPCException;
import posix.SharedMem;

public class TestShm {

    public static void main(String[] args) {
        int ftok = IPC.ftok("/temp/testSharedMemory", 'R');
        int SHM_SIZE = 1024;

        try {
            SharedMem sharedMem = new SharedMem(ftok, SHM_SIZE, 0644 | IPC.IPC_CREAT);
            CPtr attach = sharedMem.attach();

            // 写数据
            for(int i = 0 ; i < SHM_SIZE ; i++ ) {
                attach.setByte(i, (byte)'A');
            }

            // 读数据
            for(int i = 0 ; i < SHM_SIZE ; i++ ) {
                System.out.print((char) attach.getByte(i));
            }
        } catch (IPCException e) {
            e.printStackTrace();
        }
    }
}
```

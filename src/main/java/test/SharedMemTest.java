package test;

import posix.CPtr;
import posix.IPC;
import posix.IPCException;
import posix.SharedMem;

public class SharedMemTest {
	public static final int SHM_SIZE = 1024;

	public static void main(String[] args) {
		int ftok = IPC.ftok("/temp/testSharedMemory", 'R');
		try {
			SharedMem sharedMem = new SharedMem(ftok, SHM_SIZE, 0644 | IPC.IPC_CREAT);
			CPtr attach = sharedMem.attach();
			for(int i = 0 ; i < SHM_SIZE ; i++ ) System.out.print((char)attach.getByte(i));
			byte[] ba = new byte[4096];
			attach.copyOut(0, ba, 0, 0);
			System.out.println(new String(ba));
		} catch (IPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

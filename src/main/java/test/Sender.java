package test;

import posix.IPC;
import posix.IPCException;
import posix.MsgQ;

public class Sender {

	public static void main(String[] args) {
		
		int msgflag = IPC.IPC_CREAT | 0600;
		int key = 1234;
		try {
			MsgQ msgQueue = new MsgQ(key,msgflag);
			String msg = "Hello World";
			int send = msgQueue.send(1, msg.getBytes(), msg.length(), IPC.IPC_NOWAIT);
			System.out.println("Resultado : " + send);
			msgQueue.finalize();
		} catch (IPCException e) {
			e.printStackTrace();
		}
	}
}

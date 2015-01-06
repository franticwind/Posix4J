package test;

import posix.IPC;
import posix.IPCException;
import posix.MsgQ;
/**
 * http://www.thegeekstuff.com/2010/08/ipcs-command-examples/
 * @author leogsilva
 *
 */
public class Receiver {

	public static void main(String[] args) {
		int msgflag = IPC.IPC_CREAT | 0600;
		int key = 1234;
		try {
			MsgQ msgQueue = new MsgQ(key,msgflag);
			int mtype = 0;
			byte[] arr = new byte[1000];
			int recv = msgQueue.recv(new int[] { 1 }, arr, mtype, IPC.IPC_NOWAIT);
			System.out.println("Mensagem e : " + new String(arr));
			System.out.println("Resultado : " + recv);
			msgQueue.finalize();
		} catch (IPCException e) {
			e.printStackTrace();
		}
	}
}

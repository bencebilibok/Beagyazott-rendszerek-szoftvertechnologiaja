package utilities;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class Runnable_Input extends UDP_Listener implements Runnable {
	private ByteBuffer buffer;
	private ArrayBlockingQueue<Job> communicator;
	String parentPrefix;

	public Runnable_Input(int port, ArrayBlockingQueue<Job> communicator,
			String prefix) throws IOException {
		super(port);
		buffer = ByteBuffer.allocate(1000);
		this.communicator = communicator;
		this.parentPrefix = prefix;
	}

	@Override
	public void run() {
		while (true) {
			try {
				InetSocketAddress remote = listen(buffer);// a buffer feltöltése
				buffer.flip();
				Job j = new Job(buffer, remote.getHostName());
				communicator.put(j);// a job küldése a kommunikátoron keresztül
				buffer.clear();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}

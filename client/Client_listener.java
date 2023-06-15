package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

// TESZT OK
public class Client_listener implements Runnable {
	// A szerver üríti a queue-t, mielőtt új elemet adna hozzá
	private ArrayBlockingQueue<Pair<HashMap<Byte, Snake>, Point>> gridJobs; //fix méretű blokkoló várósort definiál
																			//a kígyók között
	private DatagramChannel listenerChannel; //fogadó port
	private Client client; //kliens deklarálása
	private boolean dirStepLauncher = true; // a DIrectionManagement elindítására alkalmas

	// A szerver üríti a queue-t, mielőtt új elemet adna hozzá. Nem működik, mert csak egy szál tölti fel a queue-t
	protected Client_listener(ArrayBlockingQueue<Pair<HashMap<Byte, Snake>, Point>> jobs, short listeningPort,
							  Client c) {
		gridJobs = jobs;
		client = c;
		try {
			listenerChannel = DatagramChannel.open();
			listenerChannel.socket().bind(new InetSocketAddress(listeningPort));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			boolean gameOver = false;
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (true) {
				listenerChannel.receive(buffer);
				buffer.flip();
				byte type = buffer.get();
				switch (type) {
					case 0:
						if (client.receivedPortStep) {
							client.receivedPortStep = false;
							short gamePort = buffer.getShort();
							byte number = buffer.get();
							// Játékport = gamePort és kliens száma = number
							client.launchDisplayer(number);
							client.launchSpeaker(number, gamePort);
						}
						break;
					case 1:
						client.print("<HTML><h2>Kezdés " + buffer.get() + " másodperc múlva</h2></HTML>");
						break;
					case 2:
						if (dirStepLauncher) {
							client.launchManagerDirection();
							dirStepLauncher = false;
							client.print("");
						}
						readSneaks(buffer);
						break;
					case 3:
						if (!gameOver) {
							gameOver = true;
							client.print(readFinalBuffer(buffer));
						}
						break;
					default:
						throw new Exception("A játék szerver üzenete sérült");
				}
				buffer.clear();
			}
		} catch (Exception e) {
		}
	}

	private String readFinalBuffer(ByteBuffer buffer) {
		String s = "<HTML><h2>A játék véget ért, itt vannak az eredmények:</h2>";
		byte nbSnakes = buffer.get();
		for (int i = 0; i < nbSnakes; i++) {
			byte num = buffer.get();
			short score = buffer.getShort();
			s += "<h3>A(z) " + num + ". kígyó " + score + " pontot ért el</h3>";
		}
		return s + "</HTML>";
	}

	private void readSneaks(ByteBuffer buffer) throws Exception {
		Pair<HashMap<Byte, Snake>, Point> req = decodeBufferToGame(buffer);
		// Itt töröljük az összes elemet a queue-ból, csak az utolsót vesszük figyelembe. Működik, mert csak egy szál tölti fel a queue-t
		while (gridJobs.size() > 0)
			gridJobs.poll();
		gridJobs.put(req);
	}

	// decode függvény
	private static Pair<HashMap<Byte, Snake>, Point> decodeBufferToGame(ByteBuffer buf) throws Exception {
		HashMap<Byte, Snake> snakes = new HashMap<Byte, Snake>();
		try {
			byte nbSnakes = buf.get();
			for (int i = 0; i < nbSnakes; i++) {
				byte numSnake = buf.get();
				LinkedList<Point> curSnake = new LinkedList<Point>();
				Point cur = new Point(buf.get(), buf.get());
				curSnake.add(cur);
				byte nbDir = buf.get();
				byte dir = -1;
				for (int j = 0; j < nbDir; j++) {
					dir = buf.get();
					byte length = buf.get();
					int k = j == 0 ? 1 : 0;
					for (; k < length; k++) {
						Point tmp = new Point(cur.x + (dir % 2 == 0 ? dir - 1 : 0),
								cur.y + (dir % 2 == 1 ? dir - 2 : 0));
						cur = tmp;
						curSnake.add(cur);
					}
				}
				Snake c = new Snake(dir, numSnake, curSnake);
				snakes.put(numSnake, c);
			}
			Point APPLE = new Point(buf.get(), buf.get());
			return new Pair<HashMap<Byte, Snake>, Point>(snakes, APPLE);
		} catch (Exception e) {
		}
		throw new Exception("A szerver üzenete sérült");
	}
}
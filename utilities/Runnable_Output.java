package utilities;

import game.G_Manager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class Runnable_Output extends UDP_Sender implements Runnable {
	private ByteBuffer buffer;
	private ArrayBlockingQueue<Job> communicator;
	String parentPrefix;
	G_Manager manager;

	public Runnable_Output(String address, int port,
			ArrayBlockingQueue<Job> communicator, String prefix,
			G_Manager manager) throws IOException {
		super(address, port);
		buffer = null;
		this.communicator = communicator;
		this.parentPrefix = prefix;
		this.manager = manager;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Job j = this.communicator.take();
				switch (j.type()) {
				case SEND_GAME_INFO:
					while (true) {
						buffer = BufferHandler.goPlayThere(j.port(), j.id());
						buffer.flip();
						this.send(buffer);
						Thread.sleep(10);
						if (!manager.sendGameInfo)
							break;
					}
					break;
				
				case SEND_TIMER:
					buffer=BufferHandler.sendTimer(j.timer());
					buffer.flip();
					this.send(buffer);
					break;

				case SEND_POSITIONS:
					while (!manager.gameOver) {
						synchronized (this.manager.thisGame.snakes) {
							buffer = Snake
									.encodeAllSnakes(this.manager.thisGame.snakes);
							buffer.put(this.manager.thisGame.apple.toBuffer());
						}
						buffer.flip();
						this.send(buffer);

						Thread.sleep(100);

					}
					break;
				case SEND_SCORES:
					while (manager.sendScore) {
						buffer=BufferHandler.sendScores(j.snakes);
						buffer.flip();
						this.send(buffer);

						Thread.sleep(100);

					}
					break;
				default:
					break;

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				break;
				
			}
		}

	}
}
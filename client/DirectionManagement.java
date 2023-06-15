package client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;

public class DirectionManagement implements Runnable {
	private BlockingDeque<Pair<Byte, Byte>> directionJobs;
	private ArrayBlockingQueue<Byte> dir;
	private byte direction, id;

	public DirectionManagement(BlockingDeque<Pair<Byte, Byte>> jobs, ArrayBlockingQueue<Byte> dir) {
		id = 0;
		directionJobs = jobs;
		this.dir = dir;
	}

	@Override
	public void run() {
		while (!dir.isEmpty()) {
			try {
				dir.take();
			} catch (InterruptedException e1) {
			}
		}
		while (true) {
			try {
				byte a = dir.take();
				addDirection(a);
			} catch (InterruptedException e) {
			}
		}
	}

	public void setDirection(byte directionBis) {
		direction = directionBis;
		if (!directionJobs.isEmpty()) {
			byte maDirection = directionJobs.element().a;
			if (direction == maDirection)
				directionJobs.remove();
		}
	}

	void addDirection(byte a) throws InterruptedException {
		if (directionJobs.isEmpty()) {
			if (direction % 2 != a % 2) {
				directionJobs.put(new Pair<Byte, Byte>(a, id));
				id++;
				synchronized (directionJobs) {
					directionJobs.notify();
				}
			}
			return;
		}
		byte lastDir = directionJobs.peekLast().a;
		if (lastDir % 2 != a % 2) {
			directionJobs.put(new Pair<Byte, Byte>(a, id));
			id++;
		}
	}
}

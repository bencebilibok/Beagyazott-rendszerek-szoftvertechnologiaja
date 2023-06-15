package client;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

// OK : új rács kiszámítása, amelyet helyettesíteni kell a kígyó hashmapokból
class BackGridManagement implements Runnable {
	private ArrayBlockingQueue<Pair<HashMap<Byte, Snake>, Point>> gridJobs;
	private ManagementDisplay gameDisplay;
	private byte myNumber;
	private DirectionManagement dManagement;
	private final int size = utilities.GameOptions.gridSize;
	
	protected BackGridManagement(ArrayBlockingQueue<Pair<HashMap<Byte, Snake>, Point>> jobs, ManagementDisplay display, byte number, DirectionManagement gest){
		this.gridJobs = jobs;
		this.gameDisplay = display;
		this.myNumber = number;
		dManagement = gest;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Pair<HashMap<Byte, Snake>, Point> req = (Pair<HashMap<Byte, Snake>, Point>) gridJobs.take();
				// System.out.print("We received a package of snakes... ");
				byte[][] backgrid = calcBackGrid(req.a);
				backgrid[req.b.x][req.b.y] = ManagementDisplay.APPLE;
				gameDisplay.swap(backgrid);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private byte[][] calcBackGrid(HashMap<Byte, Snake> req) {
		byte[][] grid = new byte[size][size];
		for (Snake s : req.values())
			replace(s, grid);
		return grid;
	}
	
	private void replace(Snake s, byte[][] grid) {
		byte color = ManagementDisplay.FULL;
		if(s.number==myNumber){
			color = ManagementDisplay.PERSO;
			dManagement.setDirection(s.direction);
		}
		for (Point p : s.points)
			grid[p.x][p.y] = color;
	}
}

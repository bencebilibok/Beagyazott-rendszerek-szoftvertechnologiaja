package client;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

//OK: grafikus felület + billentyűzetkérések kezelése
@SuppressWarnings("serial") //segít figyelmen kívül hagyni, hogy nincs a Serializable ősosztály implementálva
public class ManagementDisplay extends JComponent implements KeyListener {
	protected static final byte EMPTY = 0, FULL = 1, APPLE = 2, PERSO = 3,
			cellSize = 10; //
	private byte[][] grid; //a rácsot egy másik szál számítja ki
	private JFrame graph; //a GUI-t biztosító JFRAME osztály változója
	private ArrayBlockingQueue<Byte> demandDir;
	private JLabel label = new JLabel(); //label a hátralévő másodpercek mutatására a játék kezdetéig
	private final int size = utilities.GameOptions.gridSize;

	protected void swap(byte[][] backgrid) {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					grid = backgrid;
					paintImmediately(0, 0, getWidth(), getHeight());
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected ManagementDisplay(String serverName, int num, ArrayBlockingQueue<Byte> dir) {
		grid = new byte[size][size];
		setgraph(serverName, num);
		demandDir = dir;
	}

	private void setgraph(String serverName, int num) {
		graph = new JFrame("TÖBBJÁTÉKOS KÍGYÓ JÁTÉK A '"
				+ serverName.toUpperCase() + "'-EN. " +
				"TE AZ " + num + ". JÁTÉKOS VAGY");
		addKeyListener(this);
		graph.setBounds(100, 25, (size+1) * cellSize, (size + 3) * cellSize);
		label.setBounds(size * cellSize/4,size * cellSize/2, size * cellSize, size * cellSize/4);
		graph.add(label);
		graph.add(this);
		setFocusable(true);
		requestFocusInWindow();
		graph.setVisible(true);
		setFocusable(true);
	}

	@Override
	public void paintComponent(Graphics g) {
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				fill(g, i, j, grid[i][j]);
	}

	private void fill(Graphics g, int i, int j, byte color) {
		g.setColor((color == EMPTY) ? Color.WHITE
				: (color == FULL) ? Color.BLACK : (color == APPLE) ? Color.RED
						: (color == PERSO) ? Color.blue : Color.gray);
		g.fillRect(i * cellSize + cellSize / 16, j * cellSize + cellSize / 16,
				cellSize - cellSize / 8, cellSize - cellSize / 8);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		byte a = (byte) (e.getKeyCode()-37);
		if(a>=0 && a<4)
			try{
				demandDir.add(a);
			}catch(IllegalStateException t){
			}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	public void print(String string) {
		paintImmediately(0, 0, getWidth(), getHeight());
		label.setText(string);
	}
}

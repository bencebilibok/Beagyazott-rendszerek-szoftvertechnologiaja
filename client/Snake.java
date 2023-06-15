package client;
import java.awt.Color;
import java.util.LinkedList;

// A Snake osztály a kígyókat egy pontlistára kódolja
public class Snake {
	public byte direction;//a kígyó iránya
	byte number;
	LinkedList<Point> points;//a kígyó hossza

	public Snake(byte direction, byte number, LinkedList<Point> points){
		this.direction=direction;
		this.number=number;
		this.points=points;
	}
	
	@Override
	public String toString(){
		String s = "Snake " + number + " [";
		for (Point p : points) {
			s += p.toString();
			s += ",";
		}
		s += "] goes " + direction;
		return s;
	}
}

// A pont két koordináta és egy szín
class Point {
	int x, y;
	Color color;
	private final int size = utilities.GameOptions.gridSize;

	Point(int x, int y) {
		while (x < 0)
			x += size;
		while (y < 0)
			y += size;
		this.x = x % size;
		this.y = y % size;
		color = Color.white;
	}
	
	@Override
	public String toString(){
		return "("+x+","+y+")";
	}
}
package solver;

public class Coordinate {
	private int x;
	private int y;

	Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	int getX() { return this.x; }
	int getY() { return this.y; }
	int[] getArr() {
		int[] res = {x, y};
		return res;
	}

	/**
	 * Calculates the squared distance between these two points.
	 * No square roots are used to avoid possibly expensive calculations
	 * @param other		Other point
	 * @return			Distance squared
	 */
	public int manhattanDist(int x, int y) {
		
		int absX = Math.abs(this.x - x);
		int absY = Math.abs(this.y - y);
		return absX + absY;
	}

}
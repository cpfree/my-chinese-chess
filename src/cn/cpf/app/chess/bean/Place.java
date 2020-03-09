package cn.cpf.app.chess.bean;

public class Place {
	
	public final int x;
	public final int y;

	public Place(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}
	
}

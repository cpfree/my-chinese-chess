package cn.cpf.app.chess.res;

/**
 * <b>Description : </b> 棋盘位置类, 藐视棋盘的位置信息
 *
 * @author CPF
 * @date 2020/3/18
 **/
public class Place {

	private static Place[][] placePool;

	static {
		placePool = new Place[ChessDefined.RANGE_X][ChessDefined.RANGE_Y];
		for (int x = 0; x < ChessDefined.RANGE_X; x++){
			for (int y = 0; y < ChessDefined.RANGE_Y; y++){
				placePool[x][y] = new Place(x, y);
			}
		}
	}

	public static Place of(int x, int y){
		return placePool[x][y];
	}

	public final int x;
	public final int y;

	private Place(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}

	public boolean equals(Place obj) {
		return this.x == obj.x && this.y == obj.y;
	}
}

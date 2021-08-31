package cn.cpf.app.chess.modal;

import cn.cpf.app.chess.conf.ChessDefined;

/**
 * <b>Description : </b> 棋盘坐标类, 藐视棋盘的位置信息
 * <p>
 * <b>created in </b> 2020/3/18
 *
 * @author CPF
 * @since 0.1
 **/
public class Place {

    public static final Place NULL_PLACE = new Place(-1, -1);
    /**
     * 对应棋盘坐标对象池
     */
    private static final Place[][] placePool;

    static {
        // 初始化即开始建立棋盘上所有的位置坐标对象
        placePool = new Place[ChessDefined.RANGE_X][ChessDefined.RANGE_Y];
        for (int x = 0; x < ChessDefined.RANGE_X; x++) {
            for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
                placePool[x][y] = new Place(x, y);
            }
        }
    }

    public final int x;
    public final int y;

    private Place(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @param x 棋盘x坐标(从0-9)
     * @param y 棋盘y坐标(从0-10)
     * @return 对应棋盘坐标对象
     */
    public static Place of(int x, int y) {
        return placePool[x][y];
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

}

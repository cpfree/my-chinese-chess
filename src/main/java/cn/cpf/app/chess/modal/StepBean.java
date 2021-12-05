package cn.cpf.app.chess.modal;

import cn.cpf.app.chess.conf.ChessDefined;

/**
 * <b>Description : </b> 记录要走的一步棋
 *
 * @author CPF
 * Date: 2020/3/25 17:38
 */
public class StepBean {

    /**
     * 对应棋盘坐标对象池
     */
    private static final StepBean[][][][] stepBeanPool;

    static {
        // 初始化即开始建立棋盘上所有的位置坐标对象
        stepBeanPool = new StepBean[ChessDefined.RANGE_X][ChessDefined.RANGE_Y][ChessDefined.RANGE_X][ChessDefined.RANGE_Y];
        for (int x = 0; x < ChessDefined.RANGE_X; x++) {
            for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
                for (int j = 0; j < ChessDefined.RANGE_X; j++) {
                    for (int k = 0; k < ChessDefined.RANGE_Y; k++) {
                        stepBeanPool[x][y][j][k] = new StepBean(Place.of(x, y), Place.of(j, k));
                    }
                }
            }
        }
    }

    public final Place from;
    public final Place to;

    private StepBean(Place from, Place to) {
        this.from = from;
        this.to = to;
    }

    /**
     * @param fx 棋盘 from棋子 x坐标(从0-9)
     * @param fy 棋盘 from棋子 y坐标(从0-10)
     * @param tx 棋盘 to棋子 x坐标(从0-9)
     * @param ty 棋盘 to棋子 y坐标(从0-10)
     * @return 对应棋盘坐标对象
     */
    public static StepBean of(int fx, int fy, int tx, int ty) {
        return stepBeanPool[fx][fy][tx][ty];
    }

    /**
     * @param from 棋盘 from棋子 位置
     * @param to   棋盘 from棋子 位置
     * @return 对应棋盘坐标对象
     */
    public static StepBean of(Place from, Place to) {
        return stepBeanPool[from.x][from.y][to.x][to.y];
    }

    @Override
    public String toString() {
        return "StepBean{" + from + " -> " + to + '}';
    }

}

package cn.cpf.app.chess.res;

import java.awt.*;

/**
 * <b>Description : </b> 对尺寸坐标做一个接口定义, 相关参数直接根据测量或者计算得到
 *
 * @author CPF
 * Date: 2020/3/18 13:28
 */
public interface ChessDefined {

    /**
     * 棋盘X轴范围
     */
    int RANGE_X = 8;
    /**
     * 棋盘Y轴范围
     */
    int RANGE_Y = 9;

    /**
     * 棋盘尺寸
     */
    Dimension boardRect = new Dimension(530, 606);

    /**
     * 棋子宽度和高度
     */
    int PIECE_WIDTH = 56;

    /**
     * 棋盘 x 坐标初始位置
     */
    int X_INIT = 30 - PIECE_WIDTH / 2;

    /**
     * 棋盘 y 坐标初始位置
     */
    int Y_INIT = 28 - PIECE_WIDTH / 2;

    /**
     * 网格宽度(测量宽度为57.5)
     */
    int GRID_INTERVAL = 57;

    /**
     * 根据所属Place位置得到棋子相应坐标
     */
    static Point convertPlaceToLocation(int x, int y) {
        return new Point(X_INIT + x * GRID_INTERVAL, Y_INIT + y * GRID_INTERVAL);
    }

    /**
     * 根据指定位置的点找到所属Place位置
     */
    static Place convertLocationToPlace(Point point) {
        return Place.of((point.x - X_INIT) / GRID_INTERVAL, (point.y - Y_INIT) / GRID_INTERVAL);
    }

}

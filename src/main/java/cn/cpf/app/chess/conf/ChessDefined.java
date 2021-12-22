package cn.cpf.app.chess.conf;

import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.swing.ChessPiece;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
    int RANGE_X = 9;
    /**
     * 棋盘Y轴范围
     */
    int RANGE_Y = 10;
    /**
     * 棋盘尺寸
     */
    Dimension boardRect = new Dimension(536, 638);
    /**
     * 棋子宽度和高度
     */
    int PIECE_WIDTH = 56;
    /**
     * 棋子宽度和高度
     */
    int PIECE_HEIGHT = 56;
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
        final int x = (point.x - X_INIT) / GRID_INTERVAL;
        final int y = (point.y - Y_INIT) / GRID_INTERVAL;
        if (x >= ChessDefined.RANGE_X || y >= ChessDefined.RANGE_Y) {
            return null;
        }
        return Place.of(x, y);
    }

    /**
     * 获取默认的一套棋盘棋子配置
     */
    static java.util.List<ChessPiece> geneDefaultPieceSituation() {
        List<ChessPiece> list = new ArrayList<>(32);
        // 添加红色棋子
        list.add(new ChessPiece("红車1", Piece.RED_CAR, Place.of(0, 9)));
        list.add(new ChessPiece("红馬1", Piece.RED_HORSE, Place.of(1, 9)));
        list.add(new ChessPiece("红相1", Piece.RED_ELEPHANT, Place.of(2, 9)));
        list.add(new ChessPiece("红仕1", Piece.RED_COUNSELOR, Place.of(3, 9)));
        list.add(new ChessPiece("红帅", Piece.RED_BOSS, Place.of(4, 9)));
        list.add(new ChessPiece("红仕2", Piece.RED_COUNSELOR, Place.of(5, 9)));
        list.add(new ChessPiece("红相2", Piece.RED_ELEPHANT, Place.of(6, 9)));
        list.add(new ChessPiece("红馬2", Piece.RED_HORSE, Place.of(7, 9)));
        list.add(new ChessPiece("红車2", Piece.RED_CAR, Place.of(8, 9)));

        list.add(new ChessPiece("红砲1", Piece.RED_CANNON, Place.of(1, 7)));
        list.add(new ChessPiece("红砲2", Piece.RED_CANNON, Place.of(7, 7)));
        list.add(new ChessPiece("红兵1", Piece.RED_SOLDIER, Place.of(0, 6)));
        list.add(new ChessPiece("红兵2", Piece.RED_SOLDIER, Place.of(2, 6)));
        list.add(new ChessPiece("红兵3", Piece.RED_SOLDIER, Place.of(4, 6)));
        list.add(new ChessPiece("红兵4", Piece.RED_SOLDIER, Place.of(6, 6)));
        list.add(new ChessPiece("红兵5", Piece.RED_SOLDIER, Place.of(8, 6)));

        // 添加黑色棋子
        list.add(new ChessPiece("黑车1", Piece.BLACK_CAR, Place.of(0, 0)));
        list.add(new ChessPiece("黑马1", Piece.BLACK_HORSE, Place.of(1, 0)));
        list.add(new ChessPiece("黑象1", Piece.BLACK_ELEPHANT, Place.of(2, 0)));
        list.add(new ChessPiece("黑士1", Piece.BLACK_COUNSELOR, Place.of(3, 0)));
        list.add(new ChessPiece("黑将", Piece.BLACK_BOSS, Place.of(4, 0)));
        list.add(new ChessPiece("黑士2", Piece.BLACK_COUNSELOR, Place.of(5, 0)));
        list.add(new ChessPiece("黑象2", Piece.BLACK_ELEPHANT, Place.of(6, 0)));
        list.add(new ChessPiece("黑马2", Piece.BLACK_HORSE, Place.of(7, 0)));
        list.add(new ChessPiece("黑车2", Piece.BLACK_CAR, Place.of(8, 0)));

        list.add(new ChessPiece("黑炮1", Piece.BLACK_CANNON, Place.of(1, 2)));
        list.add(new ChessPiece("黑炮2", Piece.BLACK_CANNON, Place.of(7, 2)));
        list.add(new ChessPiece("黑卒1", Piece.BLACK_SOLDIER, Place.of(0, 3)));
        list.add(new ChessPiece("黑卒2", Piece.BLACK_SOLDIER, Place.of(2, 3)));
        list.add(new ChessPiece("黑卒3", Piece.BLACK_SOLDIER, Place.of(4, 3)));
        list.add(new ChessPiece("黑卒4", Piece.BLACK_SOLDIER, Place.of(6, 3)));
        list.add(new ChessPiece("黑卒5", Piece.BLACK_SOLDIER, Place.of(8, 3)));
        return list;
    }

}

package cn.cpf.app.chess.conf;

import cn.cpf.app.chess.algorithm.Role;
import com.github.cosycode.common.lang.ShouldNotHappenException;

/**
 * <b>Description : </b>
 * <p>
 * 1. 每个棋子本身的价值
 * 3. 棋子的地理优势
 * 2. 棋子评分
 * 1. 棋子嘴边多少food
 * 2. 棋子相关的点, 有影响的有多少点
 * <p>
 * <p>
 * 给定俩个 int[][] 来存放一个局势的东西.
 *
 * @author CPF
 * Date: 2020/3/25 11:44
 */
public interface Score {

//    BOSS, COUNSELOR, ELEPHANT, CAR, CANNON, HORSE, SOLDIER;

    /**
     * 棋子子力 位置 分值
     */
    enum PiecePlaceScore {

    }


    default int getPieceScore(Role role) {
        switch (role) {
            case SOLDIER:
                return 130;
            case COUNSELOR:
            case ELEPHANT:
                return 200;
            case CANNON:
            case HORSE:
                return 450;
            case CAR:
                return 950;
            case BOSS:
                return 65536;
        }
        throw new ShouldNotHappenException();
    }


    default int[][] getPieceLocationValueArray(Role role) {
        switch (role) {
            case SOLDIER:
                return new int[][]{
                        {1, 3, 9, 10, 12, 10, 9, 3, 1},
                        {18, 36, 56, 95, 118, 95, 56, 36},
                        {15, 28, 42, 73, 80, 73, 42, 28, 15},
                        {13, 22, 90, 42, 52, 42, 30, 22, 13},
                        {8, 17, 18, 21, 26, 21, 18, 17, 8},
                        {3, 0, 7, 0, 8, 0, 7, 0, 3},
                        {-1, 0, -3, 0, 3, 0, -3, 0, -1}
                };
            case COUNSELOR:

        }
        throw new ShouldNotHappenException();
    }


    default int getPieceRunValue(Role role) {
        switch (role) {
            case SOLDIER:
                return 2;
            case COUNSELOR:
            case ELEPHANT:
                return 1;
            case CAR:
            case CANNON:
                return 7;
            case HORSE:
                return 13;
            case BOSS:
                return 0;
        }
        throw new ShouldNotHappenException();
    }


    int BOSS = 100;

    int SINGLE_SHI = 100;

    int DOUBLE_SHI = 120;

    int SINGLE_ELEPHANT = 150;

    int DOUBLE_ELEPHANT = 180;

    int SINGLE_PAO = 400;

    int SINGLE_MA = 400;

    int che = 1000;

    // 耦合与阻碍
    // 威慑与保护

    int BING = 100;

    int GUOHE_BING = 300;

}

package cn.cpf.app.chess.algorithm;

import cn.cpf.app.chess.modal.Part;

/**
 * <b>Description : </b>
 * <p>
 *    <br/> 1. 每个棋子本身的价值
 *    <br/> 2. 棋子的地理评分
 * </p>
 *
 * <p>
 * <b>created in </b> 2021/12/5
 * </p>
 *
 * @author CPF
 * @since 0.1
 **/
public enum PieceScore {

    BOSS(10000, new int[]{
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            1, -8, -9, 0, 0, 0, 0, 0, 0, 0,
            5, -8, -9, 0, 0, 0, 0, 0, 0, 0,
            1, -8, -9, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    }),
    COUNSELOR(200, new int[]{
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 3, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    }),
    ELEPHANT(200, new int[]{
            0, 0, -2, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 3, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, -2, 0, 0, 0, 0, 0, 0, 0
    }),
    CAR(950, new int[]{
            -6, 5, -2, 4, 8, 8, 6, 6, 6, 6,
            6, 8, 8, 9, 12, 11, 13, 8, 12, 8,
            4, 6, 4, 4, 12, 11, 13, 7, 9, 7,
            12, 12, 12, 12, 14, 14, 16, 14, 16, 13,
            0, 0, 12, 14, 15, 15, 16, 16, 33, 14,
            12, 12, 12, 12, 14, 14, 16, 14, 16, 13,
            4, 6, 4, 4, 12, 11, 13, 7, 9, 7,
            6, 8, 8, 9, 12, 11, 13, 8, 12, 8,
            -6, 5, -2, 4, 8, 8, 6, 6, 6, 6
    }),
    CANNON(450, new int[]{
            0, 0, 1, 0, -1, 0, 0, 1, 2, 4,
            0, 1, 0, 0, 0, 0, 3, 1, 2, 4,
            1, 2, 4, 0, 3, 0, 3, 0, 0, 0,
            3, 2, 3, 0, 0, 0, 2, -5, -4, -5,
            3, 2, 5, 0, 4, 4, 4, -4, -7, -6,
            3, 2, 3, 0, 0, 0, 2, -5, -4, -5,
            1, 2, 4, 0, 3, 0, 3, 0, 0, 0,
            0, 1, 0, 0, 0, 0, 3, 1, 2, 4,
            0, 0, 1, 0, -1, 0, 0, 1, 2, 4
    }),
    HORSE(450, new int[]{
            0, -3, 5, 4, 2, 2, 5, 4, 2, 2,
            -3, 2, 4, 6, 10, 12, 20, 10, 8, 2,
            2, 4, 6, 10, 13, 11, 12, 11, 15, 2,
            0, 5, 7, 7, 14, 15, 19, 15, 9, 8,
            2, -10, 4, 10, 15, 16, 12, 11, 6, 2,
            0, 5, 7, 7, 14, 15, 19, 15, 9, 8,
            2, 4, 6, 10, 13, 11, 12, 11, 15, 2,
            -3, 2, 4, 6, 10, 12, 20, 10, 8, 2,
            0, -3, 5, 4, 2, 2, 5, 4, 2, 2
    }),
    SOLDIER(130, new int[]{
            0, 0, 0, -1, 3, 8, 13, 15, 18, 1,
            0, 0, 0, 0, 0, 17, 22, 28, 36, 3,
            0, 0, 0, -3, 7, 18, 90, 42, 56, 9,
            0, 0, 0, 0, 0, 21, 42, 73, 95, 10,
            0, 0, 0, 3, 8, 26, 52, 80, 118, 12,
            0, 0, 0, 0, 0, 21, 42, 73, 95, 10,
            0, 0, 0, -3, 7, 18, 90, 42, 56, 9,
            0, 0, 0, 0, 0, 17, 22, 28, 36, 3,
            0, 0, 0, -1, 3, 8, 13, 15, 18, 1
    });

    /**
     * 存在分
     */
    public final int existScore;

    /**
     * 占位分数组
     */
    final int[] placeScores;

    PieceScore(int existScore, int[] placeScores) {
        this.existScore = existScore;
        this.placeScores = placeScores;
    }

    /**
     * @param part 棋盘方
     * @param x    坐标x
     * @param y    坐标y
     * @return 占位分数
     */
    public int getPlaceScore(Part part, int x, int y) {
        if (Part.RED == part) {
            return placeScores[x * 10 + 9 - y];
        } else {
            return placeScores[x * 10 + y];
        }
    }
}
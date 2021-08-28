package cn.cpf.app.chess.conf;

/**
 * <b>Description : </b>
 *
 * 1. 每个棋子本身的价值
 * 3. 棋子的地理优势
 * 2. 棋子评分
 * 	1. 棋子嘴边多少food
 * 	2. 棋子相关的点, 有影响的有多少点
 *
 *
 * 	给定俩个 int[][] 来存放一个局势的东西.
 *
 * @author CPF
 * Date: 2020/3/25 11:44
 */
public interface Score {

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

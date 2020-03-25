package cn.cpf.app.chess.algorithm;

import cn.cpf.app.chess.bean.ChessPiece;
import cn.cpf.app.chess.main.ChessConfig;
import cn.cpf.app.chess.res.ChessDefined;
import cn.cpf.app.chess.res.Part;
import cn.cpf.app.chess.res.Place;
import cn.cpf.app.chess.res.Role;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * @author CPF
 * 1.双方都按自己认为的最佳着法行棋.
 *
 * 对给定的盘面用一个分值来评估，这个评估值永远是从一方（搜索程序）来评价的，红方有利时给一个正数，黑方有利时给一个负数。
 * （通常把它称为Max）看来，分值大的数表示对己方有利，而对于对方Min来说，它会选择分值小的着法。
 *
 * 用Negamax风格来描述的AlphaBeta中的评估函数，对轮到谁走棋是敏感的。
 * 在Minimax风格的AlphaBeta算法中，轮红方走棋时，评估值为100，轮黑方走棋评估值仍是100。
 * 但在Negamax风格的AlphaBeta算法中，轮红方走棋时，评估值为100，轮黑方走棋时评估值要为-100。
 */
public class AlphaBeta {

	private static final int MAX = Integer.MAX_VALUE;
	private static final int MIN = - MAX;

	public class StepBean {
		Place from;
		Place to;

		public StepBean(Place from, Place to) {
			this.from = from;
			this.to = to;
		}
	}

	/**
	 *
	 * 奇数层是电脑(max层)thisSide, 偶数层是human(min层)otherSide
	 *
	 * @param pieces
	 * @param curPart
	 * @return
	 */
	public StepBean getEvaluatedPlace(ChessPiece[][] pieces, Part curPart){
		//搜索深度
		int deep = ChessConfig.deep;
		// 1. 初始化各个变量
		int best = MIN;
		HashSet<StepBean> bestPlace = new HashSet<>();
		// 2. 获取可以下子的空位列表
		// 生成待选的列表，就是可以下子的空位
		for (int x = 0; x < ChessDefined.RANGE_X; x++) {
			for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
				ChessPiece fromPiece = pieces[x][y];
				if (fromPiece != null && fromPiece.part == curPart) {
					Place from = Place.of(x, y);
					// list 排序
					List<Place> list = fromPiece.role.getRule().find(pieces, curPart, from);
					if (list.isEmpty()) {
						continue;
					}
					for (Place to : list) {
						// 备份
						ChessPiece backupToPiece = pieces[to.x][to.y];
						int score;
						// 判断是否胜利
						if (backupToPiece.role == Role.Boss) {
							score = MAX;
						} else {
							// 走棋
							pieces[to.x][to.y] = pieces[from.x][from.y];
							pieces[from.x][from.y] = null;
							// 评分
							score = negativeMaximum(pieces, Part.getOpposite(curPart), deep - 1, -best);
							// 退回上一步
							pieces[from.x][from.y] = pieces[to.x][to.y];
							pieces[to.x][to.y] = backupToPiece;
						}
						if (score == best) { // 找到相同的分数, 就添加这一步
							bestPlace.add(new StepBean(from, to));
						}
						if (score > best) { // 找到一个更好的分，就把以前存的位子全部清除
							best = score;
							bestPlace.clear();
							bestPlace.add(new StepBean(from, to));
						}
					}
				}
			}
		}
		// 随机选择一个最好的一步
		int count = bestPlace.size();
		int ran = new Random().nextInt(count);
		return (StepBean) bestPlace.toArray()[ran];
	}

	/**
	 * 1. 每个棋子本身的价值
	 * 3. 棋子的地理优势
	 * 2. 棋子评分
	 * 	1. 棋子嘴边多少food
	 * 	2. 棋子相关的点, 有影响的有多少点
	 */
	public int evaluateBoard() {
		return 0;
	}

    private ChessPiece redBoss;
    private ChessPiece blackBoss;

    public int negativeMaximum(ChessPiece[][] pieces, Part curPart, int deep, int alphaBeta) {
        // 1. 初始化各个变量
        int best = MIN;
        // 2. 获取可以下子的空位列表
        // 生成待选的列表，就是可以下子的空位
        for (int x = 0; x < ChessDefined.RANGE_X; x++) {
            for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
                ChessPiece fromPiece = pieces[x][y];
                if (fromPiece != null && fromPiece.part == curPart) {
                    Place from = Place.of(x, y);
                    // list 排序
                    List<Place> list = fromPiece.role.getRule().find(pieces, curPart, from);
                    if (list.isEmpty()) {
                        continue;
                    }
                    for (Place to : list) {
                        // 备份
                        ChessPiece backupToPiece = pieces[to.x][to.y];
                        int score;
                        // 判断是否胜利
						if (backupToPiece.role == Role.Boss) {
							score = MAX;
						} else {
							// 走棋
							pieces[to.x][to.y] = pieces[from.x][from.y];
							pieces[from.x][from.y] = null;
							if (deep <= 1) {
								score = evaluateBoard();
							} else {
								score = negativeMaximum(pieces, Part.getOpposite(curPart), deep - 1, -best);
							}
							// 退回上一步
							pieces[from.x][from.y] = pieces[to.x][to.y];
							pieces[to.x][to.y] = backupToPiece;
						}
						if (score > best) { // 找到一个更好的分，就更新分数
							best = score;
						}
						if (score > alphaBeta) { // alpha剪枝
							break;
						}
                    }
                }
            }
        }
		return -best;
    }

}



package cn.cpf.app.chess.algorithm;

import cn.cpf.app.chess.bean.AnalysisBean;
import cn.cpf.app.chess.bean.ChessPiece;
import cn.cpf.app.chess.bean.StepBean;
import cn.cpf.app.chess.main.ChessConfig;
import cn.cpf.app.chess.res.ChessDefined;
import cn.cpf.app.chess.res.Part;
import cn.cpf.app.chess.res.Place;
import cn.cpf.app.chess.res.Role;

import java.util.ArrayList;
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
 * 在Minimax风格的AlphaBeta算法中，轮红方走棋时，评估值为 100，轮黑方走棋评估值仍是100。
 * 但在Negamax风格的AlphaBeta算法中，轮红方走棋时，评估值为 100，轮黑方走棋时评估值要为-100。
 */
public class AlphaBeta {

	private static final int MAX = Integer.MAX_VALUE;
	/**
	 * 这里要保证 Min + Max = 0, 哪怕是微不足道的差距都可能导致发生错误
	 */
	private static final int MIN = - MAX;

	/**
	 * 奇数层是电脑(max层)thisSide, 偶数层是human(min层)otherSide
	 *
	 * @param pieces  棋盘
	 * @param curPart 当前走棋方
	 * @param deep 搜索深度
	 * @return
	 */
	public static StepBean getEvaluatedPlace(ChessPiece[][] pieces, Part curPart, int deep){
		// 1. 初始化各个变量
		AnalysisBean analysisBean = new AnalysisBean(pieces);

		int best = MIN;
		HashSet<StepBean> bestPlace = new HashSet<>();
		// 2. 获取可以下子的空位列表
		// 生成待选的列表，就是可以下子的空位
		List<StepBean> stepBeanList = new ArrayList<>();
		for (int x = 0; x < ChessDefined.RANGE_X; x++) {
			for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
				ChessPiece fromPiece = pieces[x][y];
				if (fromPiece != null && fromPiece.part == curPart) {
					Place from = Place.of(x, y);
					List<Place> list = fromPiece.role.find(analysisBean, curPart, from);
					if (list.isEmpty()) {
						continue;
					}
					list.forEach(item -> {
						stepBeanList.add(new StepBean(from, item, analysisBean.getSingleScore(pieces[item.x][item.y], item.y)));
					});
				}
			}
		}


		int pieceNum = analysisBean.getPieceNum();

		if (pieceNum > 24) {
			deep --;
		} else if (pieceNum > 16) {
		} else if (pieceNum > 8) {
			deep ++;
		} else if (pieceNum > 6) {
			deep += 2;
		} else if (pieceNum > 4) {
			deep += 3;
		} else {
			deep += 4;
		}

		// list 排序
		stepBeanList.sort((o1, o2) -> o2.score - o1.score);

		for (StepBean item : stepBeanList) {
			Place from = item.from;
			Place to = item.to;
			// 备份
			ChessPiece eatenPiece = pieces[to.x][to.y];
			int score;
			// 判断是否胜利
			if (eatenPiece != null && eatenPiece.role == Role.Boss) {
				score = MAX;
			} else {
				// 走棋
				analysisBean.goForward(from, to, eatenPiece);
				// 评分
				score = negativeMaximum(analysisBean, Part.getOpposite(curPart), deep - 1, -best);
				// 退回上一步
				analysisBean.backStep(from, to, eatenPiece);
			}
			if (score == best) { // 找到相同的分数, 就添加这一步
				bestPlace.add(item);
			}
			if (score > best) { // 找到一个更好的分，就把以前存的位子全部清除
				best = score;
				bestPlace.clear();
				bestPlace.add(item);
			}
		}

		// 随机选择一个最好的一步
		int count = bestPlace.size();
		int ran = new Random().nextInt(count);
		return (StepBean) bestPlace.toArray()[ran];
	}

	/**
	 * @param analysisBean
	 * @param curPart
	 * @param deep
	 * @param alphaBeta
	 * @return
	 */
    private static int negativeMaximum(AnalysisBean analysisBean, Part curPart, int deep, int alphaBeta) {
        // 1. 初始化各个变量
        int best = MIN;
		final ChessPiece[][] pieces = analysisBean.chessPieces;
        // 2. 获取可以下子的空位列表
        // 生成待选的列表，就是可以下子的空位
		List<StepBean> stepBeanList = new ArrayList<>();
        for (int x = 0; x < ChessDefined.RANGE_X; x++) {
			for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
				final ChessPiece fromPiece = pieces[x][y];
				if (fromPiece != null && fromPiece.part == curPart) {
					final Place from = Place.of(x, y);
					// list 排序
					final List<Place> list = fromPiece.role.find(analysisBean, curPart, from);
					if (list.isEmpty()) {
						continue;
					}
					list.forEach(item -> {
						int singleScore = analysisBean.getSingleScore(pieces[item.x][item.y], item.y);
						if (singleScore > 0 || deep > ChessConfig.sha) {
							stepBeanList.add(new StepBean(from, item, singleScore));
						}
					});
				}
			}
		}

		// list 排序
		stepBeanList.sort((o1, o2) -> o2.score - o1.score);

		for (StepBean item : stepBeanList) {
			Place from = item.from;
			Place to = item.to;
			// 备份
			ChessPiece eatenPiece = pieces[to.x][to.y];
			int score;
			// 判断是否胜利
			if (eatenPiece != null && eatenPiece.role == Role.Boss) {
				score = MAX;
			} else {
				// 走棋
				analysisBean.goForward(from, to, eatenPiece);
				// 评估
				if (deep <= 1) {
					score = analysisBean.getCurPartEvaluateScore(curPart);
				} else {
					score = negativeMaximum(analysisBean, Part.getOpposite(curPart), deep - 1, -best);
				}
				// 退回上一步
				analysisBean.backStep(from, to, eatenPiece);
			}
			if (score > best) { // 找到一个更好的分，就更新分数
				best = score;
			}
			if (score > alphaBeta) { // alpha剪枝
				break;
			}
		}
		return -best;
    }

}

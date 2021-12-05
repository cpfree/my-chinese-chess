package cn.cpf.app.chess.algorithm;

import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.StepBean;
import cn.cpf.app.chess.util.ArrayUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author CPF
 * 1.双方都按自己认为的最佳着法行棋.
 * <p>
 * 对给定的盘面用一个分值来评估，这个评估值永远是从一方（搜索程序）来评价的，红方有利时给一个正数，黑方有利时给一个负数。
 * （通常把它称为Max）看来，分值大的数表示对己方有利，而对于对方Min来说，它会选择分值小的着法。
 * <p>
 * 用Negamax风格来描述的AlphaBeta中的评估函数，对轮到谁走棋是敏感的。
 * 在Minimax风格的AlphaBeta算法中，轮红方走棋时，评估值为 100，轮黑方走棋评估值仍是100。
 * 但在Negamax风格的AlphaBeta算法中，轮红方走棋时，评估值为 100，轮黑方走棋时评估值要为-100。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class AlphaBeta {

    private static final int MAX = Integer.MAX_VALUE;
    /**
     * 这里要保证 Min + Max = 0, 哪怕是微不足道的差距都可能导致发生错误
     */
    private static final int MIN = -MAX;
    /**
     * CPU线程数
     */
    private static final int CPU_PROCESSORS = Runtime.getRuntime().availableProcessors();


    /**
     * 生成待选的列表，就是可以下子的空位
     *
     * @param analysisBean 棋盘分析对象
     * @param curPart 当前走棋方
     * @return 可以下子的空位集合
     */
    private static List<StepBean> geneNestStepPlaces(final AnalysisBean analysisBean, final Part curPart, final int deep) {
        final Piece[][] pieces = analysisBean.pieces;
//        final int searchKillStepDeepLevel = Application.config().getSearchKillStepDeepLevel();
        // TODO 去掉new
        List<StepBean> stepBeanList = new ArrayList<>();
        for (int x = 0; x < ChessDefined.RANGE_X; x++) {
            for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
                final Piece fromPiece = pieces[x][y];
                if (fromPiece != null && fromPiece.part == curPart) {
                    final Place from = Place.of(x, y);
                    final List<Place> list = fromPiece.role.find(analysisBean, curPart, from);
                    // TODO 考虑下此处添加至集合的做法 在计算时 是否有优化空间.
                    if (list.isEmpty()) {
                        continue;
                    }
                    for (Place item : list) {
                        stepBeanList.add(StepBean.of(from, item));
//                        final int singleScore = analysisBean.getSingleScore(pieces[item.x][item.y], item.y);
//                        // list 排序
//                        // TODO 排序有待优化
//                        stepBeanList.sort((o1, o2) -> o2.score - o1.score);
//                        if (singleScore > 0 || deep > searchKillStepDeepLevel) {
//                        }
                    }
                }
            }
        }
        return stepBeanList;
    }

    /**
     * 奇数层是电脑(max层)thisSide, 偶数层是human(min层)otherSide
     *
     * @param pieces  棋盘
     * @param curPart 当前走棋方
     * @param deep    搜索深度
     * @return 下一步的位置
     */
    public static StepBean getEvaluatedPlace(final Piece[][] pieces, final Part curPart, int deep) {
        // 1. 初始化各个变量
        final AnalysisBean analysisBean = new AnalysisBean(pieces);
        int best = MIN;
        HashSet<StepBean> bestPlace = new HashSet<>();
        // 2. 获取可以下子的空位列表
        List<StepBean> stepBeanList = geneNestStepPlaces(analysisBean, curPart, deep);

        // 根据棋子数量, 动态调整搜索深度
        int pieceNum = analysisBean.getPieceNum();
        if (pieceNum > 20) {
            deep -= 2;
        } else if (pieceNum <= 4) {
            deep += 4;
        } else if (pieceNum <= 8) {
            deep += 2;
        }
        // 对方棋手
        final Part oppositeCurPart = Part.getOpposite(curPart);
        // 下一深度
        final int nextDeep = deep - 1;
        for (StepBean item : stepBeanList) {
            Place from = item.from;
            Place to = item.to;
            // 备份
            Piece eatenPiece = pieces[to.x][to.y];
            int score;
            // 判断是否胜利
            if (eatenPiece != null && eatenPiece.role == Role.BOSS) {
                score = MAX;
            } else {
                // 走棋
                final int invScr = analysisBean.goForward(from, to, eatenPiece);
                // 评分
                score = negativeMaximum(analysisBean, oppositeCurPart, nextDeep, -best);
                // 退回上一步
                analysisBean.backStep(from, to, eatenPiece, invScr);
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
     * 奇数层是电脑(max层)thisSide, 偶数层是human(min层)otherSide
     *
     * @param sourcePieces  棋盘
     * @param curPart 当前走棋方
     * @param deep    搜索深度
     * @return 下一步的位置
     */
    public static StepBean getEvaluatedPlaceWithParallel(final Piece[][] sourcePieces, final Part curPart, int deep) {

        final AnalysisBean analysisBean = new AnalysisBean(sourcePieces);

        final List<StepBean> stepBeanList = geneNestStepPlaces(analysisBean, curPart, deep);

        final HashSet<StepBean> bestPlace = new HashSet<>();
        // 2. 获取可以下子的空位列表

        // 根据棋子数量, 动态调整搜索深度
        final int pieceNum = analysisBean.getPieceNum();
        if (pieceNum > 20) {
            deep -= 2;
        } else if (pieceNum <= 4) {
            deep += 4;
        } else if (pieceNum <= 8) {
            deep += 2;
        }
        // 1. 初始化各个变量
        final AtomicInteger best = new AtomicInteger(MIN);
        // 对方棋手
        final Part oppositeCurPart = Part.getOpposite(curPart);
        // 下一深度
        final int nextDeep = deep - 1;
        log.debug("size : {}, content: {}", stepBeanList.size(), stepBeanList);
        stepBeanList.stream().parallel().forEach(item -> {
            log.debug("并行流 ==> Thread : {}", Thread.currentThread().getId());
            final Piece[][] pieces = ArrayUtils.deepClone(sourcePieces);
            final AnalysisBean bean = new AnalysisBean(pieces);

            final Place to = item.to;
            // 备份
            final Piece eatenPiece = pieces[to.x][to.y];
            int score;
            // 判断是否胜利
            if (eatenPiece != null && eatenPiece.role == Role.BOSS) {
                score = MAX;
            } else {
                // 走棋
                final int invScr = bean.goForward(item.from, to, eatenPiece);
                // 评分
                score = negativeMaximum(bean, oppositeCurPart, nextDeep, -best.get());
                // 退回上一步
                bean.backStep(item.from, to, eatenPiece, invScr);
            }
            if (score == best.get()) { // 找到相同的分数, 就添加这一步
                synchronized (bestPlace) {
                    bestPlace.add(item);
                }
            }
            if (score > best.get()) { // 找到一个更好的分，就把以前存的位子全部清除
                best.set(score);
                synchronized (bestPlace) {
                    bestPlace.clear();
                    bestPlace.add(item);
                }
            }
        });

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
        final Piece[][] pieces = analysisBean.pieces;
        int best = MIN;
        // 对方棋手
        final Part oppositeCurPart = Part.getOpposite(curPart);
        // 下一深度
        final int nextDeep = deep - 1;
        // 2. 生成待选的列表，就是可以下子的列表
        List<StepBean> stepBeanList = geneNestStepPlaces(analysisBean, curPart, deep);
        for (StepBean item : stepBeanList) {
            Place from = item.from;
            Place to = item.to;
            // 备份
            Piece eatenPiece = pieces[to.x][to.y];
            int score;
            // 判断是否胜利
            if (eatenPiece != null && eatenPiece.role == Role.BOSS) {
                score = MAX;
            } else {
                // 走棋
                final int invScr = analysisBean.goForward(from, to, eatenPiece);
                // 评估
                if (deep <= 1) {
                    score = analysisBean.getCurPartEvaluateScore(curPart);
                } else {
                    score = negativeMaximum(analysisBean, oppositeCurPart, nextDeep, -best);
                }
                // 退回上一步
                analysisBean.backStep(from, to, eatenPiece, invScr);
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

package cn.cpf.app.chess.algorithm;

import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.ctrl.Application;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.StepBean;
import cn.cpf.app.chess.util.ArrayUtils;
import com.github.cosycode.common.ext.bean.DoubleBean;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
     * 根据棋子数量, 动态调整搜索深度
     *
     * @param pieceNum 棋子数量
     * @return 调整搜索深度差值
     */
    private static int searchDeepSuit(final int pieceNum) {
        // 根据棋子数量, 动态调整搜索深度
        if (pieceNum > 20) {
            return -2;
        } else if (pieceNum <= 4) {
            return 4;
        } else if (pieceNum <= 8) {
            return 2;
        }
        return 0;
    }

    /**
     * 生成待选的列表，就是可以下子的空位
     *
     * @param analysisBean 棋盘分析对象
     * @param curPart 当前走棋方
     * @return 可以下子的空位集合
     */
    private static List<StepBean> geneNestStepPlaces(final AnalysisBean analysisBean, final Part curPart, final int deep) {
        final Piece[][] pieces = analysisBean.pieces;
        // 是否杀气
        final boolean killFlag = deep <= Application.config().getSearchKillStepDeepLevel();
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
                        if (killFlag) {
                            final int singleScore = analysisBean.nextStepOpportunityCost(from, item);
                            if (singleScore > 50) {
                                stepBeanList.add(StepBean.of(from, item));
                                DebugInfo.incrementAndGetHisber();
                            }
                        } else {
                            stepBeanList.add(StepBean.of(from, item));
                        }
                    }
                }
            }
        }
        return stepBeanList;
    }

    @Deprecated
    private static Collection<StepBean> geneNestStepPlacesOrder(final AnalysisBean analysisBean, final Part curPart, final int deep) {
        final Piece[][] srcPieces = analysisBean.pieces;
        // 获取可以下子的空位列表
        final List<StepBean> stepBeanList = geneNestStepPlaces(analysisBean, curPart, deep);
        // 进入循环之前计算好循环内使用常量
        List<DoubleBean<Integer, StepBean>> bestPlace = new ArrayList<>();
        // 对方棋手
        final Part oppositeCurPart = Part.getOpposite(curPart);
        // 下一深度
        final int nextDeep = deep - 1;
        log.warn("size : {}, content: {}", stepBeanList.size(), stepBeanList);
        for (StepBean item : stepBeanList) {
            final Place to = item.to;
            // 备份
            final Piece eatenPiece = srcPieces[to.x][to.y];
            int score;
            // 判断是否胜利
            if (eatenPiece != null && eatenPiece.role == Role.BOSS) {
                score = MAX;
            } else {
                // 走棋
                final int invScr = analysisBean.goForward(item.from, to, eatenPiece);
                // 评分
                score = negativeMaximumWithNoCut(analysisBean, oppositeCurPart, nextDeep);
                // 退回上一步
                analysisBean.backStep(item.from, to, eatenPiece, invScr);
            }
            // 这里添加进所有的分数
            bestPlace.add(new DoubleBean<>(score, item));
        }
        bestPlace.sort((o1, o2) -> o2.getO1() - o1.getO1());
        for (DoubleBean<Integer, StepBean> doubleBean : bestPlace) {
            System.out.println(doubleBean.getO1() + " === " + doubleBean.getO2());
        }
        return bestPlace.stream().map(DoubleBean::getO2).collect(Collectors.toList());
    }


    /**
     * 负极大值搜索算法
     *
     * @param analysisBean 局势分析对象
     * @param curPart 当前走棋方
     * @param deep 搜索深度
     * @return 负极大值搜索算法计算分值
     */
    @Deprecated
    private static int negativeMaximumWithNoCut(AnalysisBean analysisBean, Part curPart, int deep) {
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
                    score = negativeMaximumWithNoCut(analysisBean, oppositeCurPart, nextDeep);
                }
                // 退回上一步
                analysisBean.backStep(from, to, eatenPiece, invScr);
            }
            if (score > best) { // 找到一个更好的分，就更新分数
                best = score;
            }
        }
        return -best;
    }


    /**
     * 奇数层是电脑(max层)thisSide, 偶数层是human(min层)otherSide
     *
     * @param srcPieces  棋盘
     * @param curPart 当前走棋方
     * @param deep    搜索深度
     * @return 下一步的位置
     */
    public static Set<StepBean> getEvaluatedPlace(final Piece[][] srcPieces, final Part curPart, int deep) {
        // 1. 初始化各个变量
        final AnalysisBean analysisBean = new AnalysisBean(srcPieces);
        // 根据棋子数量, 动态调整搜索深度
        deep += searchDeepSuit(analysisBean.getPieceNum());
        // 2. 获取可以下子的空位列表
        List<StepBean> stepBeanList = geneNestStepPlaces(analysisBean, curPart, deep);
        // 进入循环之前计算好循环内使用常量
        Set<StepBean> bestPlace = new HashSet<>();
        int best = MIN;
        // 对方棋手
        final Part oppositeCurPart = Part.getOpposite(curPart);
        // 下一深度
        final int nextDeep = deep - 1;
        log.debug("size : {}, content: {}", stepBeanList.size(), stepBeanList);
        for (StepBean item : stepBeanList) {
            final Place to = item.to;
            // 备份
            final Piece eatenPiece = srcPieces[to.x][to.y];
            int score;
            // 判断是否胜利
            if (eatenPiece != null && eatenPiece.role == Role.BOSS) {
                score = MAX;
            } else {
                // 走棋
                final int invScr = analysisBean.goForward(item.from, to, eatenPiece);
                // 评分
                score = negativeMaximum(analysisBean, oppositeCurPart, nextDeep, -best);
                // 退回上一步
                analysisBean.backStep(item.from, to, eatenPiece, invScr);
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
        return bestPlace;
    }

    /**
     * 奇数层是电脑(max层)thisSide, 偶数层是human(min层)otherSide
     *
     * @param srcPieces  棋盘
     * @param curPart 当前走棋方
     * @param deep    搜索深度
     * @return 下一步的位置
     */
    public static Set<StepBean> getEvaluatedPlaceWithParallel(final Piece[][] srcPieces, final Part curPart, int deep) {
        // 1. 初始化各个变量
        final AnalysisBean srcAnalysisBean = new AnalysisBean(srcPieces);
        // 根据棋子数量, 动态调整搜索深度
        deep += searchDeepSuit(srcAnalysisBean.getPieceNum());
        // 2. 获取可以下子的空位列表
        final Collection<StepBean> stepBeanList = geneNestStepPlaces(srcAnalysisBean, curPart, deep);
        // 进入循环之前计算好循环内使用常量
        final Set<StepBean> bestPlace = new HashSet<>();
        final AtomicInteger best = new AtomicInteger(MIN);
        // 对方棋手
        final Part oppositeCurPart = Part.getOpposite(curPart);
        // 下一深度
        final int nextDeep = deep - 1;
        log.debug("size : {}, content: {}", stepBeanList.size(), stepBeanList);
        stepBeanList.stream().parallel().forEach(item -> {
            log.debug("并行流 ==> Thread : {}", Thread.currentThread().getId());
            final Piece[][] pieces = ArrayUtils.deepClone(srcPieces);
            final AnalysisBean analysisBean = new AnalysisBean(pieces);

            final Place to = item.to;
            // 备份
            final Piece eatenPiece = pieces[to.x][to.y];
            int score;
            // 判断是否胜利
            if (eatenPiece != null && eatenPiece.role == Role.BOSS) {
                score = MAX;
            } else {
                // 走棋
                final int invScr = analysisBean.goForward(item.from, to, eatenPiece);
                // 评分
                score = negativeMaximum(analysisBean, oppositeCurPart, nextDeep, -best.get());
                // 退回上一步
                analysisBean.backStep(item.from, to, eatenPiece, invScr);
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
        return bestPlace;
    }


    /**
     * 负极大值搜索算法
     *
     * @param analysisBean 局势分析对象
     * @param curPart 当前走棋方
     * @param deep 搜索深度
     * @param alphaBeta alphaBeta 剪枝分值
     * @return 负极大值搜索算法计算分值
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

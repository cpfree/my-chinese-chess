package cn.cpf.app.chess.algorithm;

import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.inter.MyList;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.StepBean;
import cn.cpf.app.chess.util.ArrayUtils;
import cn.cpf.app.chess.util.ListPool;
import com.github.cosycode.common.ext.bean.DoubleBean;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <b>Description : </b> AI 负极大值搜索算法
 * <p>
 * <br>对给定的盘面用一个分值来评估，这个评估值永远是从一方（搜索程序）来评价的，红方有利时给一个正数，黑方有利时给一个负数。
 * <br>（通常把它称为Max）看来，分值大的数表示对己方有利，而对于对方Min来说，它会选择分值小的着法。
 * <br>
 * <br>用Negamax风格来描述的AlphaBeta中的评估函数，对轮到谁走棋是敏感的。
 * <br>在Minimax风格的AlphaBeta算法中，轮红方走棋时，评估值为 100，轮黑方走棋评估值仍是100。
 * <br>但在Negamax风格的AlphaBeta算法中，轮红方走棋时，评估值为 100，轮黑方走棋时评估值要为-100。
 * </p>
 * <p>
 * <b>created in </b> 2017/12/22
 * </p>
 *
 * @author CPF
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class AlphaBeta {

    private static final int MAX = 100_000_000;
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
    public static int searchDeepSuit(final int pieceNum) {
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
     * 生成待选的列表，就是可以下子的空位, 如果 deep > 2 则对搜索结果进行排序.
     *
     * @param analysisBean 棋盘分析对象
     * @param curPart      当前走棋方
     * @param deep         搜索深度
     * @return 可以下子的空位集合
     */
    private static MyList<StepBean> geneNestStepPlaces(final AnalysisBean analysisBean, final Part curPart, final int deep) {
        final Piece[][] pieces = analysisBean.pieces;
        // 是否杀棋
        MyList<StepBean> stepBeanList = ListPool.localPool().getAStepBeanList();
        for (int x = 0; x < ChessDefined.RANGE_X; x++) {
            for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
                final Piece fromPiece = pieces[x][y];
                if (fromPiece != null && fromPiece.part == curPart) {
                    final Place from = Place.of(x, y);
                    // TO DO 考虑下此处添加至集合的做法 在计算时 是否有优化空间.
                    final MyList<Place> list = fromPiece.role.find(analysisBean, curPart, from);
                    if (list.isEmpty()) {
                        ListPool.localPool().addListToPool(list);
                        continue;
                    }
                    final Object[] elementData = list.eleTemplateDate();
                    for (int i = 0, len = list.size(); i < len; i++) {
                        stepBeanList.add(StepBean.of(from, (Place) elementData[i]));
                    }
                    ListPool.localPool().addListToPool(list);
                }
            }
        }
        // 是否排序, 如果搜索深度大于2, 则对结果进行排序
        // 排序后的结果, 进入极大极小值搜索算法时, 容易被剪枝.
        if (deep > 2) {
            orderStep(analysisBean, stepBeanList, curPart);
        }

        return stepBeanList;
    }

    /**
     * 对 空位列表 进行排序, 排序后的空位列表, 进入极大极小值搜索算法时, 容易被剪枝.
     *
     * @param analysisBean 棋盘分析对象
     * @param stepBeanList 可以下子的空位列表
     * @param curPart      当前走棋方
     */
    private static void orderStep(final AnalysisBean analysisBean, final MyList<StepBean> stepBeanList, final Part curPart) {
        final Piece[][] srcPieces = analysisBean.pieces;
        // 进入循环之前计算好循环内使用常量
        MyList<DoubleBean<Integer, StepBean>> bestPlace = ListPool.localPool().getADoubleBeanList();
        // 对方棋手
        final Part oppositeCurPart = Part.getOpposite(curPart);
        int best = MIN;

        final Object[] objects = stepBeanList.eleTemplateDate();
        for (int i = 0; i < stepBeanList.size(); i++) {
            final StepBean item = (StepBean) objects[i];
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
                DebugInfo.incrementAlphaBetaOrderTime();
                // 评分
                score = negativeMaximumWithNoCut(analysisBean, oppositeCurPart, -best);
                // 退回上一步
                analysisBean.backStep(item.from, to, eatenPiece, invScr);
            }
            // 这里添加进所有的分数
            bestPlace.add(new DoubleBean<>(score, item));
            if (score > best) { // 找到一个更好的分，就把以前存的位子全部清除
                best = score;
            }
        }
        /* 排序后返回 */
        // 这样排序是正确的, 可以有效消减数量
        bestPlace.sort((o1, o2) -> o2.getO1() - o1.getO1());

        stepBeanList.clear();
        bestPlace.forEach(dou -> stepBeanList.add(dou.getO2()));

        ListPool.localPool().addListToDoubleBeanListPool(bestPlace);
    }


    /**
     * 负极大值搜索算法(不带剪枝算法)
     *
     * @param analysisBean 局势分析对象
     * @param curPart      当前走棋方
     * @return 负极大值搜索算法计算分值
     */
    private static int negativeMaximumWithNoCut(AnalysisBean analysisBean, Part curPart, int alphaBeta) {
        // 1. 初始化各个变量
        final Piece[][] pieces = analysisBean.pieces;
        int best = MIN;
        // 2. 生成待选的列表，就是可以下子的列表
        MyList<StepBean> stepBeanList = geneNestStepPlaces(analysisBean, curPart, 1);

        final Object[] objects = stepBeanList.eleTemplateDate();
        for (int i = 0, len = stepBeanList.size(); i < len; i++) {
            final StepBean item = (StepBean) objects[i];
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
                DebugInfo.incrementAlphaBetaOrderTime();
                score = analysisBean.getCurPartEvaluateScore(curPart);
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
        ListPool.localPool().addListToStepBeanListPool(stepBeanList);
        return -best;
    }


    /**
     * 奇数层是电脑(max层)thisSide, 偶数层是human(min层)otherSide
     *
     * @param srcPieces 棋盘
     * @param curPart   当前走棋方
     * @param deep      搜索深度
     * @param forbidStep 禁止的步骤(长捉或长拦)
     * @return 下一步的位置
     */
    public static Set<StepBean> getEvaluatedPlace(final Piece[][] srcPieces, final Part curPart, final int deep, final StepBean forbidStep) {
        // 1. 初始化各个变量
        final AnalysisBean analysisBean = new AnalysisBean(srcPieces);
        // 2. 获取可以下子的空位列表
        MyList<StepBean> stepBeanList = geneNestStepPlaces(analysisBean, curPart, deep);
        // 3. 移除不该下的子
        stepBeanList.remove(forbidStep);
        // 进入循环之前计算好循环内使用常量
        Set<StepBean> bestPlace = new HashSet<>();
        int best = MIN;
        // 对方棋手
        final Part oppositeCurPart = Part.getOpposite(curPart);
        // 下一深度
        final int nextDeep = deep - 1;
        log.debug("size : {}, content: {}", stepBeanList.size(), stepBeanList);
        final Object[] objects = stepBeanList.eleTemplateDate();
        for (int i = 0, len = stepBeanList.size(); i < len; i++) {
            StepBean item = (StepBean) objects[i];
            final Place to = item.to;
            // 备份
            final Piece eatenPiece = srcPieces[to.x][to.y];
            int score;
            // 判断是否胜利
            if (eatenPiece != null && eatenPiece.role == Role.BOSS) {
                // 步数越少, 分值越大
                score = MAX + deep;
            } else {
                // 走棋
                final int invScr = analysisBean.goForward(item.from, to, eatenPiece);
                // 评分
                if (deep <= 1) {
                    score = analysisBean.getCurPartEvaluateScore(curPart);
                } else {
                    score = negativeMaximum(analysisBean, oppositeCurPart, nextDeep, -best);
                }
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
        ListPool.end();
        ListPool.localPool().addListToStepBeanListPool(stepBeanList);
        return bestPlace;
    }

    /**
     * 奇数层是电脑(max层)thisSide, 偶数层是human(min层)otherSide
     *
     * @param srcPieces 棋盘
     * @param curPart   当前走棋方
     * @param deep      搜索深度
     * @param forbidStep 禁止的步骤(长捉或长拦)
     * @return 下一步的位置
     */
    public static Set<StepBean> getEvaluatedPlaceWithParallel(final Piece[][] srcPieces, final Part curPart, final int deep, final StepBean forbidStep) {
        // 1. 初始化各个变量
        final AnalysisBean srcAnalysisBean = new AnalysisBean(srcPieces);
        // 2. 获取可以下子的空位列表
        MyList<StepBean> stepBeanList = geneNestStepPlaces(srcAnalysisBean, curPart, deep);
        // 3. 移除不该下的子
        stepBeanList.remove(forbidStep);
        // 进入循环之前计算好循环内使用常量
        final Set<StepBean> bestPlace = new HashSet<>();
        final AtomicInteger best = new AtomicInteger(MIN);
        // 对方棋手
        final Part oppositeCurPart = Part.getOpposite(curPart);
        // 下一深度
        final int nextDeep = deep - 1;
        log.debug("size : {}, content: {}", stepBeanList.size(), stepBeanList);

        Arrays.stream(stepBeanList.toArray()).parallel().filter(Objects::nonNull).map(StepBean.class::cast).forEach(item -> {
            log.debug("并行流 ==> Thread : {}", Thread.currentThread().getId());
            final Piece[][] pieces = ArrayUtils.deepClone(srcPieces);
            final AnalysisBean analysisBean = new AnalysisBean(pieces);

            final Place to = item.to;
            // 备份
            final Piece eatenPiece = pieces[to.x][to.y];
            int score;
            // 判断是否胜利
            if (eatenPiece != null && eatenPiece.role == Role.BOSS) {
                // 步数越少, 分值越大
                score = MAX + deep;
            } else {
                // 走棋
                final int invScr = analysisBean.goForward(item.from, to, eatenPiece);
                // 评分
                if (deep <= 1) {
                    score = analysisBean.getCurPartEvaluateScore(curPart);
                } else {
                    score = negativeMaximum(analysisBean, oppositeCurPart, nextDeep, -best.get());
                }
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
            ListPool.end();
        });
        ListPool.localPool().addListToStepBeanListPool(stepBeanList);
        ListPool.end();
        return bestPlace;
    }


    /**
     * 负极大值搜索算法
     *
     * @param analysisBean 局势分析对象
     * @param curPart      当前走棋方
     * @param deep         搜索深度
     * @param alphaBeta    alphaBeta 剪枝分值
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
        final MyList<StepBean> stepBeanList = geneNestStepPlaces(analysisBean, curPart, deep);

        final Object[] objects = stepBeanList.eleTemplateDate();
        for (int i = 0, len = stepBeanList.size(); i < len; i++) {
            final StepBean item = (StepBean) objects[i];
            Place from = item.from;
            Place to = item.to;
            // 备份
            Piece eatenPiece = pieces[to.x][to.y];
            int score;
            // 判断是否胜利
            if (eatenPiece != null && eatenPiece.role == Role.BOSS) {
                // 步数越少, 分值越大
                score = MAX + deep;
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
        ListPool.localPool().addListToStepBeanListPool(stepBeanList);
        return -best;
    }

}

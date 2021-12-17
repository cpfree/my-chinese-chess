package cn.cpf.app.chess.algorithm;

import cn.cpf.app.chess.modal.Piece;
import com.github.cosycode.common.lang.CheckException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/12/5
 * </p>
 *
 * @author CPF
 * @since 0.1
 **/
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebugInfo {

    /**
     * CPU线程数
     */
    private static final int CPU_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private static final AtomicInteger alphaBeta = new AtomicInteger(0);
    private static final AtomicInteger alphaBetaOrder = new AtomicInteger(0);
    private static final AtomicInteger pollListCount = new AtomicInteger(0);
    private static final AtomicInteger newListCount = new AtomicInteger(0);
    private static final AtomicInteger addListCount = new AtomicInteger(0);

    public static void initAlphaBetaTime() {
        alphaBeta.set(0);
        alphaBetaOrder.set(0);
    }

    public static void logEnd() {
        log.info("alpha beta time: {}, order: {}, total: {}", alphaBeta.get(), alphaBetaOrder.get(), alphaBeta.get() + alphaBetaOrder.get());
        log.info("alpha beta new: {}, add: {}, pop: {}", newListCount.get(), addListCount.get(), pollListCount.get());
    }

    public static void incrementNewListCount() {
        newListCount.incrementAndGet();
    }

    public static void incrementAddListCount() {
        addListCount.incrementAndGet();
    }

    public static void incrementPollListCount() {
        pollListCount.incrementAndGet();
    }

    public static void incrementAlphaBetaTime() {
        alphaBeta.incrementAndGet();
    }

    public static void incrementAlphaBetaOrderTime() {
        alphaBetaOrder.incrementAndGet();
    }

    /**
     * 测试棋盘分值是否是当前分数
     */
    public static void checkScoreDynamicCalc(final Piece[][] pieces, int pieceScore) {
        final int calcPieceScore = AnalysisBean.calcPieceScore(pieces);
        if (pieceScore != calcPieceScore) {
            throw new CheckException("pieceScore == calcPieceScore error!!! " + pieceScore + ", calcPieceScore: " + calcPieceScore);
        }
    }

}

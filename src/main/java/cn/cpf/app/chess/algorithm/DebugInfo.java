package cn.cpf.app.chess.algorithm;

import cn.cpf.app.chess.modal.Piece;
import com.github.cosycode.common.lang.CheckException;
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
public class DebugInfo {

    /**
     * CPU线程数
     */
    private static final int CPU_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private static final AtomicInteger alphaBeta = new AtomicInteger(0);
    private static final AtomicInteger n2 = new AtomicInteger(0);

    public static void initAlphaBetaTime() {
        alphaBeta.set(0);
        n2.set(0);
    }

    public static void logEnd() {
        log.info("alpha beta time: {}", alphaBeta.get());
        log.info("genge beta time: {}", n2.get());
    }

    public static void incrementAlphaBetaTime() {
        alphaBeta.incrementAndGet();
    }

    public static void incrementAndGetHisber() {
        n2.incrementAndGet();
    }

    /**
     * 测试棋盘分值是否是当前分数
     */
    public static void checkScoreDynamicCalc(final Piece[][] pieces, int pieceScore) {
        final int calcPieceScore = AnalysisBean.calcPieceScore(pieces);
        if (pieceScore != calcPieceScore ) {
            throw new CheckException("pieceScore == calcPieceScore error!!! " + pieceScore + ", calcPieceScore: " + calcPieceScore);
        }
    }

}

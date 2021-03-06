package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.algorithm.AlphaBeta;
import cn.cpf.app.chess.algorithm.AnalysisBean;
import cn.cpf.app.chess.algorithm.DebugInfo;
import cn.cpf.app.chess.algorithm.Role;
import cn.cpf.app.chess.conf.ChessAudio;
import cn.cpf.app.chess.modal.*;
import cn.cpf.app.chess.swing.BoardPanel;
import cn.cpf.app.chess.swing.ChessPiece;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * <b>Description : </b> 负责ui与后台数据交互, 以及功能性控制
 * <p>
 * <b>created in </b> 2021/8/27
 *
 * @author CPF
 * @since 0.1
 **/
@Slf4j
public class AppContext {

    private final BoardPanel boardPanel;

    @Getter
    private final CommandExecutor commandExecutor;

    @Getter
    private Situation situation;

    private static final Random random = new Random();

    AppContext(final BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
        commandExecutor = new CommandExecutor(boardPanel);
    }

    /**
     * 棋局初始化
     *
     * @param situation 棋局形势
     */
    public void init(Situation situation) {
        ChessAudio.OPEN_BOARD.play();
        this.situation = situation;
        boardPanel.init(situation);
        getCommandExecutor().sendCommand(CommandExecutor.CommandType.SustainAiRunIfNextIsAi);
    }

    /**
     * @return AI 计算下一步落子位置
     *
     * @param pieces     棋局棋子
     * @param part       当前走棋方
     * @param forbidStep 禁止的步骤(长捉或长拦)
     */
    public static StepBean computeStepBean(@NonNull final Piece[][] pieces, @NonNull final Part part, final StepBean forbidStep) {
        log.info("com run start");
        DebugInfo.initAlphaBetaTime();
        long t = System.currentTimeMillis();
        final AnalysisBean analysisBean = new AnalysisBean(pieces);
        // 根据棋子数量, 动态调整搜索深度
        final int deep = Application.config().getSearchDeepLevel() + AlphaBeta.searchDeepSuit(analysisBean.getPieceNum());
        // 计算
        final Set<StepBean> evaluatedPlaceSet;
        if (Application.config().isParallel()) {
            evaluatedPlaceSet = AlphaBeta.getEvaluatedPlaceWithParallel(pieces, part, deep, forbidStep);
        } else {
            evaluatedPlaceSet = AlphaBeta.getEvaluatedPlace(pieces, part, deep, forbidStep);
        }
        // 随机选择一个最好的一步
        final StepBean stepBean;
        if (evaluatedPlaceSet.isEmpty()) {
            stepBean = null;
            log.info("evaluated 没棋走了， Set == > {}", evaluatedPlaceSet);
        } else if (evaluatedPlaceSet.size() > 1) {
            int ran = random.nextInt(evaluatedPlaceSet.size());
            stepBean = (StepBean) evaluatedPlaceSet.toArray()[ran];
            log.info("evaluated Set == > {}", evaluatedPlaceSet);
            log.info("evaluated == > {}", stepBean);
        } else {
            stepBean = (StepBean) evaluatedPlaceSet.toArray()[0];
        }
        log.info("time: {}", (System.currentTimeMillis() - t));
        DebugInfo.logEnd();
        log.info("com run stop");
        return stepBean;
    }

    /**
     * 落子函数, 并附带落子后的胜负检查等操作逻辑.
     * @return 获胜方, 如果有值表示获胜方已经产生, 游戏结束. 如果为null 表示游戏可以继续.
     */
    public Part locatePiece(@NonNull Place from, @NonNull Place to, @NonNull PlayerType playerType) {
        final ChessPiece fromPiece = situation.getChessPiece(from);
        Objects.requireNonNull(fromPiece, "找不到移动的棋子");
        final Part curPart = fromPiece.piece.part;
        Piece eatenPiece = situation.movePiece(from, to);
        boardPanel.updateMark(from, to);
        // 判断是否吃掉了对方的 Boss
        if (eatenPiece != null && eatenPiece.role == Role.BOSS) {
            if (PlayerType.PEOPLE == playerType) {
                ChessAudio.WIN_BGM.play();
            } else {
                ChessAudio.LOSE_BGM.play();
            }
            JOptionPane.showMessageDialog(boardPanel, curPart.name() + "胜利", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
            log.info("游戏结束 ==> {} 胜利", curPart.name());
            return curPart;
        }
        AnalysisBean bean = new AnalysisBean(situation.generatePieces());
        /* 判断对方是否死棋: AI 计算无论对方怎么走, 都无法避免被吃掉 BOSS, 则代表对方已经死棋 */
        if (!bean.canAvoidBeEatBossAfterOneAIStep(Part.getOpposite(curPart))) {
            if (PlayerType.PEOPLE == playerType) {
                ChessAudio.WIN_BGM.play();
            } else {
                ChessAudio.BE_CHECKMATED_BY_COM.play();
            }
            JOptionPane.showMessageDialog(boardPanel, curPart.name() + "胜利", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
            log.info("游戏结束 ==> {} 胜利", curPart.name());
            return curPart;
        }
        // 是否将军: AI 计算 curPart 方 再走一步是否能够吃掉对方的 BOSS
        final boolean canEatBossNextStep = bean.canEatBossAfterOneAiStep(curPart);
        // 根据不同情况发出声音
        if (PlayerType.PEOPLE == playerType) {
            if (canEatBossNextStep) {
                ChessAudio.MAN_CHECK.play();
            } else if (eatenPiece == null){
                ChessAudio.MAN_MOVE.play();
            } else {
                ChessAudio.MAN_EAT.play();
            }
        } else {
            if (canEatBossNextStep) {
                ChessAudio.COM_CHECK.play();
            } else if (eatenPiece == null){
                ChessAudio.COM_MOVE.play();
            } else {
                ChessAudio.COM_EAT.play();
            }
        }
        return null;
    }

    /**
     * @return 返回 true: 撤销成功; 返回false: 撤销失败, 已经没有记录, 无法再继续撤销
     */
    public boolean rollbackOneStep() {
        final StepRecord stepRecord = situation.rollbackOneStep();
        if (stepRecord == null) {
            boardPanel.initMark();
        } else {
            boardPanel.updateMark(stepRecord.getFrom(), stepRecord.getTo());
        }
        return stepRecord != null;
    }

    /**
     * AI 运行一次, 若有获胜方, 则返回获胜方
     *
     * @return 获胜方, 如果有值表示获胜方已经产生, 游戏结束. 如果为null 表示游戏可以继续.
     */
    public Part aiRunOneTime() {
        final Part winner = situation.winner();
        if (winner != null) {
            log.warn("胜利方[{}]已经产生, 无法再次 AI 计算", winner);
            return winner;
        }
        final Part nextPart = situation.getNextPart();
        // 判断是否存在需要禁止走的棋路(长捉, 或长拦)
        final StepBean forbidStepBean = situation.getForbidStepBean();
        // 计算出下一步棋
        StepBean evaluatedStepBean = computeStepBean(situation.generatePieces(), nextPart, forbidStepBean);
        if (evaluatedStepBean == null) {
            log.warn("[{}]没有棋走了, 无法再次 AI 计算", nextPart);
            return Part.getOpposite(nextPart);
        }
        return locatePiece(evaluatedStepBean.from, evaluatedStepBean.to, PlayerType.COM);
    }

}

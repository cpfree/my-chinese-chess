package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.algorithm.AlphaBeta;
import cn.cpf.app.chess.algorithm.DebugInfo;
import cn.cpf.app.chess.algorithm.Role;
import cn.cpf.app.chess.conf.ChessAudio;
import cn.cpf.app.chess.modal.*;
import cn.cpf.app.chess.swing.BoardPanel;
import cn.cpf.app.chess.util.ArrayUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
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

    private final Random random = new Random();

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
    }

    /**
     * @return AI 计算下一步落子位置
     */
    public StepBean computeStepBean() {
        log.info("com run start");
        DebugInfo.initAlphaBetaTime();
        long t = System.currentTimeMillis();
        Piece[][] pieces = situation.genePiece();
        pieces = ArrayUtils.deepClone(pieces);
        final Set<StepBean> evaluatedPlaceSet;
        if (Application.config().isParallel()) {
            evaluatedPlaceSet = AlphaBeta.getEvaluatedPlaceWithParallel(pieces, situation.getNextPart(), Application.config().getSearchDeepLevel());
        } else {
            evaluatedPlaceSet = AlphaBeta.getEvaluatedPlace(pieces, situation.getNextPart(), Application.config().getSearchDeepLevel());
        }
        // 随机选择一个最好的一步
        final StepBean stepBean;
        if (evaluatedPlaceSet.size() > 1) {
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
     * @return 获胜方, 如果有值表示获胜方已经产生, 无法继续执行, 如果为null 表示游戏可以继续.
     */
    public Piece locatePiece(Place from, Place to) {
        Piece eatenPiece = situation.movePiece(from, to);
        boardPanel.updateMark(from, to);
        return eatenPiece;
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
     * @return 返回 true, 游戏可以继续, false: 已经有获胜方, 游戏结束.
     */
    public Part aiRunOneTime() {
        final Part winner = situation.winner();
        if (winner != null) {
            log.warn("胜利方[{}]已经产生, 无法再次 AI 计算", winner);
            return winner;
        }
        // 计算出下一步棋
        StepBean evaluatedStepBean = this.computeStepBean();
        final Piece eatenPiece = locatePiece(evaluatedStepBean.from, evaluatedStepBean.to);
        // 判断是否结束
        if (eatenPiece == null) {
            ChessAudio.COM_MOVE.play();
        } else if (eatenPiece.role == Role.BOSS){
            final Part part = Part.getOpposite(eatenPiece.part);
            JOptionPane.showMessageDialog(boardPanel, part.name() + "胜利", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
            log.info("游戏结束 ==> {} 胜利", part.name());
        } else {
            ChessAudio.COM_EAT_MAN.play();
        }
        return eatenPiece == null ? null : Part.getOpposite(eatenPiece.part);
    }

}

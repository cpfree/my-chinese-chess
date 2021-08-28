package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.algorithm.AlphaBeta;
import cn.cpf.app.chess.algorithm.AnalysisBean;
import cn.cpf.app.chess.conf.ChessConfig;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.StepBean;
import cn.cpf.app.chess.swing.BoardPanel;
import cn.cpf.app.chess.swing.ChessPiece;
import cn.cpf.app.chess.util.ArrayUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <b>Description : </b> 负责ui与后台数据交互
 * <p>
 * <b>created in </b> 2021/8/27
 *
 * @author CPF
 * @since 0.1
 **/
@Slf4j
public class ControlCenter {

    /**
     * 棋局形势
     */
    private final Situation situation;

    private final BoardPanel boardPanel;

    @Getter
    private final ComRunner comRunner;

    ControlCenter(final BoardPanel boardPanel, final Situation situation) {
        this.boardPanel = boardPanel;
        this.situation = situation;
        comRunner = new ComRunner(boardPanel);
    }

    public void init(List<ChessPiece> list) {
        boardPanel.init(list);
        // 初始化棋盘
        situation.init(list);
    }

    public Part getNextPart() {
        return situation.getNextPart();
    }

    public ChessPiece getPiece(Place place) {
        return situation.getPiece(place);
    }

    public AnalysisBean getAnalysisBean() {
        return new AnalysisBean(situation.getBoardPiece());
    }

    public StepBean computeStepBean() {
        long t = System.currentTimeMillis();
        ChessPiece[][] boardPiece = situation.getBoardPiece();
        boardPiece = ArrayUtils.deepClone(boardPiece);
        StepBean evaluatedPlace = AlphaBeta.getEvaluatedPlace(boardPiece, situation.getNextPart(), ChessConfig.deep);
        log.info("time: {}", (System.currentTimeMillis() - t));
        return evaluatedPlace;
    }

    /**
     * 落子
     */
    public Part locatePiece(Place from, Place to) {
        Part part = situation.realLocatePiece(from, to);
        boardPanel.updateMark(from, to);
        return part;
    }



}

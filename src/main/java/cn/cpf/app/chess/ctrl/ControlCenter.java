package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.algorithm.AlphaBeta;
import cn.cpf.app.chess.conf.ChessConfig;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.StepBean;
import cn.cpf.app.chess.swing.BoardPanel;
import cn.cpf.app.chess.swing.ChessPiece;
import cn.cpf.app.chess.util.ArrayUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <b>Description : </b> 负责ui与后台数据交互, 以及功能性控制
 * <p>
 * <b>created in </b> 2021/8/27
 *
 * @author CPF
 * @since 0.1
 **/
@Slf4j
public class ControlCenter {

    private final BoardPanel boardPanel;

    @Getter
    private final ComRunner comRunner;

    @Getter
    private final Situation situation;

    ControlCenter(final BoardPanel boardPanel, final Situation situation) {
        this.situation = situation;
        this.boardPanel = boardPanel;
        comRunner = new ComRunner(boardPanel);
    }

    public void init(List<ChessPiece> list) {
        situation.init(list);
        boardPanel.init(situation);
    }

    public StepBean computeStepBean() {
        long t = System.currentTimeMillis();
        Piece[][] pieces = situation.genePiece();
        pieces = ArrayUtils.deepClone(pieces);
        StepBean evaluatedPlace = AlphaBeta.getEvaluatedPlace(pieces, situation.getNextPart(), ChessConfig.deep);
        log.info("time: {}", (System.currentTimeMillis() - t));
        return evaluatedPlace;
    }

    /**
     * 落子
     */
    public Part locatePiece(Place from, Place to) {
        Part part = situation.movePiece(from, to);
        boardPanel.updateMark(from, to);
        return part;
    }

}

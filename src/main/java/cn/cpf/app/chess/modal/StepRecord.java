package cn.cpf.app.chess.modal;

import lombok.Getter;
import lombok.ToString;

import java.util.Date;

/**
 * <b>Description : </b> 记录每一步信息
 *
 * @author CPF
 * Date: 2020/3/18 11:06
 */
@ToString
@Getter
public class StepRecord {

    public StepRecord(Part part, int step, Piece piece, Place from, Place to, Piece eatenPiece) {
        this.part = part;
        this.step = step;
        this.piece = piece;
        this.from = from;
        this.to = to;
        this.eatenPiece = eatenPiece;
        this.date = new Date();
    }

    /**
     * 走棋方
     */
    private final Part part;
    private final int step;

    /**
     * 移动棋子
     */
    private final Piece piece;
    private final Place from;
    private final Place to;
    private final Date date;
    private final Piece eatenPiece;
}

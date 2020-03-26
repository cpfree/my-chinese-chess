package cn.cpf.app.chess.bean;

import cn.cpf.app.chess.res.Part;
import cn.cpf.app.chess.res.Piece;
import cn.cpf.app.chess.res.Place;
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
    @Getter
    private final Part part;

    @Getter
    private final int step;

    /**
     * 移动棋子
     */
    @Getter
    private final Piece piece;

    @Getter
    private final Place from;
    @Getter
    private final Place to;
    @Getter
    private final Date date;
    @Getter
    private final Piece eatenPiece;
}

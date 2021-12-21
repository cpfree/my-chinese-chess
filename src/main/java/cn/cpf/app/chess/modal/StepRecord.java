package cn.cpf.app.chess.modal;

import cn.cpf.app.chess.swing.ChessPiece;
import lombok.Getter;
import lombok.NonNull;
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

    /**
     * 走棋方
     */
    private final Part part;
    /**
     * 移动棋子
     */
    private final Piece piece;
    private final Place from;
    private final Place to;
    private final Date date;
    private final ChessPiece eatenPiece;

    public StepRecord(@NonNull Part part, @NonNull Piece piece, @NonNull Place from, @NonNull Place to, ChessPiece eatenPiece) {
        this.part = part;
        this.piece = piece;
        this.from = from;
        this.to = to;
        this.eatenPiece = eatenPiece;
        this.date = new Date();
    }
}

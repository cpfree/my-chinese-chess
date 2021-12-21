package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import lombok.ToString;

/**
 * 棋子类
 *
 * @author cpf
 */
@ToString
public class ChessPiece extends JPiece {

    public final String name;

    public final Piece piece;

    public ChessPiece(String name, Piece piece, Place place) {
        super(piece, place);
        this.name = name;
        this.piece = piece;
    }

}

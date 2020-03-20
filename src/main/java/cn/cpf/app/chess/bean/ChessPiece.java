package cn.cpf.app.chess.bean;

import cn.cpf.app.chess.res.Part;
import cn.cpf.app.chess.res.Piece;
import cn.cpf.app.chess.res.Place;
import cn.cpf.app.chess.res.Role;
import cn.cpf.app.chess.swing.JPiece;

/**
 * 棋子类
 *
 * @author cnlht
 */
public class ChessPiece extends JPiece {

    public final Part part;

    public final Role role;

    public ChessPiece(Piece piece, Place place) {
        super(piece.image, place);
        this.part = piece.part;
        this.role = piece.role;
    }

}
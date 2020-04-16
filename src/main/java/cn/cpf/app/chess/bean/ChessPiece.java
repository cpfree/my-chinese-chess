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

    public final String name;

    public final Part part;

    public final Role role;

    public final Piece piece;

    public ChessPiece(String name, Piece piece, Place place) {
        super(piece.image, place);
//        super(piece, place);
        this.name = name;
        this.piece = piece;
        this.part = piece.part;
        this.role = piece.role;
    }

    public int getSingleScore(Role role) {
        switch (role) {
            case Boss:
                return 1000000;
            case car:
                return 1000;
            case horse:
                return 460;
            case cannon:
                return 450;
            case soldier:
            case elephant:
            case Counselor:
                return 150;
            default:
                throw new RuntimeException();
        }
    }


}
package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.algorithm.Role;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;

/**
 * 棋子类
 *
 * @author cpf
 */
public class ChessPiece extends JPiece {

    public final String name;

    public final Piece piece;

    public ChessPiece(String name, Piece piece, Place place) {
        super(piece.image, place);
        this.name = name;
        this.piece = piece;
    }

    @Deprecated
    public int getSingleScore(Role role) {
        switch (role) {
            case BOSS:
                return 1000000;
            case CAR:
                return 1000;
            case HORSE:
                return 460;
            case CANNON:
                return 450;
            case SOLDIER:
            case ELEPHANT:
            case COUNSELOR:
                return 150;
            default:
                throw new RuntimeException();
        }
    }


}

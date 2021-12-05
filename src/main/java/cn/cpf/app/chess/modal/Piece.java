package cn.cpf.app.chess.modal;

import cn.cpf.app.chess.algorithm.PieceScore;
import cn.cpf.app.chess.algorithm.Role;
import cn.cpf.app.chess.conf.ChessImage;

import java.awt.*;

/**
 * 棋子类型, 和棋子图片一一对应
 */
public enum Piece {

    BLACK_BOSS(Part.BLACK, Role.BOSS, PieceScore.BOSS, ChessImage.BLACK_BOSS),
    BLACK_COUNSELOR(Part.BLACK, Role.COUNSELOR, PieceScore.COUNSELOR, ChessImage.BLACK_COUNSELOR),
    BLACK_ELEPHANT(Part.BLACK, Role.ELEPHANT, PieceScore.ELEPHANT, ChessImage.BLACK_ELEPHANT),
    BLACK_CAR(Part.BLACK, Role.CAR, PieceScore.CAR, ChessImage.BLACK_CAR),
    BLACK_HORSE(Part.BLACK, Role.HORSE, PieceScore.HORSE, ChessImage.BLACK_HORSE),
    BLACK_CANNON(Part.BLACK, Role.CANNON, PieceScore.CANNON, ChessImage.BLACK_CANNON),
    BLACK_SOLDIER(Part.BLACK, Role.SOLDIER, PieceScore.SOLDIER, ChessImage.BLACK_SOLDIER),
    RED_BOSS(Part.RED, Role.BOSS, PieceScore.BOSS, ChessImage.RED_BOSS),
    RED_COUNSELOR(Part.RED, Role.COUNSELOR, PieceScore.COUNSELOR, ChessImage.RED_COUNSELOR),
    RED_ELEPHANT(Part.RED, Role.ELEPHANT, PieceScore.ELEPHANT, ChessImage.RED_ELEPHANT),
    RED_CAR(Part.RED, Role.CAR, PieceScore.CAR, ChessImage.RED_CAR),
    RED_HORSE(Part.RED, Role.HORSE, PieceScore.HORSE, ChessImage.RED_HORSE),
    RED_CANNON(Part.RED, Role.CANNON, PieceScore.CANNON, ChessImage.RED_CANNON),
    RED_SOLDIER(Part.RED, Role.SOLDIER, PieceScore.SOLDIER, ChessImage.RED_SOLDIER),
    ;

    /**
     * 红方或黑方
     */
    public final Part part;

    public final Role role;

    public final PieceScore pieceScore;

    public final Image image;

    Piece(Part part, Role role, PieceScore pieceScore, ChessImage chessImage) {
        this.part = part;
        this.role = role;
        this.pieceScore = pieceScore;
        this.image = chessImage.getImage();
    }
}

package cn.cpf.app.chess.res;

import java.awt.*;

/**
 * 棋子类型, 和棋子图片一一对应
 */
public enum Piece {

    BlackBoss(Part.BLACK, Role.Boss, ChessImage.BlackBoss),
    BlackCounselor(Part.BLACK, Role.Counselor, ChessImage.BlackCounselor),
    BlackElephant(Part.BLACK, Role.elephant, ChessImage.BlackElephant),
    BlackCar(Part.BLACK, Role.car, ChessImage.BlackCar),
    BlackHorse(Part.BLACK, Role.horse, ChessImage.BlackHorse),
    BlackCannon(Part.BLACK, Role.cannon, ChessImage.BlackCannon),
    BlackSoldier(Part.BLACK, Role.soldier, ChessImage.BlackSoldier),
    RedBoss(Part.RED, Role.Boss, ChessImage.RedBoss),
    RedCounselor(Part.RED, Role.Counselor, ChessImage.RedCounselor),
    RedElephant(Part.RED, Role.elephant, ChessImage.RedElephant),
    RedCar(Part.RED, Role.car, ChessImage.RedCar),
    RedHorse(Part.RED, Role.horse, ChessImage.RedHorse),
    RedCannon(Part.RED, Role.cannon, ChessImage.RedCannon),
    RedSoldier(Part.RED, Role.soldier, ChessImage.RedSoldier),
    ;

    /**
     * 红方或黑方
     */
    public final Part part;

    public final Role role;

    public final Image image;

    Piece(Part part, Role role, ChessImage chessImage) {
        this.part = part;
        this.role = role;
        this.image = chessImage.getImage();
    }
}

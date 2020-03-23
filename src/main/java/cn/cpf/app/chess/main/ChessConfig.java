package cn.cpf.app.chess.main;

import cn.cpf.app.chess.bean.ChessPiece;
import cn.cpf.app.chess.res.Part;
import cn.cpf.app.chess.res.Piece;
import cn.cpf.app.chess.res.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/3/19 13:53
 */
public class ChessConfig {

    public static final Part firstPart = Part.RED;

    public static List<ChessPiece> geneDefaultPieceSituation() {
        List<ChessPiece> list = new ArrayList<>(32);
        // 添加红色棋子
        list.add(new ChessPiece(Piece.RedCar, Place.of(0, 9)));
        list.add(new ChessPiece(Piece.RedHorse, Place.of(1, 9)));
        list.add(new ChessPiece(Piece.RedElephant, Place.of(2, 9)));
        list.add(new ChessPiece(Piece.RedCounselor, Place.of(3, 9)));
        list.add(new ChessPiece(Piece.RedBoss, Place.of(4, 9)));
        list.add(new ChessPiece(Piece.RedCounselor, Place.of(5, 9)));
        list.add(new ChessPiece(Piece.RedElephant, Place.of(6, 9)));
        list.add(new ChessPiece(Piece.RedHorse, Place.of(7, 9)));
        list.add(new ChessPiece(Piece.RedCar, Place.of(8, 9)));

        list.add(new ChessPiece(Piece.RedCannon, Place.of(1, 7)));
        list.add(new ChessPiece(Piece.RedCannon, Place.of(7, 7)));
        list.add(new ChessPiece(Piece.RedSoldier, Place.of(0, 6)));
        list.add(new ChessPiece(Piece.RedSoldier, Place.of(2, 6)));
        list.add(new ChessPiece(Piece.RedSoldier, Place.of(4, 6)));
        list.add(new ChessPiece(Piece.RedSoldier, Place.of(6, 6)));
        list.add(new ChessPiece(Piece.RedSoldier, Place.of(8, 6)));

        // 添加黑色棋子
        list.add(new ChessPiece(Piece.BlackCar, Place.of(0, 0)));
        list.add(new ChessPiece(Piece.BlackHorse, Place.of(1, 0)));
        list.add(new ChessPiece(Piece.BlackElephant, Place.of(2, 0)));
        list.add(new ChessPiece(Piece.BlackCounselor, Place.of(3, 0)));
        list.add(new ChessPiece(Piece.BlackBoss, Place.of(4, 0)));
        list.add(new ChessPiece(Piece.BlackCounselor, Place.of(5, 0)));
        list.add(new ChessPiece(Piece.BlackElephant, Place.of(6, 0)));
        list.add(new ChessPiece(Piece.BlackHorse, Place.of(7, 0)));
        list.add(new ChessPiece(Piece.BlackCar, Place.of(8, 0)));

        list.add(new ChessPiece(Piece.BlackCannon, Place.of(1, 2)));
        list.add(new ChessPiece(Piece.BlackCannon, Place.of(7, 2)));
        list.add(new ChessPiece(Piece.BlackSoldier, Place.of(0, 3)));
        list.add(new ChessPiece(Piece.BlackSoldier, Place.of(2, 3)));
        list.add(new ChessPiece(Piece.BlackSoldier, Place.of(4, 3)));
        list.add(new ChessPiece(Piece.BlackSoldier, Place.of(6, 3)));
        list.add(new ChessPiece(Piece.BlackSoldier, Place.of(8, 3)));
        return list;
    }

}

package cn.cpf.app.chess.main;

import cn.cpf.app.chess.res.ChessDefined;
import cn.cpf.app.chess.res.Part;
import cn.cpf.app.chess.res.Piece;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/3/19 13:53
 */
public class ChessConfig {

    public static final Part firstPart = Part.RED;

    public static Piece[][] geneDefaultPieceSituation() {
        Piece[][] boardPiece = new Piece[ChessDefined.RANGE_X][ChessDefined.RANGE_Y];
                // 添加红色棋子
        boardPiece[0][9] = Piece.RedCar;
        boardPiece[1][9] = Piece.RedHorse;
        boardPiece[2][9] = Piece.RedElephant;
        boardPiece[3][9] = Piece.RedCounselor;
        boardPiece[4][9] = Piece.RedBoss;
        boardPiece[5][9] = Piece.RedCounselor;
        boardPiece[6][9] = Piece.RedElephant;
        boardPiece[7][9] = Piece.RedHorse;
        boardPiece[8][9] = Piece.RedCar;

        boardPiece[1][7] = Piece.RedCannon;
        boardPiece[7][7] = Piece.RedCannon;
        boardPiece[0][6] = Piece.RedSoldier;
        boardPiece[2][6] = Piece.RedSoldier;
        boardPiece[4][6] = Piece.RedSoldier;
        boardPiece[6][6] = Piece.RedSoldier;
        boardPiece[8][6] = Piece.RedSoldier;

        // 添加黑色棋子
        boardPiece[0][0] = Piece.BlackCar;
        boardPiece[1][0] = Piece.BlackHorse;
        boardPiece[2][0] = Piece.BlackElephant;
        boardPiece[3][0] = Piece.BlackCounselor;
        boardPiece[4][0] = Piece.BlackBoss;
        boardPiece[5][0] = Piece.BlackCounselor;
        boardPiece[6][0] = Piece.BlackElephant;
        boardPiece[7][0] = Piece.BlackHorse;
        boardPiece[8][0] = Piece.BlackCar;

        boardPiece[1][2] = Piece.BlackCannon;
        boardPiece[7][2] = Piece.BlackCannon;
        boardPiece[0][3] = Piece.BlackSoldier;
        boardPiece[2][3] = Piece.BlackSoldier;
        boardPiece[4][3] = Piece.BlackSoldier;
        boardPiece[6][3] = Piece.BlackSoldier;
        boardPiece[8][3] = Piece.BlackSoldier;
        return boardPiece;
    }

}

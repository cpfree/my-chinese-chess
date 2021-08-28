package cn.cpf.app.chess.algorithm;

import cn.cpf.app.chess.swing.ChessPiece;
import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.modal.Part;

public class BoardEvaluate {

    private BoardEvaluate(){}

	/**
	 * 返回对本方的实力评估, 本方为正
	 * @param chessPieces
     * @param curPart
     * @return
	 */
	public static int getCurPartEvaluateScore(ChessPiece[][] chessPieces, Part curPart) {
        int num = 0;
        for (int x = 0; x < ChessDefined.RANGE_X; x++) {
            for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
                ChessPiece piece = chessPieces[x][y];
                if (piece == null) {
                    continue;
                }
                if (piece.part == curPart) {
                    num += getSingleScore(chessPieces, piece.role, x, y);
                } else {
                    num -= getSingleScore(chessPieces, piece.role, x, y);
                }
            }
        }
        return num;
    }


    public static int getSingleScore(ChessPiece[][] chessPieces, Role role, int x, int y) {
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

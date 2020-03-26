package cn.cpf.app.chess.algorithm;

import cn.cpf.app.chess.bean.ChessPiece;
import cn.cpf.app.chess.res.Part;
import cn.cpf.app.chess.res.Role;

public class BoardEvaluate {

	/**
	 * 返回对本方的实力评估, 本方为正
	 * @param chessPieces
     * @param curPart
     * @return
	 */
	public static int getCurPartEvaluateScore(ChessPiece[][] chessPieces, Part curPart) {
        int num = 0;
        for (ChessPiece[] chessPiece : chessPieces) {
            for (ChessPiece piece : chessPiece) {
                if (piece.part == curPart) {
                    num += getSingleScore(piece.role);
                } else {
                    num -= getSingleScore(piece.role);
                }
            }
        }
        return num;
    }


    public static int getSingleScore(Role role) {
        switch (role) {
            case Boss:
                return 100;
            case car:
                return 1000;
            case horse:
                return 480;
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

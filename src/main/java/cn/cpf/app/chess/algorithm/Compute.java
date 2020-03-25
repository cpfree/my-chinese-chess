//package cn.cpf.app.chess.algorithm;
//
//import cn.cpf.app.chess.bean.ChessPiece;
//import cn.cpf.app.chess.domain.Situation;
//import cn.cpf.app.chess.res.ChessDefined;
//import cn.cpf.app.chess.res.Piece;
//import cn.cpf.app.chess.res.Place;
//
//public class Compute {
//
//	/**
//	 * 获取COM运算后得到的位置
//	 * @return
//	 */
//	public static Place getEvaluatedPlace(Situation situation){
//		ChessPiece[][] boardPiece = situation.getBoardPiece();
//		Piece[][] pieces = new Piece[ChessDefined.RANGE_X][ChessDefined.RANGE_Y];
//		for (int x = 0; x < ChessDefined.RANGE_X; x++) {
//			for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
//				ChessPiece chessPiece = boardPiece[x][y];
//				if (chessPiece != null) {
//					pieces[x][y] = chessPiece.piece;
//				}
//			}
//		}
//		return new AlphaBeta().getEvaluatedPlace(pieces, situation.getNextPart());
//	}
//
//}

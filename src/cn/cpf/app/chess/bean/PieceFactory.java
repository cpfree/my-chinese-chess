package cn.cpf.app.chess.bean;

import javax.swing.*;
import java.awt.*;

public class PieceFactory {

	private static ImageIcon Icon_white;
	private static ImageIcon Icon_black;

	public static ChessPiece getPieceByPart(Part part, Place place) {
		if (Part.RED.equals(part)) {
			return getWhitePiece(place);
		} else if (Part.BLACK.equals(part)) {
			return getBlackPiece(place);
		} else {
			System.out.println("请输入正确的类型!");
			return null;
		}
	}

	public static ChessPiece getWhitePiece(Place place) {
		if (Icon_white == null) {
			Image image = Toolkit.getDefaultToolkit().getImage(PieceFactory.class.getResource("/cn/cpf/app/gobang/image/white.png"));
			Icon_white = new ImageIcon(image);
		}
		return new ChessPiece(place, Part.RED, Icon_white);
	}

	public static ChessPiece getBlackPiece(Place place) {
		if (Icon_black == null) {
			Image image = Toolkit.getDefaultToolkit().getImage(PieceFactory.class.getResource("/cn/cpf/app/gobang/image/black.png"));
			Icon_black = new ImageIcon(image);
		}
		return new ChessPiece(place, Part.BLACK, Icon_black);
	}

}

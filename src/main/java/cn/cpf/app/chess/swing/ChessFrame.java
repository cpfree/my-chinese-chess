package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.res.ChessDefined;

import javax.swing.*;
import java.awt.*;

public class ChessFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				ChessFrame frame = new ChessFrame();
				frame.setVisible(true);
//					frame.pack();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ChessFrame() {
		setTitle("my chess");
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 100, ChessDefined.boardRect.width, ChessDefined.boardRect.height);
		contentPane = new ChessPanel();
		contentPane.setVisible(true);
		setContentPane(contentPane);
	}

}

package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.conf.ChessDefined;

import javax.swing.*;

/**
 * <b>Description : </b> 象棋启动类
 * <p>
 * <b>created in </b> 2018/8/27
 *
 * @author CPF
 * @since 1.0
 **/
public class ChessFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the frame.
	 */
	public ChessFrame() {
		setTitle("my chess");
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 100, ChessDefined.boardRect.width, ChessDefined.boardRect.height);
		// 添加菜单
		ChessMenuBar menuBar = new ChessMenuBar();
		menuBar.setVisible(true);
		setJMenuBar(menuBar);
		// 添加面板
		JPanel contentPane = new ChessPanel();
		contentPane.setVisible(true);
		setContentPane(contentPane);
	}

}

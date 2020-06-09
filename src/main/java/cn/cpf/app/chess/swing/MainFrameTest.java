package cn.cpf.app.chess.swing;

import javax.swing.*;
import java.awt.*;

/**
 * 整数进制转换器
 * @author by CPF
 */
public class MainFrameTest extends JFrame {
	private static final long serialVersionUID = -4791222140191948495L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				MainFrameTest frame = new MainFrameTest();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrameTest() throws InterruptedException {
		setBounds(100, 100, 325, 203);

		final JPanel contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);

		final JTextField textField = new JTextField();
		textField.setBounds(10, 20, 200, 30);
		contentPane.add(textField);
		textField.setColumns(50);
		textField.setText("简单的frame, 非常简单了");

		new Thread(() -> {
			while (true) {
				textField.setLocation(new Point(10, 20));
				try {
					SwingUtils.moveComp(textField, new Point(80, 200));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}
}

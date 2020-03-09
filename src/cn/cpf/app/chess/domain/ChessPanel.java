package cn.cpf.app.chess.domain;

import cn.cpf.app.chess.bean.ChessPiece;
import cn.cpf.app.chess.bean.Part;
import cn.cpf.app.chess.bean.PieceFactory;
import cn.cpf.app.chess.bean.Place;

import javax.swing.*;

import java.awt.*;

public class ChessPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private Image img;

    private Icon cell;

    /**
     * Create the panel.
     */
    public ChessPanel() {
        setLayout(new BorderLayout());
        // 520 576
        String imgPath = "/image/chessboard.jpg";
        img = Toolkit.getDefaultToolkit().getImage(ChessPanel.class.getResource(imgPath));
        // 56 56
        cell = new ImageIcon(getClass().getResource("/image/1.jpg"));



        JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        JTextField RadixTextField = new JTextField();
        RadixTextField.setBounds(31, 35, 92, 21);
        panel.add(RadixTextField);
        RadixTextField.setColumns(10);



        Image image = Toolkit.getDefaultToolkit().getImage(PieceFactory.class.getResource("/image/1.jpg"));
        ImageIcon Icon_white = new ImageIcon(image);
        panel.add(new ChessPiece(new Place(3, 4), Part.BLACK, Icon_white), BorderLayout.SOUTH);
    }


//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//
//        int imgWidth = img.getWidth(this);
//        int imgHeight = img.getHeight(this);// 获得图片的宽度与高度
//        int FWidth = getWidth();
//        int FHeight = getHeight();// 获得窗口的宽度与高度
//        System.out.println(imgWidth + " " + imgHeight + " " + FWidth + " " + FHeight + " ");
//        int x = (FWidth - imgWidth) / 2;
//        int y = (FHeight - imgHeight) / 2;
//        g.drawImage(img, x, y, null);
//
//        Image image = Toolkit.getDefaultToolkit().getImage(PieceFactory.class.getResource("/image/2.jpg"));
//        g.drawImage( image, 20, 60, null);
//    }

}

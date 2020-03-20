package cn.cpf.app.chess.swing;

import javax.swing.*;
import java.awt.*;

public class ChessPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Create the panel.
     */
    public ChessPanel() {
//        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new BoardPanel();
        add(panel, BorderLayout.CENTER);
    }

}

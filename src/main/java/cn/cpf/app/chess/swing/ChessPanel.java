package cn.cpf.app.chess.swing;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class ChessPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    @Getter
    private JPanel boardPanel;
    /**
     * Create the panel.
     */
    public ChessPanel() {
        setLayout(new BorderLayout(0, 0));
        // 透明
        this.setBackground(null);
        this.setOpaque(false);
        boardPanel = new BoardPanel();
        boardPanel.setBackground(null);
        boardPanel.setOpaque(false);
        add(boardPanel, BorderLayout.CENTER);
    }

}

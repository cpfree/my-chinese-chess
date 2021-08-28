package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.conf.ChessConfig;
import cn.cpf.app.chess.swing.BoardPanel;
import cn.cpf.app.chess.swing.ChessFrame;
import cn.cpf.app.chess.swing.ChessPanel;
import cn.cpf.app.chess.swing.ChessPiece;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * <b>Description : </b> 象棋引用启动类
 * <p>
 * <b>created in </b> 2021/8/28
 * </p>
 *
 * @author CPF
 * @since 1.0
 **/
@Slf4j
public class Application {

    private static ControlCenter controlCenter;

    public static ControlCenter instance() {
        return controlCenter;
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                JFrame frame = new ChessFrame();
                frame.setVisible(true);
                // 获取配置棋子
                BoardPanel boardPanel = (BoardPanel) ((ChessPanel) frame.getContentPane()).getBoardPanel();
                Situation situation = new Situation();
                controlCenter = new ControlCenter(boardPanel, situation);
                List<ChessPiece> list = ChessConfig.geneDefaultPieceSituation();
                controlCenter.init(list);
            } catch (Exception e) {
                log.error("", e);
            }
        });
    }

}

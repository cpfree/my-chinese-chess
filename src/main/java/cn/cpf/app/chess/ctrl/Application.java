package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.PlayerType;
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

    private static AppContext appContext;
    private static AppConfig config;

    public static AppConfig config() {
        return config;
    }

    public static AppContext context() {
        return appContext;
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                config = new AppConfig();
                config.setFirstPart(Part.RED);
                config.setBlackPlayerType(PlayerType.COM);
                config.setRedPlayerType(PlayerType.PEOPLE);
                config.setComIntervalTime(500);
                config.setSearchDeepLevel(6);
                config.setSearchKillStepDeepLevel(0);
                config.setParallel(true);
                config.setCartoon(true);
                JFrame frame = new ChessFrame();
                frame.setVisible(true);
                // 获取配置棋子
                BoardPanel boardPanel = (BoardPanel) ((ChessPanel) frame.getContentPane()).getBoardPanel();
                Situation situation = new Situation();
                appContext = new AppContext(boardPanel, situation);
                List<ChessPiece> list = ChessDefined.geneDefaultPieceSituation();
                appContext.init(list);
            } catch (Exception e) {
                log.error("", e);
            }
        });
    }

}

package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.ctrl.AppConfig;
import cn.cpf.app.chess.ctrl.Application;
import cn.cpf.app.chess.inter.LambdaMouseListener;
import cn.cpf.app.chess.modal.PlayerType;
import com.github.cosycode.common.ext.hub.Throws;
import lombok.NonNull;

import javax.swing.*;
import java.util.List;

public class ChessMenuBar extends JMenuBar {

    private static final long serialVersionUID = 1L;

    public ChessMenuBar() {
        addGameMenu();
        addDebugMenu();
        addSettingMenu();

        addMenuToMenuBar("撤销", e -> new Thread(Application.context()::rollbackOneStep).start());
        addMenuToMenuBar("撤销至开头", e -> {
            new Thread(()-> {
                while (Application.context().rollbackOneStep()){
                    Throws.con(600, Thread::sleep).logThrowable();
                }
            }).start();
        });
        addMenuToMenuBar("AI计算一次", e -> Application.context().getComRunner().runOneTime());
    }

    private void addGameMenu() {
        JMenu muGame = new JMenu("game");
        add(muGame);

        addItemToMenu(muGame, "重新开局", e -> {
            List<ChessPiece> list = ChessDefined.geneDefaultPieceSituation();
            Application.context().init(list);
        });
        addItemToMenu(muGame, "人机对弈", e -> {
            AppConfig config = Application.config();
            config.setBlackPlayerType(PlayerType.COM);
            config.setRedPlayerType(PlayerType.PEOPLE);
        });
        addItemToMenu(muGame, "机机对弈", e -> {
            AppConfig config = Application.config();
            config.setBlackPlayerType(PlayerType.COM);
            config.setRedPlayerType(PlayerType.COM);
        });
        addItemToMenu(muGame, "人人对弈", e -> {
            AppConfig config = Application.config();
            config.setBlackPlayerType(PlayerType.PEOPLE);
            config.setRedPlayerType(PlayerType.PEOPLE);
        });
    }

    private void addSettingMenu() {
        JMenu muSetting = new JMenu("setting");
        add(muSetting);
        addItemToMenu(muSetting, "COM 4", e -> Application.config().setSearchDeepLevel(4));
        addItemToMenu(muSetting, "COM 6", e -> Application.config().setSearchDeepLevel(6));
    }

    private void addDebugMenu() {
        JMenu muDebug = new JMenu("Debug");
        add(muDebug);
        addItemToMenu(muDebug, "COM 运行", e -> Application.context().getComRunner().runEnable());
        addItemToMenu(muDebug, "AI停止计算(计算完这一次)", e -> Application.context().getComRunner().stopRun());
    }

    /**
     * 在菜单 JMenu 下添加一个选择项, 标签为 label, 点击触发事件 listener
     *
     * @param jMenu    菜单
     * @param label    标签
     * @param listener 触发执行逻辑
     */
    private void addItemToMenu(@NonNull JMenu jMenu, String label, @NonNull LambdaMouseListener listener) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.addMouseListener(listener);
        jMenu.add(menuItem);
    }

    /**
     * 在当前菜单工具栏 下添加一个选择项, 标签为 label, 点击触发事件 listener
     *
     * @param label    标签
     * @param listener 触发执行逻辑
     */
    private void addMenuToMenuBar(String label, @NonNull LambdaMouseListener listener) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.addMouseListener(listener);
        add(menuItem);
    }
}

package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.conf.ChessConfig;
import cn.cpf.app.chess.ctrl.Application;
import cn.cpf.app.chess.inter.LambdaMouseListener;
import lombok.NonNull;

import javax.swing.*;

public class ChessMenuBar extends JMenuBar {

    private static final long serialVersionUID = 1L;

    public ChessMenuBar() {
        addUserMenu();
        addGameMenu();
        addDebugMenu();
        addSettingMenu();
    }

    private void addGameMenu() {
        JMenu muGame = new JMenu("game");
        add(muGame);

        addItemToMenu(muGame, "重新开局", e -> {
        });
    }

    private void addItemToMenu(@NonNull JMenu jMenu, String label, @NonNull LambdaMouseListener listener) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.addMouseListener(listener);
        jMenu.add(menuItem);
    }

    private void addSettingMenu() {
        JMenu muSetting = new JMenu("setting");
        add(muSetting);
        addItemToMenu(muSetting, "COM 4", e -> ChessConfig.deep = 4);
        addItemToMenu(muSetting, "COM 5", e -> ChessConfig.deep = 5);
        addItemToMenu(muSetting, "COM 6", e -> ChessConfig.deep = 6);
    }

    private void addDebugMenu() {
        JMenu muDebug = new JMenu("Debug");
        add(muDebug);
        addItemToMenu(muDebug, "COM 运行一次", e -> Application.instance().getComRunner().runOneTime());
        addItemToMenu(muDebug, "COM 运行", e -> Application.instance().getComRunner().runEnable());
        addItemToMenu(muDebug, "COM runIfEnable", e -> Application.instance().getComRunner().runIfEnable());
        addItemToMenu(muDebug, "COM disable", e -> Application.instance().getComRunner().stopRun());
    }

    private void addUserMenu() {
        JMenu mnUser = new JMenu("user");
        add(mnUser);
        addItemToMenu(mnUser, "个人信息", e -> {
        });
        addItemToMenu(mnUser, "退出", e -> {
        });
    }

    private BoardPanel getBoardPanel() {
        return (BoardPanel) ((ChessPanel) ((ChessFrame) ChessMenuBar.this.getParent().getParent().getParent()).getContentPane()).getBoardPanel();
    }
}

package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.ctrl.AppConfig;
import cn.cpf.app.chess.ctrl.Application;
import cn.cpf.app.chess.inter.LambdaMouseListener;
import cn.cpf.app.chess.modal.PlayerType;
import com.github.cosycode.common.ext.hub.Throws;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * <b>Description : </b> 面板菜单
 * <p>
 * <b>created in </b> 2018/9/8
 * </p>
 *
 * @author CPF
 **/
public class ChessMenuBar extends JMenuBar {

    private static final long serialVersionUID = 1L;

    public ChessMenuBar() {
        addSettingMenu();
        addDebugMenu();

        addMenuToMenuBar("撤销", e -> new Thread(Application.context()::rollbackOneStep).start());
        addMenuToMenuBar("撤销至开头", e -> new Thread(() -> {
            while (Application.context().rollbackOneStep()) {
                Throws.con(500, Thread::sleep).logThrowable();
            }
        }).start());
        addMenuToMenuBar("AI计算一次", e -> Application.context().getComRunner().runOneTime());
    }

    private void addSettingMenu() {
        JMenu muSetting = new JMenu("setting");
        add(muSetting);

        addItemToMenu(muSetting, "重新开局", e -> {
            List<ChessPiece> list = ChessDefined.geneDefaultPieceSituation();
            Application.context().init(list);
        });

        muSetting.add(new JSeparator());

        /* 添加移动棋子动画选择框 */
        JCheckBoxMenuItem cm = new JCheckBoxMenuItem("移动棋子动画");
        addLambdaMouseListener(cm, e -> Application.config().setCartoon(cm.getState()));
        muSetting.add(cm);
        // 初始化值
        cm.setState(Application.config().isCartoon());

        muSetting.add(new JSeparator());
        /* 添加游戏模式单选按钮组 */
        ButtonGroup playModalGroup = new ButtonGroup();
        JRadioButtonMenuItem rmManAi = new JRadioButtonMenuItem("人机对弈", true);
        JRadioButtonMenuItem rmManMan = new JRadioButtonMenuItem("人人对弈");
        JRadioButtonMenuItem rmAiAi = new JRadioButtonMenuItem("机机对弈");
        addLambdaMouseListener(rmManAi, e -> {
            AppConfig config = Application.config();
            config.setBlackPlayerType(PlayerType.COM);
            config.setRedPlayerType(PlayerType.PEOPLE);
        });
        addLambdaMouseListener(rmManMan, e -> {
            AppConfig config = Application.config();
            config.setBlackPlayerType(PlayerType.PEOPLE);
            config.setRedPlayerType(PlayerType.PEOPLE);
        });
        addLambdaMouseListener(rmAiAi, e -> {
            AppConfig config = Application.config();
            config.setBlackPlayerType(PlayerType.COM);
            config.setRedPlayerType(PlayerType.COM);
        });
        playModalGroup.add(rmManAi);
        playModalGroup.add(rmManMan);
        playModalGroup.add(rmAiAi);
        muSetting.add(rmManAi);
        muSetting.add(rmManMan);
        muSetting.add(rmAiAi);

        muSetting.add(new JSeparator());
        /* 设置难度单选按钮组 */
        ButtonGroup searchDeepBtnGroup = new ButtonGroup();
        final int searchDeepLevel = Application.config().getSearchDeepLevel();
        JRadioButtonMenuItem rmDeep4 = new JRadioButtonMenuItem("DEEP-4", searchDeepLevel == 4);
        JRadioButtonMenuItem rmDeep6 = new JRadioButtonMenuItem("DEEP-6", searchDeepLevel == 6);
        JRadioButtonMenuItem rmDeep8 = new JRadioButtonMenuItem("DEEP-8", searchDeepLevel == 8);
        addLambdaMouseListener(rmDeep4, e -> Application.config().setSearchDeepLevel(4));
        addLambdaMouseListener(rmDeep6, e -> Application.config().setSearchDeepLevel(6));
        addLambdaMouseListener(rmDeep8, e -> Application.config().setSearchDeepLevel(8));
        searchDeepBtnGroup.add(rmDeep4);
        searchDeepBtnGroup.add(rmDeep6);
        searchDeepBtnGroup.add(rmDeep8);
        muSetting.add(rmDeep4);
        muSetting.add(rmDeep6);
        muSetting.add(rmDeep8);
        // 初始化

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

    private void addLambdaMouseListener(Component component, @NonNull LambdaMouseListener listener) {
        component.addMouseListener(listener);
    }
}

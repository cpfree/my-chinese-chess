package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.ctrl.*;
import cn.cpf.app.chess.inter.LambdaMouseListener;
import cn.cpf.app.chess.modal.PlayerType;
import cn.cpf.app.chess.util.JsonUtils;
import com.github.cosycode.common.util.io.FileSystemUtils;
import com.github.cosycode.common.util.io.IoUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <b>Description : </b> 面板菜单
 * <p>
 * <b>created in </b> 2018/9/8
 * </p>
 *
 * @author CPF
 **/
@Slf4j
public class ChessMenuBar extends JMenuBar {

    private static final long serialVersionUID = 1L;

    public void sendCommand(CommandExecutor.CommandType commandType) {
        Application.context().getCommandExecutor().sendCommand(commandType);
    }

    public ChessMenuBar() {
        addSettingMenu();
        addDebugMenu();
        addMenuToMenuBar("终止撤销或AI计算", e -> sendCommand(CommandExecutor.CommandType.SuspendCallBackOrAiRun));
    }

    private void addSettingMenu() {
        JMenu muSetting = new JMenu("设置");
        add(muSetting);

        addItemToMenu(muSetting, "重新开局", e -> {
            List<ChessPiece> list = ChessDefined.geneDefaultPieceSituation();
            Application.context().init(new Situation(list, new SituationRecord(), Application.config().getFirstPart(), LocalDateTime.now()));
        });

        muSetting.add(new JSeparator());

        addItemToMenu(muSetting, "保存棋局", e -> {
            try {
                Situation situation = Application.context().getSituation();
                final String str = JsonUtils.toJson(situation);
                String fileName = String.format("situation-%s-%s.chess.bin", situation.getSituationStartTime().toEpochSecond(ZoneOffset.of("+8")), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd-HHmmss")));
                final String userDir = System.getProperty("user.dir");
                final File saveDir = new File(userDir + File.separator + "save");
                FileSystemUtils.insureFileDirExist(saveDir);
                File file = new File(saveDir.getPath() + File.separator + fileName);
                assert !file.exists() : "文件已存在";
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(str);
                    writer.flush();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        addItemToMenu(muSetting, "加载棋局", e -> {
            final String userDir = System.getProperty("user.dir");
            File file = new File(userDir + File.separator + "save/");
            if (!file.exists() || !file.isDirectory()) {
                file = file.getParentFile();
            }
            JFileChooser jfc = new JFileChooser(file);
            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            jfc.showOpenDialog(muSetting);
            File f = jfc.getSelectedFile();
            if (f != null) {
                try {
                    final String json = IoUtils.readFile(f).trim();
                    final Situation situation = JsonUtils.fromJson(json, Situation.class);
                    Application.context().init(situation);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        muSetting.add(new JSeparator());

        /* 添加移动棋子动画选择框 */
        JCheckBoxMenuItem cm = new JCheckBoxMenuItem("移动棋子动画");
        addLambdaMouseListener(cm, e -> Application.config().setCartoon(cm.getState()));
        muSetting.add(cm);
        // 初始化值
        cm.setState(Application.config().isCartoon());

        muSetting.add(new JSeparator());

        /* 添加 AI 计算模式选择框 */
        JCheckBoxMenuItem checkMod = new JCheckBoxMenuItem("被将军时也可以乱动");
        addLambdaMouseListener(checkMod, e -> Application.config().setActiveWhenBeCheck(checkMod.getState()));
        muSetting.add(checkMod);
        // 初始化值
        checkMod.setState(Application.config().isActiveWhenBeCheck());

        muSetting.add(new JSeparator());

        /* 添加 AI 计算模式选择框 */
        JCheckBoxMenuItem aiCalcMod = new JCheckBoxMenuItem("多核并行计算");
        addLambdaMouseListener(aiCalcMod, e -> Application.config().setParallel(aiCalcMod.getState()));
        muSetting.add(aiCalcMod);
        // 初始化值
        aiCalcMod.setState(Application.config().isParallel());

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
        JRadioButtonMenuItem rmDeep4 = new JRadioButtonMenuItem("搜索深度-4", searchDeepLevel == 4);
        JRadioButtonMenuItem rmDeep6 = new JRadioButtonMenuItem("搜索深度-6", searchDeepLevel == 6);
        JRadioButtonMenuItem rmDeep8 = new JRadioButtonMenuItem("搜索深度-8", searchDeepLevel == 8);
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
        JMenu muDebug = new JMenu("功能");
        add(muDebug);
        addItemToMenu(muDebug, "终止撤销或AI计算", e -> sendCommand(CommandExecutor.CommandType.SuspendCallBackOrAiRun));
        addItemToMenu(muDebug, "撤销一步", e -> sendCommand(CommandExecutor.CommandType.CallBackOneTime));
        addItemToMenu(muDebug, "持续撤销", e -> sendCommand(CommandExecutor.CommandType.SustainCallBack));
        addItemToMenu(muDebug, "AI计算一次", e -> sendCommand(CommandExecutor.CommandType.AiRunOneTime));
        addItemToMenu(muDebug, "AI持续运算", e -> sendCommand(CommandExecutor.CommandType.SustainAiRun));
        addItemToMenu(muDebug, "COM角色运行", e -> sendCommand(CommandExecutor.CommandType.SustainAiRunIfNextIsAi));
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

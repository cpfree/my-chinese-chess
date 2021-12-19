package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.modal.PlayerType;
import cn.cpf.app.chess.swing.BoardPanel;
import com.github.cosycode.common.ext.hub.Throws;
import com.github.cosycode.common.thread.CtrlLoopThreadComp;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

/**
 * <b>Description : </b> 命令执行器, 用于处理走棋中的命令
 * <p>
 * <b>特点: </b>每一次只能够执行一条命令,
 * </p>
 *
 * <p>
 * <b>created in </b> 2021/8/28
 * </p>
 *
 * @author CPF
 * @since 1.0
 **/
@Slf4j
public class CommandExecutor {

    /**
     * 异步调用线程, 来处理走棋命令
     */
    private final CtrlLoopThreadComp ctrlLoopThreadComp;
    private final BoardPanel boardPanel;
    /**
     * 是否持续运行标记
     */
    private volatile boolean sustain;

    public CommandExecutor(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
        this.ctrlLoopThreadComp = CtrlLoopThreadComp.ofRunnable(this::loop)
                .setMillisecond(Application.config().getComIntervalTime())
                .setName("CommandExecutor")
                .catchFun(CtrlLoopThreadComp.CATCH_FUNCTION_CONTINUE);
    }

    /**
     * 下一步骤命令
     */
    private CommandType nextCommand;

    /**
     * @param commandType 命令类型
     */
    public synchronized void sendCommand(@NonNull CommandType commandType) {
        this.nextCommand = commandType;
        sustain = false;
        this.ctrlLoopThreadComp.startOrWake();
    }

    private void loop() {
        final CommandType command;
        synchronized (this) {
            command = this.nextCommand;
            this.nextCommand = null;
        }
        if (command != null) {
            try {
                log.debug("处理事件[{}] start", command.getLabel());
                consumerCommand(command);
                log.debug("处理事件[{}] end ", command.getLabel());
            } catch (Exception e) {
                log.error("执行命令[{}]发生异常", command.getLabel(), e);
                new Thread(() -> JOptionPane.showMessageDialog(boardPanel, e.getMessage(), e.toString(), JOptionPane.ERROR_MESSAGE)).start();
            }
        } else {
            this.ctrlLoopThreadComp.pause();
        }
    }

    /**
     * 运行
     */
    private void consumerCommand(final CommandType commandType) {
        switch (commandType) {
            case SuspendCallBackOrAiRun:
                break;
            case CallBackOneTime:
                Application.context().rollbackOneStep();
                break;
            case AiRunOneTime:
                Application.context().aiRunOneTime();
                break;
            case SustainCallBack:
                sustain = true;
                while (sustain) {
                    if (!Application.context().rollbackOneStep()) {
                        sustain = false;
                        break;
                    }
                    Throws.con(400, Thread::sleep).logThrowable();
                }
                break;
            case SustainAiRun:
                sustain = true;
                while (sustain) {
                    if (Application.context().aiRunOneTime() != null) {
                        log.debug("已经决出胜方, AI执行暂停!");
                        sustain = false;
                        break;
                    }
                    Throws.con(400, Thread::sleep).logThrowable();
                }
                break;
            case SustainAiRunIfNextIsAi:
                sustain = true;
                while (sustain) {
                    // 如果下一步棋手不是 AI, 则暂停
                    if (!PlayerType.COM.equals(Application.config().getPlayerType(Application.context().getSituation().getNextPart()))) {
                        sustain = false;
                        log.debug("下一步棋手不是 AI, 暂停!");
                    } else if (Application.context().aiRunOneTime() != null) {
                        log.debug("已经决出胜方, AI执行暂停!");
                        sustain = false;
                    } else {
                        Throws.con(400, Thread::sleep).logThrowable();
                    }
                }
                break;
        }
    }

    /**
     * 命令支持枚举
     */
    @SuppressWarnings("java:S115")
    public enum CommandType {
        SuspendCallBackOrAiRun("停止撤销|AI计算"),
        CallBackOneTime("撤销一步"),
        SustainCallBack("持续撤销"),
        AiRunOneTime("AI计算一步"),
        SustainAiRun("AI持续运行"),
        SustainAiRunIfNextIsAi("COM角色运行");

        @Getter
        private final String label;

        CommandType(String label) {
            this.label = label;
        }
    }

}
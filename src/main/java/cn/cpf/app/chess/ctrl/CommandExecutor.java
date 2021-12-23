package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.PlayerType;
import cn.cpf.app.chess.swing.BoardPanel;
import com.github.cosycode.common.ext.hub.Throws;
import com.github.cosycode.common.lang.ShouldNotHappenException;
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
                .setName("CommandExecutor")
                .catchFun(CtrlLoopThreadComp.CATCH_FUNCTION_CONTINUE);
    }

    /**
     * 下一步骤命令
     */
    private CommandType nextCommand;
    /**
     * 下一步骤命令的参数
     */
    private Object nextParamObj;

    private volatile boolean isRun;

    /**
     * @param commandType 命令类型
     */
    public void sendCommand(@NonNull CommandType commandType) {
        sendCommand(commandType, null);
    }

    /**
     * @param commandType 命令类型
     * @param paramObj    命令参数
     */
    public synchronized void sendCommand(@NonNull CommandType commandType, Object paramObj) {
        this.nextCommand = commandType;
        this.nextParamObj = paramObj;
        sustain = false;
        this.ctrlLoopThreadComp.startOrWake();
    }

    /**
     * 只有在 线程没有运行的情况下, 才能添加成功
     *
     * @param commandType 命令类型
     * @param paramObj    命令参数
     * @return 是否添加成功
     */
    public synchronized boolean sendCommandWhenNotRun(@NonNull CommandType commandType, Object paramObj) {
        if (isRun) {
            return false;
        }
        sendCommand(commandType, paramObj);
        return true;
    }

    private void loop() {
        final CommandType command;
        final Object paramObj;
        synchronized (this) {
            command = this.nextCommand;
            paramObj = this.nextParamObj;
            this.nextCommand = null;
            this.nextParamObj = null;
        }
        if (command != null) {
            isRun = true;
            try {
                log.debug("处理事件[{}] start", command.getLabel());
                consumerCommand(command, paramObj);
                log.debug("处理事件[{}] end ", command.getLabel());
            } catch (Exception e) {
                log.error("执行命令[{}]发生异常", command.getLabel(), e);
                new Thread(() -> JOptionPane.showMessageDialog(boardPanel, e.getMessage(), e.toString(), JOptionPane.ERROR_MESSAGE)).start();
            }
        } else {
            this.ctrlLoopThreadComp.pause();
            isRun = false;
        }
    }

    /**
     * 运行
     */
    private void consumerCommand(final CommandType commandType, Object paramObj) {
        switch (commandType) {
            case SuspendCallBackOrAiRun:
                break;
            case CallBackOneTime:
                Application.context().rollbackOneStep();
                break;
            case AiRunOneTime:
                if (Application.context().aiRunOneTime() != null) {
                    log.debug("已经决出胜方!");
                }
                break;
            case SustainCallBack:
                sustain = true;
                while (sustain) {
                    if (!Application.context().rollbackOneStep()) {
                        sustain = false;
                        break;
                    }
                    Throws.con(Application.config().getComIntervalTime(), Thread::sleep).logThrowable();
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
                    Throws.con(Application.config().getComIntervalTime(), Thread::sleep).logThrowable();
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
                        Throws.con(Application.config().getComIntervalTime(), Thread::sleep).logThrowable();
                    }
                }
                break;
            case LocationPiece:
                final Object[] params = (Object[]) paramObj;
                Place from = (Place) params[0];
                Place to = (Place) params[1];
                PlayerType type = (PlayerType) params[2];
                Application.context().locatePiece(from, to, type);
                sendCommand(CommandExecutor.CommandType.SustainAiRunIfNextIsAi);
                break;
            default:
                throw new ShouldNotHappenException("未处理的命令: " + commandType);
        }
    }

    /**
     * 命令支持枚举(以下命令应当使用同一个线程运行, 一个事件结束之后, 另一个事件才能开始运行.)
     */
    @SuppressWarnings("java:S115")
    public enum CommandType {
        SuspendCallBackOrAiRun("停止撤销|AI计算"),
        CallBackOneTime("撤销一步"),
        SustainCallBack("持续撤销"),
        AiRunOneTime("AI计算一步"),
        SustainAiRun("AI持续运行"),
        SustainAiRunIfNextIsAi("COM角色运行"),
        LocationPiece("ui落子命令");

        @Getter
        private final String label;

        CommandType(String label) {
            this.label = label;
        }
    }

}
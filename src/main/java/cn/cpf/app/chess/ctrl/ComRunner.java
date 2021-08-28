package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.conf.ChessConfig;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.PlayerType;
import cn.cpf.app.chess.modal.StepBean;
import cn.cpf.app.chess.swing.BoardPanel;
import com.github.cosycode.common.thread.CtrlLoopThreadComp;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/8/28
 * </p>
 *
 * @author CPF
 * @since 1.0
 **/
@Slf4j
public class ComRunner {

    private final BoardPanel boardPanel;
    private final CtrlLoopThreadComp comRunThread;
    /**
     * com 是否可运行标记, 如果
     */
    @Getter
    private boolean comRunnable;
    /**
     * com 强制运行次数
     */
    @Getter
    private int forceRunTime;

    public ComRunner(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
        this.comRunThread = CtrlLoopThreadComp.ofRunnable(this::loop)
                .setContinueIfException(true)
                .setMillisecond(ChessConfig.INTERVAL_TIME)
                .setName("COM-RUN");
    }

    /**
     * 运行一次
     */
    public void runOneTime() {
        comRunnable = false;
        forceRunTime = 1;
        comRunThread.startOrWake();
    }

    /**
     * 运行多次
     */
    public void runEnable() {
        comRunnable = true;
        forceRunTime = 0;
        comRunThread.startOrWake();
    }

    /**
     * 如果 com 运行运行, 则运行一次
     */
    public void runIfEnable() {
        if (comRunnable) {
            runOneTime();
        }
    }

    /**
     * 停止运行
     */
    public void stopRun() {
        comRunnable = false;
        forceRunTime = 0;
        comRunThread.pause();
    }

    /**
     * 运行
     */
    private void loop() {
        log.info("com run start");
        while (forceRunTime > 0 || comRunnable) {
            if (forceRunTime > 0) {
                forceRunTime--;
            }
            try {
                final ControlCenter instance = Application.instance();
                // 若当前执棋手是 COM
                if (PlayerType.COM.equals(ChessConfig.getPlayerType(instance.getSituation().getNextPart()))) {
                    StepBean evaluatedPlace = instance.computeStepBean();
                    Part part = instance.locatePiece(evaluatedPlace.from, evaluatedPlace.to);
                    // 落子
                    // 判断是否结束
                    if (part != null) {
                        JOptionPane.showMessageDialog(null, part.name() + "胜利", "游戏结束了", JOptionPane.INFORMATION_MESSAGE);
                        comRunnable = false;
                    }
                } else {
                    break;
                }
            } catch (Exception e) {
                log.error("", e);
                new Thread(() -> JOptionPane.showMessageDialog(boardPanel, e.getMessage(), e.toString(), JOptionPane.ERROR_MESSAGE)).start();
                // 暂停COM
                comRunnable = false;
            }
        }
        log.info("com run stop");
    }

}
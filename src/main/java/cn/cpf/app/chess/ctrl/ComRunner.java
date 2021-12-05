package cn.cpf.app.chess.ctrl;

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
                .setMillisecond(Application.config().getComIntervalTime())
                .setName("COM-RUN");
    }

    /**
     * 运行一次, 无论下一步棋手类型是COM还是人类
     */
    public void runOneTime() {
        comRunnable = false;
        forceRunTime = 1;
        comRunThread.startOrWake();
    }

    /**
     * 连续运行
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
        log.debug("COM 运行完当前循环后暂停");
    }

    /**
     * 运行
     */
    private void loop() {
        log.info("com run start");
        if (forceRunTime > 0) {
            forceRunTime--;
        }
        if (forceRunTime <= 0 && !comRunnable) {
            stopRun();
        }
        final AppContext instance = Application.context();
        try {
            StepBean evaluatedPlace = instance.computeStepBean();
            Part part = instance.locatePiece(evaluatedPlace.from, evaluatedPlace.to);
            // 判断是否结束
            if (part != null) {
                stopRun();
            }
        } catch (Exception e) {
            log.error("出现异常", e);
            stopRun();
            new Thread(() -> JOptionPane.showMessageDialog(boardPanel, e.getMessage(), e.toString(), JOptionPane.ERROR_MESSAGE)).start();
        }
        // 如果下一步棋手不是 AI, 则暂停
        if (!PlayerType.COM.equals(Application.config().getPlayerType(instance.getSituation().getNextPart()))) {
            stopRun();
            return;
        }

        log.info("com run stop");
    }

}
package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.algorithm.AnalysisBean;
import cn.cpf.app.chess.conf.ChessAudio;
import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.conf.ChessImage;
import cn.cpf.app.chess.ctrl.Application;
import cn.cpf.app.chess.ctrl.CommandExecutor;
import cn.cpf.app.chess.ctrl.Situation;
import cn.cpf.app.chess.inter.LambdaMouseListener;
import cn.cpf.app.chess.inter.MyList;
import cn.cpf.app.chess.modal.*;
import cn.cpf.app.chess.util.ListPool;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;

/**
 * <b>Description : </b> 棋盘面板
 * <p>
 * <b>created in </b> 2018/8/27
 *
 * @author CPF
 * @since 1.0
 **/
@Slf4j
public class BoardPanel extends JPanel implements LambdaMouseListener {

    /**
     * 用于标记棋盘走棋痕迹
     */
    private final transient TraceMarker traceMarker;
    /**
     * 当前走棋开始坐标位置对应棋子
     */
    private transient ChessPiece curFromPiece;
    /**
     * 场景
     */
    private transient Situation situation;

    /**
     * Create the panel.
     */
    public BoardPanel() {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(null);
        // 初始化标记符
        traceMarker = new TraceMarker(BoardPanel.this);
        // 添加鼠标事件
        addMouseListener(this);
    }

    /**
     * 更新标记
     */
    public void updateMark(Place from, Place to) {
        // 更新标记
        curFromPiece = null;
        // 更改标记
        traceMarker.endedStep(from, to);
    }

    /**
     * 初始化所有标记
     */
    public void initMark() {
        traceMarker.initMarker();
    }

    /**
     * 添加棋子
     */
    public void init(Situation situation) {
        this.situation = situation;
        // 移除所有组件
        this.removeAll();
        // 添加棋子
        situation.getPieceList().forEach(it -> add(it.getComp()));
        situation.getSituationRecord().getEatenPieceList().forEach(it -> add(it.getComp()));
        // 初始化标记符
        traceMarker.initMarker();
        repaint();
    }

    /**
     * @param e 鼠标按压事件对象
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // 位置
        Place pointerPlace = ChessDefined.convertLocationToPlace(e.getPoint());
        if (pointerPlace == null) {
            return;
        }
        if (situation.winner() != null) {
            log.warn("已经存在胜利者: {}, 无法走棋", situation.winner());
            return;
        }
        // 当前走棋方
        @NonNull Part pointerPart = situation.getNextPart();
        // 当前焦点棋子
        ChessPiece pointerPiece = situation.getChessPiece(pointerPlace);
        // 通过当前方和当前位置判断是否可以走棋
        // step: form
        if (curFromPiece == null) {
            // 当前焦点位置有棋子且是本方棋子
            if (pointerPiece != null && pointerPiece.piece.part == pointerPart) {
                // 本方棋子, 同时是from指向
                curFromPiece = pointerPiece;
                traceMarker.setMarkFromPlace(pointerPlace);
                // 获取toList
                MyList<Place> list = curFromPiece.piece.role.find(new AnalysisBean(situation.generatePieces()), pointerPart, pointerPlace);
                traceMarker.showMarkPlace(list);
                ChessAudio.CLICK_FROM.play();
                log.info("true -> 当前焦点位置有棋子且是本方棋子");
                final ListPool listPool = ListPool.localPool();
                listPool.addListToPool(list);
                return;
            }
            log.warn("warning -> from 焦点指示错误");
            return;
        }
        if (pointerPlace.equals(curFromPiece.getPlace())) {
            log.warn("false -> from == to");
            return;
        }
        // 当前焦点位置有棋子且是本方棋子
        if (pointerPiece != null && pointerPiece.piece.part == pointerPart) {
            assert curFromPiece.piece.part == pointerPart : "当前焦点位置有棋子且是本方棋子 之前指向了对方棋子";
            // 更新 curFromPiece
            curFromPiece = pointerPiece;
            traceMarker.setMarkFromPlace(pointerPlace);
            MyList<Place> list = curFromPiece.piece.role.find(new AnalysisBean(situation.generatePieces()), pointerPart, pointerPlace);
            traceMarker.showMarkPlace(list);
            ChessAudio.CLICK_FROM.play();
            log.info("true -> 更新 curFromPiece");
            ListPool.localPool().addListToPool(list);
            return;
        }
        final StepBean stepBean = StepBean.of(curFromPiece.getPlace(), pointerPlace);
        // 如果不符合规则则直接返回
        final Piece[][] pieces = situation.generatePieces();
        if (!curFromPiece.piece.role.rule.check(pieces, pointerPart, stepBean.from, stepBean.to)) {
            // 如果当前指向棋子是本方棋子
            log.warn("不符合走棋规则");
            return;
        }
        // 如果达成长拦或者长捉, 则返回
        final StepBean forbidStepBean = situation.getForbidStepBean();
        if (forbidStepBean != null && forbidStepBean.from == stepBean.from && forbidStepBean.to == stepBean.to) {
            ChessAudio.MAN_MOV_ERROR.play();
            log.warn("长拦或长捉");
            return;
        }
        AnalysisBean analysisBean = new AnalysisBean(pieces);
        // 如果走棋后, 导致两个 BOSS 对面, 则返回
        if (!analysisBean.isBossF2FAfterStep(curFromPiece.piece, stepBean.from, stepBean.to)) {
            ChessAudio.MAN_MOV_ERROR.play();
            log.warn("BOSS面对面");
            return;
        }
        /* 模拟走一步棋, 之后再计算对方再走一步是否能够吃掉本方的 boss */
        if (analysisBean.simulateOneStep(stepBean, bean -> bean.canEatBossAfterOneAiStep(Part.getOpposite(pointerPart)))) {
            ChessAudio.MAN_MOV_ERROR.play();
            log.warn("BOSS 危险");
            if (!Application.config().isActiveWhenBeCheck()) {
                return;
            }
        }
        // 当前棋子无棋子或者为对方棋子, 且符合规则, 可以走棋
        Object[] objects = new Object[]{stepBean.from, stepBean.to, PlayerType.PEOPLE};
        final boolean sendSuccess = Application.context().getCommandExecutor().sendCommandWhenNotRun(CommandExecutor.CommandType.LocationPiece, objects);
        if (!sendSuccess) {
            log.warn("命令未发送成功: {} ==> {}", CommandExecutor.CommandType.LocationPiece, Arrays.toString(objects));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image img = ChessImage.CHESS_BOARD.getImage();
        int imgWidth = img.getWidth(this);
        int imgHeight = img.getHeight(this);// 获得图片的宽度与高度
        int fWidth = getWidth();
        int fHeight = getHeight();// 获得窗口的宽度与高度
        int x = (fWidth - imgWidth) / 2;
        int y = (fHeight - imgHeight) / 2;
        // 520 576 514 567
        log.debug(String.format("%s,%s,%s,%s,%s,%s", imgWidth, imgHeight, fWidth, fHeight, x, y));
        g.drawImage(img, 0, 0, null);
    }

}

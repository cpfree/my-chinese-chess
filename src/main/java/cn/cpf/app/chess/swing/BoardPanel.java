package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.conf.ChessImage;
import cn.cpf.app.chess.ctrl.Application;
import cn.cpf.app.chess.ctrl.Situation;
import cn.cpf.app.chess.inter.LambdaMouseListener;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Place;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

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
    private Situation situation;

    /**
     * Create the panel.
     */
    public BoardPanel() {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(null);
        // 初始化标记符
        traceMarker = new TraceMarker(BoardPanel.this);
    }

    /**
     * gui 落子, 面板执行落子的操作
     */
    public void updateMark(Place from, Place to) {
        // 更新标记
        curFromPiece = null;
        // 更改标记
        traceMarker.endedStep(from, to);
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
        // 初始化标记符
        traceMarker.initMarker();
        // 添加鼠标事件
        addMouseListener(this);
    }

    /**
     * @param e 鼠标按压事件对象
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // 位置
        Place pointerPlace = ChessDefined.convertLocationToPlace(e.getPoint());
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
                curFromPiece = situation.getChessPiece(pointerPlace);
                traceMarker.setMarkFromPlace(pointerPlace);
                // 获取toList
                List<Place> list = curFromPiece.piece.role.find(situation.genePiece(), pointerPart, pointerPlace);
                traceMarker.showMarkPlace(list);
                log.info("true -> 当前焦点位置有棋子且是本方棋子");
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
            List<Place> list = curFromPiece.piece.role.find(situation.genePiece(), pointerPart, pointerPlace);
            traceMarker.showMarkPlace(list);
            log.info("true -> 更新 curFromPiece");
            return;
        }
        // 如果不符合规则则直接返回
        if (!curFromPiece.piece.role.check(situation.genePiece(), pointerPart, curFromPiece.getPlace(), pointerPlace)) {
            // 如果当前指向棋子是本方棋子
            log.warn("不符合走棋规则");
            return;
        }
        // 当前棋子无棋子或者为对方棋子, 且符合规则, 可以走棋
        setEnabled(false);
        // 落子
        Part part = Application.instance().locatePiece(curFromPiece.getPlace(), pointerPlace);
        if (part != null) {
            JOptionPane.showMessageDialog(null, part.name() + "胜利", "游戏结束了", JOptionPane.INFORMATION_MESSAGE);
        }
        setEnabled(true);

        Application.instance().getComRunner().runIfEnable();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        log.warn("false22 -> from == to");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        log.warn("false21 -> from == to");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        log.warn("false12 -> from == to");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        log.warn("false123 -> from == to");
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
        log.info(String.format("%s,%s,%s,%s,%s,%s", imgWidth, imgHeight, fWidth, fHeight, x, y));
        g.drawImage(img, 0, 0, null);
    }

}

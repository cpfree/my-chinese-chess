package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.bean.ChessPiece;
import cn.cpf.app.chess.domain.Situation;
import cn.cpf.app.chess.inter.LambdaMouseListener;
import cn.cpf.app.chess.main.ChessConfig;
import cn.cpf.app.chess.res.*;
import com.sun.istack.internal.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

@Slf4j
public class BoardPanel extends JPanel {

    /**
     * 棋局形势
     */
    private Situation situation;

    private ChessPiece curFromPiece;

    private MarkHandle markHandle;

    private class MarkHandle {
        JPiece lastMarkFrom = new JPiece(ChessImage.Pointer.getImage());
        JPiece lastMarkTo = new JPiece(ChessImage.Pointer.getImage());
        JPiece MarkFrom = new JPiece(ChessImage.Pointer.getImage());
        JPiece curMark = new JPiece(ChessImage.Pointer.getImage());

        void endedStep(Place place) {
            markHandle.lastMarkFrom.setPlaceAndShow(markHandle.MarkFrom.getPlace());
            markHandle.lastMarkTo.setPlaceAndShow(place);
            markHandle.curMark.setVisible(false);
            markHandle.MarkFrom.setVisible(false);
        }

    }

    /**
     * Create the panel.
     */
    public BoardPanel() {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        // contentPane.setLayout(new BorderLayout(0, 0));
        setLayout(null);
        // 添加棋子
        init();
        // 添加棋盘监听器
        addPanelListener();
    }

    /**
     * 添加棋盘监听器
     */
    private void addPanelListener() {
        addMouseListener((LambdaMouseListener) e -> {
            // 位置
            Place pointerPlace = ChessDefined.convertLocationToPlace(e.getPoint());
            // 当前方
            @NonNull Part pointerPart = situation.getNextPart();
            // 当前焦点棋子
            @Nullable ChessPiece pointerPiece = situation.getPiece(pointerPlace);

            // 通过当前方和当前位置判断是否可以走棋
            // step: form
            if (curFromPiece == null) {
                // 当前焦点位置有棋子且是本方棋子
                if (pointerPiece != null && pointerPiece.part == pointerPart) {
                    // 本方棋子, 同时是from指向
                    curFromPiece = situation.getPiece(pointerPlace);
                    markHandle.MarkFrom.setPlaceAndShow(pointerPlace);
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
            if (pointerPiece != null && pointerPiece.part == pointerPart) {
                assert curFromPiece.part == pointerPart : "当前焦点位置有棋子且是本方棋子 之前指向了对方棋子";
                // 更新 curFromPiece
                curFromPiece = pointerPiece;
                markHandle.MarkFrom.setPlaceAndShow(pointerPlace);
                log.info("true -> 更新 curFromPiece");
                return;
            }
            // 如果不符合规则则直接返回
            if (!curFromPiece.role.check(situation.getAnalysisBean(), pointerPart, curFromPiece.getPlace(), pointerPlace)) {
                // 如果当前指向棋子是本方棋子
                log.warn("不符合走棋规则");
                return;
            }
            // 当前棋子无棋子或者为对方棋子, 且符合规则, 可以走棋
            setEnabled(false);
            // 数据落子
            Place form = curFromPiece.getPlace();
            situation.realLocatePiece(form, pointerPlace);
            // 更新标记
            curFromPiece = null;
            // 更改标记
            markHandle.endedStep(pointerPlace);
            setEnabled(true);
        });
    }

    /**
     * 添加棋子
     */
    private void init() {
        // 移除所有
        this.removeAll();
        // 获取棋子
        List<ChessPiece> list = ChessConfig.geneDefaultPieceSituation();

        // 初始化棋盘
        situation = new Situation();
        situation.init(list);
        list.forEach(it -> add(it.getJLabel()));

        // 初始化标记符
        markHandle = new MarkHandle();
        add(markHandle.curMark.getJLabel());
        add(markHandle.MarkFrom.getJLabel());
        add(markHandle.lastMarkFrom.getJLabel());
        add(markHandle.lastMarkTo.getJLabel());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image img = ChessImage.ChessBoard.getImage();
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

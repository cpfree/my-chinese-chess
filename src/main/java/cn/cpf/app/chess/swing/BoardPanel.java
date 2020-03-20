package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.bean.ChessPiece;
import cn.cpf.app.chess.domain.Situation;
import cn.cpf.app.chess.res.*;
import com.sun.istack.internal.Nullable;
import lombok.NonNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class BoardPanel extends JPanel {

    /**
     * 棋局形势
     */
    private Situation situation;
    /**
     * 活着的棋子
     */
    private ChessPiece[][] chessPieces = null;
    /**
     * 被吃的棋子
     */
    private List<ChessPiece> deadPieces = null;

    private ChessPiece curFromPiece;

    private MarkHandle markHandle;

    private class MarkHandle {
        JPiece lastMarkFrom = new JPiece(ChessImage.Pointer.getImage(), null);
        JPiece lastMarkTo = new JPiece(ChessImage.Pointer.getImage(), null);
        JPiece MarkFrom = new JPiece(ChessImage.Pointer.getImage(), null);
        JPiece curMark = new JPiece(ChessImage.Pointer.getImage(), null);

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
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 位置
                Place place = ChessDefined.convertLocationToPlace(e.getPoint());
                if (place.equals(curFromPiece.getPlace())) {
                    return;
                }
                // 当前方
                @NonNull Part part = situation.getNextPart();

                @Nullable Piece piece = situation.getPiece(place);

                // 通过当前方和当前位置判断是否可以走棋
                // step: form
                if (curFromPiece == null) {
                    // 当前焦点位置有棋子且是本方棋子
                    if (piece != null && piece.part == part) {
                        // 本方棋子, 同时是from指向
                        markHandle.MarkFrom.setPlaceAndShow(place);
                    }
                } else {
                    // 如果不符合规则则直接返回
                    if (!piece.role.getRule().check(situation.getBoardPiece(), part, curFromPiece.getPlace(), place)) {
                        return;
                    }
                    // 当前棋子无棋子或者为对方棋子
                    if (piece == null || piece.part != part) {
                        setEnabled(false);
                        // 数据落子
                        Place form = curFromPiece.getPlace();
                        situation.realLocatePiece(part, piece, form, place);
                        // GUI 落子
                        curFromPiece.setPlace(place);
                        if (piece != null) {
                            ChessPiece chessPiece = chessPieces[place.x][place.y];
                            chessPiece.setVisible(false);
                            BoardPanel.this.remove(chessPiece.getJLabel());
                        }
                        // 更改标记
                        markHandle.endedStep(place);
                        setEnabled(true);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    /**
     * 添加棋子
     */
    private void init() {
        // 移除所有
        this.removeAll();
        markHandle = new MarkHandle();

        situation = new Situation();
        chessPieces = new ChessPiece[ChessDefined.RANGE_X][ChessDefined.RANGE_Y];
        Piece[][] boardPiece = situation.getBoardPiece();
        for (int x = boardPiece.length - 1; x >= 0; x--) {
            for (int y = boardPiece[x].length - 1; y >= 0; y--) {
                Piece piece = boardPiece[x][y];
                ChessPiece comp = new ChessPiece(piece, Place.of(x, y));
                chessPieces[x][y] = comp;
                this.add(comp.getJLabel());
            }
        }
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
        System.out.println(String.format("%s,%s,%s,%s,%s,%s", imgWidth, imgHeight, fWidth, fHeight, x, y));
        g.drawImage(img, x, y, null);
    }
}

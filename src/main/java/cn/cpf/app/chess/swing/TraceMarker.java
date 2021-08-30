package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.conf.ChessImage;
import cn.cpf.app.chess.modal.Place;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>Description : </b> 用于标记棋盘上走棋的路径
 * <p>
 * <b>created in </b> 2018/8/27
 *
 * @author CPF
 * @since 1.0
 **/
class TraceMarker {

    private final Container container;

    private final JPiece lastMarkFrom;
    private final JPiece lastMarkTo;
    /**
     * 当前走棋的棋子标记
     */
    private final JPiece markFrom;
    /**
     * 鼠标在棋盘移动的标记
     */
    private final JPiece pointerMark;
    /**
     * 存放可以落子的位置标记
     */
    private final java.util.List<JPiece> toMarkList;


    TraceMarker(Container container) {
        this.container = container;
        lastMarkFrom = new JPiece(ChessImage.POINTER.getImage());
        lastMarkTo = new JPiece(ChessImage.POINTER.getImage());
        markFrom = new JPiece(ChessImage.POINTER.getImage());
        pointerMark = new JPiece(ChessImage.POINTER.getImage());
        toMarkList = new ArrayList<>();
    }

    /**
     * 初始化所有标记
     */
    void initMarker() {
        container.add(pointerMark.getComp());
        container.add(markFrom.getComp());
        container.add(lastMarkFrom.getComp());
        container.add(lastMarkTo.getComp());
        lastMarkFrom.hide();
        lastMarkTo.hide();
        pointerMark.hide();
        markFrom.hide();
        showMarkPlace(null);
    }

    void setMarkFromPlace(Place place) {
        markFrom.setPlaceAndShow(place);
    }

    /**
     * 结束一部走棋, 重置标记
     */
    void endedStep(Place from, Place to) {
        lastMarkFrom.setPlaceAndShow(from);
        lastMarkTo.setPlaceAndShow(to);
        pointerMark.hide();
        markFrom.hide();
        showMarkPlace(null);
    }

    /**
     * 为 集合中的所有位置 显示 可以落子的标记, 如果传入为 null or 空集合 则, 清空所有 可以落子的标记
     */
    void showMarkPlace(List<Place> placeList) {
        // 清空所有 可以落子的标记
        toMarkList.forEach(JPiece::hide);
        if (placeList == null || placeList.isEmpty()) {
            return;
        }
        // 如果 toMarkList 里面有足够的标记, 则直接从 toMarkList 里面取, 否则新建标记对象后, 添加到 toMarkList中
        int size = toMarkList.size();
        for (int i = 0; i < placeList.size(); i++) {
            if (size <= i) {
                JPiece piece = new JPiece(ChessImage.POINTER.getImage());
                toMarkList.add(i, piece);
                container.add(piece.getComp());
            }
            JPiece jPiece = toMarkList.get(i);
            jPiece.setPlaceAndShow(placeList.get(i));
        }
    }
}

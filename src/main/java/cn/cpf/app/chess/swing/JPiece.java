package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.ctrl.Application;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.util.SwingUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/3/18 14:39
 */
@Slf4j
@ToString
public class JPiece {

    @Getter
    private final JLabel comp;

    @Getter
    private Place place;

    public JPiece(Image image) {
        this(image, null);
    }

    public JPiece(Image image, Place place) {
        comp = new JLabel();
        comp.setSize(ChessDefined.PIECE_WIDTH, ChessDefined.PIECE_HEIGHT);
        comp.setIcon(new ImageIcon(image));
        if (place == null) {
            this.hide();
        } else {
            this.setPlace(place);
        }
    }

    public JPiece(Piece piece, Place place) {
        this(piece.image, place);
        comp.setText(piece.name());
        if (piece.part == Part.RED) {
            comp.setForeground(Color.red);
        } else {
            comp.setForeground(Color.black);
        }
    }

    /**
     * 棋子直接移动到棋盘上 place 对应的坐标
     */
    public void setPlace(@NonNull Place place) {
        this.place = place;
        comp.setLocation(ChessDefined.convertPlaceToLocation(place.x, place.y));
    }

    public void setVisible(boolean visible) {
        comp.setVisible(visible);
    }

    public void hide() {
        this.setVisible(false);
        this.setPlace(Place.NULL_PLACE);
    }

    public void setPlaceAndShow(Place place) {
        this.setPlace(place);
        this.setVisible(true);
    }

    /**
     * 棋子移动到棋盘上 place 对应的坐标(带动画)
     */
    public void movePlace(@NonNull Place place) {
        if (Application.config().isCartoon()) {
            uiMovePlace(place);
        } else {
            setPlace(place);
        }
    }

    /**
     * 棋子移动到棋盘上 place 对应的坐标(带动画)
     */
    private void uiMovePlace(@NonNull Place place) {
        this.place = place;
        Point toPoint = ChessDefined.convertPlaceToLocation(place.x, place.y);
        try {
            SwingUtils.moveComp(comp, toPoint);
        } catch (InterruptedException e) {
            log.error("移动处理动画时发生异常", e);
            Thread.currentThread().interrupt();
        }
    }

}
package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.res.*;
import lombok.Getter;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/3/18 14:39
 */
public class JPiece {

    @Getter
    private JLabel comp;

    @Getter
    private Place place;

    public JPiece(Image image) {
        this(image, null);
    }

    public JPiece(Image image, Place place) {
        comp = new JLabel();
        comp.setSize(ChessDefined.PIECE_WIDTH, ChessDefined.PIECE_WIDTH);
        comp.setIcon(new ImageIcon(image));
        if (place == null) {
            this.hide();
        } else {
            this.setPlace(place);
        }
    }

    public JPiece(Piece piece, Place place) {
        comp = new JLabel();
        comp.setSize(ChessDefined.PIECE_WIDTH, ChessDefined.PIECE_WIDTH);
        comp.setText(piece.name().replaceAll("Red|Black", ""));
        if (piece.part == Part.RED) {
            comp.setForeground(Color.red);
        } else {
            comp.setForeground(Color.black);
        }
        if (place == null) {
            this.hide();
        } else {
            this.setPlace(place);
        }
    }

    private void setPlace(int x, int y) {
        comp.setLocation(ChessDefined.convertPlaceToLocation(x, y));
    }

    public void hide() {
        this.setVisible(false);
        this.setPlace(-1, -1);
    }

    public void setPlace(@NonNull Place place) {
        this.place = place;
        this.setPlace(place.x, place.y);
    }

    public void setPlaceAndShow(Place place) {
        this.setPlace(place);
        this.setVisible(true);
    }

    public void setVisible(boolean visible) {
        comp.setVisible(visible);
    }

}
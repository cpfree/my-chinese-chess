package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.res.*;
import lombok.Getter;

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
    private JLabel jLabel;

    @Getter
    private Place place;

    public JPiece(Image image, Place place) {
        jLabel.setSize(ChessDefined.PIECE_WIDTH, ChessDefined.PIECE_WIDTH);
        jLabel.setIcon(new ImageIcon(image));
        this.setPlace(place);
    }

    public void setPlace(int x, int y) {
        this.place = Place.of(x, y);
        jLabel.setLocation(ChessDefined.convertPlaceToLocation(x, y));
    }

    public void setPlace(Place place) {
        this.place = place;
        if (place == null) {
            place = Place.Null_Place;
        }
        this.setPlace(place.x, place.y);
    }

    public void setPlaceAndShow(Place place) {
        this.setPlace(place);
        this.setVisible(true);
    }

    public void setVisible(boolean visible) {
        jLabel.setVisible(visible);
    }

}
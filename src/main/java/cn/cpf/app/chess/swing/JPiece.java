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
    private JLabel jLabel;

    @Getter
    private Place place;

    public JPiece(Image image) {
        jLabel = new JLabel();
        jLabel.setSize(ChessDefined.PIECE_WIDTH, ChessDefined.PIECE_WIDTH);
        jLabel.setIcon(new ImageIcon(image));
        this.hide();
    }

    public JPiece(Image image, @NonNull Place place) {
        jLabel = new JLabel();
        jLabel.setSize(ChessDefined.PIECE_WIDTH, ChessDefined.PIECE_WIDTH);
        jLabel.setIcon(new ImageIcon(image));
        this.setPlace(place);
    }

    private void setPlace(int x, int y) {
        jLabel.setLocation(ChessDefined.convertPlaceToLocation(x, y));
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
        jLabel.setVisible(visible);
    }

}
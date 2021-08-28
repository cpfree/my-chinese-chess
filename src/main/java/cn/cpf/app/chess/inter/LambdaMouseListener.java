package cn.cpf.app.chess.inter;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@FunctionalInterface
public interface LambdaMouseListener extends MouseListener {

    @Override
    default void mouseClicked(MouseEvent e) {
    }

    @Override
    default void mouseReleased(MouseEvent e) {
    }

    @Override
    default void mouseEntered(MouseEvent e) {
    }

    @Override
    default void mouseExited(MouseEvent e) {
    }

}

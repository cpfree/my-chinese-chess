package cn.cpf.app.chess.res;

import cn.cpf.app.chess.bean.ChessPiece;

import java.util.List;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/3/19 17:17
 */
public interface Rule {

    /**
     * @param pieces
     * @param part
     * @param from
     * @param to
     * @return
     */
    boolean check(ChessPiece[][] pieces, Part part, Place from, Place to);

    List<Place> find(ChessPiece[][] pieces, Part part, Place place);

    default int checkPlace(ChessPiece chessPiece, Part part) {
        if (chessPiece == null) {
            return 0;
        }
        if (chessPiece.part == part) {
            return -1;
        }
        return 1;
    }

    default void addPlaceIntoList(ChessPiece[][] chessPieces, Part part, List<Place> list, Place place, int x, int y) {
        if (chessPieces[x][y] != null) {
            return;
        }
        ChessPiece piece = chessPieces[place.x][place.y];
        if (piece == null || piece.part == part) {
            list.add(place);
        }
    }

    default void addPlaceIntoList(ChessPiece[][] chessPieces, Part part, List<Place> list, Place place) {
        ChessPiece piece = chessPieces[place.x][place.y];
        if (piece == null || piece.part == part) {
            list.add(place);
        }
    }

}

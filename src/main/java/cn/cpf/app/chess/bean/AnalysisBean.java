package cn.cpf.app.chess.bean;

import cn.cpf.app.chess.base.ArrayUtils;
import cn.cpf.app.chess.res.Part;
import cn.cpf.app.chess.res.Place;
import cn.cpf.app.chess.res.Role;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/3/25 17:33
 */
public class AnalysisBean {

    public ChessPiece[][] chessPieces;

    public AnalysisBean(ChessPiece[][] chessPieces) {
        this.chessPieces = chessPieces;
        for (ChessPiece[] chessPiece : chessPieces) {
            for (ChessPiece piece : chessPiece) {
                if (piece.role == Role.Boss) {
                    if (piece.part == Part.RED) {
                        redBoss = piece.getPlace();
                    } else {
                        blackBoss = piece.getPlace();
                    }
                }
            }
        }
    }

    private Place redBoss;
    private Place blackBoss;

    public Place getOppoPlace(Part curPart) {
        return curPart == Part.RED ? blackBoss : redBoss;
    }

    public void updatePlace(Part part, Place newPlace) {
        if (part == Part.RED) {
            redBoss = newPlace;
        } else {
            blackBoss = newPlace;
        }
    }

    /**
     * @return boss 是否为面对面
     */
    public boolean bossF2fAfterBossMove(Part curPart, Place curNextPlace) {
        Place oppPlace = curPart == Part.RED ? blackBoss : redBoss;
        if (curNextPlace.x != oppPlace.x) {
            return false;
        }
        return ArrayUtils.nullInMiddle(chessPieces[curNextPlace.x], curNextPlace.y, oppPlace.y);
    }

    /**
     * @return boss 是否为面对面
     */
    public boolean bossF2fAfterLeave(Place place) {
        if (redBoss.x != blackBoss.x || place.x != redBoss.x) {
            return false;
        }
        return ArrayUtils.oneInMiddle(chessPieces[redBoss.x], redBoss.y, blackBoss.y);
    }

}

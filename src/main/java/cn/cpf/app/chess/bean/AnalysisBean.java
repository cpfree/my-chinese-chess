package cn.cpf.app.chess.bean;

import cn.cpf.app.chess.base.ArrayUtils;
import cn.cpf.app.chess.res.ChessDefined;
import cn.cpf.app.chess.res.Part;
import cn.cpf.app.chess.res.Place;
import cn.cpf.app.chess.res.Role;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/3/25 17:33
 */
public class AnalysisBean {

    public ChessPiece[][] chessPieces;

    private Place redBoss;
    private Place blackBoss;
    private int redPieceNum;
    private int blackPieceNum;

    public AnalysisBean(ChessPiece[][] chessPieces) {
        this.chessPieces = chessPieces;
        redPieceNum = 0;
        blackPieceNum = 0;
        for (ChessPiece[] chessPiece : chessPieces) {
            for (ChessPiece piece : chessPiece) {
                if (piece != null) {
                    if (piece.part == Part.RED) {
                        redPieceNum++;
                        if (piece.role == Role.Boss) {
                            redBoss = piece.getPlace();
                        }
                    } else {
                        blackPieceNum++;
                        if (piece.role == Role.Boss) {
                            blackBoss = piece.getPlace();
                        }
                    }
                }
            }
        }
    }


    public void goForward(Place from, Place to, ChessPiece eatenPiece) {
        final ChessPiece movePiece = chessPieces[from.x][from.y];
        chessPieces[to.x][to.y] = movePiece;
        chessPieces[from.x][from.y] = null;
        if (movePiece.role == Role.Boss) {
            updatePlace(movePiece.part, to);
        }
        if (eatenPiece != null) {
            if (eatenPiece.part == Part.RED) {
                redPieceNum--;
            } else {
                blackPieceNum--;
            }
        }
    }

    public void backStep(Place from, Place to, ChessPiece eatenPiece) {
        final ChessPiece movePiece = chessPieces[to.x][to.y];
        chessPieces[from.x][from.y] = movePiece;
        chessPieces[to.x][to.y] = eatenPiece;
        // 退回上一步
        if (movePiece.role == Role.Boss) {
            updatePlace(movePiece.part, from);
        }
        if (eatenPiece != null) {
            if (eatenPiece.part == Part.RED) {
                redPieceNum++;
            } else {
                blackPieceNum++;
            }
        }
    }


    /**
     * 返回对本方的实力评估, 本方为正
     * @param curPart
     * @return
     */
    public int getCurPartEvaluateScore(Part curPart) {
        int num = 0;
        for (int x = 0; x < ChessDefined.RANGE_X; x++) {
            for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
                ChessPiece piece = chessPieces[x][y];
                if (piece == null) {
                    continue;
                }
                if (piece.part == curPart) {
                    num += getSingleScore(piece.role, curPart, y);
                } else {
                    num -= getSingleScore(piece.role, curPart, y);
                }
            }
        }
        return num;
    }

    public int getSingleScore(Role role, Part part, int y) {
        switch (role) {
            case Boss:
                return 1000000;
            case car:
                return 1000;
            case horse:
                return 482 - blackPieceNum - redPieceNum;
            case cannon:
                return 448 + blackPieceNum + redPieceNum;
            case soldier:
                if (part == Part.RED) {
                    if (y >= 5) {
                        return 150;
                    } else if (y > 2) {
                        return 300;
                    } else {
                        return 200;
                    }
                } else {
                    if (y <= 4 ) {
                        return 150;
                    } else if (y < 7) {
                        return 300;
                    } else {
                        return 200;
                    }
                }
            case elephant:
            case Counselor:
                return 150;
            default:
                throw new RuntimeException();
        }
    }

    public int getPieceNum(Part curPart) {
        return curPart == Part.RED ? redPieceNum : blackPieceNum;
    }


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

    /**
     * @return boss 是否为面对面
     */
    public List<Place> filterPlace(List<Place> places) {
        return places.stream().filter(it ->
            it.x == redBoss.x && it.y <= redBoss.y && it.y >= blackBoss.y
        ).collect(Collectors.toList());
    }

}

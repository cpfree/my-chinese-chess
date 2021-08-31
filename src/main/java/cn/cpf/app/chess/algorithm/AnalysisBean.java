package cn.cpf.app.chess.algorithm;

import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.util.ArrayUtils;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <b>Description : </b> 用于ai算法的分析运算, 相当于为运算而做的一个副本
 *
 * @author CPF
 * Date: 2020/3/25 17:33
 */
public class AnalysisBean {

    public final Piece[][] pieces;

    private Place redBoss;
    private Place blackBoss;
    private int redPieceNum;
    private int blackPieceNum;

    public AnalysisBean(@NonNull Piece[][] rawPieceArrays) {
        this.pieces = rawPieceArrays;
        redPieceNum = 0;
        blackPieceNum = 0;
        // 找出boss, 和两方棋子数量
        for (int x = 0, xLen = rawPieceArrays.length; x < xLen; x++) {
            Piece[] pieceArr = rawPieceArrays[x];
            for (int y = 0, yLen = pieceArr.length; y < yLen; y++) {
                Piece piece = pieceArr[y];
                if (piece != null) {
                    if (Part.RED == piece.part) {
                        redPieceNum++;
                        if (piece.role == Role.BOSS) {
                            redBoss = Place.of(x, y);
                        }
                    } else {
                        blackPieceNum++;
                        if (piece.role == Role.BOSS) {
                            blackBoss = Place.of(x, y);
                        }
                    }
                }
            }
        }
    }


    /**
     * 模拟走棋
     */
    public void goForward(Place from, Place to, Piece eatenPiece) {
        final Piece movePiece = pieces[from.x][from.y];
        pieces[to.x][to.y] = movePiece;
        pieces[from.x][from.y] = null;
        if (movePiece.role == Role.BOSS) {
            updateBossPlace(movePiece.part, to);
        }
        if (eatenPiece != null) {
            if (eatenPiece.part == Part.RED) {
                redPieceNum--;
            } else {
                blackPieceNum--;
            }
        }
    }

    /**
     * 模拟后退
     */
    public void backStep(Place from, Place to, Piece eatenPiece) {
        final Piece movePiece = pieces[to.x][to.y];
        pieces[from.x][from.y] = movePiece;
        pieces[to.x][to.y] = eatenPiece;
        // 退回上一步
        if (movePiece.role == Role.BOSS) {
            updateBossPlace(movePiece.part, from);
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
     *
     * @param curPart
     * @return
     */
    public int getCurPartEvaluateScore(Part curPart) {
        int num = 0;
        for (int x = 0; x < ChessDefined.RANGE_X; x++) {
            for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
                Piece piece = pieces[x][y];
                if (piece == null) {
                    continue;
                }
                if (piece.part == curPart) {
                    num += getSingleScore(piece, y);
                } else {
                    num -= getSingleScore(piece, y);
                }
            }
        }
        return num;
    }

    /**
     * @param piece
     * @param y
     * @return
     */
    public int getSingleScore(Piece piece, int y) {
        if (piece == null) {
            return 0;
        }
        switch (piece.role) {
            case BOSS:
                return 1000000;
            case CAR:
                return 1000;
            case HORSE:
                return 482 - blackPieceNum - redPieceNum;
            case CANNON:
                return 448 + blackPieceNum + redPieceNum;
            // 过河之后分数更高
            case SOLDIER:
                if (piece.part == Part.RED) {
                    if (y >= 5) {
                        return 150;
                    } else if (y > 2) {
                        return 300;
                    } else {
                        return 200;
                    }
                } else {
                    if (y <= 4) {
                        return 150;
                    } else if (y < 7) {
                        return 300;
                    } else {
                        return 200;
                    }
                }
            case ELEPHANT:
            case COUNSELOR:
                return 150;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * 返回棋盘上某一方的棋子
     */
    public int getPieceNum(Part curPart) {
        return curPart == Part.RED ? redPieceNum : blackPieceNum;
    }

    /**
     * @return 棋盘还有多少棋子
     */
    public int getPieceNum() {
        return redPieceNum + blackPieceNum;
    }


    /**
     * 获取对方Boss的位置
     */
    public Place getOppoBossPlace(Part curPart) {
        return curPart == Part.RED ? blackBoss : redBoss;
    }

    /**
     * @param part     更新 Boss 棋子的位置
     * @param newPlace 新位置
     */
    public void updateBossPlace(Part part, Place newPlace) {
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
        return ArrayUtils.nullInMiddle(pieces[curNextPlace.x], curNextPlace.y, oppPlace.y);
    }

    /**
     * @return boss 是否为面对面
     */
    public boolean bossF2fAfterLeave(Place place) {
        if (redBoss.x != blackBoss.x || place.x != redBoss.x) {
            return false;
        }
        return ArrayUtils.oneInMiddle(pieces[redBoss.x], redBoss.y, blackBoss.y);
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

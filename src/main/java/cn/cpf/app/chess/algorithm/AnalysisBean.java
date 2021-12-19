package cn.cpf.app.chess.algorithm;

import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.inter.MyList;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.util.ArrayUtils;
import lombok.NonNull;


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

    private int pieceScore;

    public AnalysisBean(@NonNull final Piece[][] rawPieceArrays) {
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
        // 计算分值
        this.pieceScore = calcPieceScore(rawPieceArrays);
    }

    /**
     * 计算棋盘评估分数
     */
    public static int calcPieceScore(final Piece[][] pieces) {
        int num = 0;
        for (int x = 0; x < ChessDefined.RANGE_X; x++) {
            for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
                Piece piece = pieces[x][y];
                if (piece == null) {
                    continue;
                }
                if (piece.part == Part.RED) {
                    num += piece.pieceScore.placeScores[x * 10 + 9 - y];
                    num += piece.pieceScore.existScore;
                } else {
                    num -= piece.pieceScore.placeScores[x * 10 + y];
                    num -= piece.pieceScore.existScore;
                }
            }
        }
        return num;
    }

    /**
     * 获取 一步（只进攻, 不防守） 可以相对增多的分值, 红方为正, 黑方为负
     *
     * @param from from 位置
     * @param to to 位置
     * @return 一步（只进攻, 不防守） 可以相对增多的分值
     */
    public int nextStepOpportunityCost(final Place from, final Place to) {
        DebugInfo.incrementAlphaBetaTime();
        // 临时分值
        int invScr = 0;
        final Piece eatenPiece = pieces[to.x][to.y];
        if (eatenPiece != null) {
            // 若是将棋, 则更新BOSS子的位置
            // 若被吃的棋子是红方, 则 - 被吃掉的棋子的存在值, 若是黑方则相反.
            if (eatenPiece.part == Part.RED) {
                invScr -= eatenPiece.pieceScore.existScore;
                invScr -= eatenPiece.pieceScore.getPlaceScore(Part.RED, to.x, to.y);
            } else {
                invScr += eatenPiece.pieceScore.existScore;
                invScr += eatenPiece.pieceScore.getPlaceScore(Part.BLACK, to.x, to.y);
            }
            // 更新分数
        }
        final Piece movePiece = pieces[from.x][from.y];
        // 如果是红方, 则 + 移动之后的棋子存在值, - 移动之前的棋子存在值, 若是黑方则相反.
        if (Part.RED == movePiece.part) {
            invScr += movePiece.pieceScore.getPlaceScore(Part.RED, to.x, to.y);
            invScr -= movePiece.pieceScore.getPlaceScore(Part.RED, from.x, from.y);
            return invScr;
        } else {
            invScr -= movePiece.pieceScore.getPlaceScore(Part.BLACK, to.x, to.y);
            invScr += movePiece.pieceScore.getPlaceScore(Part.BLACK, from.x, from.y);
            return -invScr;
        }
    }

    /**
     * 模拟走棋
     */
    public int goForward(Place from, Place to, Piece eatenPiece) {
        final Piece movePiece = pieces[from.x][from.y];
        pieces[to.x][to.y] = movePiece;
        pieces[from.x][from.y] = null;
        if (movePiece.role == Role.BOSS) {
            updateBossPlace(movePiece.part, to);
        }
        // 临时分值
        int invScr = 0;
        // 如果是红方, 则 + 移动之后的棋子存在值, - 移动之前的棋子存在值, 若是黑方则相反.
        if (Part.RED == movePiece.part) {
            invScr += movePiece.pieceScore.getPlaceScore(Part.RED, to.x, to.y);
            invScr -= movePiece.pieceScore.getPlaceScore(Part.RED, from.x, from.y);
        } else {
            invScr -= movePiece.pieceScore.getPlaceScore(Part.BLACK, to.x, to.y);
            invScr += movePiece.pieceScore.getPlaceScore(Part.BLACK, from.x, from.y);
        }
        if (eatenPiece != null) {
            // 若是将棋, 则更新BOSS子的位置
            // 若被吃的棋子是红方, 则 - 被吃掉的棋子的存在值, 若是黑方则相反.
            if (eatenPiece.part == Part.RED) {
                invScr -= eatenPiece.pieceScore.existScore;
                invScr -= eatenPiece.pieceScore.getPlaceScore(Part.RED, to.x, to.y);
                redPieceNum--;
            } else {
                invScr += eatenPiece.pieceScore.existScore;
                invScr += eatenPiece.pieceScore.getPlaceScore(Part.BLACK, to.x, to.y);
                blackPieceNum--;
            }
            // 更新分数
        }
        pieceScore += invScr;
        // debug
        DebugInfo.checkScoreDynamicCalc(pieces, pieceScore);
        DebugInfo.incrementAlphaBetaTime();
        return invScr;
    }

    /**
     * 模拟后退
     */
    public void backStep(Place from, Place to, Piece eatenPiece, int tmpScore) {
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
        // 更新分值
        pieceScore -= tmpScore;
    }

    /**
     * 返回对本方的实力评估, 本方为正
     *
     * @param curPart 当前走棋方
     * @return 当前走棋方的实力评估
     */
    public int getCurPartEvaluateScore(Part curPart) {
        if (Part.RED == curPart) {
            return pieceScore;
        } else {
            return -pieceScore;
        }
    }

    /**
     * 返回棋盘上某一方的棋子数量
     */
    public int getPieceCount(Part curPart) {
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
     * @return boss棋子移动后 是否为面对面
     */
    public boolean bossF2fAfterBossMove(Part curPart, Place curNextPlace) {
        Place oppPlace = curPart == Part.RED ? blackBoss : redBoss;
        // 若两个 boss 棋子坐标不一致, 则直接返回 false
        if (curNextPlace.x != oppPlace.x) {
            return false;
        }
        return ArrayUtils.nullInMiddle(pieces[curNextPlace.x], curNextPlace.y, oppPlace.y);
    }

    /**
     * @return true : 两个 boss 面对面, 且中间只有 place 一个棋子
     */
    public boolean isBossF2FAndWithOnlyThePlaceInMiddle(Place place) {
        // 如果 两个 boss 不是面对面 或者 当前位置不在两个boss中间, 则直接返回false
        if (redBoss.x != blackBoss.x || place.x != redBoss.x || place.y > redBoss.y || place.y < blackBoss.y) {
            return false;
        }
        return ArrayUtils.oneInMiddle(pieces[redBoss.x], redBoss.y, blackBoss.y);
    }
    /**
     * @return true : 当前位置在 两个boss 中间(包含boss的位置)
     */
    public boolean isBossF2FAndWithThePlaceInMiddle(Place place) {
        return redBoss.x == blackBoss.x && place.x == redBoss.x && place.y <= redBoss.y && place.y >= blackBoss.y;
    }

    /**
     * @return boss 是否为面对面
     */
    public MyList<Place> filterPlace(MyList<Place> places) {
        return places.filter(item -> {
            Place it = (Place) item;
            return it.x == redBoss.x && it.y <= redBoss.y && it.y >= blackBoss.y;
        });
    }
}

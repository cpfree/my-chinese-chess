package cn.cpf.app.chess.algorithm;

import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.inter.MyList;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.StepBean;
import cn.cpf.app.chess.util.ArrayUtils;
import lombok.NonNull;

import java.util.Set;
import java.util.function.Predicate;


/**
 * <b>Description : </b> 用于ai算法的分析运算, 相当于为运算而做的一个副本
 *
 * @author CPF
 * Date: 2020/3/25 17:33
 */
public class AnalysisBean {

    public final Piece[][] pieces;
    /**
     * 红方 boss 位置
     */
    private Place redBoss;
    /**
     * 黑方 boss 位置
     */
    private Place blackBoss;
    /**
     * 红方棋子数量
     */
    private int redPieceNum;
    /**
     * 黑方棋子数量
     */
    private int blackPieceNum;
    /**
     * 红方棋子存在总分值(不计算Boss的分值)
     */
    private int redPieceExistScore;
    /**
     * 黑方棋子存在总分值(不计算Boss的分值)
     */
    private int blackPieceExistScore;
    /**
     * 双方形势分值总和(红方为正,黑方为负)
     */
    private int pieceScore;

    public AnalysisBean(@NonNull final Piece[][] rawPieceArrays) {
        this.pieces = rawPieceArrays;
        redPieceExistScore = 0;
        blackPieceExistScore = 0;
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
                        } else {
                            redPieceExistScore += piece.pieceScore.existScore;
                        }
                    } else {
                        blackPieceNum++;
                        if (piece.role == Role.BOSS) {
                            blackBoss = Place.of(x, y);
                        } else {
                            blackPieceExistScore += piece.pieceScore.existScore;
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
            final PieceScore pScore = eatenPiece.pieceScore;
            final int existScore = pScore.existScore;
            final int pieceCount = redPieceNum + blackPieceNum;
            if (eatenPiece.part == Part.RED) {
                redPieceExistScore -= existScore;
                redPieceNum--;
                invScr -= existScore;
                invScr -= pScore.getPlaceScore(Part.RED, to.x, to.y);
                /*
                 * 加成分数: 棋子存在值 * (当前方棋子存在总分值 / 双方棋子存在总分值) * 0.5
                 * 1. 与别人换子是不利的, AI 下子尽量保守些.
                 * 2. 相对来讲, 如果自己的棋子子力和比较多, 那么换子是有利的, 如果自己的棋子子力和比较少, 那么换子是不利的.
                 * eg: 假如 red: 2000, black: 3000, 场上12个棋子, 此时 此时红方 kill 黑方 200, 此时可以获得 200 * 0.25 * 2000 / ( 2000 + 3000 ) = 20 分
                 * 之后 red: 2000, black: 2800, 场上11个棋子, 此时黑方再 kill 红方 200, 此时 200 * 0.25 * 2800 / ( 2000 + 2800 ) = 29 分
                 */
                invScr -= (existScore * redPieceExistScore / (redPieceExistScore + blackPieceExistScore)) >> 3;
                // 2. 如果损失的是馬(后期马越来越重要), 前期马有负分数加成, 后期马有正分数加成
                if (eatenPiece.role == Role.HORSE) {
                    invScr -= (16 - pieceCount) << 1;
                }
            } else {
                blackPieceExistScore -= existScore;
                blackPieceNum--;
                invScr += existScore;
                invScr += pScore.getPlaceScore(Part.BLACK, to.x, to.y);
                /* 该部分原理同上 */
                invScr += (existScore * redPieceExistScore / (redPieceExistScore + blackPieceExistScore)) >> 3;
                if (eatenPiece.role == Role.HORSE) {
                    invScr += (16 - pieceCount) << 1;
                }
            }
            // 更新分数
        }
        pieceScore += invScr;
        // 测试, 去掉上面的加成分数, 则下面的检查成立
//        DebugInfo.checkScoreDynamicCalc(pieces, pieceScore);
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
                redPieceExistScore += eatenPiece.pieceScore.existScore;
                redPieceNum++;
            } else {
                blackPieceExistScore += eatenPiece.pieceScore.existScore;
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
     * @param place 棋盘位置
     * @return 对应棋盘位置的棋子对象
     */
    public Piece getPiece(@NonNull Place place) {
        return pieces[place.x][place.y];
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
     * 检查一步走棋后会不会造成 Boss 面对面 (当前 part 方, 从 from 走到 to)
     *
     * @param piece 当前走棋棋子
     * @param from from
     * @param to to
     * @return true: 符合规则, false: 不符合规则
     */
    public boolean isBossF2FAfterStep(Piece piece, Place from, Place to) {
        if (Role.BOSS == piece.role) {
            return !bossF2fAfterBossMove(piece.part, to);
        } else {
            return !isBossF2FAndWithOnlyThePlaceInMiddle(from) || isBossF2FAndWithThePlaceInMiddle(to);
        }
    }

    /**
     * AI 计算 curPart 方若是走一步棋是否能够吃掉对方的 BOSS
     */
    public boolean canEatBossAfterOneAiStep(Part part) {
        final Set<StepBean> nextStepAgainEvalPlace = AlphaBeta.getEvaluatedPlace(pieces, part, 1, null);
        // 计算后的步骤中, 是否存在能吃掉 BOSS 的一步
        for (StepBean stepBean : nextStepAgainEvalPlace) {
            final Piece piece = getPiece(stepBean.to);
            if (piece != null && piece.role == Role.BOSS) {
                return true;
            }
        }
        return false;
    }

    public boolean simulateOneStep(StepBean stepBean, Predicate<AnalysisBean> predicate) {
        final Piece eatenPiece = getPiece(stepBean.to);
        if (eatenPiece != null && eatenPiece.role == Role.BOSS) {
            throw new IllegalStateException(eatenPiece.name() + " 被吃掉了, 无法继续执行下去");
        }
        // 模拟走棋
        final int invScr = goForward(stepBean.from, stepBean.to, eatenPiece);
        // 评分
        final boolean test = predicate.test(this);
        // 退回上一步
        backStep(stepBean.from, stepBean.to, eatenPiece, invScr);
        return test;
    }

    /**
     * AI计算后, part 走一步棋之后 是否能够避免对方下一步吃掉自己的 BOSS
     *
     * @param part 当前方
     */
    public boolean canAvoidBeEatBossAfterOneAIStep(Part part) {
        final Set<StepBean> nextStepAgainEvalPlace = AlphaBeta.getEvaluatedPlace(pieces, part, 2, null);
        // 计算后的步骤中, 是否存在能吃掉 BOSS 的一步
        for (StepBean stepBean : nextStepAgainEvalPlace) {
            final Piece eatenPiece = getPiece(stepBean.to);
            // 如果是 BOSS角色被吃掉, 则需要跳出循环或继续下一步循环
            if (eatenPiece != null && eatenPiece.role == Role.BOSS) {
                // 如果吃掉的是对方的 BOSS, 表示可以避免本方 BOSS 被吃掉
                if (eatenPiece.part == part) {
                    continue;
                } else {
                    return true;
                }
            }
            // 如果 stepBean 走完之后, 对方无法吃掉自己的 BOSS, 则返回true
            if (!simulateOneStep(stepBean, bean -> bean.canEatBossAfterOneAiStep(Part.getOpposite(part)))) {
                return true;
            }
        }
        return false;
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

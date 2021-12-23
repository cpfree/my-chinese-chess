package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.StepRecord;
import cn.cpf.app.chess.swing.ChessPiece;
import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;
import java.util.*;

/**
 * <b>Description : </b> 步骤记录器
 *
 * @author CPF
 * Date: 2020/3/19 13:56
 */
public class SituationRecord implements Serializable {

    /**
     * 历史记录
     */
    @Getter
    private final LinkedList<StepRecord> records = new LinkedList<>();
    /**
     * 被吃的棋子列表
     */
    @Getter
    private final transient LinkedList<ChessPiece> eatenPieceList = new LinkedList<>();
    /**
     * 黑棋当前走的步数
     */
    @Getter
    private int blackStep;
    /**
     * 红棋当前走的步数
     */
    @Getter
    private int redStep;

    /**
     * 如果是象棋初始化的时候, 已经存在的步数
     * 该变量的目的是象棋在初始化棋局的时候, 不至于因为已经存在的历史记录, 导致一步就被判定为长捉, 或长拦
     */
    private final transient int initStepNumber;

    public SituationRecord(List<StepRecord> records) {
        if (records != null) {
            records.forEach(this::addRecord);
            initStepNumber = records.size();
        } else {
            initStepNumber = 0;
        }
    }

    public SituationRecord() {
        this(null);
    }

    public void addRecord(@NonNull StepRecord stepRecord) {
        if (Part.RED == stepRecord.getPiece().part) {
            redStep++;
        } else {
            blackStep++;
        }
        if (stepRecord.getEatenPiece() != null) {
            eatenPieceList.add(stepRecord.getEatenPiece());
        }
        records.add(stepRecord);
    }

    /**
     * 弹出最新记录对象, 若列表中无元素, 则抛出异常
     */
    public StepRecord popRecord() {
        final StepRecord stepRecord = records.pollLast();
        if (stepRecord == null) {
            throw new EmptyStackException();
        } else if (stepRecord.getEatenPiece() != null) {
            final ChessPiece chessPiece = eatenPieceList.pollLast();
            assert chessPiece == stepRecord.getEatenPiece();
        }
        return stepRecord;
    }

    int getTotalStep() {
        return records.size();
    }

    /**
     * 通过步骤记录器, 获取长捉, 长拦的步骤
     *
     * @return 下一步禁止的步骤.
     */
    public StepRecord getForbidStepRecord() {
        final int length = records.size();
        List<StepRecord> list = new ArrayList<>();
        for (int n = length - 1; n >= initStepNumber ; n--) {
            final StepRecord stepRecord = records.get(n);
            if (stepRecord.getEatenPiece() != null) {
                break;
            }
            list.add(stepRecord);
        }
        // 因为是红黑方交替行走, 且每步棋子移动之后, 还要移动回去, 才能使得 piece, from, to 均相等.
        // 因此, 一次长捉, 长拦 的重复至少要4步棋以上, 因此在这里 从 n = 4 开始查询
        for (int n = 4, len = list.size(); n < len; n++) {
            for (int i = 0, j = n; j < len; i ++, j++) {
                final StepRecord iItem = list.get(i);
                final StepRecord jItem = list.get(j);
                // 因为此处, 没有吃子, 因此, 不需要比较 eatPiece
                // 同时, 如果比较 from 的话, 那么长逃也将禁止。
                // 因此只要比较 piece, to 相等即可
                if (iItem.getPiece() == jItem.getPiece() && iItem.getTo() == jItem.getTo()) {
                    if (i == n - 1) {
                        return iItem;
                    }
                } else {
                    break;
                }
            }
        }
        return null;
    }

}

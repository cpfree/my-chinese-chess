package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.StepRecord;
import cn.cpf.app.chess.swing.ChessPiece;
import lombok.Getter;
import lombok.NonNull;

import java.io.Serializable;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;

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

    public SituationRecord(List<StepRecord> records) {
        if (records != null) {
            records.forEach(this::addRecord);
        }
    }

    public SituationRecord() {
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

}

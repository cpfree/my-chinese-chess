package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.StepRecord;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * <b>Description : </b> 步骤记录器
 *
 * @author CPF
 * Date: 2020/3/19 13:56
 */
public class SituationRecord {

    /**
     * 历史记录
     */
    @Getter
    private final List<StepRecord> list = new ArrayList<>();
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

    void addRecord(@NonNull Part part, @NonNull Piece piece, @NonNull Place from, @NonNull Place to, Piece eatenPiece) {
        if (Part.RED == part) {
            redStep++;
        } else {
            blackStep++;
        }
        list.add(new StepRecord(part, list.size() + 1, piece, from, to, eatenPiece));
    }

    /**
     * 弹出最新记录对象, 若列表中无元素, 则抛出异常
     */
    StepRecord popRecord() {
        if (list.isEmpty()) {
            throw new EmptyStackException();
        }
        return list.remove(list.size() - 1);
    }

    int getTotalStep() {
        return list.size();
    }



}

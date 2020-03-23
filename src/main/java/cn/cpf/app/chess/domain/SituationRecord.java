package cn.cpf.app.chess.domain;

import cn.cpf.app.chess.bean.StepRecord;
import cn.cpf.app.chess.res.Part;
import cn.cpf.app.chess.res.Piece;
import cn.cpf.app.chess.res.Place;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/3/19 13:56
 */
public class SituationRecord {

    /**
     * 历史记录
     */
    private List<StepRecord> list = new ArrayList<>();
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

    int getTotalStep() {
        return list.size();
    }

}

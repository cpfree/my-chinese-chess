package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.swing.ChessPiece;
import cn.cpf.app.chess.conf.ChessConfig;
import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.algorithm.Role;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <b>Description : </b> 当前棋局的形势, 双方都有什么棋子, 在什么位置, 下一步该谁走.
 *
 * @author CPF
 * Date: 2020/3/18 14:22
 */
@Slf4j
public class Situation implements Serializable {

    public final transient LocalDateTime situationStartTime = LocalDateTime.now();

    /**
     * 当前棋盘
     */
    @Getter
    private ChessPiece[][] boardPiece;

    /**
     * 下一步行走的势力
     */
    @Getter
    private Part nextPart;

    /**
     * 下棋记录
     */
    @Getter
    public SituationRecord situationRecord;

    public ChessPiece getPiece(@NonNull Place place) {
        return boardPiece[place.x][place.y];
    }

    public void init(List<ChessPiece> list) {
        boardPiece = new ChessPiece[ChessDefined.RANGE_X][ChessDefined.RANGE_Y];
        list.forEach(it -> boardPiece[it.getPlace().x][it.getPlace().y] = it);
        // 获取先手方配置信息
        nextPart = ChessConfig.firstPart;
        // TODO 计算分数
        situationRecord = new SituationRecord();
        // scores = new int[ChessDefined.RANGE_X][ChessDefined.RANGE_Y];
    }

    /**
     * 真实落子
     * @param from
     * @param to
     * @return
     */
    public Part realLocatePiece(Place from, Place to){
        ChessPiece fromPiece = getPiece(from);
        Objects.requireNonNull(fromPiece);
        // 判断是否是吃子
        ChessPiece eatenPiece = getPiece(to);
        if (eatenPiece != null) {
            eatenPiece.hide();
            log.info("move {} -> {}, {} eat {}", from, to, fromPiece.role.name(), eatenPiece.role.name());
        }

        // 走棋
        boardPiece[from.x][from.y] = null;
        boardPiece[to.x][to.y] = fromPiece;
        fromPiece.setPlace(to);
        // 添加记录
        situationRecord.addRecord(nextPart, fromPiece.piece, from, to, eatenPiece == null ? null : eatenPiece.piece);

        // 势力
        this.nextPart = Part.getOpposite(this.nextPart);
        // 开额外线程判断是否胜利, 或连将
        return eatenPiece != null && eatenPiece.role == Role.BOSS ? Part.getOpposite(this.nextPart) : null;
    }

}

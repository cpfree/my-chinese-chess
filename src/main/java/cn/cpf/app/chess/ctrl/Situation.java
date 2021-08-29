package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.algorithm.Role;
import cn.cpf.app.chess.conf.ChessConfig;
import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.swing.BoardPanel;
import cn.cpf.app.chess.swing.ChessPiece;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <b>Description : </b> 当前棋局的形势, 双方都有什么棋子, 在什么位置, 下一步该谁走.
 * <p>
 * 为后台控制器和前台贡献的对象, 可以被后台控制器 {@link ControlCenter} & 前台panel {@link BoardPanel} 调用
 * </p>
 *
 * @author CPF
 * Date: 2020/3/18 14:22
 */
@Slf4j
public class Situation {

    @Getter
    private LocalDateTime situationStartTime;
    /**
     * 活着的棋子列表
     */
    @Getter
    private List<ChessPiece> pieceList;
    /**
     * 被吃的棋子列表
     */
    @Getter
    private List<ChessPiece> eatenPieceList;
    /**
     * 棋子数组
     */
    private ChessPiece[][] pieceArrays;
    /**
     * 红方boss, 单独标出
     */
    @Getter
    private ChessPiece redBoss;
    /**
     * 黑方boss, 单独标出
     */
    @Getter
    private ChessPiece blackBoss;

    @Getter
    private int redPieceNum;

    @Getter
    private int blackPieceNum;
    /**
     * 下棋记录
     */
    private SituationRecord situationRecord;
    /**
     * 下一步行走的势力
     */
    @Getter
    private Part nextPart;

    void init(List<ChessPiece> list) {
        // 成员变量初始化
        situationStartTime = LocalDateTime.now();
        eatenPieceList = new ArrayList<>();
        pieceList = new ArrayList<>(list.size());
        pieceArrays = new ChessPiece[ChessDefined.RANGE_X][ChessDefined.RANGE_Y];
        redBoss = null;
        blackBoss = null;
        redPieceNum = 0;
        blackPieceNum = 0;
        situationRecord = new SituationRecord();
        // 获取先手方配置信息
        nextPart = ChessConfig.firstPart;
        // 成员变量赋值
        pieceList.addAll(list);
        list.forEach(it -> {
            final Place place = it.getPlace();
            final Piece piece = it.piece;
            pieceArrays[place.x][place.y] = it;
            if (Part.RED == piece.part) {
                redPieceNum++;
                if (Role.BOSS == piece.role) {
                    redBoss = it;
                }
            } else {
                blackPieceNum++;
                if (Role.BOSS == piece.role) {
                    blackBoss = it;
                }
            }
        });
    }

    public ChessPiece getChessPiece(@NonNull Place place) {
        return pieceArrays[place.x][place.y];
    }

    /**
     * 根据 ChessPiece[][] 数组新建立一个 Piece[][] 数组
     */
    public Piece[][] genePiece() {
        final Piece[][] pieces = new Piece[ChessDefined.RANGE_X][ChessDefined.RANGE_Y];
        for (int x = 0; x < ChessDefined.RANGE_X; x++) {
            for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
                final ChessPiece chessPiece = pieceArrays[x][y];
                if (chessPiece != null) {
                    pieces[x][y] = chessPiece.piece;
                }
            }
        }
        return pieces;
    }

    /**
     * 移动棋子
     *
     * @param from 源位置
     * @param to   目标位置
     * @return 如果目标位置是 Boss 角色, 则返回 被吃 boss 角色的势力
     */
    Part movePiece(Place from, Place to) {
        ChessPiece fromPiece = getChessPiece(from);
        Objects.requireNonNull(fromPiece);
        // 判断是否是吃子
        ChessPiece eatenPiece = getChessPiece(to);
        if (eatenPiece != null) {
            eatenPieceList.add(eatenPiece);
            // ui 隐藏
            eatenPiece.hide();
            log.info("move {} -> {}, {} eat {}", from, to, fromPiece.name, eatenPiece.name);
        }

        // 走棋
        pieceArrays[from.x][from.y] = null;
        pieceArrays[to.x][to.y] = fromPiece;
        // ui 移动棋子
        fromPiece.setPlace(to);
        // 添加记录
        situationRecord.addRecord(nextPart, fromPiece.piece, from, to, eatenPiece == null ? null : eatenPiece.piece);

        // 变更势力
        nextPart = Part.getOpposite(nextPart);
        // 开额外线程判断是否胜利, 或连将
        return eatenPiece != null && eatenPiece.piece.role == Role.BOSS ? Part.getOpposite(nextPart) : null;
    }

}

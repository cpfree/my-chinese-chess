package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.algorithm.Role;
import cn.cpf.app.chess.conf.ChessDefined;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.StepRecord;
import cn.cpf.app.chess.swing.BoardPanel;
import cn.cpf.app.chess.swing.ChessPiece;
import cn.cpf.app.chess.util.JsonUtils;
import com.github.cosycode.common.lang.ShouldNotHappenException;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <b>Description : </b> 当前棋局的形势, 双方都有什么棋子, 在什么位置, 下一步该谁走.
 * <p>
 * 为后台控制器和前台贡献的对象, 可以被后台控制器 {@link AppContext} & 前台panel {@link BoardPanel} 调用
 * </p>
 *
 * @author CPF
 * Date: 2020/3/18 14:22
 */
@Slf4j
public class Situation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 棋子数组
     */
    private final transient ChessPiece[][] pieceArrays;
    /**
     * 红方boss, 单独标出
     */
    @Getter
    private transient ChessPiece redBoss;
    /**
     * 黑方boss, 单独标出
     */
    @Getter
    private transient ChessPiece blackBoss;

    @Getter
    private transient int redPieceNum;

    @Getter
    private transient int blackPieceNum;
    /**
     * 下棋记录
     */
    @Getter
    private final SituationRecord situationRecord;
    @Getter
    private final LocalDateTime situationStartTime;
    /**
     * 活着的棋子列表
     */
    @Getter
    @SuppressWarnings("java:S1948")
    private final List<ChessPiece> pieceList;
    /**
     * 下一步行走的势力
     */
    @Getter
    private Part nextPart;

    public Situation(@NonNull List<ChessPiece> list, @NonNull SituationRecord situationRecord, @NonNull Part nextPart, @NonNull LocalDateTime dateTime) {
        this.situationRecord = situationRecord;
        this.nextPart = nextPart;
        this.situationStartTime = dateTime;
        // 成员变量初始化
        pieceList = new ArrayList<>(list.size());
        pieceArrays = new ChessPiece[ChessDefined.RANGE_X][ChessDefined.RANGE_Y];
        redBoss = null;
        blackBoss = null;
        redPieceNum = 0;
        blackPieceNum = 0;
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
    public Piece[][] generatePieces() {
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
     * 如果已经有胜利方, 则返回胜利方, 否则返回 null
     *
     * @return 胜利方
     */
    public Part winner() {
        boolean isRedBossExist = false;
        boolean isBlankBossExist = false;
        for (int x = 0; x < ChessDefined.RANGE_X; x++) {
            for (int y = 0; y < ChessDefined.RANGE_Y; y++) {
                final ChessPiece chessPiece = pieceArrays[x][y];
                if (chessPiece != null && chessPiece.piece.role == Role.BOSS) {
                    if (chessPiece.piece.part == Part.BLACK) {
                        isBlankBossExist = true;
                    } else {
                        isRedBossExist = true;
                    }
                }
            }
        }
        if (isRedBossExist) {
            return isBlankBossExist ? null : Part.RED;
        } else if (isBlankBossExist) {
            return Part.BLACK;
        } else {
            throw new ShouldNotHappenException("两个 Boss 均不存在");
        }
    }

    /**
     * 移动棋子
     *
     * @param from 源位置
     * @param to   目标位置
     * @return 如果目标位置是 Boss 角色, 则返回 被吃 boss 角色的势力
     */
    Piece movePiece(Place from, Place to) {
        final ChessPiece fromPiece = getChessPiece(from);
        final ChessPiece eatenPiece = getChessPiece(to);
        Objects.requireNonNull(fromPiece, "找不到移动的棋子");
        // 判断是否是吃子, 如果棋子被吃掉, 则将棋子移动列表
        if (eatenPiece != null) {
            pieceList.remove(eatenPiece);
            // ui 隐藏
            log.info("move {} -> {}, {} eat {}", from, to, fromPiece.name, eatenPiece.name);
        }
        // 更改棋盘数组
        pieceArrays[from.x][from.y] = null;
        pieceArrays[to.x][to.y] = fromPiece;
        /* ui 移动from棋子, 隐藏被吃掉的棋子 */
        fromPiece.movePlace(to);
        if (eatenPiece != null) {
            eatenPiece.hide();
        }
        // 添加记录
        situationRecord.addRecord(new StepRecord(fromPiece.piece, from, to, eatenPiece));
        // 变更势力
        nextPart = Part.getOpposite(nextPart);
        // 开额外线程判断是否胜利, 或连将
        return eatenPiece == null ? null : eatenPiece.piece;
    }

    /**
     * 撤销一步棋
     *
     * @return 撤掉的步骤记录
     */
    StepRecord rollbackOneStep() {
        Objects.requireNonNull(situationRecord, "situationRecord shouldn't be null");
        List<StepRecord> list = situationRecord.getRecords();
        if (list.isEmpty()) {
            log.warn("步骤历史记录为空, 已经回退到起始状态!");
            return null;
        }
        // 弹出记录
        final StepRecord stepRecord = situationRecord.popRecord();
        final Place from = stepRecord.getFrom();
        final Place to = stepRecord.getTo();
        final ChessPiece eatenPiece = stepRecord.getEatenPiece();
        Optional.ofNullable(pieceArrays[from.x][from.y]).ifPresent(e -> {
            throw new IllegalStateException("此处不该有棋子: " + e);
        });
        // 撤回from
        final ChessPiece movePiece = getChessPiece(to);
        pieceArrays[from.x][from.y] = movePiece;
        /* 若有被吃掉的棋子, 则复活, 移动列表 */
        if (eatenPiece != null) {
            pieceList.add(eatenPiece);
        }
        pieceArrays[to.x][to.y] = eatenPiece;
        // ui移动棋子放到最后面, 先显示被吃掉的棋子后移动
        if (eatenPiece != null) {
            eatenPiece.setPlaceAndShow(to);
        }
        movePiece.movePlace(from);
        // 变更势力
        nextPart = Part.getOpposite(nextPart);
        return stepRecord;
    }

    @Override
    public String toString() {
        return "Situation:" + hashCode() + "=" + JsonUtils.toJson(this);
    }
}

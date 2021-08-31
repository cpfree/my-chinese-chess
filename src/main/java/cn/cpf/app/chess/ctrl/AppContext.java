package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.algorithm.AlphaBeta;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.StepBean;
import cn.cpf.app.chess.swing.BoardPanel;
import cn.cpf.app.chess.swing.ChessPiece;
import cn.cpf.app.chess.util.ArrayUtils;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <b>Description : </b> 负责ui与后台数据交互, 以及功能性控制
 * <p>
 * <b>created in </b> 2021/8/27
 *
 * @author CPF
 * @since 0.1
 **/
@Slf4j
public class AppContext {

    private final BoardPanel boardPanel;

    @Getter
    private final ComRunner comRunner;

    @Getter
    private final Situation situation;

    AppContext(final BoardPanel boardPanel, final Situation situation) {
        this.situation = situation;
        this.boardPanel = boardPanel;
        comRunner = new ComRunner(boardPanel);
    }

    /**
     * 将当前场景存放至文件
     *
     * @param situation 场景对象
     * @throws IOException 写入失败
     */
    public static void saveSituation(Situation situation) throws IOException {
        String fileName = String.format("situation-%s-%s-%s", LocalDateTime.now(), situation.getSituationStartTime(), LocalDateTime.now());
        String filePath = Objects.requireNonNull(Application.class.getResource("/"), "获取资源路径失败").getPath();
        File file = new File(filePath + File.separator + fileName);
        assert !file.exists() : "文件已存在";
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(new Gson().toJson(situation));
            writer.flush();
        }
    }

    /**
     * 棋局初始化
     *
     * @param list 棋子列表
     */
    public void init(List<ChessPiece> list) {
        situation.init(list, Application.config().getFirstPart());
        boardPanel.init(situation);
    }

    /**
     * @return AI 计算下一步落子位置
     */
    public StepBean computeStepBean() {
        long t = System.currentTimeMillis();
        Piece[][] pieces = situation.genePiece();
        pieces = ArrayUtils.deepClone(pieces);
        StepBean evaluatedPlace = AlphaBeta.getEvaluatedPlace(pieces, situation.getNextPart(), Application.config().getSearchDeepLevel());
        log.info("time: {}", (System.currentTimeMillis() - t));
        return evaluatedPlace;
    }

    /**
     * 落子函数, 并附带落子后的胜负检查等操作逻辑.
     */
    public Part locatePiece(Place from, Place to) {
        Part part = situation.movePiece(from, to);
        boardPanel.updateMark(from, to);
        // 判断是否结束
        if (part != null) {
            JOptionPane.showMessageDialog(boardPanel, part.name() + "胜利", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
            log.info("游戏结束 ==> {} 胜利", part.name());
        }
        return part;
    }

}

package cn.cpf.app.chess.conf;

import cn.cpf.app.chess.ctrl.Situation;
import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.Piece;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.PlayerType;
import cn.cpf.app.chess.swing.ChessPiece;
import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/3/19 13:53
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChessConfig {

    public static final LocalDateTime appStartTime = LocalDateTime.now();

    /**
     * 先手方
     */
    public static final Part firstPart = Part.RED;

    public static PlayerType redPlayerType = PlayerType.PEOPLE;

    public static PlayerType blackPlayerType = PlayerType.COM;

    public static final int sha = 0;

    public static final int INTERVAL_TIME = 600;

    public static int deep = 6;

    /**
     * 判断 part 方的 playType
     */
    public static PlayerType getPlayerType(@NonNull Part part) {
        if (part == Part.RED) {
            return redPlayerType;
        } else {
            return blackPlayerType;
        }
    }

    public static void saveSituation(Situation situation) throws IOException {
        String fileName = String.format("situation-%s-%s-%s", appStartTime, situation.getSituationStartTime(), LocalDateTime.now());
        String filePath = ChessConfig.class.getResource("/").getPath();
        File file = new File(filePath + File.separator + fileName);
        assert !file.exists() : "文件已存在";
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(new Gson().toJson(situation));
            writer.flush();
        }
    }

    public static List<ChessPiece> geneDefaultPieceSituation() {
        List<ChessPiece> list = new ArrayList<>(32);
        // 添加红色棋子
        list.add(new ChessPiece("1", Piece.RED_CAR, Place.of(0, 9)));
        list.add(new ChessPiece("1", Piece.RED_HORSE, Place.of(1, 9)));
        list.add(new ChessPiece("1", Piece.RED_ELEPHANT, Place.of(2, 9)));
        list.add(new ChessPiece("1", Piece.RED_COUNSELOR, Place.of(3, 9)));
        list.add(new ChessPiece("", Piece.RED_BOSS, Place.of(4, 9)));
        list.add(new ChessPiece("2", Piece.RED_COUNSELOR, Place.of(5, 9)));
        list.add(new ChessPiece("2", Piece.RED_ELEPHANT, Place.of(6, 9)));
        list.add(new ChessPiece("2", Piece.RED_HORSE, Place.of(7, 9)));
        list.add(new ChessPiece("2", Piece.RED_CAR, Place.of(8, 9)));

        list.add(new ChessPiece("1", Piece.RED_CANNON, Place.of(1, 7)));
        list.add(new ChessPiece("2", Piece.RED_CANNON, Place.of(7, 7)));
        list.add(new ChessPiece("1", Piece.RED_SOLDIER, Place.of(0, 6)));
        list.add(new ChessPiece("2", Piece.RED_SOLDIER, Place.of(2, 6)));
        list.add(new ChessPiece("3", Piece.RED_SOLDIER, Place.of(4, 6)));
        list.add(new ChessPiece("4", Piece.RED_SOLDIER, Place.of(6, 6)));
        list.add(new ChessPiece("5", Piece.RED_SOLDIER, Place.of(8, 6)));

        // 添加黑色棋子
        list.add(new ChessPiece("1", Piece.BLACK_CAR, Place.of(0, 0)));
        list.add(new ChessPiece("1", Piece.BLACK_HORSE, Place.of(1, 0)));
        list.add(new ChessPiece("1", Piece.BLACK_ELEPHANT, Place.of(2, 0)));
        list.add(new ChessPiece("1", Piece.BLACK_COUNSELOR, Place.of(3, 0)));
        list.add(new ChessPiece("", Piece.BLACK_BOSS, Place.of(4, 0)));
        list.add(new ChessPiece("2", Piece.BLACK_COUNSELOR, Place.of(5, 0)));
        list.add(new ChessPiece("2", Piece.BLACK_ELEPHANT, Place.of(6, 0)));
        list.add(new ChessPiece("2", Piece.BLACK_HORSE, Place.of(7, 0)));
        list.add(new ChessPiece("2", Piece.BLACK_CAR, Place.of(8, 0)));

        list.add(new ChessPiece("1", Piece.BLACK_CANNON, Place.of(1, 2)));
        list.add(new ChessPiece("2", Piece.BLACK_CANNON, Place.of(7, 2)));
        list.add(new ChessPiece("1", Piece.BLACK_SOLDIER, Place.of(0, 3)));
        list.add(new ChessPiece("2", Piece.BLACK_SOLDIER, Place.of(2, 3)));
        list.add(new ChessPiece("3", Piece.BLACK_SOLDIER, Place.of(4, 3)));
        list.add(new ChessPiece("4", Piece.BLACK_SOLDIER, Place.of(6, 3)));
        list.add(new ChessPiece("5", Piece.BLACK_SOLDIER, Place.of(8, 3)));
        return list;
    }

}

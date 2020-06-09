package cn.cpf.app.chess.main;

import cn.cpf.app.chess.bean.ChessPiece;
import cn.cpf.app.chess.domain.Situation;
import cn.cpf.app.chess.res.Part;
import cn.cpf.app.chess.res.Piece;
import cn.cpf.app.chess.res.Place;
import cn.cpf.app.chess.res.PlayerType;
import com.google.gson.Gson;
import lombok.NonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/3/19 13:53
 */
public class ChessConfig {

    public static final LocalDateTime appStartTime = LocalDateTime.now();

    public static final Part firstPart = Part.RED;

    public static final PlayerType RedPlayer = PlayerType.COM;

    public static final PlayerType BlackPlayer = PlayerType.COM;

    public static int deep = 6;

    public static final int sha = 0;

    public static final int interval_time = 600;

    public static boolean comRunnable;

    public static PlayerType getPlayerType(@NonNull Part part) {
        if (part == Part.RED) {
            return RedPlayer;
        } else {
            return BlackPlayer;
        }
    }

    public static void saveSituation(Situation situation) throws IOException {
        String fileName = String.format("situation-%s-%s-%s", appStartTime, situation.situationStartTime, LocalDateTime.now());
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
        list.add(new ChessPiece("1", Piece.RedCar, Place.of(0, 9)));
        list.add(new ChessPiece("1", Piece.RedHorse, Place.of(1, 9)));
        list.add(new ChessPiece("1", Piece.RedElephant, Place.of(2, 9)));
        list.add(new ChessPiece("1", Piece.RedCounselor, Place.of(3, 9)));
        list.add(new ChessPiece("", Piece.RedBoss, Place.of(4, 9)));
        list.add(new ChessPiece("2", Piece.RedCounselor, Place.of(5, 9)));
        list.add(new ChessPiece("2", Piece.RedElephant, Place.of(6, 9)));
        list.add(new ChessPiece("2", Piece.RedHorse, Place.of(7, 9)));
        list.add(new ChessPiece("2", Piece.RedCar, Place.of(8, 9)));

        list.add(new ChessPiece("1", Piece.RedCannon, Place.of(1, 7)));
        list.add(new ChessPiece("2", Piece.RedCannon, Place.of(7, 7)));
        list.add(new ChessPiece("1", Piece.RedSoldier, Place.of(0, 6)));
        list.add(new ChessPiece("2", Piece.RedSoldier, Place.of(2, 6)));
        list.add(new ChessPiece("3", Piece.RedSoldier, Place.of(4, 6)));
        list.add(new ChessPiece("4", Piece.RedSoldier, Place.of(6, 6)));
        list.add(new ChessPiece("5", Piece.RedSoldier, Place.of(8, 6)));

        // 添加黑色棋子
        list.add(new ChessPiece("1", Piece.BlackCar, Place.of(0, 0)));
        list.add(new ChessPiece("1", Piece.BlackHorse, Place.of(1, 0)));
        list.add(new ChessPiece("1", Piece.BlackElephant, Place.of(2, 0)));
        list.add(new ChessPiece("1", Piece.BlackCounselor, Place.of(3, 0)));
        list.add(new ChessPiece("", Piece.BlackBoss, Place.of(4, 0)));
        list.add(new ChessPiece("2", Piece.BlackCounselor, Place.of(5, 0)));
        list.add(new ChessPiece("2", Piece.BlackElephant, Place.of(6, 0)));
        list.add(new ChessPiece("2", Piece.BlackHorse, Place.of(7, 0)));
        list.add(new ChessPiece("2", Piece.BlackCar, Place.of(8, 0)));

        list.add(new ChessPiece("1", Piece.BlackCannon, Place.of(1, 2)));
        list.add(new ChessPiece("2", Piece.BlackCannon, Place.of(7, 2)));
        list.add(new ChessPiece("1", Piece.BlackSoldier, Place.of(0, 3)));
        list.add(new ChessPiece("2", Piece.BlackSoldier, Place.of(2, 3)));
        list.add(new ChessPiece("3", Piece.BlackSoldier, Place.of(4, 3)));
        list.add(new ChessPiece("4", Piece.BlackSoldier, Place.of(6, 3)));
        list.add(new ChessPiece("5", Piece.BlackSoldier, Place.of(8, 3)));
        return list;
    }

}

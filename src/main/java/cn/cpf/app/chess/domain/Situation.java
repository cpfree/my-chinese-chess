package cn.cpf.app.chess.domain;

import cn.cpf.app.chess.bean.StepRecord;
import cn.cpf.app.chess.main.ChessConfig;
import cn.cpf.app.chess.res.Part;
import cn.cpf.app.chess.res.Piece;
import cn.cpf.app.chess.res.Place;
import lombok.Getter;
import lombok.NonNull;

import java.util.Date;

/**
 * <b>Description : </b> 当前棋局的形势, 双方都有什么棋子, 在什么位置, 下一步该谁走.
 *
 * @author CPF
 * Date: 2020/3/18 14:22
 */
public class Situation {

    /**
     * 当前棋盘
     */
    @Getter
    private Piece[][] boardPiece;

    /**
     * 下一步行走的势力
     */
    @Getter
    private Part nextPart;

    /**
     * 当前棋局定点分数, 可以根据棋盘上的棋子和下一步行走方计算
     */
    @Getter
    public int[][] scores;

    /**
     * 下棋记录
     */
    @Getter
    public SituationRecord situationRecord;

    public Piece getPiece(@NonNull Place place) {
        return boardPiece[place.x][place.y];
    }

    public Situation() {
        init(ChessConfig.geneDefaultPieceSituation());
    }

    public void init(Piece[][] pieces) {
        boardPiece = pieces;
        // 获取先手方配置信息
        nextPart = ChessConfig.firstPart;
        // TODO 计算分数
        // scores = new int[ChessDefined.RANGE_X][ChessDefined.RANGE_Y];
    }



//
//    /**
//     * 判断当前局势是否胜利
//     * @return
//     */
//    public boolean isWin(Place place, Pt part){
//        return Base.isWin(board, place, part);
//    }
//
//
//    /**
//     * 评估当前局势
//     * @param boardSpace
//     * @param place
//     * @param part
//     * @return
//     */
//    public int evaluate(Pt thispt){
//        return BoardEvaluate.evaluate(board, thispt);
//    }
//
//    /**
//     * 返回某位之上的棋子
//     * @param place
//     * @return
//     */
//    public Pt getPiece(Place place){
//        return board[place.x][place.y];
//    }

    /**
     * 真实落子
     * @param part
     * @param piece
     * @param oldPlace
     * @param newPlace
     * @return
     */
    public void realLocatePiece(Part part, Piece piece, Place oldPlace, Place newPlace){
        if (piece != boardPiece[oldPlace.x][oldPlace.y]) {
            throw new RuntimeException();
        }
        // 判断是否是吃子
        if (boardPiece[newPlace.x][newPlace.y] != null) {
            System.out.println(piece.name() + " eat " + boardPiece[newPlace.x][newPlace.y].name());
        }

        // 走棋
        boardPiece[oldPlace.x][oldPlace.y] = null;
        boardPiece[newPlace.x][newPlace.y] = piece;
        // 添加记录
        situationRecord.addRecord(part, piece, oldPlace, newPlace);

        // 势力
        this.nextPart = Part.getOpposite(part);
        // 开额外线程判断是否胜利, 或连将
    }
//
//    /**
//     * 落子
//     * @param place
//     * @param part
//     */
//    protected void virtualLocatePiece(Place place, Pt part){
//        board[place.x][place.y] = part;
//    }
//
//    protected void virtualRemovePiece(Place place) {
//        board[place.x][place.y] = null;
//    }
//
//    /**
//     * @return 获取当前棋盘上可以下的点的List集合
//     */
//    public List<Place> getHasNeighborPlaces() {
//        return GenePlaces.getHasNeighborPlaces(board);
//    }
//
//    /**
//     * @return 获取当前棋盘上可以下的点的List集合
//     */
//    public List<Place> getAllBlankPlaces() {
//        return GenePlaces.getAllBlankPlaces(board);
//    }
//
//    /**
//     * 启发式搜索函数
//     * @return
//     */
//    public Collection<Place> getHeuristicPlaces(Pt thispt) {
//        return GenePlaces.getHeuristicPlaces(board, thispt);
//    }
//
//
//    @Override
//    protected Object clone() throws CloneNotSupportedException {
//        return super.clone();
//    }
//
//
//    public void setScore(Place place, int score){
//        scores[place.x][place.y] = score;
//    }
//
//
//    @Override
//    public String toString() {
//        StringBuilder strbdr = new StringBuilder();
//        strbdr.append("board \n");
//        strbdr.append(getBoardPrintString());
//        strbdr.append("score \n");
//        strbdr.append(getScorePrintString());
//        return strbdr.toString();
//    }
//
//
//    public String getBoardPrintString(){
//        Pt[][] boardCopy = CpfUtilArr.deepClone(board);
//        CpfUtilArr.transposeMatrix(boardCopy);
//        StringBuilder strbdr = new StringBuilder();
//        Pt tmp[] = null;
//        strbdr.append("\n " + " -- ");
//        for (int r = 0; r < 15; r++){
//            strbdr.append("-\t-" + String.format("%02d", r));
//        }
//        for (int i = 0; i < 15; i ++) {
//            strbdr.append("\n " + i + " - ");
//            tmp = boardCopy[i];
//            for (int k=0; k < 15; k ++){
//                strbdr.append("\t" + tmp[k]);
//            }
//        }
//        return strbdr.toString();
//    }
//
//    public String getScorePrintString(){
//        int[][] scoresCopy = CpfUtilArr.deepClone(scores);
//        CpfUtilArr.transposeMatrix(scoresCopy);
//        StringBuilder strbdr = new StringBuilder();
//        int tmp[] = null;
//        strbdr.append("\n " + " -- ");
//        for (int r = 0; r < 15; r++){
//            strbdr.append("-\t-" + String.format("%02d", r));
//        }
//        for (int i = 0; i < 15; i ++) {
//            strbdr.append("\n " + i + " - ");
//            tmp = scoresCopy[i];
//            for (int k=0; k < 15; k ++){
//                strbdr.append("\t" + tmp[k]);
//            }
//        }
//        return strbdr.toString();
//    }
//
//
//    /**
//     * 获取当前下棋角色
//     * @return
//     */
//    public Role getCurRole() {
//        return Config.getRole(curPart);
//    }
//
//    public Pt getCurPart() {
//        return curPart;
//    }
//
//    public void setCurPart(Pt curPart) {
//        this.curPart = curPart;
//    }
}

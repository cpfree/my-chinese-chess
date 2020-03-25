//package cn.cpf.app.chess.algorithm;
//
//import cn.cpf.app.gobang.entity.Place;
//import cn.cpf.app.gobang.entity.PointScoreDisposer;
//import cn.cpf.app.gobang.entity.Pt;
//import cn.cpf.app.gobang.entity.Score;
//import cn.cpf.app.gobang.global.Config;
//import cn.cpf.app.gobang.validate.ExceptionUtil;
//
///**
// * @author CPF
// * 启发式评价函数
// * 这个是专门给某一个空位打分的，不是给整个棋盘打分的
// * 并且是只给某一个角色打分
// */
//public class PointEvaluate {
//
//	/**
//	 * 评估半径
//	 */
////	private static int EVALUTE_R = 3;
//
//	/**
//	 * 小工具
//	 * @param num
//	 * @param addend
//	 * @return
//	 */
//	private static boolean isOutOfBroads(int num, int addend){
//		switch (addend) {
//		case  1 : return num >= Config.BOARDLENGTH;
//		case -1 : return num <  0;
//		case  0 :
//		default : return false;
//		}
//	}
//
//	/**
//	 * 启发式评估函数
//	 * 表示在当前位置下一个棋子后的分数
//	 * @param boardSpace
//	 * @param x
//	 * @param y
//	 * @param part
//	 */
//	public static int pointEvaluate(final Pt[][] boardSpace, final Place place , final Pt part){
//		PointScoreDisposer sDisposer = new PointScoreDisposer();
//		int pline = subPointEvaluate(boardSpace, place.x, place.y, 1, 0, part);
//		sDisposer.handleAwayOfPoint(pline);
//	    pline = subPointEvaluate(boardSpace, place.x, place.y, 0, 1, part);
//		sDisposer.handleAwayOfPoint(pline);
//	    pline = subPointEvaluate(boardSpace, place.x, place.y, 1, 1, part);
//		sDisposer.handleAwayOfPoint(pline);
//		pline = subPointEvaluate(boardSpace, place.x, place.y, 1, -1, part);
//		sDisposer.handleAwayOfPoint(pline);
//		return sDisposer.getPointScore();
//	}
//
//
//
//	/**
//	 * 对一行棋子进行打分
//	 * (x, y)  :  为评估的地方所落子的位置
//	 * (xaddend, yaddend)  :  值为(1,1) : ↘ 方向, (1,0) : → 方向, (0,1) : ↑ 方向, (1,-1) : ↗ 方向
//	 *
//	 * @param boardSpace  棋盘局势数组
//	 * @param x  落子的x坐标
//	 * @param y  落子的y坐标
//	 * @param xaddend  正方向运算时 x 轴每次增加的值
//	 * @param yaddend  正方向运算时 y 轴每次增加的值
//	 * @param part  当前所评估的势力
//	 */
//	public static int subPointEvaluate(Pt[][] boardSpace, int x, int y, int xaddend, int yaddend, Pt part) {
//		int count = 1; // 连续棋子的数量（算上评定当前[x，y]位置的棋子数 ）
//	    int block = 0; // 阻碍数量
//	    int empty = -1; // 空位位置（-1则表示无，若block为1时，空位位置自block开始算起）
//		for (int i = x + xaddend, j = y + yaddend; true; i += xaddend, j += yaddend){
//			// 若x轴越界或者y轴越界
//			if (isOutOfBroads(i, xaddend) || isOutOfBroads(j, yaddend)){
//				++ block;
//				break;
//			}
//			if (boardSpace[i][j] == null){
//				if (empty == -1 && !(isOutOfBroads(i + xaddend, xaddend) || isOutOfBroads(j + yaddend, yaddend)) && part.equals(boardSpace[i + xaddend][j + yaddend])){
//					empty = 0; // 空位默认从右往左开始数
//					continue;
//				}else {
//					break;
//				}
//			} else if (boardSpace[i][j].equals(part)) {
//				count ++;
//				if (empty != -1) { // 若存在空位位置，则空位位置也要加一
//					empty ++;
//				}
//				continue;
//			} else {
//				block++;
//				break;
//			}
//		}
//		xaddend = - xaddend;
//		yaddend = - yaddend;
//		for (int i = x+xaddend, j = y+yaddend; true; i += xaddend, j += yaddend){
//			// 若x轴越界或者y轴越界
//			if (isOutOfBroads(i, xaddend) || isOutOfBroads(j, yaddend)){
//				if (block == 0 && empty != -1) { // 正方向有阻碍 && 正方向有空位 ，则空位位置转为自坐标轴由小到大开始数
//					empty = count - empty;
//				}
//				++ block;
//				break;
//			}
//			if (boardSpace[i][j] == null){
//				if (empty == -1 && !(isOutOfBroads(i + xaddend, xaddend) || isOutOfBroads(j + yaddend, yaddend)) && part.equals(boardSpace[i + xaddend][j + yaddend])){
//					empty = count; // 无论正面是否已经有阻碍，empty暂时从右往左数
//					continue;
//				}else {
//					break;
//				}
//			}else if (boardSpace[i][j].equals(part)){
//				count ++ ;
//				continue;
//			}else {
//				if (block == 0 && empty != -1) { // 正方向有阻碍 && 正方向有空位 ，则空位位置转为自坐标轴由小到大开始数
//					empty = count - empty;
//				}
//				block ++ ;
//				break;
//			}
//		}
//		return grade(count, empty, block);
//	}
//
//
//	/**
//	 * 通过相关数量对一行棋子进行打分
//	 * @param count 棋子数
//	 * @param empty 空位位置， 空位是第几个, 空位不可能是第0个, 只能在 (-1, 1,2,3,4,....)中
//	 * @param block 阻碍数  只可能是0,1,2
//	 * @return
//	 */
//	public static int grade(int count, int empty, int block){
//		// count 不可能小于 empty + 1
//		// 空位不可能是第0个
//		if (empty == -1) {
//			if (count >= 5) {
//				return Score.FIVE;
//			}
//		} else if (empty >= 5 || count - empty >= 5){
//			// 若连续棋子的数量>=5，则直接返回
//			return Score.FIVE;
//		}
//		switch (empty) {
//		case -1: // 无空位
//			if (block == 0){
//				switch (count) {
//					case 1: return Score.ONE;
//		        	case 2: return Score.TWO;
//		        	case 3: return Score.THREE;
//		        	case 4: return Score.FOUR;
//				}
//			}else if (block == 1){
//				switch (count) {
//			        case 1: return Score.BLOCKED_ONE;
//			        case 2: return Score.BLOCKED_TWO;
//			        case 3: return Score.BLOCKED_THREE;
//			        case 4: return Score.BLOCKED_FOUR;
//				}
//			}
//			break;
//		case 1: // 第一个是空位
//			if (block == 0){
//				switch (count) {
//			        case 2: return Score.TWO/2;  		//   ■ ○ ■
//			        case 3: return Score.THREE/2;  		//   ■ ○ ■ ■
//			        case 4: return Score.BLOCKED_FOUR;	//   ■ ○ ■ ■ ■
//			        case 5: return Score.FOUR;   		//   ■ ○ ■ ■ ■ ■
//				}
//			}else if (block == 1){
//				switch (count) {
//			        case 2: return Score.BLOCKED_TWO;
//			        case 3: return Score.BLOCKED_THREE;
//			        case 4: return Score.BLOCKED_FOUR;
//			        case 5: return Score.FOUR;
//				}
//			}
//			break;
//		case 2:
//			if (block == 0){
//				switch (count) {
//		        case 3: return Score.THREE/2;		//  ■ ■ ○ ■
//		        case 4: 							//  ■ ■ ○ ■ ■
//		        case 5: return Score.BLOCKED_FOUR;	//  ■ ■ ○ ■ ■ ■
//		        case 6: return Score.FOUR;			//  ■ ■ ○ ■ ■ ■ ■
//				}
//			}else if (block == 1){
//				switch (count) {
//		        case 3: return Score.BLOCKED_THREE;
//		        case 4:
//		        case 5: return Score.BLOCKED_FOUR;
//		        case 6: return Score.FOUR;
//				}
//			}else if (block == 2){
//				switch(count) {
//				case 4:
//				case 5:
//				case 6: return Score.BLOCKED_FOUR;
//				}
//			}
//			break;
//		case 3:
//			if (block == 0){
//				switch (count) {
//		        case 4:								//  ■ ■ ■ ○ ■
//		        case 5: 							//  ■ ■ ■ ○ ■ ■
//		        case 6: return Score.BLOCKED_FOUR;	//  ■ ■ ■ ○ ■ ■ ■
//		        case 7: return Score.FOUR;			//  ■ ■ ■ ○ ■ ■ ■ ■
//				}
//			}else if (block == 1){
//				switch (count) {
//		        case 4:
//		        case 5:
//		        case 6: return Score.BLOCKED_FOUR;
//		        case 7: return Score.FOUR;
//				}
//			}else if (block == 2){
//				return Score.BLOCKED_FOUR;
//			}
//			break;
//		case 4:
//			if (block == 0){
//				return Score.FOUR;
//			}else if (block == 1){
//				switch (count) {
//		        case 5:								//  ■ ■ ■ ■ ○ ■
//		        case 6:								//  ■ ■ ■ ■ ○ ■ ■
//		        case 7:	return Score.BLOCKED_FOUR;	//  ■ ■ ■ ■ ○ ■ ■ ■
//		        case 8: return Score.FOUR;			//  ■ ■ ■ ■ ○ ■ ■ ■ ■
//				}
//			}else if (block == 2){
//				return Score.BLOCKED_FOUR;
//			}
//			break;
//		default:
//			System.err.println("count : " + count + " , empty : " + empty + " , block : " + block);
//			ExceptionUtil.throwIllegalValue();
//		}
//		return 0;
//	}
//
//}

package cn.cpf.app.chess.base;

/**
 * <b>Description : </b> 工具类, 减少安全检查, 提升速度
 *
 * @author CPF
 * Date: 2020/3/19 18:10
 */
public class ArrayUtils {

    public static int numberInMiddle(Object[] arr, int from, int to) {
        int min, max;
        if (from > to) {
            min = to;
            max = from;
        } else {
            min = from;
            max = to;
        }
        int num = 0;
        for (min++; min < max; min++) {
            if (arr[min] != null) num++;
        }
        return num;
    }

    public static int numberInMiddle(Object[][] arr, int x, int from, int to) {
        int min, max;
        if (from > to) {
            min = to;
            max = from;
        } else {
            min = from;
            max = to;
        }
        int num = 0;
        for (min++; min < max; min++) {
            if (arr[min][x] != null) num++;
        }
        return num;
    }

}

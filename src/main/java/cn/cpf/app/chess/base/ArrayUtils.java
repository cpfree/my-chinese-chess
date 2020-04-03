package cn.cpf.app.chess.base;

/**
 * <b>Description : </b> 工具类, 减少安全检查, 提升速度
 *
 * @author CPF
 * Date: 2020/3/19 18:10
 */
public class ArrayUtils {

    public static boolean nullInMiddle(Object[] arr, int from, int to) {
        int min, max;
        if (from > to) {
            min = to;
            max = from;
        } else {
            min = from;
            max = to;
        }
        for (min++; min < max; min++) {
            if (arr[min] != null) {
                return false;
            }
        }
        return true;
    }

    public static boolean oneInMiddle(Object[] arr, int from, int to) {
        int min, max;
        if (from > to) {
            min = to;
            max = from;
        } else {
            min = from;
            max = to;
        }
        boolean one = false;
        for (min++; min < max; min++) {
            if (arr[min] != null) {
                if (one) {
                    return false;
                } else {
                    one = true;
                }
            }
        }
        return one;
    }

    public static boolean nullInMiddle(Object[][] arr, int y, int from, int to) {
        int min, max;
        if (from > to) {
            min = to;
            max = from;
        } else {
            min = from;
            max = to;
        }
        for (min++; min < max; min++) {
            if (arr[min][y] != null) {
                return false;
            }
        }
        return true;
    }

    public static boolean oneInMiddle(Object[][] arr, int y, int from, int to) {
        int min, max;
        if (from > to) {
            min = to;
            max = from;
        } else {
            min = from;
            max = to;
        }
        boolean one = false;
        for (min++; min < max; min++) {
            if (arr[min][y] != null) {
                if (one) {
                    return false;
                } else {
                    one = true;
                }
            }
        }
        return one;
    }


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

    public static int numberInMiddle(Object[][] arr, int y, int from, int to) {
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
            if (arr[min][y] != null) num++;
        }
        return num;
    }

    /**
     * 转置矩阵, 矩阵必须是方形矩阵
     * @param matrix
     */
    public static <T> T[][] deepClone(T[][] matrix){
        int len = matrix.length;
        T[][] arr = matrix.clone();
        for (int i = 0; i<len;i ++){
            arr[i] = matrix[i].clone();
        }
        return arr;
    }
}

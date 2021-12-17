package cn.cpf.app.chess.util;

/**
 * <b>Description : </b> 工具类, 减少安全检查, 提升速度
 *
 * @author CPF
 * Date: 2020/3/19 18:10
 */
public class ArrayUtils {

    private ArrayUtils() {
    }

    /**
     * 在 arr[] 中, 从 from 到 to 的位置中间, 是否没有对象
     */
    public static boolean nullInMiddle(final Object[] arr, int from, int to) {
        if (from > to) {
            final int tmp = from;
            from = to;
            to = tmp;
        }
        for (from++; from < to; from++) {
            if (arr[from] != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 在 arr[] 中, 从 from 到 to 的位置中间, 是否只有1个对象
     */
    public static boolean oneInMiddle(final Object[] arr, int from, int to) {
        if (from > to) {
            final int tmp = from;
            from = to;
            to = tmp;
        }
        boolean one = false;
        for (from++; from < to; from++) {
            if (arr[from] != null) {
                if (one) {
                    return false;
                } else {
                    one = true;
                }
            }
        }
        return one;
    }

    /**
     * 在 arr[][] 中, 从 arr[from][y] 到 arr[to][y] 的位置中间, 是否没有对象
     */
    public static boolean nullInMiddle(final Object[][] arr, int y, int from, int to) {
        if (from > to) {
            final int tmp = from;
            from = to;
            to = tmp;
        }
        for (from++; from < to; from++) {
            if (arr[from][y] != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 在 arr[][] 中, 从 arr[from][y] 到 arr[to][y] 的位置中间, 是否只有1个对象
     */
    public static boolean oneInMiddle(final Object[][] arr, int y, int from, int to) {
        if (from > to) {
            final int tmp = from;
            from = to;
            to = tmp;
        }
        boolean one = false;
        for (from++; from < to; from++) {
            if (arr[from][y] != null) {
                if (one) {
                    return false;
                } else {
                    one = true;
                }
            }
        }
        return one;
    }


    /**
     * 在 arr[] 中, 从 arr[from] 到 arr[to] 的位置中间, 有多少对象
     *
     * @return 从 arr[from] 到 arr[to] 的位置中间的对象数
     */
    public static int numberInMiddle(final Object[] arr, int from, int to) {
        if (from > to) {
            final int tmp = from;
            from = to;
            to = tmp;
        }
        int num = 0;
        for (from++; from < to; from++) {
            if (arr[from] != null) num++;
        }
        return num;
    }

    /**
     * 在 arr[][] 中, 从 arr[from][y] 到 arr[to][y] 的位置中间, 有多少对象
     *
     * @return 从 arr[from][y] 到 arr[to][y] 的位置中间的对象数
     */
    public static int numberInMiddle(final Object[][] arr, int y, int from, int to) {
        if (from > to) {
            final int tmp = from;
            from = to;
            to = tmp;
        }
        int num = 0;
        for (from++; from < to; from++) {
            if (arr[from][y] != null) num++;
        }
        return num;
    }

    /**
     * 二维数组深拷贝
     */
    public static <T> T[][] deepClone(final T[][] matrix) {
        int len = matrix.length;
        T[][] arr = matrix.clone();
        for (int i = 0; i < len; i++) {
            arr[i] = matrix[i].clone();
        }
        return arr;
    }

}

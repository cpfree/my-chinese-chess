package cn.cpf.app.chess.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    /**
     * 将一个列表分割成最多指定指定数量个列表
     *
     * 若 count > list.size() , 则分割成 list.size() 个列表
     *
     * eg: 源列表 [0, 1, 2, 3, 4]
     *
     * 分别分割成:
     *  1: [[0, 1, 2, 3, 4]]
     *  2: [[0, 1, 2], [3, 4]]
     *  3: [[0, 1], [2, 3], [4]]
     *  4: [[0], [1], [2], [3], [4]]
     *  5: [[0], [1], [2], [3], [4]]
     *
     * @param list  列表
     * @param count 风格数量
     * @param <T>   列表中对象类型
     * @return 分割后的列表
     */
    public static <T> List<List<T>> partition(List<T> list, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count should not letter than 0");
        }
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        if (count == 1) {
            return Collections.singletonList(list);
        }
        final int i = list.size() / count;
        final int j = list.size() % count;
        final int len = i <= 0 ? j : count;
        final List<List<T>> pList = new ArrayList<>(len);
        for (int k = 0, s = 0, e; k < len; k++) {
            e = s + i + (k < j ? 1 : 0);
            pList.add(list.subList(s, e));
            s = e;
        }
        return pList;
    }

}

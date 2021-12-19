package cn.cpf.app.chess.util;

import cn.cpf.app.chess.algorithm.DebugInfo;
import cn.cpf.app.chess.inter.MyList;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.StepBean;
import com.github.cosycode.common.ext.bean.DoubleBean;
import com.github.cosycode.common.lang.ShouldNotHappenException;
import com.github.cosycode.common.util.common.ThreadUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * <b>Description : </b> 与 MyList 结合使用，使得List，可以重复利用
 * <p>
 * <b>created in </b> 2021/12/16
 * </p>
 *
 * @author CPF
 * @since 1.0
 **/
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListPool {

    /**
     * 将 ListPool 再次集中缓存, 重复利用
     */
    private static final ArrayBlockingQueue<ListPool> blockingQueue = new ArrayBlockingQueue<>(30);
    /**
     * 每个线程一个 ListPool
     */
    private static final ThreadLocal<ListPool> listThreadLocal = new ThreadLocal<>();
    /**
     * LinkedList 大小不超过 1， MyList&lt;Place> 大小不超过 17
     */
    private final LinkedList<MyList<Place>> placeList17 = new LinkedList<>();
    /**
     * 用于对 MyList&lt;StepBean> 进行排序
     * LinkedList 大小不超过 1， MyList 大小同 MyList&lt;StepBean>
     */
    private final LinkedList<MyList<DoubleBean<Integer, StepBean>>> douList80 = new LinkedList<>();
    /**
     * 每个线程 用到的最大数量 等同于搜索深度，例如搜索深度为 8，则列表大小不超过 8个
     * MyList&lt;StepBean> 的大小不超过 80
     */
    private final LinkedList<MyList<StepBean>> stepList80 = new LinkedList<>();
    @Getter
    private static final MyList<Place> emptyList = newList(0);

    public static void end() {
        ListPool listPool = listThreadLocal.get();
        if (log.isDebugEnabled()) {
            log.debug("end   " + listPool.hashCode() + " " + listPool);
            final Thread[] allGroupThread = ThreadUtils.getActiveThreadArrayInCurGroup(Thread.currentThread());
            Arrays.stream(allGroupThread).map(ThreadUtils::getThreadLocalMap).filter(map -> !map.isEmpty()).map(map -> map.get(listThreadLocal)).filter(Objects::nonNull).distinct().forEach(o ->
                    log.debug("threadId ==> {}, end monitor ListPool : {} ==> {}", Thread.currentThread().getId(), o.hashCode(), o)
            );
            log.debug("listThreadLocal.remove() to blockingQueue || threadId ==> {}, end monitor ListPool : {} ==> {}", Thread.currentThread().getId(), listPool.hashCode(), listPool);
        }
        listThreadLocal.remove();
        if (listPool != null) {
            blockingQueue.add(listPool);
        }
    }

    public static ListPool localPool() {
        ListPool listPool = listThreadLocal.get();
        if (listPool == null) {
            listPool = blockingQueue.poll();
            if (listPool == null) {
                listPool = new ListPool();
                log.debug("threadId ==> {}, new ListPool : {} ==> {}", Thread.currentThread().getId(), listPool.hashCode(), listPool);
            }
            listThreadLocal.set(listPool);
        }
        return listPool;
    }

    private static <T> MyList<T> newList(int capacity) {
        return new FastList<>(capacity);
    }

    public MyList<StepBean> getAStepBeanList() {
        final MyList<StepBean> poll = stepList80.poll();
        if (poll != null) {
            DebugInfo.incrementPollListCount();
            return poll;
        } else {
            DebugInfo.incrementNewListCount();
            return newList(80);
        }
    }

    public MyList<DoubleBean<Integer, StepBean>> getADoubleBeanList() {
        final MyList<DoubleBean<Integer, StepBean>> poll = douList80.poll();
        if (poll != null) {
            DebugInfo.incrementPollListCount();
            return poll;
        } else {
            DebugInfo.incrementNewListCount();
            return newList(81);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void clearAndPushToPool(MyList list) {
        final int length = list.eleTemplateDate().length;
        if (length == 17) {
            placeList17.add(list);
        } else if (length == 80) {
            stepList80.add(list);
        } else if (length == 81) {
            douList80.add(list);
        } else {
            throw new ShouldNotHappenException();
        }
    }

    /**
     * 从 pool 里面获取一个列表，pool 里面没有会自动新建
     *
     * @param capacity 建立列表的容量，保留字段
     * @return 从pool获取的列表
     */
    @SuppressWarnings("unused")
    public MyList<Place> getAPlaceList(int capacity) {
        final MyList<Place> pop = placeList17.poll();
        if (pop != null) {
            DebugInfo.incrementPollListCount();
            return pop;
        } else {
            DebugInfo.incrementNewListCount();
            return newList(17);
        }
    }

    public void addListToPool(MyList<Place> list) {
        if (list.eleTemplateDate().length == 0) {
            return;
        }
        DebugInfo.incrementAddListCount();
        list.clear();
        placeList17.add(list);
    }

    public void addListToStepBeanListPool(MyList<StepBean> list) {
        list.clear();
        DebugInfo.incrementAddListCount();
        stepList80.add(list);
    }

    public void addListToDoubleBeanListPool(MyList<DoubleBean<Integer, StepBean>> list) {
        list.clear();
        DebugInfo.incrementAddListCount();
        douList80.add(list);
    }

    @Override
    public String toString() {
        return "ListPool{" + "placeList17=" + placeList17.size() + ", douList80=" + douList80.size() + ", stepList80=" + stepList80.size() + '}';
    }

}

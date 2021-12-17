package cn.cpf.app.chess.util;

import cn.cpf.app.chess.algorithm.DebugInfo;
import cn.cpf.app.chess.inter.MyList;
import cn.cpf.app.chess.modal.Place;
import cn.cpf.app.chess.modal.StepBean;
import com.github.cosycode.common.ext.bean.DoubleBean;
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
 * <b>Description : </b>
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

    private static final ArrayBlockingQueue<ListPool> blockingQueue = new ArrayBlockingQueue<>(30);

    private static final ThreadLocal<ListPool> listThreadLocal = new ThreadLocal<>();
    @Getter
    private static final MyList<Place> emptyList = newList(0);
    private final LinkedList<MyList<Place>> list1 = new LinkedList<>();
    private final LinkedList<MyList<Place>> list3 = new LinkedList<>();
    private final LinkedList<MyList<Place>> list4 = new LinkedList<>();
    private final LinkedList<MyList<Place>> list8 = new LinkedList<>();
    private final LinkedList<MyList<Place>> list17 = new LinkedList<>();
    private final LinkedList<MyList<StepBean>> stepList80 = new LinkedList<>();
    private final LinkedList<MyList<DoubleBean<Integer, StepBean>>> listDou80 = new LinkedList<>();

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
        blockingQueue.add(listPool);
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
        final MyList<DoubleBean<Integer, StepBean>> poll = listDou80.poll();
        if (poll != null) {
            DebugInfo.incrementPollListCount();
            return poll;
        } else {
            DebugInfo.incrementNewListCount();
            return newList(81);
        }
    }

    public void clearAndPushToPool(MyList<?> list) {
        final int length = list.eleTemplateDate().length;
    }

    public MyList<Place> getAPlaceList(int capacity) {
        final MyList<Place> pop = getSuitPool(capacity).poll();
        if (pop != null) {
            DebugInfo.incrementPollListCount();
            return pop;
        } else {
            DebugInfo.incrementNewListCount();
            return newList(capacity);
        }
    }

    public void addListToPool(MyList<Place> list) {
        if (list.eleTemplateDate().length == 0) {
            return;
        }
        DebugInfo.incrementAddListCount();
        LinkedList<MyList<Place>> pool = getSuitPool(list.eleTemplateDate().length);
        list.clear();
        pool.add(list);
    }

    public void addListToStepBeanListPool(MyList<StepBean> list) {
        list.clear();
        DebugInfo.incrementAddListCount();
        stepList80.add(list);
    }

    public void addListToDoubleBeanListPool(MyList<DoubleBean<Integer, StepBean>> list) {
        list.clear();
        DebugInfo.incrementAddListCount();
        listDou80.add(list);
    }

    private LinkedList<MyList<Place>> getSuitPool(int capacity) {
        if (capacity <= 1) {
            return list1;
        } else if (capacity <= 3) {
            return list3;
        } else if (capacity <= 4) {
            return list4;
        } else if (capacity <= 8) {
            return list8;
        } else {
            return list17;
        }
    }

    @Override
    public String toString() {
        return "ListPool{" +
                "list1=" + list1.size() +
                ", list3=" + list3.size() +
                ", list4=" + list4.size() +
                ", list8=" + list8.size() +
                ", list17=" + list17.size() +
                ", list50=" + stepList80.size() +
                ", listDou50=" + listDou80.size() +
                '}';
    }
}

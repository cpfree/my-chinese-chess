package cn.cpf.app.chess.util;

import cn.cpf.app.chess.inter.MyList;
import com.github.cosycode.common.lang.ShouldNotHappenException;
import com.github.cosycode.common.util.common.ArrUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * <b>Description : </b>
 * <p>
 * 使用 ArrayList 的话会创建很多个对象,
 *
 * <p>
 * <b>created in </b> 2021/12/16
 * </p>
 *
 * @author CPF
 * @since 1.0
 **/
public class FastList<E> implements MyList<E>, Collection<E> {

    public final Object[] elementData;
    int size;

    public FastList(int capacity) {
        elementData = new Object[capacity];
    }

    @Override
    public boolean add(E e) {
        assert size < elementData.length : "容量超限: " + elementData.length;
        elementData[size] = e;
        size++;
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public E get(int index) {
        rangeCheck(index);

        return elementData(index);
    }

    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }

    @Override
    public Object[] eleTemplateDate() {
        return elementData;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void sort(Comparator<? super E> c) {
        Arrays.sort(elementData, 0, size, (Comparator) c);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void forEach(Consumer<? super E> action) {
        final Object[] objects = eleTemplateDate();
        for (int i = 0, len = size(); i < len; i++) {
            action.accept((E)objects[i]);
        }
    }

    @Override
    public boolean contains(Object obj) {
        return ArrUtils.indexOf(elementData, obj) >= 0;
    }

    @Override
    public boolean remove(Object obj) {
        if (obj == null) {
            return false;
        }
        final int idx = ArrUtils.indexOf(elementData, obj);
        if (idx == -1) {
            return false;
        }
        final int end = this.size();
        for (int j = idx; j < end - 1; j++) {
            elementData[j] = elementData[j + 1];
        }
        elementData[end - 1] = null;
        size--;
        return true;
    }

    @Override
    public void close() {
        ListPool.localPool().clearAndPushToPool(this);
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOfRange(elementData, 0, size);
    }

    @Override
    public MyList<E> filter(Predicate<? super Object> predicate) {
        size = ArrUtils.filter(eleTemplateDate(), size, predicate);
        return this;
    }

    /**
     * Checks if the given index is in range.  If not, throws an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     */
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     */
    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }

    @Override
    public Iterator<E> iterator() {
        throw new ShouldNotHappenException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new ShouldNotHappenException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new ShouldNotHappenException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new ShouldNotHappenException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new ShouldNotHappenException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new ShouldNotHappenException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new ShouldNotHappenException();
    }

    @Override
    public E set(int index, E element) {
        throw new ShouldNotHappenException();
    }

    @Override
    public void add(int index, E element) {
        throw new ShouldNotHappenException();
    }

    @Override
    public E remove(int index) {
        throw new ShouldNotHappenException();
    }

    @Override
    public int indexOf(Object o) {
        throw new ShouldNotHappenException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new ShouldNotHappenException();
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new ShouldNotHappenException();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new ShouldNotHappenException();
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new ShouldNotHappenException();
    }

}

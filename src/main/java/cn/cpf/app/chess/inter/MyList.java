package cn.cpf.app.chess.inter;

import java.util.List;
import java.util.function.Predicate;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/12/16
 * </p>
 *
 * @author CPF
 * @since 1.0
 **/
public interface MyList<E> extends List<E>, AutoCloseable {

    Object[] eleTemplateDate();

    MyList<E> filter(Predicate<? super Object> predicate);

}

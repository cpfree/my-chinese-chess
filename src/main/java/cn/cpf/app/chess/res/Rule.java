package cn.cpf.app.chess.res;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/3/19 17:17
 */
@FunctionalInterface
public interface Rule {

    boolean check(Piece[][] pieces, Part part, Place from, Place to);

}

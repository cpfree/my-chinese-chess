package cn.cpf.app.chess.modal;

/**
 * <b>Description : </b> 记录要走的一步棋
 *
 * @author CPF
 * Date: 2020/3/25 17:38
 */
public class StepBean {

    public final Place from;
    public final Place to;
    public int score;

    public StepBean(Place from, Place to) {
        this.from = from;
        this.to = to;
    }

    public StepBean(Place from, Place to, int score) {
        this.from = from;
        this.to = to;
        this.score = score;
    }

    @Override
    public String toString() {
        return "StepBean{" + from + " -> " + to + '}';
    }
}

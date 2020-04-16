import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/4/16 20:25
 */
public class MainTest {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("5");
        list.add("4");
        list.add("3");
        list.add("7");
        list.add("34");

        Collections.sort(list, (o1, o2) -> Integer.parseInt(o2) - Integer.parseInt(o1));
        System.out.println(list);
    }

}

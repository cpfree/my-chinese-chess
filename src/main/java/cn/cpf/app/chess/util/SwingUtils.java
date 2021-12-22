package cn.cpf.app.chess.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.awt.*;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/5/12 16:30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SwingUtils {

    /**
     * 频率, 每隔多少毫秒移动一次
     */
    public static final int INTERVAL = 5;
    /**
     * 基本移动时间
     */
    public static final int BASE_TIME = 100;
    public static final int PX_ONE_TIME = 2;

    /**
     * 平滑将一个组件移动至另一个地点
     * 200px长度, 每5ms移动一次, 一次移动1px, 需要1s
     *
     * @param component 组件
     * @param toPoint   移动位置
     * @throws InterruptedException 移动时异常
     */
    public static void moveComp(Component component, Point toPoint) throws InterruptedException {
        Point fromPoint = component.getLocation();
        int xSub = toPoint.x - fromPoint.x;
        int ySub = toPoint.y - fromPoint.y;
        // 移动时间倍率
        final double sqrt = Math.sqrt((Math.sqrt(Math.pow(xSub, 2) + Math.pow(ySub, 2)) / PX_ONE_TIME * INTERVAL) / BASE_TIME);
        // 移动次数
        int times = (int) (BASE_TIME * sqrt / INTERVAL);
        for (int i = 1; i < times; i++) {
            double p = ((double) i) / times;
            Thread.sleep(INTERVAL);
            component.setLocation((int) (fromPoint.x + xSub * p), (int) (fromPoint.y + ySub * p));
        }
        // 上面的计算 sqrt 可能会有误差, 如果有误差, 重新设置下扶正
        if (!toPoint.equals(component.getLocation())) {
            Thread.sleep(INTERVAL);
            component.setLocation(toPoint);
        }
    }

}

package cn.cpf.app.chess.ctrl;

import cn.cpf.app.chess.modal.Part;
import cn.cpf.app.chess.modal.PlayerType;
import lombok.Data;
import lombok.NonNull;

/**
 * <b>Description : </b> 应用配置
 * <p>
 * <b>created in </b> 2021/8/31
 *
 * @author CPF
 * @since 1.0
 **/
@Data
public class AppConfig {
    /**
     * 先手方
     */
    private Part firstPart;
    /**
     * 红色棋子执棋手类型
     */
    private PlayerType redPlayerType;
    /**
     * 黑棋棋子执棋手类型
     */
    private PlayerType blackPlayerType;
    /**
     * 普通搜索深度
     */
    private int searchDeepLevel;
    /**
     * 即便被将军时也可以乱动
     */
    private boolean activeWhenBeCheck;
    /**
     * 是否使用并行流
     */
    private boolean parallel;
    /**
     * 移动棋子是否显示动画
     */
    private boolean cartoon;
    /**
     * 搜索杀棋深度
     */
    private int searchKillStepDeepLevel;
    /**
     * COM 运行间隔时间
     */
    private int comIntervalTime = 600;

    /**
     * 判断 part 方的 playType
     */
    public PlayerType getPlayerType(@NonNull Part part) {
        if (part == Part.RED) {
            return redPlayerType;
        } else {
            return blackPlayerType;
        }
    }
}

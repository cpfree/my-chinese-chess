package cn.cpf.app.chess.conf;

import cn.cpf.app.chess.util.Utils;
import lombok.extern.slf4j.Slf4j;
import sun.audio.AudioData;
import sun.audio.AudioDataStream;
import sun.audio.AudioPlayer;

import java.io.IOException;

/**
 * <b>Description : </b> 象棋游戏播放类
 * <br> 使用的是 sun.audio.* 中的类, 在一些JVM平台可能不受支持
 *
 * <p>
 * <b>created in </b> 2021/12/21
 * </p>
 *
 * @author CPF
 * @since 1.0
 **/
@Slf4j
public enum ChessAudio {

    /**
     * from 点击时
     */
    CLICK_FROM("300"),
    /**
     * 长拦, 或者导致 BOSS面对面, 或者被将军时, 走错棋
     */
    MAN_MOV_ERROR("301"),
    /**
     * MAN 角色触发正常走棋
     */
    MAN_MOVE("302"),
    /**
     * COM 走棋
     */
    COM_MOVE("303"),
    /**
     * MAN 方吃掉 对方
     */
    MAN_EAT("304"),
    /**
     * COM 方吃掉 对方
     */
    COM_EAT("305"),
    /**
     * MAN 方 抑制/将军 对方
     */
    MAN_CHECK("306"),
    /**
     * COM 方 抑制/将军 对方
     */
    COM_CHECK("307"),
    WIN_BGM("308"),
    LOSE_BGM("309"),
    /**
     * 被 COM 角色将死
     */
    BE_CHECKMATED_BY_COM("310"),
    OPEN_BOARD("311")
    ;

    /**
     * 标记
     */
    private final String tag;

    /**
     * AudioData 对象
     */
    @SuppressWarnings("java:S3077")
    private volatile AudioData audioData;

    ChessAudio(String tag) {
        this.tag = tag;
    }

    @SuppressWarnings("java:S3077")
    private static volatile AudioDataStream audioDataStream = null;

    /**
     * 获取 AudioData 对象, 没有则生成
     *
     * @return AudioData 对象
     */
    public AudioData getAudioData() {
        if (audioData == null) {
            synchronized (this) {
                if (audioData == null) {
                    final String name = "wave/" + tag + ".wav";
                    try {
                        final byte[] bytes = Utils.readFromResource(name);
                        log.info("读取音频文件 " + bytes.length);
                        audioData = new AudioData(bytes);
                    } catch (IOException e) {
                        log.error("IO 读取异常", e);
                        audioData = new AudioData(new byte[0]);
                    }
                }
            }
        }
        return audioData;
    }

    /**
     * 播放音频
     */
    public synchronized void play() {
        if (audioDataStream != null) {
            AudioPlayer.player.stop(audioDataStream);
        }
        audioDataStream = new AudioDataStream(getAudioData());
        AudioPlayer.player.start(audioDataStream);
    }

}

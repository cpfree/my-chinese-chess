package cn.cpf.app.chess.conf;

import cn.cpf.app.chess.swing.ChessPiece;
import cn.cpf.app.chess.util.Utils;
import lombok.extern.slf4j.Slf4j;
import sun.audio.AudioData;
import sun.audio.AudioDataStream;
import sun.audio.AudioPlayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

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
     * 被将军时, 走错棋
     */
    CLICK_TO_ERROR("301"),
    /**
     * to 点击, 触发正常走棋
     */
    CLICK_TO_SUCCESS("302"),
    /**
     * COM 走棋
     */
    COM_MOVE("303"),
    MAN_EAT_COM("304"),
    COM_EAT_MAN("305"),
    MAN_JIANG_COM("306"),
    COM_JIANG_MAN("307"),
    WIN_BGM("308"),
    LOSE_BGM("309"),
    BE_LOSE_JIANG_JUN("310"),
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
                    final String name = "/wave/" + tag + ".wav";
                    final URL resource = ChessPiece.class.getResource(name);
                    try {
                        if (resource == null) {
                            throw new FileNotFoundException("未发现音频文件 : " + name);
                        }
                        final byte[] bytes = Utils.readFile(resource.getPath());
                        audioData = new AudioData(bytes);
                    } catch (FileNotFoundException e) {
                        log.error("", e);
                    } catch (IOException e) {
                        log.error("IO 读取异常", e);
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

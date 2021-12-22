package cn.cpf.app.chess.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/12/21
 * </p>
 *
 * @author CPF
 * @since 1.0
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    /**
     * 文件读取
     *
     * @param sourceFilePath 源文件路径
     * @throws IOException 文件写入异常
     */
    public static byte[] readFile(String sourceFilePath) throws IOException {
        final File file = new File(sourceFilePath);
        if (!file.exists() || !file.isFile()) {
            return new byte[0];
        }
        byte[] buf = new byte[(int) file.length()];
        try (FileInputStream in = new FileInputStream(sourceFilePath)) {
            final int read = in.read(buf);
            assert read == file.length();
        }
        return buf;
    }

}

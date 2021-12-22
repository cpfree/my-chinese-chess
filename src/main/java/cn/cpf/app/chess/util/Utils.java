package cn.cpf.app.chess.util;

import cn.cpf.app.chess.ctrl.Application;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;

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

    /**
     * 动态字节流文件读取
     *
     * @param classPath 源文件路径
     * @throws IOException 文件写入异常
     */
    public static byte[] readFromResource(String classPath) throws IOException {
        final ClassLoader classLoader = Application.class.getClassLoader();
        byte[] buf = new byte[8 * 1024];
        try (InputStream in = classLoader.getResourceAsStream(classPath)) {
            if (in == null) {
                throw new FileNotFoundException("未发现资源文件 : " + classPath);
            }
            // 动态字节流
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            int len;
            while ((len = in.read(buf)) != -1) {
                arrayOutputStream.write(buf, 0, len);
            }
            return arrayOutputStream.toByteArray();
        }
    }

}

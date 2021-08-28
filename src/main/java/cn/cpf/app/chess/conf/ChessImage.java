package cn.cpf.app.chess.conf;

import cn.cpf.app.chess.swing.ChessPiece;

import java.awt.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

/**
 * <b>Description : </b> 与image中图片一一对应
 *
 * @author CPF
 * Date: 2020/3/18 11:36
 */
public enum ChessImage {

    CHESS_BOARD("00.jpg", false),
    POINTER("0.jpg"),
    BLACK_BOSS("1.jpg"),
    BLACK_COUNSELOR("2.jpg"),
    BLACK_ELEPHANT("3.jpg"),
    BLACK_CAR("4.jpg"),
    BLACK_HORSE("5.jpg"),
    BLACK_CANNON("6.jpg"),
    BLACK_SOLDIER("7.jpg"),
    RED_BOSS("8.jpg"),
    RED_COUNSELOR("9.jpg"),
    RED_ELEPHANT("10.jpg"),
    RED_CAR("11.jpg"),
    RED_HORSE("12.jpg"),
    RED_CANNON("13.jpg"),
    RED_SOLDIER("14.jpg");

    Image image;

    ChessImage(String path) {
        this(path, true);
    }

    ChessImage(String path, boolean render) {
        image = Toolkit.getDefaultToolkit().getImage(ChessPiece.class.getResource("/image/" + path));
        if (render) {
            image = makeColorTransparent(image);
        }
    }

    /**
     * 转换图片颜色
     * <p>
     * 参考自: https://www.kutu66.com//Java_API_Classes/article_66492
     */
    private static Image makeColorTransparent(Image image) {
        ImageFilter filter = new RGBImageFilter() {
            @Override
            public int filterRGB(int x, int y, int rgb) {
                Color color1 = new Color(rgb);
                if (color1.getGreen() - color1.getBlue() - color1.getRed() > 0) {
                    return 0;
                }
                return rgb;
            }
        };
        final ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    public Image getImage() {
        return image;
    }
}

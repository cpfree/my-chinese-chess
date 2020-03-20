package cn.cpf.app.chess.res;

import cn.cpf.app.chess.bean.ChessPiece;

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

    ChessBoard("00.jpg", false),
    Pointer("0.jpg"),
    BlackBoss("1.jpg"),
    BlackCounselor("2.jpg"),
    BlackElephant("3.jpg"),
    BlackCar("4.jpg"),
    BlackHorse("5.jpg"),
    BlackCannon("6.jpg"),
    BlackSoldier("7.jpg"),
    RedBoss("8.jpg"),
    RedCounselor("9.jpg"),
    RedElephant("10.jpg"),
    RedCar("11.jpg"),
    RedHorse("12.jpg"),
    RedCannon("13.jpg"),
    RedSoldier("14.jpg");

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
     * https://www.kutu66.com//Java_API_Classes/article_66492
     *
     * @param image
     */
    public static Image makeColorTransparent(Image image) {
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

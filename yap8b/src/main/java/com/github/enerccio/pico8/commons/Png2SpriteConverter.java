package com.github.enerccio.pico8.commons;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Png2SpriteConverter {

    private static final List<Color> PICO_PALETTE = Arrays.asList(
            fromHex("000000"),
            fromHex("1D2B53"),
            fromHex("7E2553"),
            fromHex("008751"),
            fromHex("AB5236"),
            fromHex("5F574F"),
            fromHex("C2C3C7"),
            fromHex("FFF1E8"),
            fromHex("FF004D"),
            fromHex("FFA300"),
            fromHex("FFEC27"),
            fromHex("00E436"),
            fromHex("29ADFF"),
            fromHex("83769C"),
            fromHex("FF77A8"),
            fromHex("FFCCAA")
    );

    private static Color fromHex(String hex) {
        return new Color(Integer.valueOf(hex.substring(0, 2), 16),
                Integer.valueOf(hex.substring(2, 4), 16),
                Integer.valueOf(hex.substring(4, 6), 16));
    }

    private double colorDistance(Color c1, Color c2) {
        int red1 = c1.getRed();
        int red2 = c2.getRed();
        int rmean = (red1 + red2) >> 1;
        int r = red1 - red2;
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return Math.sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8));
    }

    public static BufferedImage loadImage(InputStream inputStream) throws Exception {
        return ImageIO.read(inputStream);
    }

    public byte[] convert(BufferedImage bufferedImage) throws Exception {
        byte[] map = new byte[bufferedImage.getHeight() * bufferedImage.getWidth()];
        for (int i=0; i<bufferedImage.getHeight(); i++) {
            for (int j=0; j<bufferedImage.getWidth(); j++) {
                int rgb = bufferedImage.getRGB(i, j);
                int red = (rgb & 0x00ff0000) >> 16;
                int green = (rgb & 0x0000ff00) >> 8;
                int blue = rgb & 0x000000ff;
                Color c = new Color(red, green, blue);
                map[i * bufferedImage.getWidth() + j] = findClosesColorFromPalette(c);
            }
        }
        return map;
    }

    private byte findClosesColorFromPalette(Color c) {
        List<Double> distances = new ArrayList<>();
        for (Color pc : PICO_PALETTE) {
            distances.add(colorDistance(c, pc));
        }
        int closest = 15;
        double closestV = Double.MAX_VALUE;
        for (int i=0; i<distances.size(); i++) {
            if (distances.get(i) < closestV) {
                closest = i;
                closestV = distances.get(i);
            }
        }
        return (byte) closest;
    }

}

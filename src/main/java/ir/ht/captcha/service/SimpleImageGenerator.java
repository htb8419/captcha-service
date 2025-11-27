package ir.ht.captcha.service;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;

public class SimpleImageGenerator implements ImageGenerator {
    private static final int width = 200;
    private static final int height = 70;
    private final Font font;

    public SimpleImageGenerator() {
        font = new Font("Courier", Font.BOLD, 40);
    }

    public BufferedImage generate(String text) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        // پس‌زمینه
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // نویز: خطوط تصادفی
        SecureRandom rand = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            g.setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
            int x1 = rand.nextInt(width);
            int y1 = rand.nextInt(height);
            int x2 = rand.nextInt(width);
            int y2 = rand.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }
        g.setFont(font);

        // حروف تک‌تک با چرخش و رنگ مختلف
        FontMetrics fm = g.getFontMetrics();
        int charWidth = width / text.length();
        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            int angle = rand.nextInt(30) - 15; // -15 to 15 degrees
            g.setColor(new Color(rand.nextInt(150), rand.nextInt(150), rand.nextInt(150)));

            AffineTransform original = g.getTransform();
            g.rotate(Math.toRadians(angle), i * charWidth + 20, 40);
            g.drawString(ch, i * charWidth + 6, 45);
            g.setTransform(original);
        }

        // نویز: نقطه‌های تصادفی
        for (int i = 0; i < 30; i++) {
            g.setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            g.fillRect(x, y, 2, 2);
        }

        g.dispose();
        return image;
    }
}

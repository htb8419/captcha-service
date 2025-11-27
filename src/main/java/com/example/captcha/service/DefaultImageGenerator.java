package com.example.captcha.service;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.List;

public class DefaultImageGenerator implements ImageGenerator {

    private static final int WIDTH = 200;
    private static final int HEIGHT = 70;
    private static final SecureRandom RAND = new SecureRandom();

    // چند فونت مختلف برای جلوگیری از OCR
    private static final List<String> FONTS = List.of(
            "Arial", "Courier", "Georgia", "Verdana", "Tahoma", "TimesRoman"
    );

    public BufferedImage generate(String text) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // کیفیت بهتر آنتی‌الیاس
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // پس‌زمینه کنترل‌شده
        fillSmartBackground(g);

        // نویز پیش از متن
        drawBezierNoise(g);

        drawCharacters(g, text);

        // distortion نهایی
        //image = applyWaveDistortion(image);

        drawRandomLines(g);
        drawFineDots(image);


        g.dispose();
        return image;
    }

    // ---------------------------------------------------------------------------
    // ۱) تولید پس‌زمینه با گرادیان ملایم (friendly for humans)
    // ---------------------------------------------------------------------------
    private void fillSmartBackground(Graphics2D g) {
        Color base = randomSoftColor();
        Color second = adjustColorDifference(base, 70);

        GradientPaint gp = new GradientPaint(
                0, 0, base,
                WIDTH, HEIGHT, second
        );
        g.setPaint(gp);
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }

    /** رنگ ملایم (pastel-like) */
    private Color randomSoftColor() {
        int r = 150 + RAND.nextInt(80);
        int g = 150 + RAND.nextInt(80);
        int b = 150 + RAND.nextInt(80);
        return new Color(r, g, b);
    }

    /**
     * اختلاف رنگ را کنترل می‌کند تا متن از پس‌زمینه جدا باشد
     */
    private Color adjustColorDifference(Color base, int minDiff) {
        int r = base.getRed();
        int g = base.getGreen();
        int b = base.getBlue();

        // ایجاد تفاوت رنگ منطقی
        r = clamp(r - minDiff + RAND.nextInt(minDiff * 2));
        g = clamp(g - minDiff + RAND.nextInt(minDiff * 2));
        b = clamp(b - minDiff + RAND.nextInt(minDiff * 2));

        return new Color(r, g, b);
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    // ---------------------------------------------------------------------------
    // ۲) رسم خطوط نویز Bezier
    // ---------------------------------------------------------------------------
    private void drawBezierNoise(Graphics2D g) {
        for (int i = 0; i < 3; i++) {
            g.setStroke(new BasicStroke(1.8f));
            g.setColor(new Color(RAND.nextInt(100), RAND.nextInt(100), RAND.nextInt(100), 120));

            int x1 = RAND.nextInt(WIDTH);
            int y1 = RAND.nextInt(HEIGHT);
            int ctrlX = RAND.nextInt(WIDTH);
            int ctrlY = RAND.nextInt(HEIGHT);
            int x2 = RAND.nextInt(WIDTH);
            int y2 = RAND.nextInt(HEIGHT);

            QuadCurve2D q = new QuadCurve2D.Float(x1, y1, ctrlX, ctrlY, x2, y2);
            g.draw(q);
        }
    }

    // ---------------------------------------------------------------------------
    // ۳) رسم کاراکترهای کپچا با چرخش و رنگ مناسب
    // ---------------------------------------------------------------------------
    private void drawCharacters(Graphics2D g, String text) {
        int charWidth = WIDTH / (text.length() + 1);
        int baseline = HEIGHT - 20;

        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));

            g.setFont(new Font(FONTS.get(RAND.nextInt(FONTS.size())), Font.BOLD, 40));

            // رنگ متن کمی تیره‌تر از پس‌زمینه
            Color c = new Color(RAND.nextInt(100), RAND.nextInt(100), RAND.nextInt(100));
            g.setColor(c);

            int angle = RAND.nextInt(26) - 13; // -13° تا +13°
            AffineTransform old = g.getTransform();

            int x = (i + 1) * charWidth - 10;
            g.rotate(Math.toRadians(angle), x, baseline);

            g.drawString(ch, x, baseline);
            g.setTransform(old);
        }
    }

    // ---------------------------------------------------------------------------
    // ۴) distortion (Wave)
    // ---------------------------------------------------------------------------
    private BufferedImage applyWaveDistortion(BufferedImage src) {
        BufferedImage dest = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                int newX = (int) (x + 3 * Math.sin(2 * Math.PI * y / 30));
                int newY = (int) (y + 3 * Math.cos(2 * Math.PI * x / 30));

                if (newX >= 0 && newX < WIDTH && newY >= 0 && newY < HEIGHT)
                    dest.setRGB(x, y, src.getRGB(newX, newY));
                else
                    dest.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
        return dest;
    }

    // ---------------------------------------------------------------------------
    // ۵) نویز نقطه‌ای خفیف (مزاحم OCR ولی آزاردهنده نیست)
    // ---------------------------------------------------------------------------
    private void drawFineDots(BufferedImage img) {
        for (int i = 0; i < 200; i++) {
            int x = RAND.nextInt(WIDTH);
            int y = RAND.nextInt(HEIGHT);
            int rgb = new Color(RAND.nextInt(120), RAND.nextInt(120), RAND.nextInt(120)).getRGB();
            img.setRGB(x, y, rgb);
        }
    }
    private void drawRandomLines(Graphics2D g){
        SecureRandom rand = new SecureRandom();
        for (int i = 0; i < 7; i++) {
            g.setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
            int x1 = rand.nextInt(WIDTH);
            int y1 = rand.nextInt(WIDTH);
            int x2 = rand.nextInt(WIDTH);
            int y2 = rand.nextInt(WIDTH);
            g.drawLine(x1, y1, x2, y2);
        }
    }
}

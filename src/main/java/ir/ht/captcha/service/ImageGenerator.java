package ir.ht.captcha.service;

import java.awt.image.BufferedImage;

public interface ImageGenerator {

    BufferedImage generate(String text);
}

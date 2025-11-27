package com.example.captcha.service;

import java.awt.image.BufferedImage;

public interface ImageGenerator {

    BufferedImage generate(String text);
}

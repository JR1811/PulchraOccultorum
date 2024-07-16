package net.shirojr.pulchra_occultorum.util;

import net.minecraft.util.math.MathHelper;

@SuppressWarnings("unused")
public class ArgbHelper {
    private final int argb;

    public ArgbHelper(int argb) {
        this.argb = argb;
    }

    public ArgbHelper(float normalizedAlpha, float normalizedRed, float normalizedGreen, float normalizedBlue) {
        int alpha = fullColorValue(normalizedAlpha);
        int red = fullColorValue(normalizedRed);
        int green = fullColorValue(normalizedGreen);
        int blue = fullColorValue(normalizedBlue);
        this.argb = (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public int getArgb() {
        return argb;
    }

    int getAlpha() {
        return (argb >> 24) & 0xFF;
    }

    public ArgbHelper setAlpha(float normalized) {
        normalized = MathHelper.clamp(normalized, 0f, 1f);
        return new ArgbHelper(
                normalized,
                ArgbHelper.normalized(getRed()),
                ArgbHelper.normalized(getGreen()),
                ArgbHelper.normalized(getBlue())
        );
    }

    public int getRed() {
        return (argb >> 16) & 0xFF;
    }

    public ArgbHelper setRed(float normalized) {
        normalized = MathHelper.clamp(normalized, 0f, 1f);
        return new ArgbHelper(
                ArgbHelper.normalized(getAlpha()),
                normalized,
                ArgbHelper.normalized(getGreen()),
                ArgbHelper.normalized(getBlue())
        );
    }

    public int getGreen() {
        return (argb >> 8) & 0xFF;
    }

    public ArgbHelper setGreen(float normalized) {
        normalized = MathHelper.clamp(normalized, 0f, 1f);
        return new ArgbHelper(
                ArgbHelper.normalized(getAlpha()),
                ArgbHelper.normalized(getRed()),
                normalized,
                ArgbHelper.normalized(getBlue())
        );
    }

    public int getBlue() {
        return argb & 0xFF;
    }

    public ArgbHelper setBlue(float normalized) {
        normalized = MathHelper.clamp(normalized, 0f, 1f);
        return new ArgbHelper(
                ArgbHelper.normalized(getAlpha()),
                ArgbHelper.normalized(getRed()),
                ArgbHelper.normalized(getGreen()),
                normalized
        );
    }

    public ArgbHelper setBrightness(float factor) {
        factor = MathHelper.clamp(factor, 0f, 1f);
        int red = (int) Math.clamp(getRed() * factor, 0, 255);
        int green = (int) Math.clamp(getGreen() * factor, 0, 255);
        int blue = (int) Math.clamp(getBlue() * factor, 0, 255);

        return new ArgbHelper(
                ArgbHelper.normalized(getAlpha()),
                ArgbHelper.normalized(red),
                ArgbHelper.normalized(green),
                ArgbHelper.normalized(blue)
        );
    }

    public static float normalized(int color) {
        return color / 255f;
    }

    public static int fullColorValue(float normalized) {
        return Math.round(normalized * 255);
    }

    public static int getARGB(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}

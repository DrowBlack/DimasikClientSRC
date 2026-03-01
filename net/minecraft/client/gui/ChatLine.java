package net.minecraft.client.gui;

import dimasik.utils.client.AnimationTest;
import dimasik.utils.client.Easing;

public class ChatLine<T> {
    private final int updateCounterCreated;
    private final T lineString;
    private final int chatLineID;
    private boolean isClient;
    private AnimationTest slideAnimation;
    public AnimationTest yAnim = new AnimationTest(Easing.EASE_OUT_SINE, 450L);
    public AnimationTest alphaAnim = new AnimationTest(Easing.EASE_OUT_SINE, 700L);

    public ChatLine(int updatedCounterCreated, T lineString, int chatLineID, boolean isClient) {
        this.slideAnimation = new AnimationTest(Easing.EASE_OUT_EXPO, 450L);
        this.lineString = lineString;
        this.updateCounterCreated = updatedCounterCreated;
        this.chatLineID = chatLineID;
        this.isClient = isClient;
    }

    public T getLineString() {
        return this.lineString;
    }

    public int getUpdatedCounter() {
        return this.updateCounterCreated;
    }

    public int getChatLineID() {
        return this.chatLineID;
    }

    public boolean isClient() {
        return this.isClient;
    }

    public AnimationTest getSlideAnimation() {
        return this.slideAnimation;
    }
}

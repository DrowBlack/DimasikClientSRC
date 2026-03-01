package com.mojang.text2speech;

import com.mojang.text2speech.Narrator;

public class NarratorDummy
implements Narrator {
    @Override
    public void say(String msg, boolean interrupt) {
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean active() {
        return false;
    }

    @Override
    public void destroy() {
    }
}

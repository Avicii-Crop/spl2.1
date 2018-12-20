package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class BookOrderEvent implements Event {
    private int processTick;
    private int currentTick;

    public BookOrderEvent(int processTick) {
        this.processTick = processTick;
    }

    public int getTick() {
        return currentTick;
    }
}
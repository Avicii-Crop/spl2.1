package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class BookOrderEvent implements Event {
    private int processTick;

    public BookOrderEvent(int processTick) {
        this.processTick = processTick;
    }

}
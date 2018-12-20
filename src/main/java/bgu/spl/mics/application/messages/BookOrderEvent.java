package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent implements Event {
    private int orderTick;
    private int processTick;
    private int issuedTick;
    private Customer customer;

    public BookOrderEvent(int orderTick) {
        this.orderTick = orderTick;
    }

    public Customer getCustomer() {
        return customer;
    }
}
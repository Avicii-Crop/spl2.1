package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.BookStoreRunner.InputJson.OrderSchedule;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.Arrays;
import java.util.Collections;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private OrderSchedule[] orderSchedule;
	private Customer cusmtomer;
	private int currentTick = 0;

	public APIService(String name, OrderSchedule[] orderSchedule, Customer customer) {
		super(name);
		this.orderSchedule = orderSchedule;
		this.cusmtomer = customer;
		Arrays.sort(orderSchedule);
			}
	@Override
	protected void initialize() {
		int index = 0;
		subscribeBroadcast(TickBroadcast.class, br -> currentTick=br.getTick());
			while (orderSchedule[index].getTick() == currentTick) {
				index++;
				BookOrderEvent bookOrderEvent = new BookOrderEvent(currentTick);
				Future orderReceipt = sendEvent(bookOrderEvent);
			}
		}
	}


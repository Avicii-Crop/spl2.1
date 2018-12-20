package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

//Avishai

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class
SellingService extends MicroService{
	private int currentTick = 0;
	private MoneyRegister moneyRegister;

	public SellingService(String num) {
		super("selling" + num);
		this.moneyRegister = MoneyRegister.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, br -> currentTick=br.getTick());
		subscribeEvent(BookOrderEvent.class, ev -> {
			Customer c = ev.getCustomer();
			Future<Integer> bookPrice = sendEvent(new AvailabilityEvent());
			synchronized (c) {
				if (bookPrice.get() != -1 && bookPrice.get() < c.getAvailableCreditAmount()) {
					Future<OrderResult> orderResult = sendEvent(new PurchaseEvent());
					if (orderResult.get() == OrderResult.SUCCESSFULLY_TAKEN){
						sendEvent(new DeliveryEvent());
//						complete(BookOrderEvent, );

					}
				}
			}});
	}

}

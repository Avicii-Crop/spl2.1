package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.BookStoreRunner.InputJson.OrderSchedule;
import bgu.spl.mics.application.messages.BookOrderEvent;
	import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.Arrays;

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
	private Customer customer;
	private int currentTick = 0;



	public APIService(int i, OrderSchedule[] orderSchedule, Customer customer) {
		super("webAPI" + i);
		this.orderSchedule = orderSchedule;
		this.customer = customer;
		Arrays.sort(orderSchedule);
			}
	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, br -> {
			currentTick=br.getTick();
			int index = 0;
			while (orderSchedule[index].getTick() == currentTick) {
				index++;
				BookOrderEvent bookOrderEvent = new BookOrderEvent(currentTick,customer);
				Future<OrderReceipt> orderReceipt = sendEvent(bookOrderEvent);
				if (orderReceipt.get() != null){
					customer.addOrderReciept(orderReceipt);
				}
			}
		});
		}
	}


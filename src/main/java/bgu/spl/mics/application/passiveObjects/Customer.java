package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.BookStoreRunner;

import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 *  * You must not alter any of the given public methods of this class.
 *  * <p>
 * You may add fields and methods 	to this class as you see fit (including public methods).
 */
public class Customer {
	private String name;
	private int id;
	private String address;
	private int distance;
	private List<OrderReceipt> receipts;
	private CreditCard creditCard;


	/**
     * Retrieves the name of the customer.
     */

	public Customer(String name, int id, String address, int distance , int creditCard, int availableAmountInCreditCard){
		this.id = id;
		this.name = name;
		this.address = address;
		this.distance = distance;
		this.creditCard=new CreditCard(creditCard,availableAmountInCreditCard);

	}
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return receipts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return creditCard.amount;
	}

	public void setAvailableCreditAmount(int availableCreditAmount) {
		creditCard.amount = availableCreditAmount;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {

		return creditCard.number;
	}

	public void addOrderReciept(){};

	public class CreditCard{
		protected int number;
		protected int amount;

		public CreditCard(int creditCard, int availableAmountInCreditCard) {
			number=creditCard;
			amount=availableAmountInCreditCard;
		}



	}

	public class OrderSchedule implements Comparable<OrderSchedule>{
            private String bookTitle;
            private int tick;

            public OrderSchedule(String bookTitle,int tick){
                this.bookTitle=bookTitle;
                this.tick=tick;
            }

            public int getTick() {
                return tick;
            }

            public String getBookTitle() {
                return bookTitle;
            }

            public void setBookTitle(String bookTitle) {
                this.bookTitle = bookTitle;
            }

            @Override
            public int compareTo(OrderSchedule o) {
                return this.tick-o.getTick();
            }
        }

}

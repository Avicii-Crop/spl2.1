package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.*;
import java.util.*;


/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    private static Gson gson=new Gson();
    private static Thread timer;
    private static Thread[] sellingServices;
    private static Thread[] inventoryServices;
    private static Thread[] logisticServices;
    private static Thread[] resourceServices;
    private static Thread[] apiServices;
    public static void main(String[] args) {

        Gson gson=new Gson();
        JsonObject json = new JsonObject();
        JsonParser pars = new JsonParser();



        try {
            json = pars.parse(new FileReader(args[0])).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        initInventory(json);
        initResources(json);
        initServices(json.getAsJsonObject("services"));

    }

    private static void initServices(JsonObject services) {

//        sellingServices=new Thread[numOfServices];
//        for(int i=0;i<numOfServices;i++) {
//            sellingServices[i] = new Thread(new SellingService(i));
//            sellingServices[i].start();
//        }
//
//        numOfServices=services.getAsJsonPrimitive("inventoryService").getAsInt();
//        inventoryServices=new Thread[numOfServices];
//        for(int i=0;i<numOfServices;i++) {
//            inventoryServices[i] = new Thread(new SellingService(i));
//            inventoryServices[i].start();
//        }
        try {
            int numOfServices=services.getAsJsonPrimitive("selling").getAsInt();
            initService(SellingService.class,sellingServices=new Thread[numOfServices]);

            numOfServices=services.getAsJsonPrimitive("inventoryService").getAsInt();
            initService( InventoryService.class,inventoryServices=new Thread[numOfServices]);

            numOfServices=services.getAsJsonPrimitive("logistics").getAsInt();
            initService( LogisticsService.class,logisticServices=new Thread[numOfServices]);

            numOfServices=services.getAsJsonPrimitive("resourcesService").getAsInt();
            initService( ResourceService.class,resourceServices=new Thread[numOfServices]);



        } catch (ClassNotFoundException|NoSuchMethodException|
                IllegalAccessException|InvocationTargetException|InstantiationException e) {
            e.printStackTrace();
        }
        initTime(services.getAsJsonObject("time"));
    }

    private static void initService(Class<?> type,Thread[] threads) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Constructor con=type.getConstructor(Integer.class);

            for(int i=0;i<threads.length;i++){
                Constructor c;
                threads[i]=new Thread((Runnable) con.newInstance(i));
            }


    }


    private static void initTime(JsonObject time) {
        int speed=time.getAsJsonPrimitive("speed").getAsInt();
        int duration=time.getAsJsonPrimitive("duration").getAsInt();
        timer=new Thread(new TimeService("timer",speed,duration));
    }


    private static void initResources(JsonObject json) {
        JsonArray jsonArray=json.getAsJsonArray("initialResources").get(0).getAsJsonObject().getAsJsonArray("vehicles");
        ResourcesHolder.getInstance().load(gson.fromJson(jsonArray,DeliveryVehicle[].class));

    }

    private static void initInventory(JsonObject json) {
        JsonArray jsonArray=json.getAsJsonArray("initialInventory");
        Inventory.getInstance().load(gson.fromJson(jsonArray,BookInventoryInfo[].class));
    }



    /**
     * Ths class represent  the JSON object which we are getting in the input to initialize the store.
     */


        public class InitBook{
            private String bookTitle;
            private int amount;
            private int price;

            /**
             * Initialize BookInventoryInfo object
             * @return BookInventoryInfo object
             */
            public BookInventoryInfo getBookInventoryInfo(){
                return new BookInventoryInfo(bookTitle,amount,price);
            }

            public String getBookTitle() {
                return bookTitle;
            }

            public int getAmount() {
                return amount;
            }

            public int getPrice() {
                return price;
            }
        }

        public class InitVehicle{
            private DeliveryVehicle[] vehicles;

            public DeliveryVehicle[] getVehicles() {
                return vehicles;
            }
        }

        public class  InitService{
            private InitTime time;
            private int selling;
            private int inventoryService;
            private int resourcesService;
            private InitCustomer[] customers;

            public InitCustomer[] getCustomers() {
                return customers;
            }

            public InitTime getTime() {
                return time;
            }

            public int getInventoryService() {
                return inventoryService;
            }

            public int getResourcesService() {
                return resourcesService;
            }

            public int getSelling() {
                return selling;
            }


        }
        public class InitCustomer{
            private int id;
            private String name;
            private String address;
            private int distance;
            private CreditCard creditCard;
            private OrderSchedule[] orderSchedule;

            /**
             * Initialize Customer object.
             * @return Customer object
             */
            public Customer getCustomer(){
                return new Customer(name,id, address,distance,creditCard.getNumber(),creditCard.getAmount());
            }

            public int getDistance() {
                return distance;
            }

            public int getId() {
                return id;
            }

            public CreditCard getCreditCard() {
                return creditCard;
            }

            public OrderSchedule[] getOrderSchedule() {
                return orderSchedule;
            }

            public String getAddress() {
                return address;
            }

            public String getName() {
                return name;
            }



        }

        public class CreditCard{
            private int number;
            private int amount;

            public CreditCard(int number,int amount){
                this.number=number;
                this.amount=amount;
            }

            public int getAmount() {
                return amount;
            }

            public int getNumber() {
                return number;
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

        public class InitTime{
            private int speed;
            private int duration;

            public InitTime(int speed,int duration){
                this.speed=speed;
                this.duration=duration;
            }

            public int getSpeed(){return speed;}

            public int getDuration(){return duration;}


        }




}

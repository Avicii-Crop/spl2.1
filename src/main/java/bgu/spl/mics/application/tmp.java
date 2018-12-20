package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static CountDownLatch counter;
    public static void main(String[] args) {
        Gson gson = new Gson();
        int numofthreads;
        int count = 0;
        JsonParser parser = new JsonParser();
        JsonObject jsob = new JsonObject();

        try {
            jsob = parser.parse(new FileReader(args[0])).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String customers_output = "customers_output";
        String inventory_output = "inventory_output";
        String order_receipts_output = "order_receipts_output";
        String money_register_output = "money_register_output";

        // initial inventory
        JsonArray initial_Inventory_Array = jsob.getAsJsonArray("initialInventory");
        BookInventoryInfo[] bookInventoryInfo = gson.fromJson(initial_Inventory_Array, BookInventoryInfo[].class);
        Inventory.getInstance().load(bookInventoryInfo);

        // initial resources
        JsonArray initial_Resources_Array = jsob.getAsJsonArray("initialResources");
        JsonObject temp = initial_Resources_Array.get(0).getAsJsonObject();
        JsonArray temp1 = temp.getAsJsonArray("vehicles");
        DeliveryVehicle[] deliveryVehicles = gson.fromJson(temp1, DeliveryVehicle[].class);
        ResourcesHolder.getInstance().load(deliveryVehicles);


        // initial services
        JsonObject services = jsob.getAsJsonObject("services");

        // inital time service
        JsonObject time = services.getAsJsonObject("time");
        JsonPrimitive objspeed = time.getAsJsonPrimitive("speed");
        JsonPrimitive objduration = time.getAsJsonPrimitive("duration");
        int speed = objspeed.getAsInt();
        int duration = objduration.getAsInt();
        TimeService timeService = new TimeService(speed, duration);
        Thread timer = new Thread(timeService);

        // initial selling
        JsonPrimitive selling = services.getAsJsonPrimitive("selling");
        int selling_num = selling.getAsInt();
        Thread[] sellingServices = new Thread[selling_num];
        for (int i = 0; i < selling_num; i++) {
            SellingService selling_service = new SellingService();
            sellingServices[i] = new Thread(selling_service);
        }

        // initial inventory service
        JsonPrimitive inventoryService = services.getAsJsonPrimitive("inventoryService");
        int inventory_num = inventoryService.getAsInt();
        Thread[] inventoryServices = new Thread[inventory_num];
        for (int i = 0; i < inventory_num; i++) {
            InventoryService inventory_service = new InventoryService();
            inventoryServices[i] = new Thread(inventory_service);
        }

        // initial logistics
        JsonPrimitive logistics = services.getAsJsonPrimitive("logistics");
        int logistics_num = logistics.getAsInt();
        Thread[] logisticsServices = new Thread[logistics_num];
        for (int i = 0; i < logistics_num; i++) {
            LogisticsService logistics_service = new LogisticsService();
            logisticsServices[i] = new Thread(logistics_service);
        }

        // initial resources service
        JsonPrimitive resource = services.getAsJsonPrimitive("resourcesService");
        int resource_num = resource.getAsInt();
        Thread[] resourceServices = new Thread[resource_num];
        for (int i = 0; i < resource_num; i++) {
            ResourceService resource_service = new ResourceService();
            resourceServices[i] = new Thread(resource_service);
        }

        // initial customers
        ConcurrentHashMap<String,Integer> orderSchedule = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer,Customer> customer_to_print = new ConcurrentHashMap<>();
        JsonArray customers = services.getAsJsonArray("customers");
        Customer[] customers_array = gson.fromJson(customers, Customer[].class);
        Thread[] apiservices = new Thread[customers.size()];
        for(int i = 0; i < customers.size() ; i ++  )
        {
            customer_to_print.put(customers_array[i].getId() , customers_array[i]);
            JsonObject tmp = customers.get(i).getAsJsonObject();
            JsonArray order_schedule = tmp.getAsJsonArray("orderSchedule");

            for(int j = 0 ; j < order_schedule.size() ; j ++  )
            {
                JsonObject obj = order_schedule.get(j).getAsJsonObject();
                JsonPrimitive objbookTitle = obj.getAsJsonPrimitive("bookTitle");
                JsonPrimitive objtick = obj.getAsJsonPrimitive("tick");
                orderSchedule.put(objbookTitle.getAsString() , objtick.getAsInt());
            }
            APIService apiService = new APIService(orderSchedule , customers_array[i]);
            apiservices[i] = new Thread(apiService);
            orderSchedule.clear();

        }
        numofthreads = 1 + sellingServices.length + inventoryServices.length + logisticsServices.length + resourceServices.length + apiservices.length;
        Thread[] thread_array = new Thread[numofthreads];

        for (int i = 0; i < sellingServices.length; i++) {
            thread_array[count] = sellingServices[i];
            count++;
        }

        for (int i = 0; i < inventoryServices.length; i++) {
            thread_array[count] = inventoryServices[i];
            count++;
        }

        for (int i = 0; i < logisticsServices.length; i++) {
            thread_array[count] = logisticsServices[i];
            count++;
        }

        for (int i = 0; i < resourceServices.length; i++) {
            thread_array[count] = resourceServices[i];
            count++;
        }

        for (int i = 0; i < apiservices.length; i++) {
            thread_array[count] = apiservices[i];
            count++;
        }

        counter = new CountDownLatch(numofthreads - 1);

        thread_array[thread_array.length-1] = timer;

        for(int i = 0 ; i < thread_array.length ; i ++)
        {
            thread_array[i].start();
        }


        for (int i = 0 ; i < thread_array.length ; i ++)
        {
            try {
                thread_array[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(123);
        CreateOutputFiles(customer_to_print,customers_output,inventory_output,order_receipts_output,money_register_output);
        ConcurrentHashMap<Integer,Customer> a = new ConcurrentHashMap<>();
        System.out.println(customers_array[0].getId());
        Deserialize(customers_output , a );
        System.out.println(a.get(11).getAddress());

    }

    private static void CreateOutputFiles(ConcurrentHashMap<Integer,Customer> customers_array, String customers_output, String inventory_output, String order_receipts_output, String money_register_output)
    {
        Printoutputs(customers_output,customers_array); // first output file
        Inventory.getInstance().printInventoryToFile(inventory_output); // second output file
        MoneyRegister.getInstance().printOrderReceipts(order_receipts_output); //third output file
        Printoutputs(money_register_output,(MoneyRegister.getInstance())); // fourth output file
    }
    public static void Printoutputs (String filename, Serializable s)
    {
        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;
        try {
            fileOutputStream = new FileOutputStream(filename);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(s);
            objectOutputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static <T> T Deserialize (String filename, T result){
        try
        {
            // Reading the object from a file
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            result = (T)in.readObject();

            in.close();
            file.close();
            return result;
        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }

        catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }
        return null;
    }


}
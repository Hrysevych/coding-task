package pl.beutysite.recruit;

import pl.beutysite.recruit.orders.*;

import java.math.BigDecimal;
import java.util.*;

public class OrdersManagementSystemImpl implements OrdersManagementSystem {

    //external systems
    private final TaxOfficeAdapter taxOfficeAdapter;
    private final ItemsRepository itemsRepository;


    private Set<Order> ordersQueue=new LinkedHashSet<>();
    private Order newOrder=null;

    public OrdersManagementSystemImpl(TaxOfficeAdapter taxOfficeAdapter, ItemsRepository itemsRepository) {
        this.taxOfficeAdapter = taxOfficeAdapter;
        this.itemsRepository = itemsRepository;
    }

    @Override
    public void createOrder(int itemId, int customerId, OrderFlag... flags) {

        if (0 == flags.length) {
            throw new NoSuchElementException("No flags passed to order");
        }

        //fetch price and calculate discount and taxes
        BigDecimal itemPrice = itemsRepository.fetchItemPrice(itemId);

        //check if any of flags is priority, not only first
        boolean priority = false;
        for (OrderFlag flag : flags) {
            if (OrderFlag.PRIORITY.equals(flag)) {
                priority = true;
                break;
            }
        }
        //create and queue order

        //if there is only one flag
        if (1 == flags.length) {
            OrderFlag flag = flags[0];
            switch (flag) {
                case STANDARD:
                    newOrder = new StandardOrder(itemId, customerId, itemPrice);
                    break;
                case PRIORITY:
                    newOrder = new PriorityOrder(itemId, customerId, itemPrice);
                    break;
                case INTERNATIONAL:
                    newOrder = new InternationalOrder(itemId, customerId, itemPrice);
                    break;
                case DISCOUNTED:
                    newOrder = new DiscountedOrder(itemId, customerId, itemPrice);
            }
        } else {
            newOrder = new ComplexOrder(itemId, customerId, itemPrice, flags);
        }

        //JIRA-18883 Fix priority orders not always being fetched first
        if (priority) {
            List<Order> tempList = new ArrayList<>(ordersQueue);
            tempList.add(0, newOrder);
            ordersQueue = new LinkedHashSet<>(tempList);
        } else {
            ordersQueue.add(newOrder);
        }

        //send tax due amount
        taxOfficeAdapter.registerTax(newOrder.getTax());
    }

    @Override
    public Order fetchNextOrder() {
        return ordersQueue.iterator().next();
    }
}

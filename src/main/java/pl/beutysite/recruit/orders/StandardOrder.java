package pl.beutysite.recruit.orders;

import java.math.BigDecimal;

/**
 * Created by deHrys on 2017-01-30.
 */
public class StandardOrder extends Order {
    public StandardOrder(int itemId, int customerId, BigDecimal price) {
        super(itemId, customerId, price);
    }
}

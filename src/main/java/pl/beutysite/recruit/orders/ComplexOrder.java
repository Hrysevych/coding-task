package pl.beutysite.recruit.orders;

import pl.beutysite.recruit.OrderFlag;
import pl.beutysite.recruit.PercentageCalculationsHelper;
import pl.beutysite.recruit.SeriousEnterpriseEventBus;
import pl.beutysite.recruit.SeriousEnterpriseEventBusLookup;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * Created by deHrys on 2017-01-30.
 */
public class ComplexOrder extends Order {

    EnumSet<OrderFlag> orderFlags = EnumSet.noneOf(OrderFlag.class);

    private ComplexOrder(int itemId, int customerId, BigDecimal price) {
        super(itemId, customerId, price);
    }

    public ComplexOrder(int itemId, int customerId, BigDecimal price, OrderFlag... orderFlags) {
        this(itemId, customerId, price);
        this.orderFlags.addAll(Arrays.asList(orderFlags));
    }

    public void process() {
        SeriousEnterpriseEventBus seeb = SeriousEnterpriseEventBusLookup.seeb;
        seeb.sendEvent("Order processing started");

        if (orderFlags.contains(OrderFlag.PRIORITY)) {
            seeb.sendEvent("*** This is priority order, hurry up! ***");
        }
        if (orderFlags.contains(OrderFlag.INTERNATIONAL)) {
            seeb.sendEvent("Dispatch translated order confirmation email");
        }
        if (orderFlags.contains(OrderFlag.DISCOUNTED)) {
            seeb.sendEvent("Run fraud detection and revenue integrity check");
        }

        seeb.sendEvent("Initiate shipment");
        seeb.sendEvent("Order processing finished");
    }

    @Override
    public BigDecimal getPrice() {
        BigDecimal price = super.getPrice();
        if (orderFlags.contains(OrderFlag.PRIORITY)) {
            price = PercentageCalculationsHelper.addPercentage(price, PriorityOrder.PRIORITY_ORDER_FEE);
        }
        if (orderFlags.contains(OrderFlag.DISCOUNTED)) {
            price = PercentageCalculationsHelper.subtractPercentage(price, DiscountedOrder.STANDARD_DISCOUNT);
        }
        return price;
    }

    @Override
    public BigDecimal getTax() {
        if (orderFlags.contains(OrderFlag.INTERNATIONAL)) {
            return PercentageCalculationsHelper.getPercentagePart(getPrice(), InternationalOrder.INTERNATIONAL_TAX);
        } else {
            return super.getTax();
        }
    }
}

package pl.beutysite.recruit.orders;

import pl.beutysite.recruit.SeriousEnterpriseEventBus;
import pl.beutysite.recruit.SeriousEnterpriseEventBusLookup;
import pl.beutysite.recruit.PercentageCalculationsHelper;

import java.math.BigDecimal;

public class PriorityOrder extends Order {

    static final BigDecimal PRIORITY_ORDER_FEE = new BigDecimal("1.5");

    public PriorityOrder(int itemId, int customerId, BigDecimal price) {
        super(itemId, customerId, price);
    }

    public void process() {
        SeriousEnterpriseEventBus seeb = SeriousEnterpriseEventBusLookup.seeb;
        seeb.sendEvent("Order processing started");

        seeb.sendEvent("*** This is priority order, hurry up! ***");

        seeb.sendEvent("Initiate shipment");
        seeb.sendEvent("Order processing finished");
    }

    @Override
    public BigDecimal getPrice() {

        return PercentageCalculationsHelper.addPercentage(super.getPrice(), PRIORITY_ORDER_FEE);
    }
}

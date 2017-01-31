package pl.beutysite.recruit.orders;

import pl.beutysite.recruit.SeriousEnterpriseEventBus;
import pl.beutysite.recruit.SeriousEnterpriseEventBusLookup;
import pl.beutysite.recruit.PercentageCalculationsHelper;

import java.math.BigDecimal;

public class DiscountedOrder extends Order {

    static final BigDecimal STANDARD_DISCOUNT = new BigDecimal("11");

    public DiscountedOrder(int itemId, int customerId, BigDecimal price) {
        super(itemId, customerId, price);
    }

    @Override
    public BigDecimal getPrice() {
        return PercentageCalculationsHelper.subtractPercentage(super.getPrice(), STANDARD_DISCOUNT);
    }

    public void process() {
        SeriousEnterpriseEventBus seeb = SeriousEnterpriseEventBusLookup.seeb;
        seeb.sendEvent("Order processing started");

        seeb.sendEvent("Run fraud detection and revenue integrity check");

        seeb.sendEvent("Initiate shipment");
        seeb.sendEvent("Order processing finished");
    }
}

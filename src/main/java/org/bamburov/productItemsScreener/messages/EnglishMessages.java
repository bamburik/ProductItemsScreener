package org.bamburov.productItemsScreener.messages;

public class EnglishMessages implements Messages {
    @Override
    public String getProductBecameCheaperMessage() {
        return "Product became cheaper!";
    }

    @Override
    public String getWasMessage() {
        return "Was";
    }

    @Override
    public String getNowMessage() {
        return "Now";
    }

    @Override
    public String getLinkMessage() {
        return "Link";
    }

    @Override
    public String getTodayIsTheLastDayOfSubscriptionPleasePay() {
        return "Today is the last day of the subscription. To prolong your subscription, please pay for it using the /buy_subscription command or enter the promo code with the /enter_promo command.";
    }
}


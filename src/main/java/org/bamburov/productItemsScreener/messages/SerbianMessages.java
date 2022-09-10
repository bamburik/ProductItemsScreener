package org.bamburov.productItemsScreener.messages;

public class SerbianMessages implements Messages {
    @Override
    public String getProductBecameCheaperMessage() {
        return "Proizvod je postao jeftiniji!";
    }

    @Override
    public String getWasMessage() {
        return "Bio";
    }

    @Override
    public String getNowMessage() {
        return "Sada";
    }

    @Override
    public String getLinkMessage() {
        return "Link";
    }

    @Override
    public String getTodayIsTheLastDayOfSubscriptionPleasePay() {
        return "Danas je poslednji dan pretplate. Da biste produžili pretplatu, platite je pomoću komande /buy_subscription ili unesite promot kod pomoću komande /enter_promo.";
    }
}

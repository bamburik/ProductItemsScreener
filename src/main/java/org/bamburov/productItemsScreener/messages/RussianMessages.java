package org.bamburov.productItemsScreener.messages;

public class RussianMessages implements Messages {
    @Override
    public String getProductBecameCheaperMessage() {
        return "Продукт стал дешевле!";
    }

    @Override
    public String getWasMessage() {
        return "Было";
    }

    @Override
    public String getNowMessage() {
        return "Стало";
    }

    @Override
    public String getLinkMessage() {
        return "Ссылка";
    }

    @Override
    public String getTodayIsTheLastDayOfSubscriptionPleasePay() {
        return "Сегодня - последний день подписки. Для продления подписки, пожалуйста, оплатите ее, используя команду /buy_subscription или введите промокод с помощью /enter_promo команды.";
    }
}

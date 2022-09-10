package org.bamburov.productItemsScreener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.bamburov.productItemsScreener.config.Props;
import org.bamburov.productItemsScreener.messages.EnglishMessages;
import org.bamburov.productItemsScreener.messages.Messages;
import org.bamburov.productItemsScreener.messages.RussianMessages;
import org.bamburov.productItemsScreener.messages.SerbianMessages;
import org.bamburov.productItemsScreener.models.Product;
import org.bamburov.productItemsScreener.models.User;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class Main {

    private static List<User> users;
    private static WebDriver driver;
    private static MongoClient mongoClient;
    private static MongoCollection<Document> usersMongoCollection;

    public static void main(String[] args) {

        setProperties();

        mongoClient = new MongoClient(Props.getMongoHost(), Props.getMongoPort());

        usersMongoCollection = mongoClient.getDatabase("myBotDB").getCollection("users");
        users = serializeUsersJson();

        Set<String> allInterestedProductsLinks = new HashSet<>();
        for (User user : users) {
            if (user.getInterestedProducts() != null && !LocalDate.now().isAfter(LocalDate.parse(user.getLastDayOfSubscription()))) {
                for (Product product : user.getInterestedProducts()) {
                    allInterestedProductsLinks.add(product.getLink());
                }
            }
        }

        TelegramBot bot = new TelegramBot(Props.getBotToken());
        openChrome();
        for (String link : allInterestedProductsLinks) {
            double currentPrice;
            try {
                driver.get(link);
                String currentPriceStr;
                if (link.startsWith("https://www.ekupi.")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".final-price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.tehnomanija")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-price-web-contant .price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://dijaspora.shop/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product_view_row .currency_switch_RSD .regular-price .price , .product_view_row .currency_switch_RSD .special-price .price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.superalati.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-price-and-info .amount")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[,]","").split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.winwin.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-shop .special-price [itemprop='price'] , .product-shop .regular-price [itemprop='price']")).getAttribute("content");
                    currentPrice = Double.parseDouble(currentPriceStr.trim());
                }
                else if (link.startsWith("https://www.emmi.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-shop .regular-price .price , .product-shop .special-price .price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://koncarelektro.com/")) {
                    currentPriceStr = driver.findElement(By.xpath("(//*[contains(@class,'single-product-wrapper')]//*[@class='price']//ins//bdi) | (//*[contains(@class,'single-product-wrapper')]//*[@class='price']/span/span/bdi)")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.shoppster.com/sr-RS/")) {
                    currentPriceStr = driver.findElement(By.xpath("//ung-product-details//ung-price-value[not(.//*[contains(@class,'discount')])]//span[contains(@class,'price__value--normal')]")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://eurotehna.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector("div.product-info .product-price , div.product-info .product-price-new")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[,]","").split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.nitom.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector("#product-preview .product-price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://lirsshop.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".woo-summary-wrap .price bdi")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[,]","").split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.bcgroup-online.com/")) {
                    currentPriceStr = driver.findElement(By.cssSelector("#prodinfo #price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://crafter.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-essential [itemprop='price'] .price-value")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.market.metalac.com/")) {
                    currentPriceStr = driver.findElement(By.cssSelector("form .product-price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://eplaneta.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-info-main [data-price-type='finalPrice']")).getAttribute("data-price-amount");
                    currentPrice = Double.parseDouble(currentPriceStr.trim());
                }
                else if (link.startsWith("https://www.drtechno.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-info-main [data-price-type='finalPrice']")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://maxshop.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product .product__price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.tempo-tehnika.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-preview-price .JSAkcCena")).getAttribute("data-akc_cena");
                    currentPrice = Double.parseDouble(currentPriceStr.trim());
                }
                else if (link.startsWith("https://spektar.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-view__info .price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.ctshop.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector("#product-page-price .price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.tri-o.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector("[itemprop='price']")).getAttribute("content");
                    currentPrice = Double.parseDouble(currentPriceStr.trim());
                }
                else if (link.startsWith("https://www.vmelektronik.com/")) {
                    currentPriceStr = driver.findElement(By.xpath("(//*[contains(@class,'summary')]//*[@class='price']//ins//bdi) | (//*[contains(@class,'summary')]//*[@class='price']/span/bdi)")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.elbraco.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".col-prod-info .prod-price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.gstore.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-preview-info .main-price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.dudico.com/")) {
                    currentPriceStr = driver.findElement(By.xpath("//*[@class='product__price']")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.linkplus.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-preview-info .JSweb_price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://gigatron.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".main-box .ppra_price-number")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else {
                    throw new IllegalArgumentException();
                }
            }
            catch (WebDriverException e) {
                System.out.println("Хождение по " + link + " линке пошло по пизде");
                System.out.println(e.toString());
                System.out.println(e.getMessage());
                e.printStackTrace();
                continue;
            }
            catch (IllegalArgumentException e) {
                System.out.println("Хождение по " + link + " линке пошло по пизде");
                System.out.println(e.toString());
                System.out.println(e.getMessage());
                e.printStackTrace();
                continue;
            }
            for (User user : users) {
                if (user.getInterestedProducts() != null && !LocalDate.now().isAfter(LocalDate.parse(user.getLastDayOfSubscription()))) {
                    Messages messages = getMessages(user.getLang());

                    // Ask user to pay or use a promo if it's the last day of subscription
                    if(LocalDate.now().isEqual(LocalDate.parse(user.getLastDayOfSubscription()))) {
                        SendMessage request = new SendMessage(user.getChatId(), messages.getTodayIsTheLastDayOfSubscriptionPleasePay());
                        bot.execute(request);
                    }

                    for (Product product : user.getInterestedProducts()) {
                        if (product.getLink().equals(link) && currentPrice < product.getInitialPrice()) {
                            String message = messages.getProductBecameCheaperMessage() + "\n" +
                                    messages.getLinkMessage() + " - " + link + "\n" +
                                    messages.getWasMessage() + " " + product.getInitialPrice() + " din\n" +
                                    messages.getNowMessage() + " " + currentPrice + " din";
                            SendMessage request = new SendMessage(user.getChatId(), message);
                            bot.execute(request);

                            // update price in db
                            Bson filter = eq("chatId", user.getChatId());
                            Bson update = pull("interestedProducts", new Document().append("link", link).append("initialPrice", product.getInitialPrice()));
                            usersMongoCollection.updateOne(filter, update);

                            filter = eq("chatId", user.getChatId());
                            update = push("interestedProducts", new Document().append("link", link).append("initialPrice", currentPrice));
                            usersMongoCollection.updateOne(filter, update);
                        }
                    }
                }
            }
        }
        driver.quit();
        mongoClient.close();
    }

    private static List<User> serializeUsersJson() {
        List<User> result = new ArrayList<>();
        MongoCollection<Document> users = usersMongoCollection;
        MongoCursor<Document> iterator = users.find().iterator();
        while (iterator.hasNext()) {
            result.add(new Gson().fromJson(iterator.next().toJson(), new TypeToken<User>() {}.getType()));
        }
        return result;
    }

    private static void openChrome() {
        if (!Props.isChromeInContainer()) {
            System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
            driver = new ChromeDriver();
        }
        else {
            try {
                driver = new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub"), DesiredCapabilities.chrome());
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }        }
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }

    private static Messages getMessages(String lang) {
        switch (lang) {
            case "Srpski":
                return new SerbianMessages();
            case "English":
                return new EnglishMessages();
            case "Русский":
                return new RussianMessages();
            default:
                throw new IllegalArgumentException();
        }
    }

    private static void setProperties() {
        try (InputStream input = new FileInputStream(System.getProperty("propPath"))) {
            Properties prop = new Properties();
            prop.load(input);
            Props.setChromeInContainer(Boolean.parseBoolean(prop.getProperty("chromeInContainer")));
            Props.setBotName(prop.getProperty("bot.name"));
            Props.setBotToken(prop.getProperty("bot.token"));
            Props.setMongoHost(prop.getProperty("mongo.host"));
            Props.setMongoPort(Integer.parseInt(prop.getProperty("mongo.port")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

package org.bamburov.productItemsScreener.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @SerializedName("chatId")
    @Expose
    private String chatId;
    @SerializedName("interestedProducts")
    @Expose
    private List<Product> interestedProducts;
    @SerializedName("lastDayOfSubscription")
    @Expose
    private String lastDayOfSubscription;
    @SerializedName("lang")
    @Expose
    private String lang;
}


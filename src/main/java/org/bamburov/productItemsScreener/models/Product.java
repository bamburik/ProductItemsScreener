package org.bamburov.productItemsScreener.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("initialPrice")
    @Expose
    private double initialPrice;
}

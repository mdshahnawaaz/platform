package com.logistic.platform.models;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@Data
@ToString
public class ProductRequest {

    private Long amount;
    private Long quantity;
    private String id;
    private String currency;

}

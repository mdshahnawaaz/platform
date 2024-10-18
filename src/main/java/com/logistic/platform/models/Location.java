package com.logistic.platform.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Location {

    private String driverId;
    private double latitude;
    private double longitude;
    private long timestamp;

}

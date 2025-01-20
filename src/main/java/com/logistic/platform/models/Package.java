package com.logistic.platform.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name="Package")
public class Package {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  int trackingId;
    private String packageName;
    private PackageCuuentStatus currentStatus;
    private LocalDateTime lastUpdated;

}

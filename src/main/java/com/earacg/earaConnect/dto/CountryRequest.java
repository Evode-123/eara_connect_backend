package com.earacg.earaConnect.dto;

import lombok.Data;

@Data
public class CountryRequest {
    private String name;
    private String isoCode; // New field for ISO code
    private Long eacId;
}
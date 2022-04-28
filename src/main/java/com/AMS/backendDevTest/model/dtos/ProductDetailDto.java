package com.AMS.backendDevTest.model.dtos;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ProductDetailDto {
	
	private String id;
    private String name;
    private Double price;
    private Boolean availability;
}
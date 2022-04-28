package com.AMS.backendDevTest.model.dtos;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SimilarProdsResponseDto {
    
	private ProductDetailDto[] similarProducts;
   
}

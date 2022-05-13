package com.AMS.backendDevTest.controller;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.AMS.backendDevTest.model.dtos.ProductDetailDto;
import com.AMS.backendDevTest.service.SimilarProdsService;

import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class SimilarProductsControllerUnitTest {

	@InjectMocks
	private SimilarProductsController similarProdsController;
	
	@Mock
	private SimilarProdsService mockService;
	
	ProductDetailDto product1=new ProductDetailDto("1","Shirt",9.99,true);
	ProductDetailDto product2=new ProductDetailDto("2","Dress",19.99,true);
	ProductDetailDto product3=new ProductDetailDto("3","Blazer",29.99,false);
	ProductDetailDto product4=new ProductDetailDto("4","Boots",39.99,true);
	
	@Test
	void whenGetSimilarProds_ByValidId_shouldReturnAllSimilarProdsDetail_OK() {
		List<ProductDetailDto> similarProducts = new ArrayList<ProductDetailDto>();
		similarProducts.add(product2);
		similarProducts.add(product3);
		similarProducts.add(product4);
		Flux<ProductDetailDto> expectedProducts = Flux.fromIterable(similarProducts);
		
		when(mockService.getSimilarProds("1")).thenReturn(expectedProducts);
		
		ResponseEntity<Flux<ProductDetailDto>> result = similarProdsController.getSimilarProdsById("1");
		
		assertThat(result.getStatusCodeValue()).isEqualTo(200);
		assertEquals(expectedProducts, result.getBody());
	}
	
	@Test
	void whenGetSimilarProds_ByNotValidId_shouldThrowNotFoundException() {
		when(mockService.getSimilarProds("abcd")).thenThrow(HttpClientErrorException.class);
		
		ResponseEntity<Flux<ProductDetailDto>> result = similarProdsController.getSimilarProdsById("abcd");

		assertThat(result.getStatusCodeValue()).isEqualTo(404);
	}
	
	@Test
	void whenGetSimilarProds_ByValidId_WithProdsNotFound_shouldReturnAllProdsExceptMissingOnes() {
		List<ProductDetailDto> similarProducts = new ArrayList<ProductDetailDto>();
		similarProducts.add(product1);
		similarProducts.add(product2);
		Flux<ProductDetailDto> expected = Flux.fromIterable(similarProducts);
		
		when(mockService.getSimilarProds("4")).thenReturn(expected);
		
		ResponseEntity<Flux<ProductDetailDto>> result = similarProdsController.getSimilarProdsById("4");

		assertThat(result.getStatusCodeValue()).isEqualTo(200);
		assertEquals(expected, result.getBody());
	}
	
	@Test
	void whenGetSimilarProds_ByValidId_WithProdsWithError_shouldReturnAllProdsExceptProblematicOnes() {
		List<ProductDetailDto> similarProducts = new ArrayList<ProductDetailDto>();
		similarProducts.add(product1);
		similarProducts.add(product2);
		Flux<ProductDetailDto> expected = Flux.fromIterable(similarProducts);

		when(mockService.getSimilarProds("5")).thenReturn(expected);
		
		ResponseEntity<Flux<ProductDetailDto>> result = similarProdsController.getSimilarProdsById("5");

		assertThat(result.getStatusCodeValue()).isEqualTo(200);
		assertEquals(expected, result.getBody());
	}
}

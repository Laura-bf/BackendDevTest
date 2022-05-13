package com.AMS.backendDevTest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.AMS.backendDevTest.model.dtos.ProductDetailDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class SimilarProdsServiceUnitTest {
	
	@InjectMocks
	private SimilarProdsService similarProdsService;
	
	@Mock
	private ExternalProductApiCaller apiCallerMock;
	
    private ProductDetailDto product1=new ProductDetailDto("1","Shirt",9.99,true);
    private ProductDetailDto product2=new ProductDetailDto("2","Dress",19.99,true);
    private ProductDetailDto product3=new ProductDetailDto("3","Blazer",29.99,false);
    private ProductDetailDto product4=new ProductDetailDto("4","Boots",39.99,true);
	
	@Test
	public void whenGetSimilarProds_WithValidIdAndExistingProds_shouldReturnAllSimilarProdsDetail() {
		String[] similarIds = {"2", "3", "4"};
		List<ProductDetailDto> similarProducts = new ArrayList<ProductDetailDto>();
		similarProducts.add(product2);
		similarProducts.add(product3);
		similarProducts.add(product4);
		Flux<ProductDetailDto> expected = Flux.fromIterable(similarProducts);
		
		when(apiCallerMock.getSimilarIds("1")).thenReturn(Mono.just(similarIds));
		when(apiCallerMock.getProductDetail("2")).thenReturn(product2);
		when(apiCallerMock.getProductDetail("3")).thenReturn(product3);
		when(apiCallerMock.getProductDetail("4")).thenReturn(product4);
		
		assertEquals(expected.toString(), similarProdsService.getSimilarProds("1").toString());
	}
	
	@Test
	public void whenGetSimilarProds_withNotValidId_shouldThrow_NOT_FOUND_Exception() {
		when(apiCallerMock.getSimilarIds("abcd")).thenThrow(WebClientResponseException.class);
		try {
			similarProdsService.getSimilarProds("abcd");
			fail("Exception expected!");
		} catch(WebClientResponseException e) {
			assertThat(WebClientResponseException.class.equals(e.getClass()));
		}catch(Exception e) {
			fail("wrong exception thrown");
		}
	}
	
	@Test
	public void whenGetSimilarProds_ProductNotFound_shouldReturnAllProdsExceptMissingOnes() {
		String[] similarIds = {"1", "2", "5"};
		List<ProductDetailDto> similarProducts = new ArrayList<ProductDetailDto>();
		similarProducts.add(product1);
		similarProducts.add(product2);
		Flux<ProductDetailDto> expected = Flux.fromIterable(similarProducts);
		
		when(apiCallerMock.getSimilarIds("4")).thenReturn(Mono.just(similarIds));
		when(apiCallerMock.getProductDetail("1")).thenReturn(product1);
		when(apiCallerMock.getProductDetail("2")).thenReturn(product2);
		when(apiCallerMock.getProductDetail("5")).thenReturn(null);
		
		assertEquals(expected.toString(), similarProdsService.getSimilarProds("4").toString());
	}
	
	@Test
	public void whenGetSimilarProds_whenClientServerError_shouldReturnAllProdsExceptProblematicOnes() {
		String[] similarIds = {"1", "2", "6"};
		List<ProductDetailDto> similarProducts = new ArrayList<ProductDetailDto>();
		similarProducts.add(product1);
		similarProducts.add(product2);
		Flux<ProductDetailDto> expected = Flux.fromIterable(similarProducts);
		
		when(apiCallerMock.getSimilarIds("5")).thenReturn(Mono.just(similarIds));
		when(apiCallerMock.getProductDetail("1")).thenReturn(product1);
		when(apiCallerMock.getProductDetail("2")).thenReturn(product2);
		when(apiCallerMock.getProductDetail("6")).thenReturn(null);
		
		assertEquals(expected.toString(), similarProdsService.getSimilarProds("5").toString());
	}
}

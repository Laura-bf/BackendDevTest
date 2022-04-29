package com.AMS.backendDevTest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.AMS.backendDevTest.model.dtos.ProductDetailDto;

import reactor.core.publisher.Flux;

@SpringBootTest
class SimilarProdsServiceTest {

	@Autowired
	private SimilarProdsService similarProdsService;
	
	ProductDetailDto product1=new ProductDetailDto("1","Shirt",9.99,true);
	ProductDetailDto product2=new ProductDetailDto("2","Dress",19.99,true);
	ProductDetailDto product3=new ProductDetailDto("3","Blazer",29.99,false);
	ProductDetailDto product4=new ProductDetailDto("4","Boots",39.99,true);

	@Test
	public void whenGetSimilarProds_WithValidIdAndExistingProds_shouldReturnAllSimilarProdsDetail() {
		List<ProductDetailDto> similarProducts = new ArrayList<ProductDetailDto>();
		similarProducts.add(product2);
		similarProducts.add(product3);
		similarProducts.add(product4);
		
		Flux<ProductDetailDto> expected = Flux.fromIterable(similarProducts);
		
		assertEquals(expected.toString(), similarProdsService.getSimilarProds("1").toString());
	}
	
	@Test
	public void whenGetSimilarProds_withNotValidId_shouldThrow_NOT_FOUND_Exception() {
		try {
			similarProdsService.getSimilarProds("8");
			fail("Exception expected!");
		} catch(HttpClientErrorException e) {
			assertThat(HttpClientErrorException.class.equals(e.getClass()));
			assertThat(e.getStatusCode().equals(HttpStatus.NOT_FOUND));
		}catch(Exception e) {
			fail("wrong exception thrown");
		}
	}
	
	@Test
	public void whenGetSimilarProds_ProductNotFound_shouldReturnAllProdsExceptMissingOnes() {
		List<ProductDetailDto> similarProducts = new ArrayList<ProductDetailDto>();
		similarProducts.add(product1);
		similarProducts.add(product2);
		
		Flux<ProductDetailDto> expected = Flux.fromIterable(similarProducts);
		
		assertEquals(expected.toString(), similarProdsService.getSimilarProds("4").toString());
	}
	
	@Test
	public void whenGetSimilarProds_whenClientServerError_shouldReturnAllProdsExceptProblematicOnes() {
		List<ProductDetailDto> similarProducts = new ArrayList<ProductDetailDto>();
		similarProducts.add(product1);
		similarProducts.add(product2);
		
		Flux<ProductDetailDto> expected = Flux.fromIterable(similarProducts);;
		
		assertEquals(expected.toString(), similarProdsService.getSimilarProds("5").toString());
	}
}

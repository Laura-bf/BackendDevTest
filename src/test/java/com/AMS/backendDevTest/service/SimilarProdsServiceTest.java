package com.AMS.backendDevTest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.AMS.backendDevTest.model.dtos.ProductDetailDto;
import com.AMS.backendDevTest.model.dtos.SimilarProdsResponseDto;

@SpringBootTest
class SimilarProdsServiceTest {

	@Autowired
	private SimilarProdsService similarProdsService;
	
	ProductDetailDto product1=new ProductDetailDto("1","Shirt",9.99,true);
	ProductDetailDto product2=new ProductDetailDto("2","Dress",19.99,true);
	ProductDetailDto product3=new ProductDetailDto("3","Blazer",29.99,false);
	ProductDetailDto product4=new ProductDetailDto("4","Boots",39.99,true);
	SimilarProdsResponseDto expected = new SimilarProdsResponseDto();

	@Test
	public void whenGetSimilarProds_WithValidIdAndExistingProds_shouldReturnAllSimilarProdsDetail() {
		ProductDetailDto[] similarProducts = new ProductDetailDto[3];
		similarProducts[0] = product2;
		similarProducts[1] = product3;
		similarProducts[2] = product4;
		
		expected.setSimilarProducts(similarProducts);
		
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
		ProductDetailDto[] similarProducts = new ProductDetailDto[2];
		similarProducts[0] = product1;
		similarProducts[1] = product2;
		
		expected.setSimilarProducts(similarProducts);
		
		assertEquals(expected.toString(), similarProdsService.getSimilarProds("4").toString());
	}
	
	@Test
	public void whenGetSimilarProds_whenClientServerError_shouldReturnAllProdsExceptProblematicOnes() {
		ProductDetailDto[] similarProducts = new ProductDetailDto[2];
		similarProducts[0] = product1;
		similarProducts[1] = product2;
		
		expected.setSimilarProducts(similarProducts);
		
		assertEquals(expected.toString(), similarProdsService.getSimilarProds("5").toString());
	}
}

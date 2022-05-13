package com.AMS.backendDevTest.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.AMS.backendDevTest.model.dtos.ProductDetailDto;

@SpringBootTest
class ExternalProductApiCallerIntegrationTest {
	
	WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:3001").build();
	private ProductDetailDto product1=new ProductDetailDto("1","Shirt",9.99,true);
    private ProductDetailDto emptyProduct = new ProductDetailDto(null,null,null,null);


	@Test
	public void whenGetSimilarIds_WithValidId_shouldReturnAllSimilarIds() {
		String[] similarIds = {"2", "3", "4"};
		webTestClient
			.get().uri("product/1/similarids")
			.exchange()
			.expectStatus().isOk()
			.expectBody(String[].class).isEqualTo(similarIds);
	}
	
	@Test
	public void whenGetSimilarIds_WithNotValidId_shouldThrowException() {
		webTestClient
			.get().uri("product/abcd/similarids")
			.exchange()
			.expectStatus().isNotFound()
			.expectBody().equals(null);
	}
	
	@Test
	public void whenGetProductDetail_WithValidIdOfExistingProduct_shouldReturnProductDetail_OK() {
		webTestClient
		.get().uri("product/1")
		.exchange()
		.expectStatus().isOk()
		.expectBody(ProductDetailDto.class).toString().equals(product1.toString());
	}
	
	@Test
	public void whenGetProductDetail_WithValidIdOfNonExistingProduct_shouldReturnEmptyProduct_NOT_FOUND() {
		webTestClient
		.get().uri("product/5")
		.exchange()
		.expectStatus().isNotFound()
		.expectBody(ProductDetailDto.class).toString().equals(emptyProduct.toString());
	}
	
	@Test
	public void whenGetProductDetail_WithValidIdOfProblematicProduct_shouldReturnEmptyProduct_ERROR() {
		webTestClient
		.get().uri("product/6")
		.exchange()
		.expectStatus().isEqualTo(500)
		.expectBody(ProductDetailDto.class).toString().equals(emptyProduct.toString());
	}
}
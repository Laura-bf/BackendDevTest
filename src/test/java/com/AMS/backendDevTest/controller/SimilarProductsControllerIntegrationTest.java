package com.AMS.backendDevTest.controller;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.HttpClientErrorException;

import com.AMS.backendDevTest.model.dtos.ProductDetailDto;
import com.AMS.backendDevTest.service.SimilarProdsService;

import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = SimilarProductsController.class)
class SimilarProductsControllerIntegrationTest {

	@Autowired
	private WebTestClient webTestClient;
	
	@MockBean
	private SimilarProdsService mockService;
	
	ProductDetailDto product1=new ProductDetailDto("1","Shirt",9.99,true);
	ProductDetailDto product2=new ProductDetailDto("2","Dress",19.99,true);
	ProductDetailDto product3=new ProductDetailDto("3","Blazer",29.99,false);
	ProductDetailDto product4=new ProductDetailDto("4","Boots",39.99,true);

	
	@Test
	void whenGetSimilarProds_ByValidId_shouldReturnAllSimilarProdsDetail_OK() throws Exception {
		List<ProductDetailDto> similarProducts = new ArrayList<ProductDetailDto>();
		similarProducts.add(product2);
		similarProducts.add(product3);
		similarProducts.add(product4);
		
		Flux<ProductDetailDto> expected = Flux.fromIterable(similarProducts);
		
		when(mockService.getSimilarProds("1")).thenReturn(expected);
		
		webTestClient.get()
			.uri("/product/1/similar")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$[0].id").isEqualTo("2")
		    .jsonPath("$[1].name").isEqualTo("Blazer")
		    .jsonPath("$[2].price").isEqualTo(39.99);
	}
	
	@Test
	void whenGetSimilarProds_ByNotValidId_shouldThrowException() throws Exception {
		
		when(mockService.getSimilarProds("abcd")).thenThrow(HttpClientErrorException.class);
		
		webTestClient.get()
			.uri("/product/abcd/similar")
			.exchange()
			.expectStatus().isNotFound()
			.expectBody()
			.getClass().equals(HttpClientErrorException.class);
	}
	
	@Test
	void whenGetSimilarProds_ByValidId_WithProdsNotFound_shouldReturnAllProdsExceptMissingOnes() throws Exception {
		List<ProductDetailDto> similarProducts = new ArrayList<ProductDetailDto>();
		similarProducts.add(product1);
		similarProducts.add(product2);
		Flux<ProductDetailDto> expected = Flux.fromIterable(similarProducts);
		
		when(mockService.getSimilarProds("4")).thenReturn(expected);
		
		webTestClient.get()
			.uri("/product/4/similar")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$[0].id").isEqualTo("1")
			.jsonPath("$[1].name").isEqualTo("Dress");	
	}
	
	@Test
	void whenGetSimilarProds_ByValidId_WithProdsWithError_shouldReturnAllProdsExceptProblematicOnes() throws Exception {
		List<ProductDetailDto> similarProducts = new ArrayList<ProductDetailDto>();
		similarProducts.add(product1);
		similarProducts.add(product2);
		Flux<ProductDetailDto> expected = Flux.fromIterable(similarProducts);

		when(mockService.getSimilarProds("5")).thenReturn(expected);

		webTestClient.get()
			.uri("/product/5/similar")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$[0].id").isEqualTo("1")
			.jsonPath("$[1].name").isEqualTo("Dress");	
	}
}

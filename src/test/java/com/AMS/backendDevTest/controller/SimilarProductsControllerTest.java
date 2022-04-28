package com.AMS.backendDevTest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import com.AMS.backendDevTest.BackendDevTestApplication;
import com.AMS.backendDevTest.model.dtos.ProductDetailDto;
import com.AMS.backendDevTest.model.dtos.SimilarProdsResponseDto;
import com.AMS.backendDevTest.service.SimilarProdsService;

@SpringBootTest(classes = {BackendDevTestApplication.class})
@AutoConfigureMockMvc
class SimilarProductsControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private SimilarProdsService mockService;
	
	ProductDetailDto product1=new ProductDetailDto("1","Shirt",9.99,true);
	ProductDetailDto product2=new ProductDetailDto("2","Dress",19.99,true);
	ProductDetailDto product3=new ProductDetailDto("3","Blazer",29.99,false);
	ProductDetailDto product4=new ProductDetailDto("4","Boots",39.99,true);
	SimilarProdsResponseDto expected = new SimilarProdsResponseDto();

	
	@Test
	void whenGetSimilarProds_ByValidId_shouldReturnAllSimilarProdsDetail_OK() throws Exception {
		ProductDetailDto[] similarProducts = new ProductDetailDto[3];
		similarProducts[0] = product2;
		similarProducts[1] = product3;
		similarProducts[2] = product4;
		expected.setSimilarProducts(similarProducts);
		String jsonExpected = "{\"similarProducts\":[{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true},"
				+ "{\"id\":\"3\",\"name\":\"Blazer\",\"price\":29.99,\"availability\":false},"
				+ "{\"id\":\"4\",\"name\":\"Boots\",\"price\":39.99,\"availability\":true}]}";
		
		when(mockService.getSimilarProds("1")).thenReturn(expected);
		
		mockMvc.perform(get("/product/1/similar"))
		.andExpectAll(
				status().isOk(),
			    content().contentType(MediaType.APPLICATION_JSON),
			    jsonPath("$.similarProducts[0].id").value("2"),
			    jsonPath("$.similarProducts[1].name").value("Blazer"),
			    jsonPath("$.similarProducts[2].price").value(39.99),
				content().json(jsonExpected));		
	}
	
	@Test
	void whenGetSimilarProds_ByNotValidId_shouldThrowException() throws Exception {
		
		when(mockService.getSimilarProds("abcd")).thenThrow(HttpClientErrorException.class);
		
		mockMvc.perform(get("/product/abcd/similar"))
			.andExpect(status().isNotFound());
	}
	
	@Test
	void whenGetSimilarProds_ByValidId_WithProdsNotFound_shouldReturnAllProdsExceptMissingOnes() throws Exception {
		ProductDetailDto[] similarProducts = new ProductDetailDto[2];
		similarProducts[0] = product1;
		similarProducts[1] = product2;
		expected.setSimilarProducts(similarProducts);
		String jsonExpected = "{\"similarProducts\":[{\"id\":\"1\",\"name\":\"Shirt\",\"price\":9.99,\"availability\":true},{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}]}";
		
		when(mockService.getSimilarProds("4")).thenReturn(expected);
		
		mockMvc.perform(get("/product/4/similar"))
			.andExpectAll(
				status().isOk(),
			    content().contentType(MediaType.APPLICATION_JSON),
			    jsonPath("$.similarProducts[0].id").value("1"),
			    jsonPath("$.similarProducts[1].id").value("2"),
				content().json(jsonExpected));		
	}
	
	@Test
	void whenGetSimilarProds_ByValidId_WithProdsWithError_shouldReturnAllProdsExceptProblematicOnes() throws Exception {
		ProductDetailDto[] similarProducts = new ProductDetailDto[2];
		similarProducts[0] = product1;
		similarProducts[1] = product2;
		expected.setSimilarProducts(similarProducts);
		String jsonExpected = "{\"similarProducts\":[{\"id\":\"1\",\"name\":\"Shirt\",\"price\":9.99,\"availability\":true},{\"id\":\"2\",\"name\":\"Dress\",\"price\":19.99,\"availability\":true}]}";
		
		when(mockService.getSimilarProds("5")).thenReturn(expected);
		
		mockMvc.perform(get("/product/5/similar"))
			.andExpectAll(
				status().isOk(),
			    content().contentType(MediaType.APPLICATION_JSON),
			    jsonPath("$.similarProducts[0].id").value("1"),
			    jsonPath("$.similarProducts[1].id").value("2"),
				content().json(jsonExpected));		
	}
}

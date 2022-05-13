package com.AMS.backendDevTest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.AMS.backendDevTest.model.dtos.ProductDetailDto;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ExtendWith(OutputCaptureExtension.class)
class ExternalProductApiCallerUnitTest {
	
	private static MockWebServer mockWebServer;
    private static ExternalProductApiCaller productDetailService;

	private ProductDetailDto product1=new ProductDetailDto("1","Shirt",9.99,true);
    private ProductDetailDto emptyProduct = new ProductDetailDto(null,null,null,null);

    @BeforeAll
    static void setUp() {
    	mockWebServer = new MockWebServer();
    	productDetailService = new ExternalProductApiCaller(WebClient.create(mockWebServer.url("http://localhost:3001").toString()));
    }
    
    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
    
    @Test
    public void whenGetSimilarIds_WithValidId_shouldReturnAllSimilarIds() {
    	String[] similarIds = {"2","3","4"};
    	
    	mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(similarIds.toString()));
    	
    	String[] result = productDetailService.getSimilarIds("1").block();
    	
    	assertEquals("2", result[0]);
    	assertEquals("3", result[1]);
    	assertEquals("4", result[2]);
    	assertThat(result.length==3);
    }
    
	@Test
	public void whenGetProductDetail_WithValidIdOfExistingProduct_shouldReturnProductDetail() {
		mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(product1.toString()));
		
		assertEquals(product1.toString(), productDetailService.getProductDetail("1").toString());
	}
	
	@Test
	public void whenGetProductDetail_WithValidIdOfNonExistingProduct_shouldReturnEmptyProduct(CapturedOutput output) {
		mockWebServer.enqueue(new MockResponse().setResponseCode(404).setBody(emptyProduct.toString()));
	
		assertEquals(emptyProduct.toString(), productDetailService.getProductDetail("5").toString());
		assertTrue(output.getOut().contains("Product id=5 not found - it won't be shown"));
	}
	
	@Test
	public void whenGetProductDetail_WithValidIdOfProlematicProduct_shouldReturnEmptyProduct(CapturedOutput output) {
		mockWebServer.enqueue(new MockResponse().setResponseCode(500));
	
		assertEquals(null, productDetailService.getProductDetail("6"));
		assertTrue(output.getOut().contains("Product id=6 problems retrieving info - it won't be shown"));
	}

}

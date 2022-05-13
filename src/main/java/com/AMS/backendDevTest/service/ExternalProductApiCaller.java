package com.AMS.backendDevTest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import com.AMS.backendDevTest.model.dtos.ProductDetailDto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Service
public class ExternalProductApiCaller {
	
	@Autowired
	private WebClient webClient;
	
	public Mono<String[]> getSimilarIds(String id) {
		return webClient.get()
				.uri("/product/" + id + "/similarids")
				.retrieve()
				.onStatus(status -> status.value() == 404, clientResponse -> Mono.empty())
				.onStatus(status -> status.value() == 500, clientResponse -> Mono.empty())
				.bodyToMono(String[].class);
	}
	
	public ProductDetailDto getProductDetail(String id) throws HttpClientErrorException{
		ResponseEntity<ProductDetailDto> productDetail = webClient.get()
				.uri("/product/" + id)
				.retrieve()
				.onStatus(status -> status.value() == 404, clientResponse -> Mono.empty())
				.onStatus(status -> status.value() == 500, clientResponse -> Mono.empty())
				.toEntity(ProductDetailDto.class)
				.block();
		if(productDetail.getStatusCodeValue()==404)
			log.info("Product id=" + id + " not found - it won't be shown");
		if(productDetail.getStatusCodeValue()==500)
			log.info("Product id=" + id + " problems retrieving info - it won't be shown");
		
		return productDetail.getBody();
	}
}

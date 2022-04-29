package com.AMS.backendDevTest.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.AMS.backendDevTest.model.dtos.ProductDetailDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SimilarProdsService {
	
	public Flux<ProductDetailDto> getSimilarProds(String id) {
		return getSimilarProductsDetail(getSimilarIds(id));
	}
	
	private String[] getSimilarIds(String id) {
		String url = "http://localhost:3001/product/" + id + "/similarids";
		return new RestTemplate().getForObject(url, String[].class);

	}
	
	private Flux<ProductDetailDto> getSimilarProductsDetail(String[] similarIds){
		List<String> idsToSearch = Arrays.asList(similarIds);
		return Flux.fromIterable(idsToSearch.stream().map(id -> getProductDetail(id)).filter(p -> p!=null).filter(p -> p.getId()!=null).toList());
	}
	
	private ProductDetailDto getProductDetail(String id) throws HttpClientErrorException{
		WebClient webClient = WebClient.create();
		String url = "http://localhost:3001/product/" + id;
		ResponseEntity<ProductDetailDto> productDetail = webClient.get()
				.uri(url)
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

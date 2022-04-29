package com.AMS.backendDevTest.service;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import com.AMS.backendDevTest.model.dtos.ProductDetailDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SimilarProdsService {

	public Flux<ProductDetailDto> getSimilarProds(String id){
		
		String[] similarIds = getSimilarIds(id).block();
		
		Flux<ProductDetailDto> similarProducts = getSimilarProductsDetail(similarIds);
		
		return similarProducts;
	}
	
	private Mono<String[]> getSimilarIds(String id) {
		WebClient client = WebClient.create();
		String url = "http://localhost:3001/product/" + id + "/similarids";
		
		Mono<String[]> similarIds = client.get()
				.uri(url)
				.retrieve()
				.bodyToMono(String[].class);
		
		return similarIds;
	}
	
	private Flux<ProductDetailDto> getSimilarProductsDetail(String[] similarIds){
		List<String> idsToSearch = Arrays.asList(similarIds);
		Flux<ProductDetailDto> products = Flux.fromIterable(idsToSearch.stream().map(id -> getProductDetail(id)).toList());
		
		return products;
	}

	private ProductDetailDto getProductDetail(String id) throws HttpClientErrorException{
		WebClient client = WebClient.create();
		String url = "http://localhost:3001/product/" + id;
		ProductDetailDto productDetail = client.get()
				.uri(url)
				.retrieve()
				.bodyToMono(ProductDetailDto.class)
				.block();
		
		return productDetail;
	}
}

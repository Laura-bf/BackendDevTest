package com.AMS.backendDevTest.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.AMS.backendDevTest.model.dtos.ProductDetailDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SimilarProdsService {
	
	@Autowired
	private ExternalProductApiCaller externalProdApiCaller;

	public Flux<ProductDetailDto> getSimilarProds(String id) {
		return getSimilarProductsDetail(externalProdApiCaller.getSimilarIds(id));
	}
	
	private Flux<ProductDetailDto> getSimilarProductsDetail(Mono<String[]> similarIds){
		List<String> idsToSearch = Arrays.asList(similarIds.block());
		return Flux.fromIterable(idsToSearch.stream()
				.map(id -> externalProdApiCaller.getProductDetail(id))
				.filter(p -> p!=null)
				.filter(p -> p.getId()!=null)
				.toList());
	}
}

package com.AMS.backendDevTest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.AMS.backendDevTest.model.dtos.ProductDetailDto;
import com.AMS.backendDevTest.model.dtos.SimilarProdsResponseDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SimilarProdsService {
	
	public SimilarProdsResponseDto getSimilarProds(String id){
		return new SimilarProdsResponseDto(getSimilarProducts(getSimilarIds(id)));
	}

	private String[] getSimilarIds(String id) {
		String url = "http://localhost:3001/product/" + id + "/similarids";
		return new RestTemplate().getForObject(url, String[].class);
	}
	
	private ProductDetailDto[] getSimilarProducts(String[] similarIds){
		List<ProductDetailDto> products = new ArrayList<ProductDetailDto>();
		for(String id : similarIds) {
			try {
				products.add(getProductDetail(id));
			}catch (HttpClientErrorException e) {
				log.info("Product id=" + id + " not found - it won't be shown");
			}catch (HttpServerErrorException e){
				log.info("Product id=" + id + " problems retrieving info - it won't be shown");
			}
		}
		return products.toArray(new ProductDetailDto[0]);
	}

	private ProductDetailDto getProductDetail(String id) throws HttpClientErrorException{
		String url = "http://localhost:3001/product/" + id;
		ProductDetailDto productDetail = new RestTemplate().getForObject(url, ProductDetailDto.class);
		
		return productDetail;
	}
}

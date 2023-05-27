package pers.qh.dao;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pers.qh.search.Product;

public interface ProductMapper extends ElasticsearchRepository<Product,Long> {
}
package vn.edu.hcmute.bt09graphql.service;

import vn.edu.hcmute.bt09graphql.dto.request.ProductRequest;
import vn.edu.hcmute.bt09graphql.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> findAll();

    List<ProductResponse> findAllOrderByPriceAsc();

    List<ProductResponse> findByCategoryId(Long categoryId);

    ProductResponse findById(Long id);

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    boolean delete(Long id);
}

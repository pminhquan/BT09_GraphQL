package vn.edu.hcmute.bt09graphql.service;

import vn.edu.hcmute.bt09graphql.dto.request.CategoryRequest;
import vn.edu.hcmute.bt09graphql.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> findAll();

    CategoryResponse findById(Long id);

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    boolean delete(Long id);
}

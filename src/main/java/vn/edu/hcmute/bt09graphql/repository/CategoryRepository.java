package vn.edu.hcmute.bt09graphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hcmute.bt09graphql.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}

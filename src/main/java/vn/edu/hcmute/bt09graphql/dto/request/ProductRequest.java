package vn.edu.hcmute.bt09graphql.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private String title;
    private Integer quantity;
    private String description;
    private BigDecimal price;
    private Long userId;
    private Long categoryId;
}

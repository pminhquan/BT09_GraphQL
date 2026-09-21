package vn.edu.hcmute.bt09graphql.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String title;
    private Integer quantity;
    private String description;
    private BigDecimal price;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private String userFullname;
}

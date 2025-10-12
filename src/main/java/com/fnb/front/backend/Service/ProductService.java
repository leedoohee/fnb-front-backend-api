package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.Product;
import com.fnb.front.backend.controller.domain.ProductOption;
import com.fnb.front.backend.controller.domain.response.ProductOptionResponse;
import com.fnb.front.backend.controller.domain.response.ProductResponse;
import com.fnb.front.backend.repository.ProductRepository;
import com.fnb.front.backend.repository.ReviewRepository;
import com.fnb.front.backend.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ReviewRepository reviewRepository;

    public List<ProductResponse> getProducts() {
        List<ProductResponse> response  = new ArrayList<>();
        List<Product> products          = productRepository.findProducts();

        for (Product product : products) {
            response.add(ProductResponse.builder()
                    .applyMemberGradeDisType(product.getApplyMemberGradeDisType())
                    .price(product.getPrice())
                    .applyMemberGrades(product.getApplyMemberGrades())
                    .status(product.getStatus())
                    .description(product.getDescription())
                    .name(product.getName())
                    .maxPurchaseQuantity(product.getMaxQuantity())
                    .minPurchaseQuantity(product.getMinQuantity())
                    .productAttachFiles(product.getProductAttachFiles())
                    .build());
        }

        return response;
    }

    public ProductResponse getInfo(int productId) {
        List<ProductOptionResponse>  productOptionResponses      = new ArrayList<>();
        Product product                                          = this.productRepository.findProduct(productId);
        int reviewCount                                          = this.reviewRepository.findReviews(productId).size();

        for (ProductOption productOption : product.getProductOption()) {
            productOptionResponses.add(ProductOptionResponse.builder()
                    .id(productOption.getProductOptionId())
                    .optionPrice(BigDecimal.valueOf(productOption.getPrice()))
                    .productId(productOption.getProductId())
                    .name(productOption.getName())
                    .build());
        }

        return ProductResponse.builder()
                .applyMemberGradeDisType(product.getApplyMemberGradeDisType())
                .price(product.getPrice())
                .applyMemberGrades(product.getApplyMemberGrades())
                .status(product.getStatus())
                .description(product.getDescription())
                .name(product.getName())
                .maxPurchaseQuantity(product.getMaxQuantity())
                .minPurchaseQuantity(product.getMinQuantity())
                .reviewCount(reviewCount)
                .productOptions(productOptionResponses)
                .build();
    }

    public boolean validate(int productId, int quantity) {
        Product product = this.productRepository.findProduct(productId);

        if (product.isInfiniteQty()) {
            return true;
        }

        assert CommonUtil.isMinAndMaxBetween(product.getMinQuantity(), product.getMaxQuantity(), quantity) : "주문 수량이 초과 또는 미만입니다.";

        return true;
    }
}

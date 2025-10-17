package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.OrderProduct;
import com.fnb.front.backend.controller.domain.Product;
import com.fnb.front.backend.controller.domain.ProductOption;
import com.fnb.front.backend.controller.domain.response.ProductOptionResponse;
import com.fnb.front.backend.controller.domain.response.ProductResponse;
import com.fnb.front.backend.repository.ProductRepository;
import com.fnb.front.backend.repository.ReviewRepository;
import com.fnb.front.backend.util.CommonUtil;
import com.fnb.front.backend.util.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ReviewRepository reviewRepository;

    public List<ProductResponse> getProducts() {
        List<ProductResponse> response  = new ArrayList<>();
        List<Product> products          = this.productRepository.findProducts(ProductStatus.SALE.getValue());

        assert !products.isEmpty() : "상품이 존재하지 않습니다.";

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

        assert product != null : "존재하지 않는 상품입니다.";

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

        assert product != null : "존재하지 않는 상품입니다.";

        if (product.isInfiniteQty()) {
            return true;
        }

        assert CommonUtil.isMinAndMaxBetween(product.getMinQuantity(), product.getMaxQuantity(), quantity) : "주문 수량이 초과 또는 미만입니다.";

        return true;
    }

    public boolean afterApproveForProduct(List<OrderProduct> orderProducts) {
        for (OrderProduct orderProduct : orderProducts) {
            if(orderProduct.getProduct() != null && orderProduct.getProduct().isInfiniteQty()) {
                continue;
            }

            if (!CommonUtil.isMinAndMaxBetween(Objects.requireNonNull(orderProduct.getProduct()).getMinQuantity(),
                    orderProduct.getProduct().getMaxQuantity(), orderProduct.getQuantity())) {
                return false;
            }

            this.productRepository.updateMinusQuantity(Objects.requireNonNull(orderProduct.getProduct()).getProductId(),
                    orderProduct.getQuantity());
        }

        return true;
    }

    public void afterCancelForProduct(List<OrderProduct> orderProducts) {
        for (OrderProduct orderProduct : orderProducts) {
            this.productRepository.updatePlusQuantity(orderProduct.getProduct().getProductId(), orderProduct.getQuantity());
        }
    }
}

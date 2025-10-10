package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.OrderProduct;
import com.fnb.front.backend.controller.domain.Product;
import com.fnb.front.backend.controller.domain.ProductOption;
import com.fnb.front.backend.controller.domain.event.OrderResultEvent;
import com.fnb.front.backend.controller.domain.response.AdditionalOptionResponse;
import com.fnb.front.backend.controller.domain.response.ProductOptionResponse;
import com.fnb.front.backend.controller.domain.response.ProductResponse;
import com.fnb.front.backend.repository.ProductRepository;
import com.fnb.front.backend.repository.ReviewRepository;
import com.fnb.front.backend.util.CommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final ReviewRepository reviewRepository;

    public ProductService(ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleQuantityToOrder(OrderResultEvent event) {
        List<OrderProduct> orderProducts = event.getOrderProducts();
        List<Integer> productIdList      = orderProducts.stream().map(OrderProduct::getProductId).toList();
        List<Product> products           = this.productRepository.findProducts(productIdList);

        for (OrderProduct orderProduct : orderProducts) {
            Product product = products.stream()
                                .filter(element -> element.getId() == orderProduct.getProductId()).findFirst().orElse(null);

            if(product != null && product.isInfiniteQty()) {
                continue;
            }

            if (product != null && CommonUtil.isMinAndMaxBetween(product.getMinQuantity(), product.getMaxQuantity(), orderProduct.getQuantity())) {
                this.productRepository.updateQuantity(product.getId(), orderProduct.getQuantity());
            } else {
                throw new RuntimeException("재고 부족");
            }
        }
    }

    public List<ProductResponse> getProducts() {
        List<ProductResponse> response = new ArrayList<>();
        List<Product> products = productRepository.findProducts();

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
                    .build());
        }

        return response;
    }

    public ProductResponse getInfo(int productId) {
        List<ProductOptionResponse>  productOptionResponses      = new ArrayList<>();
        List<AdditionalOptionResponse> additionalOptionResponses = new ArrayList<>();
        Product product                                          = this.productRepository.findProduct(productId);
        int reviewCount                                          = this.reviewRepository.findReviews(productId).size();

        for (ProductOption productOption : product.getProductOption()) {
            productOptionResponses.add(ProductOptionResponse.builder()
                    .id(productOption.getId())
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
                .additionalOptions(additionalOptionResponses)
                .build();
    }

    public boolean validate(int productId, int quantity) {
        boolean result  = true;
        Product product = this.productRepository.findProduct(productId);

        if (!product.isInfiniteQty()) {
            result = false;
        }

        if (!CommonUtil.isMinAndMaxBetween(product.getMinQuantity(), product.getMaxQuantity(), quantity)) {
            result = false;
        }

        return result;
    }
}

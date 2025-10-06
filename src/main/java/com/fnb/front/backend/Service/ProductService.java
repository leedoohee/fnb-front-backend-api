package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.OrderAdditionalOption;
import com.fnb.front.backend.controller.domain.OrderProduct;
import com.fnb.front.backend.controller.domain.Product;
import com.fnb.front.backend.controller.domain.ProductOption;
import com.fnb.front.backend.controller.domain.event.OrderResultEvent;
import com.fnb.front.backend.controller.domain.response.AdditionalOptionResponse;
import com.fnb.front.backend.controller.domain.response.ProductOptionResponse;
import com.fnb.front.backend.controller.domain.response.ProductResponse;
import com.fnb.front.backend.repository.ProductRepository;
import com.fnb.front.backend.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    @TransactionalEventListener()
    public void handleQuantityToOrder(OrderResultEvent event) {
        List<OrderProduct> orderProducts = event.getOrderProducts();

        for (OrderProduct orderProduct : orderProducts) {
            //재고 차감
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
//
    public ProductResponse getInfo(int productId) {
        Product product                                          = this.productRepository.findProduct(productId);
        List<ProductOption> options                              = this.productRepository.findOptions(productId);
        int reviewCount                                          = this.reviewRepository.findReviews(productId).size();

        List<ProductOptionResponse>  productOptionResponses      = new ArrayList<>();
        List<AdditionalOptionResponse> additionalOptionResponses = new ArrayList<>();

        for (ProductOption productOption : options) {
            productOptionResponses.add(ProductOptionResponse.builder()
                    .id(productOption.getId())
                    .optionPrice(BigDecimal.valueOf(productOption.getPrice()))
                    .productId(productOption.getProductId())
                    .name(productOption.getName())
                    .build());
        }


        ProductResponse.builder()
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

        return ProductResponse.builder().build();
    }
}

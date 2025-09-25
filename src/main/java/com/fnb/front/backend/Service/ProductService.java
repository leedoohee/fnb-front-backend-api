package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.AdditionalOption;
import com.fnb.front.backend.controller.domain.OrderProduct;
import com.fnb.front.backend.controller.domain.Product;
import com.fnb.front.backend.controller.domain.ProductOption;
import com.fnb.front.backend.controller.domain.event.OrderResultEvent;
import com.fnb.front.backend.controller.domain.response.AdditionalOptionResponse;
import com.fnb.front.backend.controller.domain.response.ProductOptionResponse;
import com.fnb.front.backend.controller.domain.response.ProductResponse;
import com.fnb.front.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @TransactionalEventListener()
    public void handleQuantityToOrder(OrderResultEvent event) {
        List<OrderProduct> orderProducts = event.getOrderProducts();

        for (OrderProduct orderProduct : orderProducts) {
            //재고 차감
        }
    }

    public List<ProductResponse> getProducts(int merchantId) {
        List<ProductResponse> response = new ArrayList<>();
        List<Product> products = productRepository.findByMerchantId(merchantId);

        for (Product product : products) {
            response.add(ProductResponse.builder()
                    .applyMemberGradeDisType(product.getApplyMemberGradeDisType())
                    .price(product.getPrice())
                    .applyMemberGrades(product.getApplyMemberGrades())
                    .status(product.getStatus())
                    .description(product.getDescription())
                    .name(product.getName())
                    .merchantId(product.getMerchantId())
                    .maxPurchaseQuantity(product.getMaxPurchaseQuantity())
                    .minPurchaseQuantity(product.getMinPurchaseQuantity())
                    .build());
        }

        return response;
    }

    public ProductResponse getInfo(int productId) {
        Product product                             = this.productRepository.find(productId);
        List<ProductOption> options                 = this.productRepository.findOptionsById(productId);
        List<AdditionalOption> additionalOptions    = this.productRepository.findAdditionalOptsById(productId);
        List<ProductOptionResponse>  productOptionResponses      = new ArrayList<>();
        List<AdditionalOptionResponse> additionalOptionResponses = new ArrayList<>();
        //리뷰추가

        for (ProductOption productOption : options) {
            productOptionResponses.add(ProductOptionResponse.builder()
                    .id(productOption.getId())
                    .optionPrice(productOption.getOptionPrice())
                    .productId(productOption.getProductId())
                    .name(productOption.getName())
                    .description(productOption.getDescription())
                    .build());
        }

        for (AdditionalOption additionalOption : additionalOptions) {
            additionalOptionResponses.add(AdditionalOptionResponse.builder()
                    .description(additionalOption.getDescription())
                    .productId(additionalOption.getProductId())
                    .name(additionalOption.getName())
                    .price(additionalOption.getPrice())
                    .build());
        }

        ProductResponse.builder()
                .applyMemberGradeDisType(product.getApplyMemberGradeDisType())
                .price(product.getPrice())
                .applyMemberGrades(product.getApplyMemberGrades())
                .status(product.getStatus())
                .description(product.getDescription())
                .name(product.getName())
                .merchantId(product.getMerchantId())
                .maxPurchaseQuantity(product.getMaxPurchaseQuantity())
                .minPurchaseQuantity(product.getMinPurchaseQuantity())
                .productOptions(productOptionResponses)
                .additionalOptions(additionalOptionResponses)
                .build();

        return ProductResponse.builder().build();
    }
}

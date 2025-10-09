package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.request.order.CartInfoResponse;
import com.fnb.front.backend.controller.domain.request.order.CartItemRequest;
import com.fnb.front.backend.controller.domain.request.order.CartRequest;
import com.fnb.front.backend.controller.domain.request.order.OptionInfoResponse;
import com.fnb.front.backend.repository.CartRepository;
import com.fnb.front.backend.repository.MemberRepository;
import com.fnb.front.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository, MemberRepository memberRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public boolean create(CartRequest cartRequest) {
        Cart cart = this.cartRepository.findCart(cartRequest.getMemberId());

        if (cart != null) {
            this.cartRepository.deleteCart(cart.getId());
            this.cartRepository.deleteCartItem(cart.getId());
        }

        int cartId = this.cartRepository.insertCart(Cart.builder()
                            .basicOptionId(cartRequest.getBasicOptionId())
                            .memberId(cartRequest.getMemberId())
                            .productId(cartRequest.getProductId())
                            .createdAt(LocalDateTime.now())
                            .build());

        if (cartId > 0) {
            for (CartItemRequest cartItemRequest : cartRequest.getCartItemRequests()) {
                this.cartRepository.insertCartItem(CartItem.builder()
                            .cartId(cartId)
                            .additionalOptionId(cartItemRequest.getAdditionalOptionId())
                            .createdAt(LocalDateTime.now())
                            .build());
            }
        }

        return true;
    }

    @Transactional(readOnly = true)
    public CartInfoResponse getInfo(String memberId) {
        Member member                           = this.memberRepository.findMember(memberId);
        Cart cart                               = this.cartRepository.findCart(memberId);
        List<CartItem> cartItems                = this.cartRepository.findCartItems(cart.getId());
        List<Integer> optionIdList              = cartItems.stream().map(CartItem::getAdditionalOptionId).toList();
        Product product                         = this.productRepository.findProduct(cart.getProductId());
        List<ProductOption> productOption       = this.productRepository.findOptions(cart.getProductId());
        List<ProductOption> additionalOptions   = this.productRepository.findOptions(optionIdList, cart.getProductId());
        List<OptionInfoResponse> optionInfoResponses = new ArrayList<>();

        ProductOption basicOption = productOption.stream()
                        .filter(option -> option.getOptionId() == cart.getBasicOptionId()).findFirst().orElse(null);

        optionInfoResponses.add(OptionInfoResponse.builder()
                        .optionGroupId(basicOption != null ? basicOption.getOptionGroupId() : null)
                        .optionType(basicOption != null ? basicOption.getOptionType() : null)
                        .optionId(basicOption != null ? basicOption.getOptionId() : 0)
                        .optionName(basicOption != null ? basicOption.getName() : null)
                        .price(basicOption != null ? basicOption.getPrice() : 0)
                        .build());

        for (ProductOption additionalOption : additionalOptions) {
            optionInfoResponses.add(OptionInfoResponse.builder()
                            .optionGroupId(additionalOption.getOptionGroupId())
                            .optionType(additionalOption.getOptionType())
                            .optionId(additionalOption.getOptionId())
                            .optionName(additionalOption.getName())
                            .build());
        }

        return CartInfoResponse.builder()
                .memberId(memberId)
                .memberName(member.getName())
                .address(member.getAddress())
                .productId(cart.getProductId())
                .productName(product.getName())
                .description(product.getDescription())
                .minQuantity(product.getMinQuantity())
                .maxQuantity(product.getMaxQuantity())
                .cartId(cart.getId())
                .options(optionInfoResponses)
                .build();
    }
}

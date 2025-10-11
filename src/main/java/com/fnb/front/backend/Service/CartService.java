package com.fnb.front.backend.Service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.response.CartInfoResponse;
import com.fnb.front.backend.controller.domain.request.CartItemRequest;
import com.fnb.front.backend.controller.domain.request.CartRequest;
import com.fnb.front.backend.controller.domain.response.OptionInfoResponse;
import com.fnb.front.backend.repository.CartRepository;
import com.fnb.front.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;

    private final PaymentRepository paymentRepository;

    public CartService(CartRepository cartRepository, PaymentRepository paymentRepository) {
        this.cartRepository = cartRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public boolean create(CartRequest cartRequest) {
        Cart cart = this.cartRepository.findCart(cartRequest.getMemberId());

        if (cart != null) {
            this.cartRepository.deleteCart(cart.getId());
            this.cartRepository.deleteCartItem(cart.getId());
        }

        int cartId = this.cartRepository.insertCart(Cart.builder()
                            .memberId(cartRequest.getMemberId())
                            .productId(cartRequest.getProductId())
                            .createdAt(LocalDateTime.now())
                            .build());

        if (cartId > 0) {
            for (CartItemRequest cartItemRequest : cartRequest.getCartItemRequests()) {
                this.cartRepository.insertCartItem(CartItem.builder()
                            .cartId(cartId)
                            .optionId(cartItemRequest.getOptionId())
                            .optionType(cartItemRequest.getOptionType())
                            .optionGroupId(cartItemRequest.getOptionGroupId())
                            .createdAt(LocalDateTime.now())
                            .build());
            }
        }

        return true;
    }

    public CartInfoResponse getInfo(String memberId) {
        Cart cart                       = this.cartRepository.findCart(memberId);
        List<PaymentType> paymentTypes  = this.paymentRepository.findPaymentType();

        List<OptionInfoResponse> optionInfoResponses = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
            optionInfoResponses.add(OptionInfoResponse.builder()
                    .optionGroupId(cartItem.getOptionGroupId())
                    .price(cartItem.getProductOption().getPrice())
                    .optionName(cartItem.getProductOption().getName())
                    .optionId(cartItem.getOptionId())
                    .build());
        }

        return CartInfoResponse.builder()
                .minQuantity(cart.getProduct().getMinQuantity())
                .maxQuantity(cart.getProduct().getMaxQuantity())
                .productId(cart.getProduct().getId())
                .description(cart.getProduct().getDescription())
                .productName(cart.getProduct().getName())
                .address(cart.getMember().getAddress())
                .memberId(cart.getMember().getMemberId())
                .cartId(cart.getId())
                .options(optionInfoResponses)
                .paymentTypes(paymentTypes)
                .build();
    }
}

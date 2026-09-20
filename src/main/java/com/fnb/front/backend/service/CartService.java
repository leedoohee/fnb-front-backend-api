package com.fnb.front.backend.service;

import com.fnb.front.backend.controller.domain.*;
import com.fnb.front.backend.controller.domain.request.CartUpdateRequest;
import com.fnb.front.backend.controller.domain.response.CartInfoResponse;
import com.fnb.front.backend.controller.domain.request.CartItemRequest;
import com.fnb.front.backend.controller.domain.request.CartRequest;
import com.fnb.front.backend.controller.domain.response.OptionInfoResponse;
import com.fnb.front.backend.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    @Transactional
    public boolean create(CartRequest cartRequest, String memberId) {
        int cartId = this.cartRepository.insertCart(Cart.builder()
                            .memberId(memberId)
                            .productId(cartRequest.getProductId())
                            .createdAt(LocalDateTime.now())
                            .build());

        for (CartItemRequest cartItemRequest : cartRequest.getCartItemRequests()) {
            this.cartRepository.insertCartItem(CartItem.builder()
                        .cartId(cartId)
                        .optionId(cartItemRequest.getOptionId())
                        .optionType(cartItemRequest.getOptionType())
                        .optionGroupId(cartItemRequest.getOptionGroupId())
                        .createdAt(LocalDateTime.now())
                        .build());
        }

        return true;
    }

    @Transactional
    public boolean delete(int cartId, String memberId) {
        Cart cart = this.cartRepository.findCart(cartId, memberId);

        if (cart == null) {
            throw new IllegalArgumentException("장바구니가 존재하지 않습니다.");
        }

        this.cartRepository.deleteCartItem(cartId);
        this.cartRepository.deleteCart(cartId, memberId);

        return true;
    }

    @Transactional
    public boolean update(CartUpdateRequest cartUpdateRequest, String memberId) {
        Cart cart = this.cartRepository.findCart(cartUpdateRequest.getCartId(), memberId);

        if (cart == null) {
            throw new IllegalArgumentException("장바구니가 존재하지 않습니다.");
        }

        this.cartRepository.updateCart(cartUpdateRequest.getCartId(), cartUpdateRequest.getQuantity());

        return true;
    }

    public List<CartInfoResponse> getInfo(String memberId) {
        List<Cart> carts                             = this.cartRepository.findCarts(memberId);
        List<OptionInfoResponse> optionInfoResponses = new ArrayList<>();
        List<CartInfoResponse> cartInfoResponses     = new ArrayList<>();

        for (Cart cart : carts) {
            for (CartItem cartItem : cart.getCartItems()) {
                optionInfoResponses.add(OptionInfoResponse.builder()
                        .optionGroupId(cartItem.getOptionGroupId())
                        .price(cartItem.getProductOption().getPrice())
                        .optionName(cartItem.getProductOption().getName())
                        .optionId(cartItem.getOptionId())
                        .build());
            }

            cartInfoResponses.add(CartInfoResponse.builder()
                    .minQuantity(cart.getProduct().getMinQuantity())
                    .maxQuantity(cart.getProduct().getMaxQuantity())
                    .productId(cart.getProduct().getProductId())
                    .description(cart.getProduct().getDescription())
                    .productName(cart.getProduct().getName())
                    .address(cart.getMember().getAddress())
                    .memberId(cart.getMember().getMemberId())
                    .cartId(cart.getCartId())
                    .options(optionInfoResponses)
                    .build());
        }

        return cartInfoResponses;
    }
}

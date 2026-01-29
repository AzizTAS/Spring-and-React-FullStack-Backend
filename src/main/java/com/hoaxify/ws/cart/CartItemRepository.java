package com.hoaxify.ws.cart;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartIdAndProductId(long cartId, long productId);

    void deleteByCartId(long cartId);

}

package com.bridgelabz.bookstore.service;

import com.bridgelabz.bookstore.dto.CartDTO;
import com.bridgelabz.bookstore.exception.CustomException;
import com.bridgelabz.bookstore.model.Cart;
import com.bridgelabz.bookstore.model.UserData;
import com.bridgelabz.bookstore.repository.BookRepository;
import com.bridgelabz.bookstore.repository.CartRepository;
import com.bridgelabz.bookstore.repository.UserRepository;
import com.bridgelabz.bookstore.util.TokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    TokenUtility tokenUtility;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookRepository bookRepository;

    public Cart addToCart(String token, CartDTO cartDTO) {
        Cart cart;
        List<Integer> bookIdList = cartDTO.getBookIdList();
        List<Integer> quantityList = cartDTO.getQuantity();
        long userId = tokenUtility.decodeToken(token);
        UserData userData = userRepository.findById(userId).orElseThrow(() -> new CustomException("User id " + userId + " not found"));

        for (int i = 0; i < bookIdList.size(); i++) {
            if (quantityList.get(i) > bookRepository.findBookById(bookIdList.get(i)).getQuantity())
                throw new CustomException("Book quantity exceeded for book id "+ bookIdList.get(i));
        }
        if (cartRepository.existsById(userId)) {
            cart=cartRepository.findById(userId).orElseThrow(() -> new CustomException("User id " + userId + " not found"));
            cart.setUserData(userData);
            cart.setBookIdList(cartDTO.getBookIdList());
            cart.setQuantity(cartDTO.getQuantity());
            return cartRepository.save(cart);
        } else {
            cart = new Cart(userId, userData, cartDTO.getBookIdList(), cartDTO.getQuantity());
            try {

                return cartRepository.save(cart);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
    public List<Cart> getAllCarts(String token) {
        long userId = tokenUtility.decodeToken(token);
        UserData user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User id " + userId + " not found"));
        if (!user.isAdmin()) throw new CustomException("User is not Admin");
        if (cartRepository.findAll().isEmpty()) throw new CustomException("No cart added yet");
        return cartRepository.findAll();
    }

    public Cart getCartById(String token, long id) {
        long userId = tokenUtility.decodeToken(token);
        if (userId != id) throw new CustomException("Token is not matching with user id " + id);
        UserData user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User id " + userId + " not found"));
        if (!user.isAdmin()) throw new CustomException("User is not Admin");
        return cartRepository.findById(id).orElseThrow(() -> new CustomException("Cart og id " + userId + " not found"));
    }

    public String deleteCartById(String token, long id) {
        long userId = tokenUtility.decodeToken(token);
        if (userId != id) throw new CustomException("Token is not matching with user id " + id);
        if (cartRepository.existsById(id)) {
            cartRepository.deleteById(id);
        } else throw new CustomException("cart not found of id " + id);
        return "Cart deleted of id " + id;
    }

    public Cart UpdateCart(String token, CartDTO cartDTO, long cartId) {
        long userId = tokenUtility.decodeToken(token);
        if (userId == cartId) {
            return addToCart(token, cartDTO);
        } else throw new CustomException("cartId " + cartId + " not matching with token");
    }
}

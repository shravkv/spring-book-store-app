package com.bridgelabz.bookstore.service;

import com.bridgelabz.bookstore.dto.OrderDTO;
import com.bridgelabz.bookstore.email.EmailService;
import com.bridgelabz.bookstore.exception.CustomException;
import com.bridgelabz.bookstore.model.Book;
import com.bridgelabz.bookstore.model.Order;
import com.bridgelabz.bookstore.model.UserData;
import com.bridgelabz.bookstore.repository.OrderRepository;
import com.bridgelabz.bookstore.util.TokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    TokenUtility tokenUtility;
    @Autowired
    UserService userService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    BookService bookService;
    @Autowired
    EmailService emailService;

    public Order placeOrder(String token, OrderDTO orderDTO) {
        long userId = tokenUtility.decodeToken(token);
        UserData user = userService.getUserById(userId).orElseThrow(() -> new CustomException("User id " + userId + " Not found!"));
        Book book = bookService.getBookById(token, orderDTO.getBookId());
        double price = book.getPrice() * orderDTO.getQuantity();
        LocalDate purchaseDate = LocalDate.now();
        Order order = new Order(user, user.getAddress(), book, orderDTO.getQuantity(), price, purchaseDate);
        Order newOrder = orderRepository.save(order);
        bookService.updateQuantity(token,book.getBookId(),book.getQuantity()-order.getQuantity());
        return newOrder;
    }

    public List<Order> getAllOrders(String token) {
        long userId = tokenUtility.decodeToken(token);
        UserData user = userService.getUserById(userId).orElseThrow(() -> new CustomException("User id " + userId + " Not found!"));
        if (!user.isAdmin()) throw new CustomException("User is not Admin");
        if (orderRepository.findAll().isEmpty()) throw new CustomException("User table is empty");
        else return orderRepository.findAll();
    }

    public Order getOrderById(String token, long orderId) {
        long userId = tokenUtility.decodeToken(token);
        return orderRepository.findById(orderId).orElseThrow(() -> new CustomException("order id " + orderId + " not found"));
    }

    public String deleteOrderById(String token, long orderId) {
        long userId = tokenUtility.decodeToken(token);
        UserData user = userService.getUserById(userId).orElseThrow(() -> new CustomException("User id " + userId + " Not found!"));
        orderRepository.deleteById(orderId);
        return "deleted order of id "+orderId +" by "+ user.getFirstName();
    }
    public Order updateOrderById(String token, long orderId, OrderDTO orderDTO) {
        long userId = tokenUtility.decodeToken(token);
        UserData user = userService.getUserById(userId).orElseThrow(() -> new CustomException("User of userId " + userId + " Not found!"));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new CustomException("Order od userId " + orderId + " Not found!"));
        Book book = bookService.getBookById(token, orderDTO.getBookId());
        order.setBook(book);
        double price = book.getPrice() * orderDTO.getQuantity();
        order.setPrice(price);
        LocalDate purchaseDate = LocalDate.now();
        order.setPurchaseDate(purchaseDate);
        order.setUser(user);
        order.setAddress(user.getAddress());
        order.setQuantity(orderDTO.getQuantity());
        Order updatedOrder = orderRepository.save(order);
        return updatedOrder;
    }


}

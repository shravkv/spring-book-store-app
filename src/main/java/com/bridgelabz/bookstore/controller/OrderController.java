package com.bridgelabz.bookstore.controller;

import com.bridgelabz.bookstore.dto.OrderDTO;
import com.bridgelabz.bookstore.service.OrderService;
import com.bridgelabz.bookstore.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookstore/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/placeorder")
    public ResponseEntity<ResponseDTO> placeOrder(@RequestHeader(name = "Authorization") String token, @RequestBody OrderDTO orderDTO){
        ResponseDTO responseDTO = new ResponseDTO("Order Placed Successfully", orderService.placeOrder(token,orderDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
    @GetMapping("/getall")
    public ResponseEntity<ResponseDTO> getAllOrders(@RequestHeader(name = "Authorization") String token){
        ResponseDTO responseDTO = new ResponseDTO("GET Call Success", orderService.getAllOrders(token).toString());
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    @GetMapping("/get/{orderId}")
    public ResponseEntity<ResponseDTO> getOrderById(@RequestHeader(name = "Authorization") String token,@PathVariable long orderId){
        ResponseDTO responseDTO = new ResponseDTO("GET Call Success", orderService.getOrderById(token,orderId));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<ResponseDTO> deleteOrderById(@RequestHeader(name = "Authorization") String token,@PathVariable long orderId){
        ResponseDTO responseDTO = new ResponseDTO("GET Call Success", orderService.deleteOrderById(token,orderId));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/update/{orderId}")
    public ResponseEntity<ResponseDTO> updateOrderById(@RequestHeader(name = "Authorization") String token,@PathVariable long orderId, @RequestBody OrderDTO orderDTO){
        ResponseDTO responseDTO = new ResponseDTO("GET Call Success", orderService.updateOrderById(token,orderId,orderDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}

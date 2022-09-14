package com.bridgelabz.bookstore.service;

import com.bridgelabz.bookstore.dto.BookDTO;
import com.bridgelabz.bookstore.email.EmailService;
import com.bridgelabz.bookstore.exception.CustomException;
import com.bridgelabz.bookstore.model.Book;
import com.bridgelabz.bookstore.model.UserData;
import com.bridgelabz.bookstore.repository.BookRepository;
import com.bridgelabz.bookstore.repository.UserRepository;
import com.bridgelabz.bookstore.util.TokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class BookService {

    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenUtility tokenUtility;
    @Autowired
    EmailService emailService;

    public Book insertBook(String token, BookDTO bookDTO) {
        long userId = tokenUtility.decodeToken(token);
        UserData user = userRepository.findById(userId).orElseThrow(() -> new CustomException("tokens do not match the user"));
        if (user.isAdmin()) {
            Book book =  bookRepository.save(new Book(bookDTO));
            emailService.sendEmail(user.getEmail(), "new book added", "new book if id "+ book.getBookId()+" is added to book Store by "+ user.getFirstName()+" "+user.getLastName() +". \nBook Details:\n"+book);
            return book;
        } else throw new CustomException("User is not an Admin");
    }

    public List<Book> getAllBooks() {
        if (!bookRepository.findAll().isEmpty()) {
            return bookRepository.findAll();
        } else throw new CustomException("Books Table is Empty!");
    }

    public Book getBookById(String token, Long bookId) {
        long userId = tokenUtility.decodeToken(token);
        userRepository.findById(userId).orElseThrow(() -> new CustomException("tokens do not match the user"));
        return bookRepository.findById(bookId).orElseThrow(() -> new CustomException("Books id " + bookId + " not found!"));
    }

    public String deleteBookById(String token, Long bookId) {
        long userId = tokenUtility.decodeToken(token);
        UserData user = userRepository.findById(userId).orElseThrow(() -> new CustomException("tokens do not match the user"));
        bookRepository.findById(bookId).orElseThrow(() -> new CustomException("Books id " + bookId + " not found!"));
        if (user.isAdmin()) {
            bookRepository.deleteById(bookId);
            return "Book id: " + bookId;
        } else throw new CustomException("User is not Admin");
    }

    public Book updateBookById(String token, Long bookId, BookDTO bookDTO) {
        long userId = tokenUtility.decodeToken(token);
        userRepository.findById(userId).orElseThrow(() -> new CustomException("tokens do not match the user"));
        bookRepository.findById(bookId).orElseThrow(() -> new CustomException("Books id " + bookId + " not found!"));
        Book book = new Book(bookDTO);
        book.setBookId(bookId);
        return bookRepository.save(book);
    }

    public List<Book> getBookByName(String token, String name) {
        long userId = tokenUtility.decodeToken(token);
        userRepository.findById(userId).orElseThrow(() -> new CustomException("tokens do not match the user"));
        if (!bookRepository.findBookByName(name).isEmpty()) {
            return bookRepository.findBookByName(name);
        } else throw new CustomException("Books name " + name + " not found!");
    }

    public Book updateQuantity(String token, long bookId, long quantity) {
        long userId = tokenUtility.decodeToken(token);
        userRepository.findById(userId).orElseThrow(() -> new CustomException("tokens do not match the user"));
        bookRepository.findById(bookId).orElseThrow(() -> new CustomException("Books id " + bookId + " not found!"));
        Book book = bookRepository.findBookById(bookId);
        book.setQuantity(quantity);
        return bookRepository.save(book);
    }
}

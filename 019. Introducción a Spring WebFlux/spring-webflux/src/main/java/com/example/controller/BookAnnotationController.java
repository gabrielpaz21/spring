package com.example.controller;

import com.example.model.Book;
import com.example.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class BookAnnotationController {

    private final BookService service;

    public BookAnnotationController(BookService service) {
        this.service = service;
    }

    @GetMapping("/books/{id}")
    public Mono<Book> findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/books")
    public Flux<Book> findAll() {
        return service.findAll();
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Book> create(@RequestBody Mono<Book> book) {
        return book.flatMap(service::create);
    }

    @PutMapping("/books")
    public Mono<Book> update(@RequestBody Mono<Book> book) {
        return book.flatMap(service::create);
    }

    @DeleteMapping("/books/{id}")
    public Mono<Void> deleteById(@PathVariable Long id) {
        return service.deleteById(id);
    }

    @DeleteMapping("/books")
    public Mono<Void> deleteAll() {
        return service.deleteAll();
    }

}

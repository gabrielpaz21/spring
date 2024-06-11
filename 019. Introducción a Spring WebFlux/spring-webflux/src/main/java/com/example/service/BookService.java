package com.example.service;

import com.example.model.Book;
import com.example.repository.BookRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public Mono<Book> findById(Long id) {
        return this.repository.findById(id);
    }

    public Flux<Book> findAll() {
        return this.repository.findAll();
    }

    public Mono<Book> create(Book book) {
        if (book.getId() != null)
            return Mono.error(new IllegalArgumentException("Id must not be null"));

        return this.repository.save(book);
    }

    public Mono<Book> update(Book book) {
        if (book.getId() == null)
            return Mono.error(new IllegalArgumentException("id must be null"));

        return this.repository.existById(book.getId()).flatMap(exist -> exist ?
                this.repository.save(book) :
                Mono.error(new IllegalArgumentException("Book must exist")));
    }

    public Mono<Void> deleteById(Long id) {
        return this.repository.deleteById(id);
    }

    public Mono<Void> deleteAll() {
        return this.repository.deleteAll();
    }

}

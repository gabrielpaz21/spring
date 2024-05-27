package com.example.dao;

import com.example.model.Book;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Repository
public class BookDAOImpl implements BookDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Book> findAllWithCategories() {

        var graph = entityManager.createEntityGraph("book-categories");
        return entityManager.createQuery("SELECT b FROM Book b", Book.class)
                .setHint("javax.persistence.fetchgraph", graph)
                .getResultList();
    }

    @Override
    public List<Book> findAllEager() {
        var graph = entityManager.createEntityGraph(Book.class);
        graph.addAttributeNodes("categories");
        graph.addSubgraph("ratings").addAttributeNodes("user");
        return entityManager.createQuery("SELECT b FROM Book b", Book.class)
                .setHint("javax.persistence.fetchgraph", graph)
                .getResultList();

    }
}

package com.example.example1;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository
public class ProductDAOImpl implements ProductDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Product> findAllWithCriteria() {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        var criteria = criteriaBuilder.createQuery(Product.class);

        var query = criteria.select(criteria.from(Product.class));

        return entityManager.createQuery(query).getResultList();

    }

    @Override
    public ProductStats fetchStats() {

        Query query1 = entityManager.createQuery("select count(p.id) from Product p");
        Long products = (Long) query1.getSingleResult();

        Query query2 = entityManager.createQuery("select count(o.id) from Offer o");
        Long offers = (Long) query2.getSingleResult();

        Query query3 = entityManager.createQuery("select count(c.id) from Customer c");
        Long customers = (Long) query3.getSingleResult();

        return new ProductStats(products, offers, customers);
    }
}

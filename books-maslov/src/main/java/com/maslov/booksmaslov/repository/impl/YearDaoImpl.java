package com.maslov.booksmaslov.repository.impl;

import com.maslov.booksmaslov.domain.YearOfPublish;
import com.maslov.booksmaslov.repository.YearRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.maslov.booksmaslov.sql.SQLConstants.GET_ALL_YEARS;
import static com.maslov.booksmaslov.sql.SQLConstants.GET_YEAR_BY_DATE;

@Component
@Slf4j
public class YearDaoImpl implements YearRepo {
    @PersistenceContext
    private final EntityManager em;


    public YearDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<YearOfPublish> getAllYears() {
        var query = em.createQuery(GET_ALL_YEARS, YearOfPublish.class);
        return query.getResultList();
    }

    @Override
    public Optional<YearOfPublish> getYearByDate(String year) {
        YearOfPublish yearOfPublish = null;
        var query = em.createQuery(GET_YEAR_BY_DATE, YearOfPublish.class);
        query.setParameter("year", year);
        try {
            yearOfPublish = query.getSingleResult();
        } catch (NoResultException ex) {
            log.error("date not in db, new date will be created in db");
        }
        return Optional.ofNullable(yearOfPublish);
    }

    @Override
    @Transactional
    public YearOfPublish createYear(YearOfPublish year) {
        log.info("Created new Year");
        em.persist(year);
        em.flush();
        return year;
    }
}

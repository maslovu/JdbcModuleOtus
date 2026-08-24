package com.maslov.booksmaslov.repository.impl;

import com.maslov.booksmaslov.domain.YearOfPublish;
import com.maslov.booksmaslov.exception.MaslovBookException;
import com.maslov.booksmaslov.repository.YearRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    public YearOfPublish getYearByDate(String year) {
        var query = em.createQuery(GET_YEAR_BY_DATE, YearOfPublish.class);
        query.setParameter("year", year);
        return checkResult(query, year);
    }

    @Override
    public Optional<YearOfPublish> getYearById(long id) {
        return Optional.ofNullable(em.find(YearOfPublish.class, id));
    }

    @Override
    public YearOfPublish createYear(YearOfPublish year) {
        log.info("Created new Year");
        if (year.getId() == 0) {
            em.persist(year);
            return year;
        }
        return em.merge(year);
    }

    private YearOfPublish checkResult(TypedQuery<YearOfPublish> query, String year) {
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.warn("Has not year with name: {}", year);
            throw new MaslovBookException(String.format("Has not year with name %s", year));
        }
    }
}

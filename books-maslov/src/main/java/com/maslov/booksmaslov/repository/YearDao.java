package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.YearOfPublish;

import java.util.List;
import java.util.Optional;

public interface YearDao {
    List<YearOfPublish> getAllYears();

    YearOfPublish getYearByDate(String date);

    Optional<YearOfPublish> getYearById(long id);

    YearOfPublish createYear(YearOfPublish year);
}

package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.YearOfPublish;

import java.util.List;
import java.util.Optional;

public interface YearRepo {
    List<YearOfPublish> getAllYears();

    Optional<YearOfPublish> getYearByDate(String date);

    YearOfPublish createYear(YearOfPublish year);
}

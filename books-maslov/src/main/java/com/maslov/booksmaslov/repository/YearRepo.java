package com.maslov.booksmaslov.repository;

import com.maslov.booksmaslov.domain.YearOfPublish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YearRepo extends JpaRepository<YearOfPublish, Long> {
    YearOfPublish findByDateOfPublish(String year);
}

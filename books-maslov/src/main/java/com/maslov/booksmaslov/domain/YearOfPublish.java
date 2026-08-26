package com.maslov.booksmaslov.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "year_of_publish")
public class YearOfPublish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "year", unique = true, nullable = false)
    private String year;

    public YearOfPublish(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return year;
    }
}

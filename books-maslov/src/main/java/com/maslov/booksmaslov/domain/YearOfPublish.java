package com.maslov.booksmaslov.domain;

import jakarta.persistence.*;
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
    private String dateOfPublish;

    public YearOfPublish(String dateOfPublish) {
        this.dateOfPublish = dateOfPublish;
    }

    @Override
    public String toString() {
        return "YearOfPublish{" +
                "id=" + id +
                ", dateOfPublish='" + dateOfPublish + '\'' +
                '}';
    }
}

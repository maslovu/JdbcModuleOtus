package com.maslov.booksmaslov.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "books")
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Fetch(FetchMode.JOIN)
    @ManyToOne(targetEntity = Genre.class, cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Fetch(FetchMode.JOIN)
    @ManyToOne(targetEntity = YearOfPublish.class, cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "year_id")
    private YearOfPublish year;

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(targetEntity = Author.class, cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "book_authors", joinColumns = {@JoinColumn(name = "book_id")},
            inverseJoinColumns = {@JoinColumn(name = "author_id")})
    private List<Author> author;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(targetEntity = Comment.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private List<Comment> listOfComments;
}

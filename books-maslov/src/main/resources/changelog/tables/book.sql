CREATE TABLE genres(
    id bigserial,
    name varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE year_of_publish (
    id bigserial,
    year varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE authors(
    id bigserial,
    author_name varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE books(
    book_id bigserial,
    name varchar(255),
    year_id bigint REFERENCES year_of_publish(id),
    genre_id bigint REFERENCES genres(id),
    PRIMARY KEY (book_id)
);

CREATE TABLE comments(
    comment_id bigserial,
    book_id bigint REFERENCES books(book_id) ON DELETE CASCADE ,
    comment_book varchar(255),
    PRIMARY KEY (comment_id)
);

CREATE TABLE books_authors(
    book_id bigint REFERENCES books(book_id) ON DELETE CASCADE,
    author_id  bigint REFERENCES authors(id),
    PRIMARY KEY (book_id, author_id)
);

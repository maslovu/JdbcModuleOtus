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

CREATE TABLE books(
    id bigserial,
    name varchar(255),
    year_id bigint REFERENCES year_of_publish(id) on delete cascade,
    genre_id bigint REFERENCES genres(id) on delete cascade,
    PRIMARY KEY (id)
);

CREATE TABLE comments(
    id bigserial,
    book_id bigint REFERENCES books(id) on delete cascade,
    comment_book varchar(255),
    PRIMARY KEY (id)
);

alter table comments
    add constraint FK_Comment_BookId
        foreign key (book_id) references books (id) on delete cascade;

CREATE TABLE authors(
    id bigserial,
    author_name varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE book_authors(
    book_id bigint REFERENCES books(id) ON UPDATE CASCADE ON DELETE CASCADE,
    author_id  bigint REFERENCES authors(id) ON UPDATE CASCADE,
    CONSTRAINT book_authors_pkey PRIMARY KEY (book_id, author_id)
);

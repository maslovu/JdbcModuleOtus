package com.maslov.booksmaslov.sql;

public class SQLConstants {
    private SQLConstants() {
    }

    public static final String GET_ALL_BOOKS = "select b from Book b";
    public static final String SELECT_BOOK_BY_NAME = "select b from Book b where b.name =:name";
    public static final String SELECT_BOOK_META_BY_ID = "SELECT b FROM Book b " +
            "LEFT JOIN FETCH b.authors " +
            "LEFT JOIN FETCH b.genre " +
            "LEFT JOIN FETCH b.year " +
            "WHERE b.id = :id";
    public static final String SELECT_BOOK_COMMENTS_BY_ID = "SELECT b FROM Book b " +
            "LEFT JOIN FETCH b.comments " +
            "WHERE b.id = :id";
    public static final String GET_ALL_GENRES = "select g from Genre g";
    public static final String GET_GENRE_BY_NAME = "select g from Genre g where g.name =:name";

    public static final String GET_ALL_AUTHORS = "select a from Author a";
    public static final String GET_AUTHOR_BY_NAME = "select a from Author a where a.authorName =:author_name";

    public static final String GET_ALL_YEARS = "select y from YearOfPublish y";
    public static final String GET_YEAR_BY_DATE = "select y from YearOfPublish y where y.dateOfPublish =:year";

    public static final String GET_BOOK_FOR_COMMENTS_BY_ID = "select b from Book b where b.id =:id ";

}

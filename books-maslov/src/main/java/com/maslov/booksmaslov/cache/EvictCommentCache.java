package com.maslov.booksmaslov.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Caching(evict = {
        @CacheEvict(value = "comments", key = "'comments::book::' + #bookId"),
        @CacheEvict(value = "book", key = "'book::' + #bookId")
})
public @interface EvictCommentCache {
}

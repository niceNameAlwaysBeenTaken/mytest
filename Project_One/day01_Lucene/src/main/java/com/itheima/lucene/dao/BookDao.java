package com.itheima.lucene.dao;

import com.itheima.lucene.pojo.Book;

import java.util.List;

public interface BookDao {

    List<Book> findAll();
}

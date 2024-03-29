package com.tensquare.article.dao;

import com.tensquare.article.pojo.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentDao extends MongoRepository<Comment,String> {
    // 通过方法的命名方式查询
    List<Comment> findByArticleid(String articleId);
}

package com.tensquare.article.service;

import com.tensquare.article.dao.CommentDao;
import com.tensquare.article.exception.MyException;
import com.tensquare.article.pojo.Comment;
import com.tensquare.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询所有评论
     * @return
     */
    public List<Comment> findAll() {
        return commentDao.findAll();
    }

    public Comment findById(String commentId) {
        return commentDao.findById(commentId).get();
    }

    public void add(Comment comment) {
        comment.set_id(idWorker.nextId() + "");
        comment.setThumbup(0);
        if(null == comment.getParentid()){
            comment.setParentid("0");
        }
        commentDao.save(comment);
    }

    public void update(Comment comment) {
        commentDao.save(comment);
    }

    public void deleteById(String commentId) {
        commentDao.deleteById(commentId);
    }

    /**
     * 通过文章的编辑查询
     * @param articleId
     * @return
     */
    public List<Comment> findByArticleId(String articleId) {
        // 使用方法命名规则做查询
        List<Comment> list = commentDao.findByArticleid(articleId);
        return list;
    }

    /**
     * 点赞
     * @param id
     */
    public void thumbup(String id) {
        String userId = "007";
        String key = "thumbup_" + userId + "_" + id;
        // 判断是否点过赞了
        if(null != redisTemplate.opsForValue().get(key)){
            // 点赞过了
            throw new MyException("不能重复点赞");
        }

        /* 方式一
        Comment comment = commentDao.findById(id).get();
        comment.setThumbup(comment.getThumbup()+1);
        commentDao.save(comment);
        */

        /**
         * 使用$inc 增加
         * query 条件
         * update 要更新的值
         * CollectionName: 集合名称
         *
         */
        Query query = new Query();
        // where id=#{id}
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        update.inc("thumbup", 1);
        mongoTemplate.updateFirst(query,update,"comment");
        // 存入redis
        redisTemplate.opsForValue().set(key,1);

    }
}

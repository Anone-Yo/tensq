package com.tensquare.article.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.rabbitmq.client.AMQP;
import com.tensquare.article.RabbitmqConfig;
import com.tensquare.article.client.NoticeClient;
import com.tensquare.article.dao.ArticleDao;
import com.tensquare.article.exception.MyException;
import com.tensquare.article.pojo.Article;
import com.tensquare.article.pojo.Notice;
import com.tensquare.util.IdWorker;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ArticleService {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private NoticeClient noticeClient;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private DirectExchange exchange;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 查询所有的文章列表
     * @return
     */
    public List<Article> findAll() {
        return articleDao.selectList(null);
    }

    /**
     * 通过编号查询
     * @param articleId
     * @return
     */
    public Article findById(String articleId) {
        return articleDao.selectById(articleId);
    }

    /**
     * 添加文章
     * @param article
     */
    public void add(Article article) {
        // 设置了ID，ID的值不是自动增长，是手工输入
        article.setId(idWorker.nextId() + "");
        String authorId = "2";
        article.setVisits(0);   //浏览量
        article.setThumbup(0);  //点赞数
        article.setComment(0);  //评论数
        article.setUserid(authorId);
        // 保存到数据库
        articleDao.insert(article);
        // 取出哪些人订阅了这个作者
        // members 获取这个集合下的所有元素
        String auth_user = "article_author_" + authorId;
        Set<String> members = redisTemplate.opsForSet().members(auth_user);
        if(null != members){
            Notice notice = null;
            for (String userId : members) {
                notice = new Notice();
                // 此处为添加文章，所有action为publish
                notice.setAction("publish");
                notice.setOperatorId(authorId);
                notice.setReceiverId(userId);
                notice.setTargetId(article.getId());
                notice.setTargetType("article");
                notice.setType("sys"); // sys，系统通知，由用户的订阅行为发生的
                notice.setState("0"); // 未读取
                noticeClient.add(notice);
            }
        }
        // 写消息给mq，绑定routingkey=作者的ID
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ARTICLE, authorId, article.getId());
    }

    /**
     * 修改文章
     * @param article
     */
    public void update(Article article) {
        articleDao.updateById(article);
    }

    /**
     * 通过编号删除
     * @param articleId
     * @return
     */
    public void deleteById(String articleId) {
        articleDao.deleteById(articleId);
    }

    /**
     * 条件分页查询
     * @param page
     * @param size
     * @param map
     * @return
     */
    public Page<Article> findPage(int page, int size, Map<String,Object> map) {
        // 分页对象，交给dao来处理，设置总记录数
        Page<Article> articlePage = new Page<Article>(page,size);
        // 判断条件
        String id = (String) map.get("id");
        // 构建查询的条件
        EntityWrapper<Article> wrapper = new EntityWrapper<Article>();
        if(!StringUtils.isEmpty(id)){
            // ID相等 where id=#{id}, 添加条件
            wrapper.eq("id", id);
        }
        String title = (String) map.get("title");
        if(!StringUtils.isEmpty(title)){
            // 两边加%
            wrapper.like("title", title);
        }
        List<Article> articles = articleDao.selectPage(articlePage, wrapper);
        // 设置分页的结果集
        articlePage.setRecords(articles);
        return articlePage;
    }

    /**
     * 用户订阅作者
     * @param articleId
     * @return
     */
    public boolean subscribe(String articleId) {
        //1. 获取作者的ID
        Article article = articleDao.selectById(articleId);
        String authId = article.getUserid();
        // 用户的ID
        String userId = "3";
        // 用户所订阅的作者集合
        String user_auth = "article_subscribe_userId_" + userId;
        //2. 判断用户是否订阅了这个作者
        Boolean flag = redisTemplate.opsForSet().isMember(user_auth, authId);
        // 作者下的所有用户集合
        String auth_user = "article_author_" + authId;
        // 使用MQ，
        // 1.根据用户创建消息队列
        Queue queue = new Queue("article_subscribe_" + userId);

        // 2. 交换器与队列进行绑定/解绑, 绑定的routingkey为作者的编号
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(authId);

        if(flag){
            //3 如果存在，取消订阅
            redisTemplate.opsForSet().remove(user_auth, authId);
            // 删除作者的关注者
            redisTemplate.opsForSet().remove(auth_user, userId);
            // 交换器与队列解绑
            rabbitAdmin.removeBinding(binding);
            return false;
        }else{
            // 用户没有订阅这个作者，则要订阅,添加到用户订阅的作者集合中
            redisTemplate.opsForSet().add(user_auth, authId);
            // 作者的用户集合也添加这个用户
            redisTemplate.opsForSet().add(auth_user, userId);
            // 创建队列
            rabbitAdmin.declareQueue(queue);
            // 交换器与队列进行绑定
            rabbitAdmin.declareBinding(binding);
            return true;
        }
    }

    /**
     * 点赞
     * @param articleId
     */
    public void thumbup(String articleId) {
        String userId = "9527";
        // 防止重复点赞
        String key = "article_thumbup_aid_" + articleId + "_uid_" + userId;
        if(null != redisTemplate.opsForValue().get(key)){
            throw new MyException("不能重复点赞");
        }
        // 先查出，再更新
        Article article = articleDao.selectById(articleId);
        article.setThumbup(article.getThumbup() + 1);
        articleDao.updateById(article);
        // 存入redis中
        redisTemplate.opsForValue().set(key, "1");

        // 通知作者
        Notice notice = new Notice();
        // 此处为点赞
        notice.setAction("thumbup");
        notice.setOperatorId(userId);
        notice.setReceiverId(article.getUserid());
        notice.setTargetId(article.getId());
        notice.setTargetType("article");
        notice.setType("user"); // user，只对作者通知
        notice.setState("0"); // 未读取
        noticeClient.add(notice);
    }
}

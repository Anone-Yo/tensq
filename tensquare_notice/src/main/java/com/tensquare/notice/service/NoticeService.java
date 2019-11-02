package com.tensquare.notice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.entity.Result;
import com.tensquare.notice.client.ArticleClient;
import com.tensquare.notice.client.UserClient;
import com.tensquare.notice.dao.NoticeDao;
import com.tensquare.notice.dao.NoticeFreshDao;
import com.tensquare.notice.pojo.Notice;
import com.tensquare.notice.pojo.NoticeFresh;
import com.tensquare.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class NoticeService {

    @Autowired
    private NoticeDao noticeDao;
    @Autowired
    private NoticeFreshDao noticeFreshDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ArticleClient articleClient;

    @Autowired
    private UserClient userClient;

    /**
     * 通过编号查询
     * @param id
     * @return
     */
    public Notice findById(String id) {
        Notice notice = noticeDao.selectById(id);
        // 调用文章微服通过编号查询
        Result articleResult = articleClient.findById(notice.getTargetId());
        if(articleResult.isFlag()){
            // 成功就有返回文章对象
            Map<String,Object> articleMap = (Map<String,Object>)articleResult.getData();
            // 设置通过对象中的目标名称
            notice.setTargetName(((String) articleMap.get("title")));
        }
        Result userResult = userClient.selectById(notice.getOperatorId());
        if(userResult.isFlag()){
            Map<String,Object> userMap = (Map<String,Object>)userResult.getData();
            // 设置操作者名称
            notice.setOperatorName(((String) userMap.get("nickname")));
        }
        return notice;
    }

    /**
     * 添加
     * @param notice
     */
    public void add(Notice notice) {
        notice.setId(idWorker.nextId() + "");
        // 消息的发布时间应该是系统的当前时间
        notice.setCreatetime(new Date());
        noticeDao.insert(notice);

        // 添加推送
        /* 改用RabbitMQ来处理
        NoticeFresh nf = new NoticeFresh();
        nf.setNoticeId(notice.getId());
        nf.setUserId(notice.getReceiverId());
        noticeFreshDao.insert(nf);*/
    }

    /**
     * 分页
     * @param page
     * @param size
     * @return
     */
    public Page<Notice> findPage(int page, int size) {
        Page<Notice> noticePage = new Page<Notice>(page,size);
        List<Notice> notices = noticeDao.selectPage(noticePage, null);
        for (Notice notice : notices) {
            setNames(notice);
        }
        noticePage.setRecords(notices);
        return noticePage;
    }

    /**
     * 修改
     * @param notice
     */
    public void update(Notice notice) {
        noticeDao.updateById(notice);
    }

    /**
     * 通过用户ID查询未推送的消息
     * @param userId
     * @param page
     * @param size
     * @return
     */
    public Page<NoticeFresh> freshPage(String userId, int page, int size) {
        // 分页对象
        Page<NoticeFresh> noticeFreshPage = new Page<>(page, size);
        // 查询的条件
        EntityWrapper<NoticeFresh> wrapper = new EntityWrapper<NoticeFresh>();
        wrapper.eq("userId", userId);
        // 查询
        List<NoticeFresh> list = noticeFreshDao.selectPage(noticeFreshPage, wrapper);
        noticeFreshPage.setRecords(list);
        return noticeFreshPage;
    }

    /**
     * 设置名称
     * @param notice
     */
    private void setNames(Notice notice){
        Result articleResult = articleClient.findById(notice.getTargetId());
        if(articleResult.isFlag()){
            // 成功就有返回文章对象
            Map<String,Object> articleMap = (Map<String,Object>)articleResult.getData();
            // 设置通过对象中的目标名称
            notice.setTargetName(((String) articleMap.get("title")));
        }
        Result userResult = userClient.selectById(notice.getOperatorId());
        if(userResult.isFlag()){
            Map<String,Object> userMap = (Map<String,Object>)userResult.getData();
            // 设置操作者名称
            notice.setOperatorName(((String) userMap.get("nickname")));
        }
    }
}

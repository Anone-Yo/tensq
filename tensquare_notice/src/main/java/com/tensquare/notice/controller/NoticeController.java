package com.tensquare.notice.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.notice.pojo.Notice;
import com.tensquare.notice.pojo.NoticeFresh;
import com.tensquare.notice.service.NoticeService;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/notice")
public class NoticeController {
    
    @Autowired
    private NoticeService noticeService;

    /**
     * 通过编号查询通知
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable String id){
        Notice notice = noticeService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",notice);
    }

    /**
     * 添加
     */
    @PostMapping
    public Result add(@RequestBody Notice notice){
        noticeService.add(notice);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /**
     * 分页查询
     */
    @GetMapping("/findPage/{page}/{size}")
    public Result findPage(@PathVariable int page, @PathVariable int size){
        // 查询到分页结果
        Page<Notice> noticePage = noticeService.findPage(page,size);
        // 封装成分页结果，按前端接收的数据格式
        PageResult<Notice> pageResult = new PageResult<Notice>(noticePage.getTotal(), noticePage.getRecords());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 修改
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable String id, @RequestBody Notice notice){
        notice.setId(id);
        noticeService.update(notice);
        return new Result(true,StatusCode.OK,"更新成功");
    }


    /**
     * 通过用户ID查询未推送的消息
     */
    @GetMapping("/freshPage/{userId}/{page}/{size}")
    public Result freshPage(@PathVariable String userId, @PathVariable int page, @PathVariable int size){
        Page<NoticeFresh> noticeFreshPage = noticeService.freshPage(userId, page, size);
        PageResult<NoticeFresh> pageResult = new PageResult<>(noticeFreshPage.getTotal(), noticeFreshPage.getRecords());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }
}

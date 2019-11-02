package com.tensquare.article.controller;

import com.tensquare.article.pojo.Comment;
import com.tensquare.article.service.CommentService;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 查询所有的评论列表
     * @return
     */
    @GetMapping
    public Result findAll(){
        // 查询所有的评论列表
        List<Comment> list = commentService.findAll();
        return new Result(true,StatusCode.OK,"查询成功",list);
    }

    @GetMapping("/{commentId}")
    public Result findById(@PathVariable String commentId){
        Comment comment = commentService.findById(commentId);
        return new Result(true,StatusCode.OK,"查询成功",comment);
    }

    @PostMapping
    public Result add(@RequestBody Comment comment){
        // 调用业务添加
        commentService.add(comment);
        return new Result(true,StatusCode.OK,"添加评论成功");
    }

    @PutMapping("/{commentId}")
    public Result update(@RequestBody Comment comment,@PathVariable String commentId){
        // 调用业务修改
        comment.set_id(commentId);
        commentService.update(comment);
        return new Result(true,StatusCode.OK,"修改评论成功");
    }

    @DeleteMapping("/{commentId}")
    public Result deleteById(@PathVariable String commentId){
        commentService.deleteById(commentId);
        return new Result(true,StatusCode.OK,"删除成功");
    }
    
    @GetMapping("/article/{articleId}")
    public Result findByArticleId(@PathVariable String articleId){
        List<Comment> commentList = commentService.findByArticleId(articleId);
        return new Result(true,StatusCode.OK,"查询成功",commentList);
    }
    
    @PutMapping("/thumbup/{id}")
    public Result thumbup(@PathVariable String id){
        commentService.thumbup(id);
        return new Result(true,StatusCode.OK,"点赞成功");
    }

}

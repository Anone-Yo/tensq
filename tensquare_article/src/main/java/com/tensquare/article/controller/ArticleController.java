package com.tensquare.article.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.tensquare.article.pojo.Article;
import com.tensquare.article.service.ArticleService;
import com.tensquare.entity.PageResult;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/article")
@CrossOrigin
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    /**
     * 查询所有的文章列表
     * @return
     */
    @GetMapping
    public Result findAll(){
        // 查询所有的文章列表
        List<Article> list = articleService.findAll();
        return new Result(true,StatusCode.OK,"查询成功",list);
    }
    
    @GetMapping("/{articleId}")
    public Result findById(@PathVariable String articleId){
        Article article = articleService.findById(articleId);
        return new Result(true,StatusCode.OK,"查询成功",article);
    }
    
    @PostMapping
    public Result add(@RequestBody Article article){
        // 调用业务添加
        articleService.add(article);
        return new Result(true,StatusCode.OK,"添加文章成功");
    }

    @PutMapping("/{articleId}")
    public Result update(@RequestBody Article article,@PathVariable String articleId){
        // 调用业务修改
        article.setId(articleId);
        articleService.update(article);
        return new Result(true,StatusCode.OK,"修改文章成功");
    }

    @DeleteMapping("/{articleId}")
    public Result deleteById(@PathVariable String articleId){
        articleService.deleteById(articleId);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * /search/{page}/{size}
     * 文章分页
     */
    @PostMapping("/search/{page}/{size}")
    public Result findPage(@PathVariable int page, @PathVariable int size, @RequestBody Map<String,Object> map){
        // 调用业务层实现分页查询
        Page<Article> articlePage = articleService.findPage(page,size, map);
        // 包装到分页结果里，前端接收的数据格式{flag:xx, code: xx, message: xxx, data: {total: xx, rows: 分页的结果集}}
        PageResult<Article> pageResult = new PageResult<Article>(articlePage.getTotal(), articlePage.getRecords());
        // 包装一下返回的结果到Result里
        Result result = new Result(true, StatusCode.OK, "查询成功", pageResult);
        return result;
    }

    /**
     *  用户来订阅作者
     * @param articleId
     * @return
     */
    @PostMapping("/subscribe/{articleId}")
    public Result subscribe(@PathVariable String articleId){
        boolean flag = articleService.subscribe(articleId);
        if(flag){
            return new Result(true,StatusCode.OK,"订阅成功");
        }else{
            return new Result(true,StatusCode.OK,"取消订阅成功");
        }
    }

    /**
     * 点赞
     */
    @PutMapping("/thumbup/{articleId}")
    public Result thumbup(@PathVariable String articleId){
        articleService.thumbup(articleId);
        return new Result(true,StatusCode.OK,"点赞成功");
    }
}

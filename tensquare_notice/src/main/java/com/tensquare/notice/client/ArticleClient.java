package com.tensquare.notice.client;

import com.tensquare.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("tensquare-article")
public interface ArticleClient {

    @GetMapping("/article/{articleId}")
    Result findById(@PathVariable("articleId") String articleId);
}

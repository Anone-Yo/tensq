package com.tensquare.article.client;

import com.tensquare.article.pojo.Notice;
import com.tensquare.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("tensquare-notice")
public interface NoticeClient {
    @PostMapping("/notice")
    Result add(@RequestBody Notice notice);
}

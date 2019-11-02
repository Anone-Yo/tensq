package com.tensquare.encrypt.filter;

import com.google.common.base.Charsets;
import com.netflix.discovery.util.StringUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import com.tensquare.encrypt.rsa.RsaKeys;
import com.tensquare.encrypt.service.RsaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.InputStream;

@Component
public class EncryptFilter extends ZuulFilter {
    @Override
    public String filterType() {
        // pre转发前触发
        return "pre";
    }

    @Override
    public int filterOrder() {
        // 过滤器执行的顺序, 值越小越优先
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        // 是否需要过滤，如果为true要过滤，调用run方法
        return true;
    }

    @Autowired
    private RsaService rsaService;

    /**
     * 过滤中执行的方法
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        // 1. 获取请求对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        // 2. 获取请求数据
        try {
            InputStream is = request.getInputStream();
            // 密文
            String ecryptContent = StreamUtils.copyToString(is, Charsets.UTF_8);
            if(!StringUtils.isEmpty(ecryptContent)){
                // 3. 如果有请求参数，才需要解密
                String paremeters = rsaService.rsaDecryptDataPEM(ecryptContent, RsaKeys.getServerPrvKeyPkcs8());
                byte[] bytes = paremeters.getBytes();
                // 4. 解密后，修改原有的请求内容
                HttpServletRequest req = new HttpServletRequestWrapper(request){
                    @Override
                    public ServletInputStream getInputStream() throws IOException {
                        return new ServletInputStreamWrapper(bytes);
                    }

                    @Override
                    public int getContentLength() {
                        return bytes.length;
                    }

                    @Override
                    public long getContentLengthLong() {
                        return bytes.length;
                    }
                };
                requestContext.setRequest(req);
                // 5. 设置请求头的content-type application/json;charset=utf-8
                requestContext.addZuulRequestHeader("Content-Type","application/json;charset=utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }
}

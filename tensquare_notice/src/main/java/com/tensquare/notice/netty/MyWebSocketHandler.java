package com.tensquare.notice.netty;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.notice.config.ApplicationContextProvider;
import com.tensquare.notice.config.RabbitmqConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class MyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static ConcurrentHashMap<String,Channel> channelMap = new ConcurrentHashMap<String,Channel>();

    /**
     * 1.用户浏览器与服务的通信
     * 2.获取RabbitMQ中的消息
     * 3.推送给前端
     * 4.监听消息队列(用户在线时，服务器主动推送)
     * @param ctx   与浏览器的通道 需要缓存起来（用户在线时，要推送）
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // ctx 与客户端建立的通道
        // msg 传递的消息 必须有用户的ID，【约定用户第一次连接传递数据{"userId":"1"}】
        String userId = objectMapper.readTree(msg.text()).get("userId").asText();
        // 获取连接
        Channel channel = channelMap.get(userId);
        if(null == channel){
            // 第一次连接
            channelMap.put(userId, ctx.channel());
        }else{
            if(!channel.isActive()){
                // 不可用，已经关闭
                // 使用新的通道
                channelMap.put(userId, ctx.channel());
            }
        }
        // 处理用户浏览器发送过来的请求 WeSocket
        // 1. 获取用户编号 找到对应的消息队列
        RabbitAdmin rabbitAdmin = ApplicationContextProvider.getApplicationContext().getBean("rabbitAdmin", RabbitAdmin.class);
        String queueName = "article_subscribe_" + userId;
        // 2. 访问RabbitMQ取出消息的个数
        // 消息队列信息
        Properties queueProperties = rabbitAdmin.getQueueProperties(queueName);
        int count = 0;
        if(null != queueProperties){
            // 队列存在
            count = (int)queueProperties.get(RabbitAdmin.QUEUE_MESSAGE_COUNT);
        }

        // 3. 推送给前端
        HashMap<String,Integer> countMap = new HashMap();
        countMap.put("sysNoticeCount", count);
        Result result = new Result(true, StatusCode.OK, "查询成功", countMap);
        ctx.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(result)));
        if(count > 0){
            // 消费了消息，要清理队列上的消息
            rabbitAdmin.purgeQueue(queueName,true);
        }
        // 4. 添加监听
        //   RabbitMQ监听窗口
        SimpleMessageListenerContainer messageListenerContainer = ApplicationContextProvider.getApplicationContext().getBean("sysNoticeContainer", SimpleMessageListenerContainer.class);
        // 监听队列
        // @RabbitListner(queues="asdfas")
        messageListenerContainer.addQueueNames(queueName);

    }
}

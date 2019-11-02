package com.tensquare.notice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.tensquare.entity.Result;
import com.tensquare.entity.StatusCode;
import com.tensquare.notice.netty.MyWebSocketHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.util.HashMap;

public class SysNoticeListener implements ChannelAwareMessageListener {

    private static ObjectMapper objectMapper = new ObjectMapper();
    /**
     * 用户在线时的处理
     * @param message
     * @param channel
     * @throws Exception
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        // 收到MQ上的消息时，消息的消费一个一个来的，一次只消费一个消息
        MessageProperties messageProperties = message.getMessageProperties();
        // 队列的名称
        String queueName = messageProperties.getConsumerQueue();
        // 1. 解析队列的名称，从中获取用户的ID，channelMap key值就是用户的ID，获取用户的通道
        // article_subscribe_3
        String userId = queueName.substring(queueName.lastIndexOf("_") + 1);
        io.netty.channel.Channel nettyChannel = MyWebSocketHandler.channelMap.get(userId);
        if(null == nettyChannel){
            // 不消费消息了
            channel.txRollback();
        }
        // 2. 消息的数量就是1个
        HashMap<String,Integer> countMap = new HashMap();
        countMap.put("sysNoticeCount", 1);
        Result result = new Result(true, StatusCode.OK, "查询成功", countMap);
        // 3. 通过通道，向用户的浏览器推送
        nettyChannel.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(result)));
    }
}

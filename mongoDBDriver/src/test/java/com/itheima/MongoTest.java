package com.itheima;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MongoTest {
    MongoClient client = null;
    MongoDatabase db = null;
    MongoCollection<Document> collection = null;
    @Before
    public void init(){
        client = new MongoClient("192.168.137.140", 27017);
        //2. 切换数据库
        db = client.getDatabase("commentdb");
        //3. 使用集合
        collection = db.getCollection("comment");
    }

    @After
    public void destroy(){
        client.close();
    }

    @Test
    public void findAll(){
        // db.集合.find()
        /*//1. 创建连接客户端
        MongoClient client = new MongoClient("192.168.137.140", 27017);
        //2. 切换数据库
        MongoDatabase db = client.getDatabase("commentdb");
        //3. 使用集合
        MongoCollection<Document> collection = db.getCollection("comment");*/
        //4. 集合上再做查询
        FindIterable<Document> documents = collection.find();
        //5. 遍历文件，输出
        for (Document doc : documents) {
            System.out.println(doc.getString("_id") + ": " + doc.getString("content"));
        }
       /* //6. 关闭连接客户端
        client.close();*/
    }

    @Test
    public void findById(){
        // db.comment.find({_id: 1})
        // 条件{_id: 1}，key value
        BasicDBObject bson = new BasicDBObject("_id","1");
        FindIterable<Document> documents = collection.find(bson);
        for (Document doc : documents) {
            System.out.println(doc.getString("_id") + ": " + doc.getString("content"));
        }
    }

    @Test
    public void add(){
        // Document
        //Document doc = Document.parse("{\"_id\":\"7\",\"content\":\"这个手机好\",\"userid\":\"1014\",\"thumbup\":123,\"share\":123}");
        // db.comment.insert({_id:"7",content:"这个手机好",userid:"1014",thumbup:123,share:NumberInt(123)});

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("_id","8");
        map.put("content","这个手机好");
        map.put("userid","1014");
        map.put("thumbup",123);
        map.put("share",123);
        Document doc = new Document(map);
        collection.insertOne(doc);
    }

    @Test
    public void update(){
        //db.comment.update({_id: "007"},{$set:{content:"十次方课程2.0"}})
        // 条件
        BasicDBObject condition = new BasicDBObject("_id","6");
        BasicDBObject value = new BasicDBObject("userid","9527");
        BasicDBObject set = new BasicDBObject("$set", value);
        collection.updateOne(condition, set);
    }

    @Test
    public void deleteById(){
        // db.comment.remove({_id: "007"})
        BasicDBObject condition = new BasicDBObject("_id","8");
        collection.deleteOne(condition);
    }
}

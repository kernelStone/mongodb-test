package com.mongodb.test;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.test.bean.User;
import com.mongodb.test.logic.UserManagerService;
import com.mongodb.test.service.UserService;
import com.mongodb.test.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class Test {
    @Resource
    UserService userService;
    @Resource
    UserManagerService userManagerService;

    @org.junit.Test
    public void testFindAll() {
        log.info("{} ", JSONObject.toJSONString(userService.findAll()));
    }

    @org.junit.Test
    public void insertOne() {
        for (int i = 10; i < 20; i++) {
            userService.insert(User.builder().userName("zhuleishitou000" + i).status(1).createDate(new Date())
                .modifyDate(new Date()).build());
        }

    }

    @org.junit.Test
    public void queryBySort() {
        List<User> userList = userService.findAll(Sort.by(Sort.Direction.DESC, "createDate"));
        log.info("{} ", userList);

    }

    @org.junit.Test
    public void queryByExample() {
        User user = User.builder().build();
        user.setStatus(0);
        Example<User> example = Example.of(user);
        List<User> userList = userService.findAll(example, Sort.by(Sort.Direction.DESC, "createDate"));
        log.info("{} ", userList);
    }

    /**
     * 模糊查询
     */
    @org.junit.Test
    public void queryByExampleMatcher() {
        User user = User.builder().build();
        user.setUserName("zhulei");

        //实例化对象
        ExampleMatcher matching = ExampleMatcher.matching();
        //设置搜索的字段
        matching = matching.withMatcher("userName", ExampleMatcher.GenericPropertyMatchers.contains());
        //最后用这个构造获取Example
        Example<User> example = Example.of(user, matching);
        //写在后面不管用
        //example.getMatcher().withMatcher("userName", ExampleMatcher.GenericPropertyMatchers.contains());
        List<User> userList = userService.findAll(example, Sort.by(Sort.Direction.DESC, "createDate"));
        log.info("{} ", userList);
    }

    /**
     * 范围查询
     */
    @org.junit.Test
    public void queryRand() {

        Query query = new Query();
        query.addCriteria(Criteria.where("createDate").lte(DateUtil.addHour(new Date(), -1))
            .gte(DateUtil.addDay(new Date(), -1)));
        //排序
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createDate"));
        query.with(sort);
        query.limit(5);
        query.skip(1);

        User user = User.builder().build();
        user.setUserName("zhulei");

        List<User> userList = userService.find(query);
        log.info("{} ", userList);
    }

    /**
     * 分页查询
     */
    @org.junit.Test
    public void queryByPage() {
        //查询条件
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(new Sort.Order(Sort.Direction.ASC, "createDate")));
        PageImpl<User> userPage = userService.find(pageRequest);
        log.info("{} ", userPage.getContent());
        //
    }

    /**
     * 测试更新
     */
    @org.junit.Test
    public void testUpdateOne() {
        log.info(" {} ", JSONObject.toJSONString(userService.findById("657aa12ea19b972cf410f1ce")));
        log.info(" {} ", JSONObject.toJSONString(userService.existsById("657aa12ea19b972cf410f1ce")));
        //userService.updateSelective(User.builder()
        //    .id(new ObjectId("657aa12ea19b972cf410f1ce"))
        //    .userName("wangshuo001").build());
    }

    @org.junit.Test
    public void testUpdate() {
        Query query = new Query(Criteria.where("_id").is("657aa12ea19b972cf410f1cf"));
        //UpdateResult updateResult = userService.update(query, User.builder().id(
        //    new ObjectId("657aa12ea19b972cf410f1ce")).status(1).build());
        //log.info(" {} ", updateResult);
    }

    /**
     * 测试乐关锁
     */
    @org.junit.Test
    public void testOptimisticLock(){
        //userService.findById()
        User user=  userService.insert(User.builder()
            .userName("hello")
            .status(1)
            .createDate(new Date())
            .modifyDate(new Date())
            .build());
        User tmp = userService.findOne(user).orElse(null);
        user.setUserName("helloWord");
        userService.save(user);
        userService.save(tmp); // throws OptimisticLockingFailureException
    }

    /**
     * 测试锁
     */
    @org.junit.Test
    public void testLock() throws InterruptedException {
        userManagerService.lockUpdateUser(User.builder().id("657aa12ea19b972cf410f1cf").build());
    }

    @org.junit.Test
    public void testLock1() throws InterruptedException {
        userManagerService.lockUpdateUser(User.builder().id("657aa12ea19b972cf410f1cf").build());
    }
    /**
     * 事务测试
     */
    @org.junit.Test
    public void testTransaction() {
        userManagerService.updateUser(User.builder()
            //.id(new ObjectId("657aa12ea19b972cf410f1d0"))
            .userName("wangshuo002")
            .build());
    }

}

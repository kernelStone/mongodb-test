package com.mongodb.test.logic;

import com.mongodb.client.result.UpdateResult;
import com.mongodb.test.bean.User;
import com.mongodb.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * 单节点 mongodb 不支持事务，需要搭建 MongoDB 复制集。
 */
@Service
@Slf4j
public class UserManagerService {
    @Resource
    UserService userService;

    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) {
        userService.updateSelective(user);
        if (true) {
            throw new RuntimeException(" 测试事物 ");
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public UpdateResult lockUpdateUser(User user) throws InterruptedException {
        try {
            User userResult = userService.lock(user.getId());
            userResult.setUserName("lock - " + userResult.getUserName());
            userService.updateSelective(userResult);
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            log.error("",e);
            throw new RuntimeException(e);
        } finally {
            return userService.unLock(user.getId());
        }
    }
}

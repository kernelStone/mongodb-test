package com.mongodb.test.logic;

import com.mongodb.test.bean.User;
import com.mongodb.test.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 单节点 mongodb 不支持事务，需要搭建 MongoDB 复制集。
 */
@Service
public class UserManagerService {
    @Resource
    UserService userService;

    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) {
      userService.updateSelective(user);
        if(true){
            throw new RuntimeException(" 测试事物 ");
        }
        return null;
    }
}

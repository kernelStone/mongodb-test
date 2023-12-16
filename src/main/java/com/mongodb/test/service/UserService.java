package com.mongodb.test.service;

import com.mongodb.test.bean.User;
import com.mongodb.test.dao.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<String, User, UserRepository>{
    public UserService() {
        super(User.class);
    }
}

package com.vostroi.components.service;

import com.vostroi.api.users.bean.User;
import com.vostroi.api.users.service.UserService;
import com.vostroi.components.dao.UserDao;
import com.vostroi.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Administrator
 * @date 2020/8/9 13:15
 * @projectName mcloud
 * @title: UsersServiceImpl
 * @description: TODO
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public JpaRepository getRepositry() {
        return userDao;
    }

    @Override
    public User findById(String id) {
        Optional<User> u = userDao.findById(id);
        if (Optional.ofNullable(u).isPresent() && u.isPresent()) {
            return u.get();
        }
        return null;
    }

}

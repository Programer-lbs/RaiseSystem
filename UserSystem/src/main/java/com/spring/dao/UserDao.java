package com.spring.dao;

import com.spring.domain.User;

public interface UserDao {

    //用户登录请求
    User checkUser(User user);

    //检验用户名是否注册
    User isRegisted(User user);

    //增加用户
    void addUser(User user);

    //删除用户
    void deleteUser(Integer uid);

    //修改信息
    void updateUser(User user);


}

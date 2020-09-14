package com.spring.service;

import com.spring.domain.User;

public interface UserService {

    //判断用户名是否注册
    User isRegisted(User user);


    //判断用户名是否合法，密码是否合法
    String userIsRight(User user);


    //注册用户
    void userRegister(User user);


    //发送注册邮件
    void sendRegistMail(String email,String code) throws Exception;

}

package com.spring.service.impl;

import com.spring.dao.UserDao;
import com.spring.domain.User;
import com.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private JavaMailSenderImpl sender;
    @Autowired
    private Jedis jedis;
    @Autowired
    private BCryptPasswordEncoder encoder;

    //判断用户名是否注册
    @Override
    public User isRegisted(User user) {
      User resUser =   userDao.isRegisted(user);
      return resUser;
    }

    @Override
    public String userIsRight(User user) {

        if(user.getUsername().isEmpty() || user.getPassword().isEmpty()){
            return "用户名和密码不能为空";
        }

        //判断用户名是否符合长度
        if(user.getUsername().length() >= 18){
            return  "用户名长度不能超过18个字符";
        }
        //判断密码是否符合规则
        String pwd = user.getPassword();
        if(pwd.contains(" ")){
            return "密码不能包含空格";
        }

        if(pwd.length()<8){
            return "密码长度不能少于8个字符";
        }

        boolean flag1=false,flag2=false,flag3=false;

        for(int i = 0;i<pwd.length();i++){
            if(Character.isUpperCase(pwd.charAt(i))){
                flag1 = true;
            }else if(Character.isLowerCase(pwd.charAt(i))){
                flag2 = true;
            }else if(Character.isDigit(pwd.charAt(i))){
                flag3 = true;
            }else{
                //如果到这里说明字符串中有特殊字符，不合法
                return "密码不能含有特殊字符";
            }
        }
        if(!(flag1 && flag2 && flag3)){
            return "密码必须包含大写字母、小写字母、数字";
        }

        User u = isRegisted(user);
        if(u!=null){
            return "用户名已注册";
        }
        return "isRight";
    }
    @Override
    public void userRegister(User user) {
        user.setRole("user");

        //使用BCryptPasswordEncoder对密码加密储存
        user.setPassword(encoder.encode(user.getPassword()));
        userDao.addUser(user);
    }
    //发送注册邮箱
    @Override
    public void sendRegistMail(String email,String code) throws Exception{
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("注册验证码");
        message.setText("您的注册验证码为--"+code+"--请在60秒内填写注册!如有疑问请联系1782452111@qq.com");
        message.setTo(email);
        message.setFrom("1782452111@qq.com");
        sender.send(message);
    }
}

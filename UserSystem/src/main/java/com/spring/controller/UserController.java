package com.spring.controller;

import com.spring.domain.User;
import com.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;

@Controller
public class UserController {


    @Autowired
    private UserService userService;
    @Autowired
    private Jedis jedis;

    @GetMapping("/")
    public String toRegist(Map<String,Object> maps){
        maps.put("msg",null);
        maps.put("user",new User());
        return "regist";
    }

    @PostMapping("/getcode")
    public String getcode(User user, Map<String,Object> maps, HttpSession session){
        System.out.println(user);
        //信息回显
        maps.put("user",user);

        if(!"isRight".equals(userService.userIsRight(user))){
            maps.put("msg",userService.userIsRight(user));
            return "regist";
        }

        if(user.getEmail().isEmpty()){
            maps.put("msg","请先填写邮箱");
            return "regist";
        }

        //请求次数
        String questNum = user.getEmail()+":num";

        //生成验证码
        String code  = UUID.randomUUID().toString().substring(0,6);
        jedis.setex(user.getEmail(),60,code);

        //验证请求次数，超过三次则不能再验证
        if(jedis.get(questNum)==null){
            jedis.setex(questNum,24*60*60,"1");
        }else if(Integer.parseInt(jedis.get(questNum))<3){
            jedis.incr(questNum);
        }else{
            maps.put("msg","今日你获取验证码已超过三次，不能再获取!");
            return "regist";
        }

        //发送验证码
        try {
            userService.sendRegistMail(user.getEmail(),code);
        }catch (Exception e){
            maps.put("msg","邮箱地址不合法!");
            jedis.del(questNum,user.getEmail());
            return "regist";
        }

        maps.put("msg","验证码发送成功!");
        session.setAttribute("user",user);
        return "regist";
    }

    @PostMapping("/register")
    public String register(String code,Map<String,Object> maps,HttpSession session){
        //从session中获取用户
        User user = (User)session.getAttribute("user");

        //信息回显
        maps.put("user",user);
        if(user==null){
            maps.put("msg","请将信息填写完整并获取验证码");
            return "regist";
        }

        String realCode = jedis.get(user.getEmail());
       Integer requestNum = Integer.parseInt(jedis.get(user.getEmail()+":num"));


        if(requestNum==0){
            maps.put("msg","请先获取验证码");
            return "regist";
        }else if(realCode==null){
            maps.put("msg","您的验证码已过期，请重新获取验证码");
            return "regist";
        }else if(!realCode.equals(code)){
            maps.put("msg","验证码输入错误!");
            return "regist";
        }else{
            userService.userRegister(user);
            maps.put("msg","注册成功!");
            return "regist";
        }
    }
}

package com.neu.crm.settings.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neu.crm.exceptions.loginException;
import com.neu.crm.settings.domain.User;
import com.neu.crm.settings.service.UserService;
import com.neu.crm.settings.service.impl.UserServiceImpl;
import com.neu.crm.utils.MD5Util;
import com.neu.crm.utils.PrintJson;
import com.neu.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    //这里用的是模板方法，还没用到框架，这样能减少servlet的创建数量
    //判断你的请求来进行不同的处理
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("欢迎来到用户控制系统");
        //获取请求路径，就是你要干啥？
        String path = request.getServletPath();
        //然后进行判断，看到底是想干啥
        //如果是登录请求
        if("/settings/user/login.do".equals(path)){
            //调用登录方法
            System.out.println("调用登录方法");
            login(request,response);
        }
        //如果是其他请求
        else if(" ".equals(path)){

        }

    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将密码转为密文形式才能对比
        loginPwd= MD5Util.getMD5(loginPwd);
        //获取浏览器ip地址
        String ip = request.getRemoteAddr();
        System.out.println("ip======="+ip);
        //未来业务层开发，统一使用代理类形态的接口对象,而不是直接new一个对象
        UserService service = (UserService) ServiceFactory.getService(new UserServiceImpl());
        //然后调用方法查询
        try {
            User user = service.login(loginAct,loginPwd,ip);
            //如果获取到了，将这个对象保存在 Session域中
            request.getSession().setAttribute("user",user);
            //调用方法将登录结果以json格式返回{"success":true}
            PrintJson.printJsonFlag(response,true);
        } catch (loginException e) {
            e.printStackTrace();
            //如果登录失败了，还需要返回失败的信息{"success":false;"massage":?}
            String massage = new String(e.getMessage());
            //可以用一个vo对象来存储信息，但是没必要
            //用map来存储
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("success",false);
            map.put("massage",massage);
            //转成json格式并返回
            PrintJson.printJsonObj(response,map);
        }

    }
}

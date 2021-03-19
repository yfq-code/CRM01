package com.neu.crm.settings.service;

import com.neu.crm.exceptions.loginException;
import com.neu.crm.settings.domain.User;
import com.neu.crm.workbench.domain.Activity;

import java.util.List;


public interface UserService {
    //登录验证
    User login(String loginAct, String loginPwd, String ip) throws loginException;
    //获取用户列表
    List<User> getUserList();

}

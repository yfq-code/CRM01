package com.neu.crm.settings.dao;

import com.neu.crm.settings.domain.User;
import com.neu.crm.workbench.domain.Activity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserDao {
    //登录验证,传入账号密码
    User login(@Param("loginAct")String name, @Param("loginPwd")String password);
    //获取所有user
    List<User> getUserList();

}

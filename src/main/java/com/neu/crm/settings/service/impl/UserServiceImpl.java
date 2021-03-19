package com.neu.crm.settings.service.impl;

import com.neu.crm.exceptions.loginException;
import com.neu.crm.settings.dao.UserDao;
import com.neu.crm.settings.domain.User;
import com.neu.crm.settings.service.UserService;
import com.neu.crm.utils.DateTimeUtil;
import com.neu.crm.utils.SqlSessionUtil;
import com.neu.crm.workbench.domain.Activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    //首先要创建一个dao对象
    private UserDao dao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws loginException {
        System.out.println("service login");
        User user =null;
        /*Map<String,String> loginMap = new HashMap<String,String>();
        loginMap.put("loginAct",loginAct);
        loginMap.put("loginPwd",loginPwd);*/
        user = dao.login(loginAct,loginPwd);
        if(user == null){
            throw new loginException("账号密码错误");
        }
        //如果该用户存在，就需要判断其他的三项信息是否合法
        //1、失效时间
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        if(expireTime.compareTo(currentTime)<0){
            throw new loginException("账号已失效");
        }

        //2、判断锁定状态
        String lockState = user.getLockState();
        if("0".equals(lockState)){
            throw new loginException("账号已锁定");
        }

        //3、判断ip地址,这个就不判断了
        /*String allowIps = user.getAllowIps();
        if(!allowIps.contains(ip)){
            throw new loginException("ip地址受限");
        }*/

        //以上全部满足
        return user;
    }

    @Override
    public List<User> getUserList() {
        //调用dao对象的方法
        List<User> uList = dao.getUserList();
        return uList;
    }

}

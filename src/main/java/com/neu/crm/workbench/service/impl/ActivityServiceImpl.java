package com.neu.crm.workbench.service.impl;

import com.neu.crm.settings.dao.UserDao;
import com.neu.crm.settings.domain.User;
import com.neu.crm.utils.SqlSessionUtil;
import com.neu.crm.vo.PaginationVO;
import com.neu.crm.workbench.dao.ActivityDao;
import com.neu.crm.workbench.dao.ActivityRemarkDao;
import com.neu.crm.workbench.domain.Activity;
import com.neu.crm.workbench.domain.ActivityRemark;
import com.neu.crm.workbench.service.ActivityService;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    //引入dao
    private ActivityDao dao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao rdao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao udao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);


    @Override
    public boolean save(Activity a) {
        int count = dao.save(a);
        if(count==1) return true;
        else return false;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        //业务层需要分三步返回结果
        //1、取得total
        int total = dao.getTotalByCondition(map);
        //2、取得dataList
        List<Activity> dataList = dao.getActivityListByCondition(map);
        //3、组成PaginationVO对象
        PaginationVO<Activity> vo = new PaginationVO<Activity>();
        vo.setTotal(total);
        vo.setDataList(dataList);

        //将vo返回
        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag = true;
        //也要同时删除备注表里的信息
        //查询出需要删除的备注的数量
        int count1 = rdao.getCountByAids(ids);
        //删除备注，返回受到影响的条数（实际删除的数量）
        int count2 = rdao.deleteByAids(ids);
        if(count1!=count2) flag = false;
        //删除市场活动，返回受到影响的条数（实际删除的数量）
        int count = dao.delete(ids);
        if(count!=ids.length) flag=false;
        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        //首先要取 userList  调用的是userdao
        List<User> uList = udao.getUserList();
        //然后取 Activity 调用的是dao
        Activity a = dao.getActivityById(id);
        System.out.println(a);
        Map<String,Object>map = new HashMap<String,Object>();
        map.put("uList", uList);
        map.put("a", a);
        return map;
    }

    @Override
    public boolean update(Activity a) {
        boolean res = false;
        //调用dao
        int num = dao.update(a);
        if(num == 1) res = true;
        return res;
    }

    @Override
    public Activity detail(String id) {
        Activity a = dao.getActivityById(id);
        return a;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String id) {
        return rdao.getRemarkListByAid(id);
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) {
        int res = rdao.saveRemark(ar);
        return res==1?true:false;
    }

    @Override
    public boolean deleteRemark(String id) {
        int res = rdao.deleteRemark(id);
        return res==1?true:false;
    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {
        int res = rdao.updateRemark(ar);
        return res==1?true:false;
    }

    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> aList = dao.getActivityListByClueId(clueId);
        return aList;
    }

    @Override
    public List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map) {
        return dao.getActivityListByNameAndNotByClueId(map);
    }

    @Override
    public List<Activity> getActivityListByName(String aname) {
        return dao.getActivityListByName(aname);
    }


}

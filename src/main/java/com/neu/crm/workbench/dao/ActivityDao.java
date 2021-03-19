package com.neu.crm.workbench.dao;

import com.neu.crm.vo.PaginationVO;
import com.neu.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityDao {
    //添加操作
    int save(Activity a);

    int getTotalByCondition(Map<String, Object> map);
    List<Activity> getActivityListByCondition(Map<String, Object> map);


    int delete(String[] ids);

    Activity getActivityById(String id);

    int update(Activity a);

    Activity detail(String id);

    List<Activity> getActivityListByClueId(String clueId);

    List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map);

    List<Activity> getActivityListByName(String aname);
}

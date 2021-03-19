package com.neu.crm.workbench.service;

import com.neu.crm.settings.dao.UserDao;
import com.neu.crm.utils.SqlSessionUtil;
import com.neu.crm.vo.PaginationVO;
import com.neu.crm.workbench.dao.ActivityDao;
import com.neu.crm.workbench.domain.Activity;
import com.neu.crm.workbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    //保存市场活动信息
    boolean save(Activity a);


    PaginationVO<Activity> pageList(Map<String, Object> map);

    boolean delete(String[] ids);

    Map<String, Object> getUserListAndActivity(String id);

    boolean update(Activity a);

    Activity detail(String id);

    List<ActivityRemark> getRemarkListByAid(String id);

    boolean saveRemark(ActivityRemark ar);

    boolean deleteRemark(String id);

    boolean updateRemark(ActivityRemark ar);

    List<Activity> getActivityListByClueId(String clueId);

    List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map);

    List<Activity> getActivityListByName(String aname);
}

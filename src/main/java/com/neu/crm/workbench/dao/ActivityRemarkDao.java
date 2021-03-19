package com.neu.crm.workbench.dao;


import com.neu.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {
    int getCountByAids(String[] ids);
    int deleteByAids(String[] ids);

    List<ActivityRemark> getRemarkListByAid(String id);

    int saveRemark(ActivityRemark ar);

    int deleteRemark(String id);

    int updateRemark(ActivityRemark ar);
}

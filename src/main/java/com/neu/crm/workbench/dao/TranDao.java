package com.neu.crm.workbench.dao;

import com.neu.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {

    Tran detail(String id);

    int save(Tran t);

    int getTotal();

    List<Map<String, Object>> getCharts();
}

package com.neu.crm.workbench.service;

import com.neu.crm.workbench.domain.Tran;
import com.neu.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

public interface TranService {

    boolean save(Tran t, String customerName);

    Tran detail(String id);

    List<TranHistory> getHistoryListByTranId(String tranId);

    Map<String, Object> getCharts();
}

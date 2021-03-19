package com.neu.crm.workbench.service.impl;

import com.neu.crm.utils.DateTimeUtil;
import com.neu.crm.utils.SqlSessionUtil;
import com.neu.crm.utils.UUIDUtil;
import com.neu.crm.workbench.dao.CustomerDao;
import com.neu.crm.workbench.dao.TranDao;
import com.neu.crm.workbench.dao.TranHistoryDao;
import com.neu.crm.workbench.domain.Customer;
import com.neu.crm.workbench.domain.Tran;
import com.neu.crm.workbench.domain.TranHistory;
import com.neu.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);


    @Override
    public boolean save(Tran t, String customerName) {
        boolean flag = true;
        Customer cus = customerDao.getCustomerByName(customerName);
        //如果该客户不存在就要加入该客户
        if(cus==null){
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setName(customerName);
            cus.setCreateBy(t.getCreateBy());
            cus.setCreateTime(DateTimeUtil.getSysTime());
            cus.setContactSummary(t.getContactSummary());
            cus.setNextContactTime(t.getNextContactTime());
            cus.setOwner(t.getOwner());
            //添加客户
            int count1 = customerDao.save(cus);
            if(count1!=1){
                flag = false;
            }
        }
        //然后开始添加交易记录
        //将客户id封装到t对象中
        t.setCustomerId(cus.getId());

        //添加交易记录
        int count2 = tranDao.save(t);
        if(count2!=1){
            flag = false;
        }

        //添加交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setCreateBy(t.getCreateBy());
        int count3 = tranHistoryDao.save(th);
        if(count3!=1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Tran detail(String id) {
        Tran t =tranDao.detail(id);
        return t;
    }

    @Override
    public List<TranHistory> getHistoryListByTranId(String tranId) {
        return tranHistoryDao.getHistoryListByTranId(tranId);
    }

    @Override
    public Map<String, Object> getCharts() {
        //获取交易条数
        int total = tranDao.getTotal();
        //取得dataList
        List<Map<String,Object>> dataList = tranDao.getCharts();
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("total", total);
        map.put("dataList", dataList);
        return map;
    }
}

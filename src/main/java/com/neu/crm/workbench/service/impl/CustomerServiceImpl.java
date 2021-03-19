package com.neu.crm.workbench.service.impl;

import com.neu.crm.utils.SqlSessionUtil;
import com.neu.crm.workbench.dao.CustomerDao;
import com.neu.crm.workbench.service.CustomerService;

import java.util.List;

/**
 * Author 北京动力节点
 */
public class CustomerServiceImpl implements CustomerService {

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    public List<String> getCustomerName(String name) {

        List<String> sList = customerDao.getCustomerName(name);
        return sList;
    }
}

















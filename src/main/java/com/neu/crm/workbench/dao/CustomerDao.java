package com.neu.crm.workbench.dao;

import com.neu.crm.workbench.domain.Customer;

import java.util.List;

public interface CustomerDao {

    Customer getCustomerByName(String name);

    int save(Customer cus);

    List<String> getCustomerName(String name);
}

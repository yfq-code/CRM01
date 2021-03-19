package com.neu;


import com.neu.crm.utils.PrintJson;
import com.neu.crm.utils.ServiceFactory;
import com.neu.crm.workbench.service.CustomerService;
import com.neu.crm.workbench.service.impl.CustomerServiceImpl;
import org.junit.Test;

import java.util.List;

public class test01 {
    @Test
    public void testCustomer(){
        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        String name = "e";
        List<String> cnameList = cs.getCustomerName(name);
        System.out.println(cnameList);
    }

}
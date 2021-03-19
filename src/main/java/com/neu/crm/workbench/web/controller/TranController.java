package com.neu.crm.workbench.web.controller;


import com.neu.crm.settings.domain.User;
import com.neu.crm.settings.service.UserService;
import com.neu.crm.settings.service.impl.UserServiceImpl;
import com.neu.crm.utils.DateTimeUtil;
import com.neu.crm.utils.PrintJson;
import com.neu.crm.utils.ServiceFactory;
import com.neu.crm.utils.UUIDUtil;
import com.neu.crm.workbench.domain.Tran;
import com.neu.crm.workbench.domain.TranHistory;
import com.neu.crm.workbench.service.CustomerService;
import com.neu.crm.workbench.service.TranService;
import com.neu.crm.workbench.service.impl.CustomerServiceImpl;
import com.neu.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("欢迎来到交易控制系统");
        //获取请求路径，就是你要干啥？
        String path = request.getServletPath();
        //然后进行判断，看到底是想干啥
        if ("/workbench/Tran/add.do".equals(path)) {
            add(request,response);
        }else if ("/workbench/Tran/getCustomerName.do".equals(path)) {
            getCustomerName(request,response);
        }else if ("/workbench/Tran/save.do".equals(path)) {
            save(request,response);
        }else if ("/workbench/Tran/detail.do".equals(path)) {
            detail(request,response);
        }else if ("/workbench/Tran/getHistoryListByTranId.do".equals(path)) {
            getHistoryListByTranId(request,response);
        }else if ("/workbench/Tran/getCharts.do".equals(path)) {
            getCharts(request,response);
        }
    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得交易阶段数量统计图表的数据");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        /*
            业务层为我们返回
                total
                dataList
                通过map打包以上两项进行返回
         */
        Map<String,Object> map = ts.getCharts();
        PrintJson.printJsonObj(response, map);
    }

    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("跳转到交易历史查询页");
        String tranId = request.getParameter("tranId");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> thList = ts.getHistoryListByTranId(tranId);
        //阶段和可能性之间的对应关系
        Map<String,String> pMap = (Map<String,String>)this.getServletContext().getAttribute("pMap");
        //将交易历史列表遍历
        for(TranHistory th : thList){
            //根据每条交易历史，取出每一个阶段
            String stage = th.getStage();
            String possibility = pMap.get(stage);
            th.setPossibility(possibility);
        }
        PrintJson.printJsonObj(response,thList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("跳转到详细信息页");
        String id = request.getParameter("id");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran t = ts.detail(id);

        String stage = t.getStage();
        Map<String,String> pMap = (Map<String,String>)this.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);


        t.setPossibility(possibility);

        request.setAttribute("t", t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request, response);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到保存交易记录的操作");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName"); //此处我们暂时只有客户名称，还没有id
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran t = new Tran();
        t.setId(id);
        t.setOwner(owner);
        t.setMoney(money);
        t.setName(name);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);
        t.setType(type);
        t.setSource(source);
        t.setActivityId(activityId);
        t.setContactsId(contactsId);
        t.setCreateTime(createTime);
        t.setCreateBy(createBy);
        t.setDescription(description);
        t.setContactSummary(contactSummary);
        t.setNextContactTime(nextContactTime);
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.save(t,customerName);

        if(flag){
            //如果添加交易成功，跳转到列表页
            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");
        }
    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到查询用户姓名的操作");
        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        String name = request.getParameter("name");
        List<String> cnameList = cs.getCustomerName(name);
        PrintJson.printJsonObj(response, cnameList);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转到交易添加页的操作");
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = us.getUserList();
        request.setAttribute("uList", uList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request, response);
    }

}
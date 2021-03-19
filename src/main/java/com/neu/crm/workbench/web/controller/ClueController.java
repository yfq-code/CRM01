package com.neu.crm.workbench.web.controller;

import com.neu.crm.settings.domain.User;
import com.neu.crm.settings.service.UserService;
import com.neu.crm.settings.service.impl.UserServiceImpl;
import com.neu.crm.utils.DateTimeUtil;
import com.neu.crm.utils.PrintJson;
import com.neu.crm.utils.ServiceFactory;
import com.neu.crm.utils.UUIDUtil;
import com.neu.crm.workbench.domain.Activity;
import com.neu.crm.workbench.domain.Clue;
import com.neu.crm.workbench.domain.Tran;
import com.neu.crm.workbench.service.ActivityService;
import com.neu.crm.workbench.service.ClueService;
import com.neu.crm.workbench.service.impl.ActivityServiceImpl;
import com.neu.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("欢迎来到线索控制系统");
        //获取请求路径，就是你要干啥？
        String path = request.getServletPath();
        //然后进行判断，看到底是想干啥
        if("/workbench/Clue/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if("/workbench/Clue/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/Clue/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/Clue/getActivityListByClueId.do".equals(path)){
            getActivityListByClueId(request,response);
        }else if("/workbench/Clue/unbund.do".equals(path)){
            unbund(request,response);
        }else if("/workbench/Clue/getActivityListByNameAndNotByClueId.do".equals(path)){
            getActivityListByNameAndNotByClueId(request,response);
        }else if("/workbench/Clue/bund.do".equals(path)){
            bund(request,response);
        }else if("/workbench/Clue/getActivityListByName.do".equals(path)){
            getActivityListByName(request,response);
        }else if("/workbench/Clue/convert.do".equals(path)){
            convert(request,response);
        }
    }

    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("执行线索转换的操作");
        String clueId = request.getParameter("clueId");
        //接收是否需要创建交易的标记
        String flag = request.getParameter("flag");
        //如果是通过表单传过来的，则标志位flag为a，表示需要建立交易
        Tran t = null;
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        //如果需要创建交易
        if("a".equals(flag)){
            t = new Tran();
            //接收交易表单中的参数
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();

            t.setId(id);
            t.setMoney(money);
            t.setName(name);
            t.setExpectedDate(expectedDate);
            t.setStage(stage);
            t.setActivityId(activityId);
            t.setCreateBy(createBy);
            t.setCreateTime(createTime);
        }

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        /*
            为业务层传递的参数：
            1.必须传递的参数clueId，有了这个clueId之后我们才知道要转换哪条记录
            2.必须传递的参数t，因为在线索转换的过程中，有可能会临时创建一笔交易（业务层接收的t也有可能是个null）
         */
        boolean flag1 = cs.convert(clueId,t,createBy);
        if(flag1){
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");
        }

    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("查询市场活动列表（根据名称模糊查）");
        String aname = request.getParameter("aname");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.getActivityListByName(aname);
        PrintJson.printJsonObj(response, aList);
    }

    private void bund(HttpServletRequest request, HttpServletResponse response) {
        String cid = request.getParameter("cid");
        String aids[] = request.getParameterValues("aid");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.bund(cid,aids);
        PrintJson.printJsonFlag(response, flag);
    }

    private void getActivityListByNameAndNotByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("查询市场活动列表（根据名称模糊查+排除掉已经关联指定线索的列表）");
        String aname = request.getParameter("aname");
        String clueId = request.getParameter("clueId");

        Map<String,String> map = new HashMap<String,String>();
        map.put("aname", aname);
        map.put("clueId", clueId);
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.getActivityListByNameAndNotByClueId(map);
        PrintJson.printJsonObj(response, aList);
    }

    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("解除市场线索关联");
        String Id = request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.unbund(Id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取线索关联的市场活动信息");
        String clueId = request.getParameter("clueId");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.getActivityListByClueId(clueId);
        PrintJson.printJsonObj(response, aList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("显示线索详细信息");
        String id= request.getParameter("id");
        System.out.println(id);
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue c = cs.detail(id);
        //这里不是用的ajax请求，直接转发就行了
        request.setAttribute("c", c);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request, response);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("保存线索信息");
        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Clue c = new Clue();
        c.setId(id);
        c.setAddress(address);
        c.setWebsite(website);
        c.setState(state);
        c.setSource(source);
        c.setPhone(phone);
        c.setOwner(owner);
        c.setNextContactTime(nextContactTime);
        c.setMphone(mphone);
        c.setJob(job);
        c.setFullname(fullname);
        c.setEmail(email);
        c.setDescription(description);
        c.setCreateTime(createTime);
        c.setCreateBy(createBy);
        c.setContactSummary(contactSummary);
        c.setCompany(company);
        c.setAppellation(appellation);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = cs.save(c);

        PrintJson.printJsonFlag(response, flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取用户列表");
        //是与用户相关的操作，要用UserService
        UserService service = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> ulist = service.getUserList();
        //以json格式返回
        PrintJson.printJsonObj(response,ulist);
    }


}
package com.neu.crm.workbench.web.controller;

import com.neu.crm.exceptions.loginException;
import com.neu.crm.settings.domain.User;
import com.neu.crm.settings.service.UserService;
import com.neu.crm.settings.service.impl.UserServiceImpl;
import com.neu.crm.utils.*;
import com.neu.crm.vo.PaginationVO;
import com.neu.crm.workbench.domain.Activity;
import com.neu.crm.workbench.domain.ActivityRemark;
import com.neu.crm.workbench.service.ActivityService;
import com.neu.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {
    //这里用的是模板方法，还没用到框架，这样能减少servlet的创建数量
    //判断你的请求来进行不同的处理
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("欢迎来到用户控制系统");
        //获取请求路径，就是你要干啥？
        String path = request.getServletPath();
        //然后进行判断，看到底是想干啥
        if("/workbench/Activity/getUserList.do".equals(path)){
            getUserList(request,response);
        }
        //如果是其他请求
        else if("/workbench/Activity/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/Activity/pageList.do".equals(path)){
            pageList(request,response);
        }else if("/workbench/Activity/delete.do".equals(path)){
            delete(request,response);
        }else if("/workbench/Activity/getUserListAndActivity.do".equals(path)){
            getUserListAndActivity(request,response);
        }else if("/workbench/Activity/update.do".equals(path)){
            update(request,response);
        }else if("/workbench/Activity/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/Activity/getRemarkListByAid.do".equals(path)){
            getRemarkListByAid(request,response);
        }else if("/workbench/Activity/saveRemark.do".equals(path)){
            saveRemark(request,response);
        }else if("/workbench/Activity/deleteRemark.do".equals(path)){
            deleteRemark(request,response);
        }else if("/workbench/Activity/updateRemark.do".equals(path)){
            updateRemark(request,response);
        }
    }



    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取用户列表");
        //是与用户相关的操作，要用UserService
        UserService service = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> ulist = service.getUserList();
        //以json格式返回
        PrintJson.printJsonObj(response,ulist);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("添加市场活动");
        String id = UUIDUtil.getUUID(); //用UUIDUtil工具类创造一个随机字符串
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        //活动创建时间：当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        //活动创建人：当前登录用户
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        //将以上参数放入一个activity对象中，然后当做参数进行保存
        Activity a = new Activity();
        a.setId(id);
        a.setCost(cost);
        a.setStartDate(startDate);
        a.setOwner(owner);
        a.setName(name);
        a.setEndDate(endDate);
        a.setDescription(description);
        a.setCreateTime(createTime);
        a.setCreateBy(createBy);

        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag =service.save(a);
        PrintJson.printJsonFlag(response,flag);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到查询市场活动信息列表的操作（结合条件查询+分页查询）");
        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String pageNoStr = request.getParameter("pageNo");
        int pageNo = Integer.valueOf(pageNoStr);
        //每页展现的记录数
        String pageSizeStr = request.getParameter("pageSize");
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数,这些就是不需要展示的
        int skipCount = (pageNo-1)*pageSize;

        //以上这些参数放入一个map中，然后用mybatis进行查询
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        //调用service层
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        /*前端想要拿到的是数据集合list 和 数据量 total
        * 有两种方式 1、返回一个map  包含两个元素 List<Activity> list 、 int total
        *          2、采用vo的方式,即返回一个对象，也包含以上两个元素，但是可以使用泛型的方式增加复用率
        * 采用 方法2
        * */
        PaginationVO<Activity> vo = service.pageList(map);
        System.out.println(vo);
        PrintJson.printJsonObj(response, vo);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行市场活动的删除操作");
        //获取请求参数，是一个id的数组
        String ids[] = request.getParameterValues("id");
        //调用service层，直接传进去,返回删除结果
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = service.delete(ids);
        PrintJson.printJsonFlag(response, flag);
    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到查询用户信息列表和根据市场活动id查询单条记录的操作");
        String id = request.getParameter("id");
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        //需要返回的是一个userList 和一个 Activity 对象 ，用一个map存起来
        Map<String,Object> map = service.getUserListAndActivity(id);
        PrintJson.printJsonObj(response, map);
    }

    private void update(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到更新市场活动的操作");
        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        //活动修改时间：当前系统时间
        String editTime = DateTimeUtil.getSysTime();
        //活动修改人：当前登录用户
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        //将以上参数放入一个activity对象中，然后当做参数进行保存
        Activity a = new Activity();
        a.setId(id);
        a.setCost(cost);
        a.setStartDate(startDate);
        a.setOwner(owner);
        a.setName(name);
        a.setEndDate(endDate);
        a.setDescription(description);
        a.setCreateTime(editTime);
        a.setCreateBy(editBy);

        //调用service
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = service.update(a);
        PrintJson.printJsonFlag(response,flag);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("欢迎进入市场信息查询页面");
        String id = request.getParameter("id");
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Activity a = service.detail(id);
        //放入请求作用域，然后转发
        request.setAttribute("a",a);
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);
    }

    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("activityId");
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<ActivityRemark> rList = service.getRemarkListByAid(id);
        PrintJson.printJsonObj(response,rList);
    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行添加备注操作");
        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");
        //生成一个主键
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "0";

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setActivityId(activityId);
        ar.setCreateBy(createBy);
        ar.setCreateTime(createTime);
        ar.setEditFlag(editFlag);

        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = service.saveRemark(ar);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("success", flag);
        map.put("ar", ar);

        PrintJson.printJsonObj(response, map);
    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行删除备注操作");
        String id = request.getParameter("id");
        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = service.deleteRemark(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行更新备注操作");
        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "1";

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditFlag(editFlag);
        ar.setEditBy(editBy);
        ar.setEditTime(editTime);

        ActivityService service = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = service.updateRemark(ar);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("success", flag);
        map.put("ar", ar);
        PrintJson.printJsonObj(response, map);
    }
}
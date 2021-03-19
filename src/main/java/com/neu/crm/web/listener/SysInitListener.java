package com.neu.crm.web.listener;

import com.neu.crm.settings.domain.DicValue;
import com.neu.crm.settings.service.DicService;
import com.neu.crm.settings.service.impl.DicServiceImpl;
import com.neu.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;


public class SysInitListener implements ServletContextListener {
    /*
        该方法是用来监听上下文域对象的方法，当服务器启动，上下文域对象创建
        对象创建完毕后，马上执行该方法，将数据字典存入全局作用域进行缓存

        event: 该参数能够取得监听的对象
               监听的是什么对象，就可以通过该参数能取得什么对象
               例如我们现在监听的是上下文域对象，通过该参数就可以取得上下文域对象
     */
    public void contextInitialized(ServletContextEvent event) {
        //System.out.println("上下文域对象创建了");
        System.out.println("服务器缓存处理数据字典开始");
        ServletContext application = event.getServletContext();
        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());

        /*
            应该管业务层要7个list
            可以打包成为一个map
            业务层应该是这样来保存数据的：
                map.put("appellationList",dvList1);
                map.put("clueStateList",dvList2);
                map.put("stageList",dvList3);
                ....
                ...
         */
        Map<String, List<DicValue>> map = ds.getAll();
        //将map解析为上下文域对象中保存的键值对
        Set<String> set = map.keySet();
        for(String key:set){
            application.setAttribute(key, map.get(key));
        }
        System.out.println("服务器缓存处理数据字典结束");


        Map<String,String> pMap = new HashMap<String,String>();

        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");

        Enumeration<String> e = rb.getKeys();

        while (e.hasMoreElements()){

            //阶段
            String key = e.nextElement();
            //可能性
            String value = rb.getString(key);

            pMap.put(key, value);


        }

        //将pMap保存到服务器缓存中
        application.setAttribute("pMap", pMap);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }


}

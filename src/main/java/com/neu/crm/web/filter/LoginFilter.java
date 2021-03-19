package com.neu.crm.web.filter;

import com.neu.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//这里的过滤器用来检测合法登录，除了登录页面之外的页面需要过滤
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        System.out.println("进入到验证有没有登录过的过滤器");
        //如果是合法登录，对话作用域中会有user对象
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String path = request.getServletPath();  //相对路径
        System.out.println(path);
        //不应该被拦截的资源，自动放行请求
        if("/login.jsp".equals(path) || "/settings/user/login.do".equals(path)){
            chain.doFilter(req, resp);
        }else{
            //获取user
            User user = (User) request.getSession().getAttribute("user");
            if(user != null){
                //放行
                chain.doFilter(req,resp);
            }else{
                //否则重定向到登录页面,用getContextPath得到项目的主路径
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }
        }
    }

    @Override
    public void destroy() {

    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
}

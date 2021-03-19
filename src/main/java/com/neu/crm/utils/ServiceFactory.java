package com.neu.crm.utils;

public class ServiceFactory {
	/*该方法用于返回对象的代理*/
	public static Object getService(Object service){
		
		return new TransactionInvocationHandler(service).getProxy();
		
	}
	
}

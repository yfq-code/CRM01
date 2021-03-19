package com.neu.crm.vo;

import java.util.List;

/**
 * 采用的是 泛型编程
 * */
public class PaginationVO<T>{
    private int total;
    private List<T> dataList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}

package com.donate.commmon.model;


import java.util.HashMap;
import java.util.Map;

public class Pagination {

    // totalPage
    private int totalPage=0;

    // curPageNO
    private int curPageNO=1;

    // pageSize
    private int pageSize = 10;

    // totalRecord
    private int totalRecord=0;

    // orderBy
    private String orderBy;

    // orderType
    private String orderType;

    // orderType
    private String queryFrom;// 主要是前台查询融资记录时有用到

    // conditions
    private Map<String, Object> conditions = new HashMap<String, Object>();

    // fromRecord
    private int fromRecord;

    // toRecord
    private int toRecord;

    public void toBePage() {
        this.setTotalPage((this.getTotalRecord() / this.getPageSize())
                + (this.getTotalRecord() % this.getPageSize() > 0 ? 1 : 0));
        this.setFromRecord(this.getPageSize() * this.getCurPageNO() - this.getPageSize());
        this.setToRecord(this.getPageSize());
    }

    public int getLastIndex() {
        if ((this.getToRecord() + this.getPageSize()) > this.getTotalRecord()) {
            return this.getTotalRecord() + 1;
        }
        return this.getToRecord() + this.getPageSize() + 1;
    }

    public void setLastIndex(int lastIndex) {
        int fromRecord = lastIndex - 1;
        if (fromRecord < 0) {
            this.fromRecord = 0;
        } else {
            this.fromRecord = fromRecord;
        }
    }

    public int getFromRecord() {
        return fromRecord;
    }

    public void setFromRecord(int fromRecord) {
        this.fromRecord = fromRecord;
    }

    public String getQueryFrom() {
        return queryFrom;
    }

    public void setQueryFrom(String queryFrom) {
        this.queryFrom = queryFrom;
    }

    public int getToRecord() {
        return toRecord;
    }

    public void setToRecord(int toRecord) {
        this.toRecord = toRecord;
    }

    public Pagination() {
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurPageNO() {
        return curPageNO;
    }

    public void setCurPageNO(Integer curPageNO) {
        this.curPageNO = curPageNO == null?1:curPageNO.intValue();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) { 
        this.pageSize = pageSize==null?20:pageSize.intValue();
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Map<String, Object> getConditions() {
        return conditions;
    }

    public void setConditions(Map<String, Object> conditions) {
        this.conditions = conditions;
    }

}
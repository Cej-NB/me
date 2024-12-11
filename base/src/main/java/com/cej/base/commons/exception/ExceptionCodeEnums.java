package com.cej.base.commons.exception;

public enum  ExceptionCodeEnums {

    DEFAULT_EXCEPTION(10000, "全局默认异常");

    private int code;
    private String des;

    ExceptionCodeEnums(int code,String des){
        this.code = code;
        this.des = des;
    }

    public int getCode() {
        return code;
    }

    public String getDes() {
        return des;
    }
}

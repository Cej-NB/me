package com.cej.commons.vo;

import java.io.Serializable;

public class ResResult implements Serializable {

    //返回编码
    private int code;
    //返回信息
    private String msg;
    //返回内容
    private Object data;

    public ResResult(){
        this.code = 0;
        this.msg = "成功";
    }

    public ResResult(String msg){
        this.code = 0;
        this.msg = msg;
    }

    public ResResult(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public ResResult(int code, String msg, Object data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public void setCode(int code){this.code = code;}
    public int getCode(){return this.code;}
    public void setMsg(){this.msg = msg;}
    public String getMsg(){return this.msg;}
    public void setData(Object data){
        this.data = data;
    }
    public Object getData(){
        return this.data;
    }

    public static ResResult fail(String msg){
        return new ResResult(-1,msg);
    }

    public static ResResult success(String msg){
        return new ResResult(msg);
    }

    public static ResResult success(){
        return new ResResult();
    }

    public static ResResult success(Object object){
        return new ResResult(0, "成功", object);
    }

    public String toString(){
        return "{code="+this.code+",msg="+this.msg+",data="+this.data+"}";
    }
}

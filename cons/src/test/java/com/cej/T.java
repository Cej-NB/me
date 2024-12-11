package com.cej;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;

public class T {
    public static void main(String[] args) {
        //1、初始化SecurityManagement
        IniSecurityManagerFactory factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        //2、获取Subject对象
        Subject subject = SecurityUtils.getSubject();
        //3、创建token对象
        AuthenticationToken token = new UsernamePasswordToken("root","123");
        //4、完成登录
        subject.login(token);
    }
}

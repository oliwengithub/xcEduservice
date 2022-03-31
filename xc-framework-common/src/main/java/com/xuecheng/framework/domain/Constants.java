package com.xuecheng.framework.domain;

/**
 *
 * @author: olw
 * @date: 2020/10/28 19:33
 * @description:  系统常量类
 */
public class Constants {

    /** 系统默认固定角色 **/

    /** 超级管理员 **/
    public static final String SYSTEM_ROLE_SUPER = "1";
    /** 管理员 **/
    public static final String SYSTEM_ROLE_ADMIN = "2";
    /** 讲师 **/
    public static final String SYSTEM_ROLE_TEACHER = "3";
    /** 机构联系人 **/
    public static final String SYSTEM_ROLE_COMPANY = "5";
    /** 普通用户 **/
    public static final String SYSTEM_ROLE_STUDENT = "4";

    /*** 系统用户状态 **/

    /** 正常 **/
    public static final String USER_STATUS_NORMAL = "103001";
    /** 暂停 **/
    public static final String USER_STATUS_SUSPEND = "103002";
    /** 注销 **/
    public static final String USER_STATUS_CANCEL = "103003";

    /** 系统用户类型 **/
    /** 系统管理员 **/
    public static final String USER_TYPE_SYSTEM = "101001";
    /** 教学管理员 **/
    public static final String USER_TYPE_TEACHER = "101002";
    /** 普通用户 **/
    public static final String USER_TYPE_STUDENT = "101003";

    /** 正常 **/
    public static final String SYSTEM_STATUS_NORMAL = "1";
    /** 失效 **/
    public static final String SYSTEM_STATUS_CANCEL = "0";
}

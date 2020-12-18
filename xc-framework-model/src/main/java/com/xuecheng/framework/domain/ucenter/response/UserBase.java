package com.xuecheng.framework.domain.ucenter.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 *
 * @author: olw
 * @date: 2020/12/13 20:17
 * @description:  用户基本信息对象
 */
@Data
@ToString
public class UserBase implements Serializable {
    private static final long serialVersionUID = -5689152670530015983L;

    private String name;
    private String userpic;
    private String utype;
    private String sex;

}

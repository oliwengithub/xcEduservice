package com.xuecheng.framework.domain.ucenter.request;

import com.xuecheng.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author: olw
 * @date: 2020/10/28 21:20
 * @description:  菜单查询模板
 */
@Data
@ToString
public class RequestMenuList extends RequestData {


    /**
     * 菜单名称
    */
    private String menuName;

    /**
     * 父级菜单名称
     */
    private String parentMenuName;

    /**
     * 是否菜单
     */
    private String isMenu;


}

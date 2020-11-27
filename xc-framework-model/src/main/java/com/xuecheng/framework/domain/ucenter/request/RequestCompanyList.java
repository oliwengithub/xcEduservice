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
public class RequestCompanyList extends RequestData {


    /**
     * 机构名称
    */
    private String name;


    /**
     * 联系人姓名
     */
    private String linkname;

    /**
     * 机构状态
     */
    private String status;

}

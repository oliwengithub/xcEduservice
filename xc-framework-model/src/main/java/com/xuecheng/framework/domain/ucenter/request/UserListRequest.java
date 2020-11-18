package com.xuecheng.framework.domain.ucenter.request;

import com.xuecheng.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;

/**
 * Created by mrt on 2018/4/13.
 */
@Data
@ToString
public class UserListRequest extends RequestData {

    private String name;

    private String phone;

    /**选择需要过滤的用户角色*/
    private String roleId;

    /**当前操作用户角色*/
    private String sysRoleId;
}

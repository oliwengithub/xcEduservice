package com.xuecheng.framework.domain.ucenter.ext;

import com.xuecheng.framework.domain.ucenter.XcUser;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserInfo extends XcUser {

    private String roleIds;
    private String companyId;
}

package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.framework.domain.ucenter.response.UcenterCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author: olw
 * @date: 2020/10/18 17:12
 * @description:  用户信息业务层
 */
@Service
public class UserService {


    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;

    @Autowired
    private XcUserRepository xcUserRepository;

    @Autowired
    private XcMenuMapper xcMenuMapper;

    /**
     * 获取用户信息
     * @author: olw
     * @Date: 2020/10/18 17:11
     * @param username
     * @returns: com.xuecheng.framework.domain.ucenter.ext.XcUserExt
    */
    public XcUserExt getUserExt (String username) {
        // 获取用户信息
        XcUser xcUser = findXcUserByUsername(username);
        if (xcUser == null) {
            // 这里不做异常处理 直接返回null
            // 因为认证服务通过远程调用获取用户信息进行比对
            // 异常信息在认证服务统一处理
            /* ExceptionCast.cast(UcenterCode.UCENTER_ACCOUNT_NOTEXISTS);*/
            return null;
        }

        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);
        String userId = xcUser.getId();
        // 获取用户组织信息（公司）
        XcCompanyUser xcCompanyUser = findByUserId(userId);
        if (xcCompanyUser != null) {
            xcUserExt.setCompanyId(xcCompanyUser.getCompanyId());
        }
        // 获取用户的权限
        List<XcMenu> menuList = xcMenuMapper.findMenuByUserId(userId);
        if (menuList != null && menuList.size() > 0) {
            xcUserExt.setPermissions(menuList);
        }
        return xcUserExt;

    }

    /**
     * 根据用户姓名获取信息
     * @author: olw
     * @Date: 2020/10/18 17:25
     * @param username
     * @returns: com.xuecheng.framework.domain.ucenter.XcUser
    */
    public XcUser findXcUserByUsername (String username) {
       return xcUserRepository.findXcUserByUsername(username);
    }

    /**
     * 根据用户id获取用户组织
     * @author: olw
     * @Date: 2020/10/18 17:34
     * @param userId
     * @returns: com.xuecheng.framework.domain.ucenter.XcCompanyUser
    */
    public XcCompanyUser findByUserId (String userId) {
        return xcCompanyUserRepository.findByUserId(userId);
    }

    public QueryResult<XcUser> findAll () {

        return new QueryResult<>();
    }

    public boolean add (XcUser user) {
        return true;
    }


    public boolean del (String userId) {
        return true;
    }

    public boolean update (XcUser user) {
        return true;
    }
}

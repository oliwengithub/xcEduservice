package com.xuecheng.ucenter.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.ext.UserInfo;
import com.xuecheng.framework.domain.ucenter.request.UserListRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface XcUserMapper {


    public Page<UserInfo> findUserListPage (UserListRequest userListRequest);
}

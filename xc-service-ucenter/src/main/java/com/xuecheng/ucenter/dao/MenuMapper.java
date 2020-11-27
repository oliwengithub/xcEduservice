package com.xuecheng.ucenter.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.ext.XcMenuNode;
import com.xuecheng.framework.domain.ucenter.request.RequestMenuList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

@Mapper
public interface MenuMapper {

    public List<XcMenu> findMenuByUserId (String userId);
    public Page<XcMenu> findMenuList (RequestMenuList requestMenuList);
    public List<XcMenuNode> findMenuTree (@Param("id") String id);
}

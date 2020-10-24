package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface XcMenuMapper  {

    public List<XcMenu> findMenuByUserId (String userId);
}

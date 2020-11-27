package com.xuecheng.ucenter;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.ucenter.ext.UserInfo;
import com.xuecheng.framework.domain.ucenter.ext.XcMenuNode;
import com.xuecheng.framework.domain.ucenter.request.UserListRequest;
import com.xuecheng.ucenter.dao.MenuMapper;
import com.xuecheng.ucenter.dao.XcUserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMapper {

    @Resource
    MenuMapper menuMapper;

    @Resource
    XcUserMapper xcUserMapper;

    @Test
    public void testMenuMapper () {
        //List<XcMenu> menuByUserId = menuMapper.findMenuByUserId("49");
        List<XcMenuNode> menuTree = menuMapper.findMenuTree(null);
        System.out.println(menuTree);
    }

    @Test
    public void testUserMapper () {
        UserListRequest userListRequest = new UserListRequest();
        userListRequest.setSysRoleId("1");
        Page<UserInfo> userListPage = xcUserMapper.findUserListPage(userListRequest);
        System.out.println(userListPage);
    }
}

package com.xuecheng.ucenter;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcPermission;
import com.xuecheng.ucenter.dao.XcMenuRepository;
import com.xuecheng.ucenter.dao.XcPermissionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDao {

    @Autowired
    XcMenuRepository xcMenuRepository;

    @Autowired
    XcPermissionRepository xcPermissionRepository;

    @Test
    public void testDao () {
        List<XcPermission> xcPermissions = xcPermissionRepository.findXcPermissionByRoleId("4028828275e513410175e51497480000");
        String menuIds = xcPermissions.stream().map(p -> p.getMenuId()).collect(Collectors.joining(","));
        System.out.println(menuIds);
    }

    @Test
    public void testDel () {
        List<XcPermission> xcPermissions = xcPermissionRepository.findXcPermissionByRoleId("4028828275e513410175e51497480000");
        xcPermissionRepository.deleteAll(xcPermissions);
        String menuIds = xcPermissions.stream().map(p -> p.getMenuId()).collect(Collectors.joining(","));
        System.out.println(menuIds);
    }
}

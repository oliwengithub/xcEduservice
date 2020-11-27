package com.xuecheng.ucenter;

import com.xuecheng.framework.domain.ucenter.XcPermission;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.ucenter.dao.XcPermissionRepository;
import com.xuecheng.ucenter.service.XcRoleService;
import net.bytebuddy.asm.Advice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestService {

    @Resource
    XcRoleService roleService;

    @Autowired
    XcPermissionRepository xcPermissionRepository;

    @Test
    public void testRoleService () {
        String roleId = "4028828275e513410175e51497480000";
        String menuIds = "894477851082883072";
        String[] menuIdsString = menuIds.split(",");
        List<String> menuIdsAdd = Arrays.asList(menuIdsString);

        List<XcPermission> permissionsAdd = menuIdsAdd.stream().map(s -> {
            XcPermission xcPermission = new XcPermission();
            xcPermission.setRoleId(roleId);
            xcPermission.setMenuId(s);
            xcPermission.setCreateTime(new Date());
            return xcPermission;
        }).collect(Collectors.toList());

        /*List<XcPermission> permissionsAdd = new ArrayList<>();
        for (String s : menuIdsAdd) {
            XcPermission xcPermission = new XcPermission();
            xcPermission.setRoleId(roleId);
            xcPermission.setMenuId(s);
            xcPermission.setCreateTime(new Date());
            permissionsAdd.add(xcPermission);
        }*/
        List<XcPermission> xcPermissions = xcPermissionRepository.saveAll(permissionsAdd);
        System.out.println(xcPermissions);

    }
}

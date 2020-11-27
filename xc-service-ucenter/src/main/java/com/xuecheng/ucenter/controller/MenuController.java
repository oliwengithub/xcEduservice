package com.xuecheng.ucenter.controller;

import com.xuecheng.api.ucenter.MenuControllerApi;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.ext.XcMenuNode;
import com.xuecheng.framework.domain.ucenter.request.RequestMenuList;
import com.xuecheng.framework.domain.ucenter.response.MenuResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.ucenter.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author: olw
 * @date: 2020/10/28 21:11
 * @description:  菜单
 */
@RestController
@RequestMapping("/ucenter/menu")
public class MenuController implements MenuControllerApi {

    @Autowired
    MenuService menuService;

    //@PreAuthorize("hasAuthority('find_all_menu')")
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findAllMenu (@PathVariable("page") int page, @PathVariable("size") int size, RequestMenuList requestMenuList) {
        return menuService.findMenuList(page, size, requestMenuList);
    }

    //@PreAuthorize("hasAuthority('add_menu')")
    @Override
    @PostMapping("/add")
    public ResponseResult add (@RequestBody XcMenu xcMenu) {
        return menuService.add(xcMenu);
    }

    //@PreAuthorize("hasAuthority('get_menu_info')")
    @Override
    @GetMapping("/get/{menuId}")
    public MenuResult getMenuInfo (@PathVariable("menuId") String menuId) {
        return menuService.findMenuById(menuId);
    }

    //@PreAuthorize("hasAuthority('eidt_menu')")
    @Override
    @PutMapping("/edit")
    public ResponseResult edit (@RequestBody XcMenu xcMenu) {
        return menuService.update(xcMenu);
    }

    //@PreAuthorize("hasAuthority('del_menu')")
    @Override
    @PostMapping("/del/{menuId}")
    public ResponseResult del (@PathVariable("menuId") String menuId) {
        return menuService.del(menuId);
    }

    //@PreAuthorize("hasAuthority('find_menu_tree')")
    @Override
    @GetMapping("/tree")
    public List<XcMenuNode> findMenuTree (String menuId) {
        return menuService.findMenuTree(menuId);
    }
}

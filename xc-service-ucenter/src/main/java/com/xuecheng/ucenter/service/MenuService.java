package com.xuecheng.ucenter.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.ext.XcMenuNode;
import com.xuecheng.framework.domain.ucenter.request.RequestMenuList;
import com.xuecheng.framework.domain.ucenter.response.MenuCode;
import com.xuecheng.framework.domain.ucenter.response.MenuResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.ucenter.dao.MenuMapper;
import com.xuecheng.ucenter.dao.XcMenuRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author: olw
 * @date: 2020/10/21 18:49
 * @description:  角色业务层
 */
@Service
public class MenuService {

    @Autowired
    XcMenuRepository xcMenuRepository;

    @Resource
    MenuMapper menuMapper;

    public ResponseResult add (XcMenu xcMenu) {
        XcMenu xcMenuByCode = xcMenuRepository.findXcMenuByCode(xcMenu.getCode());
        if (xcMenuByCode != null) {
            ExceptionCast.cast(MenuCode.MENU_CODE_EXIST);
        }
        xcMenu.setUpdateTime(new Date());
        xcMenu.setCreateTime(new Date());
        xcMenu.setStatus("1");
        xcMenuRepository.save(xcMenu);
        return new ResponseResult(CommonCode.SUCCESS);
    }


    public ResponseResult del (String menuId) {
        Optional<XcMenu> optional = xcMenuRepository.findById(menuId);
        if (optional.isPresent()) {
            XcMenu xcMenu = optional.get();
            // 0无效，1有效
            xcMenu.setStatus("0");
            return this.update(xcMenu);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    public ResponseResult update (XcMenu xcMenu) {
        Optional<XcMenu> optional = xcMenuRepository.findById(xcMenu.getId());
        if (!optional.isPresent()) {
            // 需要更新的菜单不存在
            ExceptionCast.cast(MenuCode.MENU_CODE_NOTEXIST);
        }
        XcMenu one = optional.get();
        xcMenu.setCreateTime(one.getCreateTime());
        xcMenu.setUpdateTime(new Date());
        xcMenuRepository.save(xcMenu);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 获取菜单列表
     * @author: olw
     * @Date: 2020/11/16 20:33
     * @param page
     * @param size
     * @param requestMenuList
     * @returns: com.xuecheng.framework.model.response.QueryResponseResult
    */
    public QueryResponseResult findMenuList (int page, int size, RequestMenuList requestMenuList) {
        if (requestMenuList == null) {
            requestMenuList = new RequestMenuList();
        }
        page = page <= 0 ? 1 : page;
        size = size <= 0 ? 10 : size;
        PageHelper.startPage(page, size);
        Page<XcMenu> menuList = menuMapper.findMenuList(requestMenuList);
        QueryResult<XcMenu> queryResult = new QueryResult<>();
        queryResult.setList(menuList.getResult());
        queryResult.setTotal(menuList.getTotal());
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }


    public MenuResult findMenuById (String menuId) {
        Optional<XcMenu> optional = xcMenuRepository.findById(menuId);
        if (optional.isPresent()) {
            return new MenuResult(CommonCode.SUCCESS, optional.get());
        }
        return new MenuResult(MenuCode.MENU_CODE_NOTEXIST, null);

    }

    /**
     * 获取菜单树
     * @author: olw
     * @Date: 2020/11/17 19:55
     * @param menuId
     * @returns: java.util.List<com.xuecheng.framework.domain.ucenter.ext.XcMenuNode>
    */
    public List<XcMenuNode> findMenuTree (String menuId) {
        if (StringUtils.isEmpty(menuId)) {
            // 防止sql异常 “” ！= null  暂时这样处理
            menuId = null;
        }
        return menuMapper.findMenuTree(menuId);
    }
}

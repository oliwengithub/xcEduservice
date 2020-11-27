package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.ext.XcMenuNode;
import com.xuecheng.framework.domain.ucenter.request.RequestMenuList;
import com.xuecheng.framework.domain.ucenter.response.MenuResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api(value = "菜单管理")
public interface MenuControllerApi {

    @ApiOperation("获取所有菜单列表")
    public QueryResponseResult findAllMenu (int page, int size, RequestMenuList requestMenuList);

    @ApiOperation("添加菜单")
    public ResponseResult add (XcMenu xcMenu);

    @ApiOperation("获取菜单")
    public MenuResult getMenuInfo (String menu);

    @ApiOperation("更新菜单")
    public ResponseResult  edit (XcMenu xcMenu);

    @ApiOperation("删除菜单")
    public ResponseResult del (@PathVariable("menuId") String menuId);

    @ApiOperation("获取树形菜单")
    public List<XcMenuNode> findMenuTree (String menuId);
}

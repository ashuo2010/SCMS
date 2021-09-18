package com.ashuo.scms.controller;


import com.ashuo.scms.common.lang.ServerResponse;
import com.ashuo.scms.dto.MenuDto;
import com.ashuo.scms.entity.Mainmenu;
import com.ashuo.scms.entity.Submenu;
import com.ashuo.scms.service.MainmenuService;
import com.ashuo.scms.service.SubmenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author AShuo
 * @since 2021-04-05
 */
@Api(tags = "菜单接口")
@RestController
public class MenuController {
    @Autowired
    MainmenuService mainmenuService;
    @Autowired
    SubmenuService submenuService;

    @ApiOperation("获取所有一级二级菜单")
    @GetMapping("/menu")
    public ServerResponse getMenu() {
        // 主菜单
        List<Mainmenu> main = mainmenuService.list();

        // 子菜单
        List<Submenu> sub = submenuService.list();
        if (main == null || sub == null) {
            return ServerResponse.createByErrorCodeMessage(400, "获取菜单列表失败");
        }
        // 将子菜单按类别添加到主菜单
        Map<Integer, List<Submenu>> map = new HashMap<>();
        for (Submenu s : sub) {
            if (!map.containsKey(s.getMid())) {
                map.put(s.getMid(), new ArrayList<>());
            }
            map.get(s.getMid()).add(s);
        }
        // 封装菜单对象
        List<MenuDto> list = new ArrayList<>();
        for (Mainmenu m : main) {
            list.add(new MenuDto(m, map.get(m.getMainmenuId())));
        }
        return ServerResponse.createBySuccess(list);
    }
}

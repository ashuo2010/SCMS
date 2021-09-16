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
@Api(tags = "负菜单接口")
@RestController
public class SubmenuController {

}

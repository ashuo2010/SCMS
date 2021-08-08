package com.ashuo.scms.controller;


import com.ashuo.scms.common.lang.ServerResponse;
import com.ashuo.scms.entity.Item;
import com.ashuo.scms.entity.QueryInfo;
import com.ashuo.scms.service.ItemService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author AShuo
 * @since 2021-04-01
 */
@Api(tags = "运动项目接口")
@RestController
@Slf4j
@RequestMapping("/item")
public class ItemController {
    @Autowired
    ItemService itemService;

    @ApiOperation("查询项目")
    @GetMapping("/queryItem")
    @RequiresAuthentication
    public Object queryItem(QueryInfo queryInfo) {

        if (StringUtils.isBlank(queryInfo.getQuery())) {
            queryInfo.setQuery(null);
        }
        //分页查询
        Page<Item> page = new Page<Item>(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        IPage<Item> itemList = itemService.getAllItem(page, queryInfo.getQuery());
        return ServerResponse.createBySuccess(itemList);
    }


    @ApiOperation("添加项目")
    @PostMapping("/addItem")
    @RequiresRoles(value = {"1"})
    public Object addItem(@RequestBody Item item) {

        if (item == null) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败，Item信息为空");
        }
        //通过Item名称和性别判断是否已存在相同Item
        Item tempItem = new Item();
        tempItem.setItemName(item.getItemName());
        tempItem.setItemSex(item.getItemSex());
        if (itemService.getItemByCondition(tempItem) != null) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败，Item已存在");
        }
        //设置创建时间
        item.setCreateTime(LocalDateTime.now());
        item.setEditTime(LocalDateTime.now());
        int effNum = 0;
        try {
            effNum = itemService.addItem(item);
        } catch (Exception e) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败");
        }
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败");
        }
        return ServerResponse.createBySuccessMessage("添加成功");
    }

    @ApiOperation("删除项目")
    @DeleteMapping("/deleteItem")
    @RequiresRoles(value = {"1"})
    public Object deleteItem(Integer itemId) {
        int effNum = 0;
        try {
            effNum = itemService.removeItem(itemId);
        } catch (Exception e) {
            return ServerResponse.createByErrorCodeMessage(400, "删除失败");
        }
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "删除失败");
        }
        return ServerResponse.createBySuccessMessage("删除成功");
    }


    @ApiOperation("获取项目详情信息")
    @GetMapping("/getItem")
    @RequiresRoles(value = {"1"})
    public Object getItem(Item itemCondition) {

        Item item = itemService.getItemByCondition(itemCondition);
        if (item != null) {
            return ServerResponse.createBySuccess(item);
        }
        return ServerResponse.createByErrorMessage("查询不到该Item信息");
    }

    @ApiOperation("修改项目")
    @PutMapping("/editItem")
    @RequiresRoles(value = {"1"})
    public Object editItem(@RequestBody Item item) {
        if (item == null || item.getItemName() == null) {
            return ServerResponse.createByErrorCodeMessage(400, "修改失败，Item信息为空");
        }
        if (item.equals(itemService.getItemByCondition(item))) {
            return ServerResponse.createByErrorCodeMessage(400, "修改失败，Item已存在");
        }

        item.setEditTime(LocalDateTime.now());
        int effNum = 0;
        try {
            effNum = itemService.modifyItem(item);
        } catch (Exception e) {
            return ServerResponse.createByErrorCodeMessage(400, "修改失败");
        }
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "修改失败");
        }
        return ServerResponse.createBySuccessMessage("修改成功");
    }
}

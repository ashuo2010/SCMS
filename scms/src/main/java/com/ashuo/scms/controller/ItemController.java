package com.ashuo.scms.controller;


import com.ashuo.scms.common.lang.ServerResponse;
import com.ashuo.scms.entity.Item;
import com.ashuo.scms.entity.QueryInfo;
import com.ashuo.scms.entity.Season;
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
import java.util.List;

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
    public ServerResponse queryItem(QueryInfo queryInfo, Item item) {

        if (StringUtils.isBlank(queryInfo.getQuery())) {
            queryInfo.setQuery(null);
        }
        //如果输入框有内容,则根据内容搜索
        item.setItemName(queryInfo.getQuery());
        Page<Item> page = new Page(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        IPage<Item> itemList = itemService.getItemByItemCondition(page, item);
        return ServerResponse.createBySuccess(itemList);
    }


    @ApiOperation("添加项目")
    @PostMapping("/addItem")
    @RequiresRoles(value = {"1"})
    public ServerResponse addItem(@RequestBody Item item) {

        if (item == null) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败，项目信息为空");
        }
        //通过seasonId、parentId和性别判断是否已存在相同Item
        Item tempItem = new Item();
        tempItem.setParentId(item.getParentId());
        tempItem.setItemSex(item.getItemSex());
        if (item.getSeason() != null && item.getSeason().getSeasonId() != null && item.getSeason().getSeasonId() != 0) {
            Season season = item.getSeason();
            tempItem.setSeason(season);
        }
        if (itemService.getOneItemByCondition(tempItem) != null) {
            return ServerResponse.createByErrorCodeMessage(400, "添加失败，改项目已存在");
        }
        Item itemTemplate = itemService.getItemTemplateDetail(item);

        //设置报名人数
        item.setAthleteAmount(0);
        //根据模板设置item名称等信息
        if (itemTemplate != null) {
            item.setItemName(itemTemplate.getItemName());
            item.setItemUnit(itemTemplate.getItemUnit());
            item.setItemAmount(itemTemplate.getItemAmount());
            item.setAthleteAmount(null);
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
    public ServerResponse deleteItem(Integer itemId) {
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
    public ServerResponse getItem(Item itemCondition) {

        Item item = itemService.getOneItemByCondition(itemCondition);
        if (item != null) {
            return ServerResponse.createBySuccess(item);
        }
        return ServerResponse.createByErrorMessage("查询不到该项目信息");
    }

    @ApiOperation("修改项目")
    @PutMapping("/editItem")
    @RequiresRoles(value = {"1"})
    public ServerResponse editItem(@RequestBody Item item) {
        if (item == null || item.getItemName() == null) {
            return ServerResponse.createByErrorCodeMessage(400, "修改失败，项目信息为空");
        }

        //通过Item名称和性别判断是否已存在相同Item
        Item tempItem = new Item();
        tempItem.setItemName(item.getItemName());
        tempItem.setItemSex(item.getItemSex());
        //如果不是添加模板，则需要加上seasonId来区分是否会和之前届的冲突
        if (item.getSeason() != null && item.getSeason().getSeasonId() != null&& item.getSeason().getSeasonId() != 0 ) {
            Season season = item.getSeason();
            tempItem.setSeason(season);
        }
        if (item.equals(itemService.getOneItemByCondition(tempItem))) {
            return ServerResponse.createByErrorCodeMessage(400, "修改失败，项目已存在");
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

    @ApiOperation("查询项目模板")
    @GetMapping("/queryItemTemplate")
    @RequiresRoles(value = {"1"})
    public ServerResponse queryItemTemplate() {
        List<Item> itemList = itemService.getItemTemplateList();
        return ServerResponse.createBySuccess(itemList);
    }

    @ApiOperation("获取项目模板详情信息")
    @GetMapping("/getItemTemplate")
    @RequiresRoles(value = {"1"})
    public ServerResponse getItemTemplate(Item itemCondition) {
        Item item = itemService.getItemTemplateDetail(itemCondition);
        if (item != null) {
            return ServerResponse.createBySuccess(item);
        }
        return ServerResponse.createByErrorMessage("查询不到该项目信息");
    }
    @ApiOperation("删除项目模板")
    @DeleteMapping("/deleteItemTemplate")
    @RequiresRoles(value = {"1"})
    public ServerResponse deleteItemTemplate(Item item) {
        Item tempItem=new Item();
        tempItem.setParentId(item.getItemId());
        IPage<Item> itemList = itemService.getItemByItemCondition(new Page(1, 9999), tempItem);
        if (itemList!=null && itemList.getRecords()!=null&&itemList.getRecords().size()!=0){
            return ServerResponse.createByErrorCodeMessage(400, "删除失败,该模板下有已举行的项目");
        }
        int effNum = 0;
        try {
            effNum = itemService.removeItem(item.getItemId());
        } catch (Exception e) {
            return ServerResponse.createByErrorCodeMessage(400, "删除失败");
        }
        if (effNum == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "删除失败");
        }
        return ServerResponse.createBySuccessMessage("删除成功");
    }
}

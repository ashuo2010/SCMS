package com.ashuo.scms.mapper;

import com.ashuo.scms.entity.Item;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author AShuo
 * @since 2021-04-01
 */
public interface ItemMapper {
    //增加Item
    int insertItem(Item item);

    //修改Item
    int updateItem(@Param("item") Item item);

    //删除Item
    int deleteItem(int itemId);

    //按条件查询Item
    IPage<Item> queryItemByItemName(Page<Item> page, @Param("itemName") String itemName);

    //获取单个Item信息
    Item queryItemByItemCondition(@Param("item") Item item);

}

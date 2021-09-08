package com.ashuo.scms.service;

import com.ashuo.scms.entity.Item;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author AShuo
 * @since 2021-04-01
 */
public interface ItemService {
    IPage<Item> getItemByItemCondition(Page<Item> page, Item item);

    Item getOneItemByCondition(Item item);

    int addItem(Item item);

    int modifyItem(Item item);

    int removeItem(int itemId);


}

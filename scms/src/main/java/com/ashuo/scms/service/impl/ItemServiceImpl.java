package com.ashuo.scms.service.impl;

import com.ashuo.scms.entity.Item;
import com.ashuo.scms.mapper.ItemMapper;
import com.ashuo.scms.service.ItemService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author AShuo
 * @since 2021-04-01
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemMapper itemMapper;

    @Override
    public IPage<Item> getAllItem(Page<Item> page, String itemName) {
        IPage<Item> itemList = itemMapper.queryItemByItemName(page, itemName);
        return itemList;
    }


    @Override
    public Item getItemByCondition(Item itemCondition) {
        if (itemCondition == null) {
            return null;
        }
        Item item = itemMapper.queryItemByItemCondition(itemCondition);
        return item;
    }


    @Override
    public int addItem(Item item) {
        if (item == null) {
            return 0;
        } else {
            int effNum = itemMapper.insertItem(item);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

    @Override
    public int modifyItem(Item item) {
        if (item == null) {
            return 0;
        } else {
            int effNum = itemMapper.updateItem(item);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

    @Override
    public int removeItem(int itemId) {
        if (itemId == 0) {
            return 0;
        } else {
            int effNum = itemMapper.deleteItem(itemId);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

}

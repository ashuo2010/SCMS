package com.ashuo.scms.service;

import com.ashuo.scms.entity.Record;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author AShuo
 * @since 2021-09-10
 */
public interface RecordService extends IService<Record> {

    IPage<Record> getRecordByRecordCondition(Page<Record> page, Record record);

    int addRecord(Record record);

    int modifyRecord(Record record);

}

package com.ashuo.scms.service.impl;

import com.ashuo.scms.entity.Record;
import com.ashuo.scms.mapper.RecordMapper;
import com.ashuo.scms.service.RecordService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author AShuo
 * @since 2021-09-10
 */
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
    @Autowired
    RecordMapper recordMapper;

    @Override
    public IPage<Record> getRecordByRecordCondition(Page<Record> page, Record record) {
        if (record == null) {
            return null;
        }
        IPage<Record> recordList = recordMapper.queryRecordByRecordCondition(page, record);
        return recordList;
    }

    @Override
    public int addRecord(Record record) {
        if (record == null) {
            return 0;
        } else {
            int effNum = recordMapper.insertRecord(record);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

    @Override
    public int modifyRecord(Record record) {
        if (record == null) {
            return 0;
        } else {
            int effNum = recordMapper.updateRecord(record);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }


}

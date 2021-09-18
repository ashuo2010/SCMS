package com.ashuo.scms.mapper;

import com.ashuo.scms.entity.Record;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author AShuo
 * @since 2021-09-10
 */
public interface RecordMapper extends BaseMapper<Record> {
    //增加单个Record
    int insertRecord(Record record);

    //修改Record
    int updateRecord(Record record);

    //按条件查询Record
    IPage<Record> queryRecordByRecordCondition(Page<Record> page, @Param("record") Record record);


}

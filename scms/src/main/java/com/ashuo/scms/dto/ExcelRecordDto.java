package com.ashuo.scms.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * 个人项目总分排名
 *
 * @author AShuo
 * @since 2021-04-05
 */
@Data
public class ExcelRecordDto implements Serializable {
    @ExcelProperty(value = "运动会", index = 0)
    private String seasonName;

    @ExcelProperty(value = "项目名称", index = 1)
    private String itemName;

    @ExcelProperty(value = "项目成绩", index = 2)
    private BigDecimal recordScore;

    private String itemUnit;

    @ExcelProperty(value = "运动员", index = 3)
    private String nickname;

    @ExcelProperty(value = "团体", index = 4)
    private String teamName;

    @ExcelProperty(value = "记录创建时间", index = 5)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    public ExcelRecordDto(String seasonName, String itemName, BigDecimal recordScore, String itemUnit, String nickname, String teamName, LocalDateTime createTime) {
        this.seasonName = seasonName;
        this.itemName = itemName;
        this.recordScore = recordScore;
        this.itemUnit = itemUnit;
        this.nickname = nickname;
        this.teamName = teamName;
        this.createTime = createTime;
    }
}

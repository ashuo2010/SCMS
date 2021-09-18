package com.ashuo.scms.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 所有项目成绩
 *
 * @author AShuo
 * @since 2021-04-05
 */
@Data
@NoArgsConstructor
public class AthleteScoreDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "运动会")
    private String seasonName;

    @ExcelProperty(value = "团体名称")
    private String teamName;

    @ExcelProperty(value = "学号")
    private String userNo;

    @ExcelProperty(value = "姓名")
    private String nickname;

    @ExcelProperty(value = "性别")
    private String userSex;

    @ExcelProperty(value = "参赛项目")
    private String itemName;

    @ExcelProperty(value = "项目分数")
    private BigDecimal score;

    @ExcelProperty(value = "项目单位")
    private String itemUnit;

    @ExcelProperty(value = "项目是否破纪录")
    private String isBreakRecord;

    @ExcelProperty(value = "记分员")
    private String scorer;

    @ExcelProperty(value = "分数最后修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date editTime;

}

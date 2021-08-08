package com.ashuo.scms.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 项目下的运动员
 *
 * @author AShuo
 * @since 2021-04-05
 */
@Data
@NoArgsConstructor
public class ItemAthleteDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "班级")
    private String teamName;

    @ExcelProperty(value = "学号")
    private String userNo;

    @ExcelProperty(value = "姓名")
    private String nickname;

    @ExcelProperty(value = "性别")
    private String userSex;

    @ExcelProperty(value = "参赛项目")
    private String itemName;

    @ExcelProperty(value = "项目地点")
    private String itemPlace;

    @ExcelProperty(value = "记分员")
    private String scorer;

}

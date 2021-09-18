package com.ashuo.scms.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 个人项目总分排名
 *
 * @author AShuo
 * @since 2021-04-05
 */
@Data
public class ExcelPersonRankingDto implements Serializable {
    @ExcelProperty(value = "团体名称", index = 0)
    private String teamName;
    @ExcelProperty(value = "学号", index = 1)
    private String userNo;
    @ExcelProperty(value = "姓名", index = 0)
    private String userName;
    @ExcelProperty(value = "性别", index = 0)
    private String userSex;
    @ExcelProperty(value = "个人总分数", index = 0)
    private Integer rank;

    public ExcelPersonRankingDto(String teamName, String userNo, String userName, String userSex, Integer rank) {
        this.teamName = teamName;
        this.userNo = userNo;
        this.userName = userName;
        this.userSex = userSex;
        this.rank = rank;
    }
}

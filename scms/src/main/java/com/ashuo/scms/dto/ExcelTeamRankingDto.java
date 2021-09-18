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
public class ExcelTeamRankingDto implements Serializable {
    @ExcelProperty(value = "团体名称", index = 0)
    private String teamName;
    @ExcelProperty(value = "团体总得分", index = 1)
    private Integer score;
    @ExcelProperty(value = "排名", index = 2)
    private Integer rank;

    public ExcelTeamRankingDto(String teamName, Integer score, Integer rank) {
        this.teamName = teamName;
        this.score = score;
        this.rank = rank;
    }
}

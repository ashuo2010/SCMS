package com.ashuo.scms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author AShuo
 * @since 2021-09-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Season implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "season_id", type = IdType.AUTO)
    private Integer seasonId;

    private String seasonName;

    private String seasonTopicDesc;

    private LocalDateTime seasonBeginTime;

    private LocalDateTime seasonEndTime;

    private String seasonStatus;

    private Integer seasonAthleteAmount;

    private LocalDateTime createTime;

    private LocalDateTime editTime;


}

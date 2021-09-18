package com.ashuo.scms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author AShuo
 * @since 2021-04-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel("菜单信息")
public class Mainmenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "mainmenu_id", type = IdType.AUTO)
    private Integer mainmenuId;

    private String title;

    private String path;

    private Integer type;

}

package com.ashuo.scms.service;

import com.ashuo.scms.entity.Athlete;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author AShuo
 * @since 2021-04-05
 */
public interface AthleteService {

    IPage<Athlete> getAthleteByCondition(Page<Athlete> page, Athlete athlete);

    int addAthlete(Athlete athlete);

    int modifyAthlete(Athlete athlete);

    int removeAthlete(int athleteId);


}
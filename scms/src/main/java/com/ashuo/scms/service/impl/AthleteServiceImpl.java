package com.ashuo.scms.service.impl;

import com.ashuo.scms.dto.AthleteScoreDto;
import com.ashuo.scms.entity.Athlete;
import com.ashuo.scms.entity.Item;
import com.ashuo.scms.mapper.AthleteMapper;
import com.ashuo.scms.mapper.ItemMapper;
import com.ashuo.scms.service.AthleteService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author AShuo
 * @since 2021-04-05
 */
@Service
public class AthleteServiceImpl implements AthleteService {

    @Autowired
    AthleteMapper athleteMapper;

    @Autowired
    ItemMapper itemMapper;

    @Override
    public IPage<Athlete> getAthleteByCondition(Page<Athlete> page, Athlete athleteCondition) {
        if (athleteCondition == null) {
            return null;
        }
        IPage<Athlete> athleteList = athleteMapper.queryAthleteByAthleteCondition(page, athleteCondition);
        return athleteList;
    }


    @Override
    public int addAthlete(Athlete athlete) {
        if (athlete == null) {
            return 0;
        } else {
            int effNum = athleteMapper.insertAthlete(athlete);
            //将对应项目报名数量增加一
            Item item = itemMapper.queryItemByItemCondition(athlete.getItem());
            item.setAthleteAmount(item.getAthleteAmount() + 1);
            int effNum2 = itemMapper.updateItem(item);
            if (effNum != 1 || effNum2 != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

    @Override
    public int modifyAthlete(Athlete athlete) {
        if (athlete == null) {
            return 0;
        } else {
            int effNum = athleteMapper.updateAthlete(athlete);
            if (effNum != 1) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

    @Override
    public int removeAthlete(int athleteId) {
        if (athleteId == 0) {
            return 0;
        } else {
            Athlete athlete = new Athlete();
            athlete.setAthleteId(athleteId);
            Page<Athlete> page = new Page<>(1, 1);
            IPage<Athlete> athleteList = athleteMapper.queryAthleteByAthleteCondition(page, athlete);
            athlete = athleteList.getRecords().get(0);
            Item item = itemMapper.queryItemByItemCondition(athlete.getItem());
            //将对应项目报名数量增减一
            item.setAthleteAmount(item.getAthleteAmount() - 1);
            int effNum = itemMapper.updateItem(item);
            int effNum2 = athleteMapper.deleteAthlete(athleteId);
            if (effNum <= 0 || effNum2 <= 0) {
                return 0;
            } else {
                return effNum;
            }
        }
    }

    @Override
    public List<AthleteScoreDto> getAllPersonScore() {
        return athleteMapper.queryAllPersonScore();
    }
}

package com.ashuo.scms.task;

import com.ashuo.scms.entity.Season;
import com.ashuo.scms.mapper.SeasonMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author AShuo
 * @date 2021年09月17日 16:02
 */
@Service
public class TimeTask {
    @Autowired
    private SeasonMapper seasonMapper;

    //0 0/1 * * * ? 每分钟执行一次
    @Scheduled(cron = "0 0/1 * * * ?")
    public void autoCloseSeason() {
        IPage seasonByCondition = seasonMapper.querySeasonBySeasonCondition(new Page(1, 500), new Season());
        System.out.println("执行定时任务+：" + LocalDateTime.now());
        List<Season> seasonList = seasonByCondition.getRecords();
        if (seasonList != null && seasonList.size() > 0) {
            //将状态为开启中且已经结束已经过去的运动会状态结束
            seasonList.stream().filter(o -> o.getSeasonStatus() == 1 && o.getSeasonEndTime().isBefore(LocalDateTime.now())).forEach(s -> {
                s.setSeasonStatus(0);
                seasonMapper.updateSeason(s);
            });

        }

    }
}
package com.ashuo.scms.controller;

import com.ashuo.scms.common.lang.ServerResponse;
import com.ashuo.scms.dto.AthleteScoreDto;
import com.ashuo.scms.entity.*;
import com.ashuo.scms.service.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author AShuo
 * @since 2021-04-05
 */
@Api(tags = "项目分数接口")
@RestController
@Slf4j
@RequestMapping("/score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private RankingService rankingService;

    @Autowired
    private AthleteService athleteService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private RecordService recordService;

    @ApiOperation("查询运动员项目分数")
    @GetMapping("/queryScore")
    @RequiresAuthentication
    public Object queryScore(QueryInfo queryInfo, Score score) {

        if (StringUtils.isBlank(queryInfo.getQuery())) {
            queryInfo.setQuery(null);
        }
        //分页查询
        Page<Score> page = new Page<Score>(queryInfo.getCurrentPage(), queryInfo.getPageSize());

        IPage<Score> scoreList = scoreService.getScoreByScoreCondition(page, score);
        return ServerResponse.createBySuccess(scoreList);
    }

    @ApiOperation("查询团体项目分数")
    @GetMapping("/queryTeamScore")
    @RequiresAuthentication
    public Object queryTeamScore(QueryInfo queryInfo, int teamId) {

        if (StringUtils.isBlank(queryInfo.getQuery())) {
            queryInfo.setQuery(null);
        }
        //分页查询
        Page<Score> page = new Page<Score>(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        IPage<Score> scoreList = scoreService.getScoreByTeamId(page, teamId);
        return ServerResponse.createBySuccess(scoreList);
    }


    //用于返回所有运动员的分数，可用于导出Excel
    @ApiOperation("项目下所有运动员的分数")
    @GetMapping("/queryAthleteScore")
    @RequiresAuthentication
    public Object queryAthleteScore(QueryInfo queryInfo, Score score) {

        if (StringUtils.isBlank(queryInfo.getQuery())) {
            queryInfo.setQuery(null);
        }
        User user = new User();
        user.setNickname(queryInfo.getQuery());
        if (score == null) {
            score = new Score();
        }
        score.setUser(user);
        //分页查询
        Page<AthleteScoreDto> page = new Page<AthleteScoreDto>(queryInfo.getCurrentPage(), queryInfo.getPageSize());
        IPage<AthleteScoreDto> scoreList = scoreService.getAthleteScoreDto(page, score);
        return ServerResponse.createBySuccess(scoreList);
    }

    @Transactional
    @ApiOperation("添加分数")
    @PostMapping("/addScore")
    @RequiresRoles(value = {"2"})
    public Object addScore(@RequestBody Score score) {
        try {
            if (score == null) {
                return ServerResponse.createByErrorCodeMessage(400, "添加失败，Score信息为空");
            }
            Item item = itemService.getOneItemByCondition(score.getItem());
            if (item != null && item.getSeason() != null && "0".equals(item.getSeason().getSeasonStatus())) {
                return ServerResponse.createByErrorCodeMessage(400, "分数录入失败，该届运动会已结束");
            }
            //分数纪录处理
            scoreRecordHandle(score);

            //设置创建、修改时间
            score.setCreateTime(LocalDateTime.now());
            score.setEditTime(LocalDateTime.now());
            //添加成绩
            scoreService.addScore(score);

            //设置1,表示已记分
            Athlete athlete = new Athlete();
            athlete.setItem(score.getItem());
            athlete.setUser(score.getUser());
            athlete.setStatus(1);
            athleteService.modifyAthlete(athlete);

            //分数排名处理
            scoreRankingHandle(score);

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("添加分数失败");
        }
        return ServerResponse.createBySuccessMessage("添加成功");

    }


    @ApiOperation("获取分数详情")
    @GetMapping("/getScore")
    @RequiresAuthentication
    public Object getScore(int scoreId) {

        Score score = scoreService.getOneScoreByScoreId(scoreId);
        if (score != null) {
            return ServerResponse.createBySuccess(score);
        }
        return ServerResponse.createByErrorMessage("查询不到该Score信息");
    }


    @Transactional
    @ApiOperation("修改分数")
    @PutMapping("/editScore")
    @RequiresRoles(value = {"2"})
    public Object editScore(@RequestBody Score score) {
        if (score == null || score.getScore() == 0) {
            return ServerResponse.createByErrorCodeMessage(400, "修改失败，Score信息为空");
        }

        Item item = itemService.getOneItemByCondition(score.getItem());
        if (item != null && item.getSeason() != null && "0".equals(item.getSeason().getSeasonStatus())) {
            return ServerResponse.createByErrorCodeMessage(400, "分数修改失败，该届运动会已结束");
        }
        //分数纪录处理
        scoreRecordHandle(score);
        //设置修改时间
        score.setEditTime(LocalDateTime.now());
        //修改分数
        scoreService.modifyScore(score);

        //分数排名处理
        scoreRankingHandle(score);

        return ServerResponse.createBySuccessMessage("修改成功");

    }

    private void scoreRecordHandle(Score score) {
        //查询该项目成绩是否破纪录
        Record record = new Record();
        //根据itemId获取parentId
        record.setItem(itemService.getOneItemByCondition(score.getItem()));
        List<Record> recordList = recordService.getRecordByRecordCondition(new Page<Record>(1, 999), record).getRecords();
        //设置分数数据
        record.setUser(score.getUser());
        record.setRecordScore(score.getScore());
        //记录生效
        record.setRecordStatus("1");
        record.setCreateTime(LocalDateTime.now());
        record.setEditTime(LocalDateTime.now());
        //如果该项目存在记录
        if (recordList != null && recordList.size() > 0) {
            //获取该项目之前记录
            Record itemRecord = recordList.get(0);
            //如果分数破纪录或持平记录
            if (score.getScore() >= itemRecord.getRecordScore()) {

                //如果分数破纪录，如果和记录持平则直接添加
                if (score.getScore() > itemRecord.getRecordScore()) {
                    //使之前记录失效
                    recordList.stream().forEach(r -> {
                        r.setRecordStatus("0");
                        recordService.modifyRecord(r);
                    });

                }
                //保存录入的分数记录
                recordService.addRecord(record);
                score.setIsBreakRecord("1");
            } else {
                //如果没有破纪录或者和记录持平
                score.setIsBreakRecord("0");
            }
        }else {
            recordService.addRecord(record);
            score.setIsBreakRecord("1");
        }
    }

    private void scoreRankingHandle(Score score) {
        //删除原ranking中对应item的排名数据
        rankingService.removeRanking(score.getItem().getItemId());

        //获取分数单位
        score = scoreService.getOneScoreByScoreId(score.getScoreId());

        //获取前三，并重新计算排名
        //判断分数单位，如果单位为秒，则ASC升序排列
        String condition = "DESC";
        if ("秒".equals(score.getItem().getItemUnit())) {
            condition = "ASC";
        }
        List<Score> scoreList = scoreService.getScoreByItemIdLimit(score.getItem().getItemId(), condition);
        //添加到Ranking对象中
        List<Ranking> rankingList = new ArrayList<>();

        int i = 3;
        for (Score s : scoreList) {
            Ranking ranking = new Ranking();
            ranking.setItem(s.getItem());
            ranking.setUser(s.getUser());
            ranking.setTeam(s.getUser().getTeam());
            //设置排名得分3、2、1
            ranking.setRank(i);
            ranking.setEditTime(LocalDateTime.now());
            rankingList.add(ranking);
            i--;
        }
        rankingService.addRanking(rankingList);
    }
}



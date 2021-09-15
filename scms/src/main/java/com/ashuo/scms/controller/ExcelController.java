package com.ashuo.scms.controller;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.excel.EasyExcel;
import com.ashuo.scms.common.lang.ServerResponse;
import com.ashuo.scms.dto.AthleteScoreDto;
import com.ashuo.scms.dto.ExcelPersonRankingDto;
import com.ashuo.scms.dto.ExcelTeamRankingDto;
import com.ashuo.scms.dto.ExcelUserDto;
import com.ashuo.scms.entity.*;
import com.ashuo.scms.service.*;
import com.ashuo.scms.util.DateFormatterUtil;
import com.ashuo.scms.util.ModelExcelListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "Excel接口")
@RestController
@Slf4j
@RequestMapping("/excel")
public class ExcelController {
    @Autowired
    TeamService teamService;

    @Autowired
    UserService userService;

    @Autowired
    RankingService rankingService;

    @Autowired
    ScoreService scoreService;

    @Autowired
    AthleteService athleteService;

    @Autowired
    HttpServletResponse response;

    @ApiOperation("导出团体总分排名")
    @GetMapping("/exportTeamRanking")
    @RequiresAuthentication
    public void exportTeamRanking() throws Exception {
        //设置主体风格
        XSSFWorkbook workbook = new XSSFWorkbook();
        String[] columnNames = {"班级名称", "班级总得分", "排名"};
        Sheet sheet = workbook.createSheet();
        Font titleFont = workbook.createFont();
        titleFont.setFontName("simsun");
        titleFont.setBold(true);
        titleFont.setColor(IndexedColors.BLACK.index);
        XSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFillForegroundColor(IndexedColors.LIME.index);
        titleStyle.setFont(titleFont);
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < columnNames.length; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(columnNames[i]);
            cell.setCellStyle(titleStyle);
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 15 / 10);
        }

        //设置数据
        Page<Ranking> page = new Page<>();
        IPage<Ranking> pageRankingList = rankingService.getTeamTotalRanking(page, null);
        List<Ranking> rankingList = pageRankingList.getRecords();
        List<ExcelTeamRankingDto> excelTeamRankingDtoList = new ArrayList<>();
        int i = 1;
        for (Ranking r : rankingList) {
            excelTeamRankingDtoList.add(new ExcelTeamRankingDto(r.getAthlete().getUser().getTeam().getTeamName(), r.getRank(), i));
            i++;
        }

        //设置内容表格格式
        XSSFCellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //创建数据行并写入值
        for (int j = 0; j < excelTeamRankingDtoList.size(); j++) {
            ExcelTeamRankingDto excelTeamRankingDto = excelTeamRankingDtoList.get(j);
            int lastRowNum = sheet.getLastRowNum();
            Row dataRow = sheet.createRow(lastRowNum + 1);
            dataRow.createCell(0).setCellValue(excelTeamRankingDto.getTeamName());
            dataRow.getCell(0).setCellStyle(contentStyle);
            dataRow.createCell(1).setCellValue(excelTeamRankingDto.getScore());
            dataRow.getCell(1).setCellStyle(contentStyle);
            dataRow.createCell(2).setCellValue(excelTeamRankingDto.getRank());
            dataRow.getCell(2).setCellStyle(contentStyle);

        }
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode("团体总分排名.xlsx", "utf-8"));
        response.setHeader("Access-Control-Expose-Headers", "content-Disposition");
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @ApiOperation("导出个人总分排名")
    @GetMapping("/exportPersonRanking")
    @RequiresAuthentication
    public void exportPersonRanking() throws Exception {
        //设置主体风格
        XSSFWorkbook workbook = new XSSFWorkbook();
        String[] columnNames = {"班级名称", "运动员学号", "姓名", "性别", "个人总分数"};
        Sheet sheet = workbook.createSheet();
        Font titleFont = workbook.createFont();
        titleFont.setFontName("simsun");
        titleFont.setBold(true);
        titleFont.setColor(IndexedColors.BLACK.index);
        XSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFillForegroundColor(IndexedColors.LIME.index);
        titleStyle.setFont(titleFont);
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < columnNames.length; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(columnNames[i]);
            cell.setCellStyle(titleStyle);
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 15 / 10);
        }

        //设置数据
        Page<Ranking> page = new Page<>();
        IPage<Ranking> pageRankingList = rankingService.getUserTotalRanking(page, new Ranking());
        List<Ranking> rankingList = pageRankingList.getRecords();
        List<ExcelPersonRankingDto> excelPersonRankingDtoList = new ArrayList<>();
        for (Ranking r : rankingList) {
            excelPersonRankingDtoList.add(new ExcelPersonRankingDto(r.getAthlete().getUser().getTeam().getTeamName(), r.getAthlete().getUser().getUserNo(), r.getAthlete().getUser().getNickname(), r.getAthlete().getUser().getUserSex(), r.getRank()));
        }

        //设置内容表格格式
        XSSFCellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //创建数据行并写入值
        for (int j = 0; j < excelPersonRankingDtoList.size(); j++) {
            ExcelPersonRankingDto excelPersonRankingDto = excelPersonRankingDtoList.get(j);
            int lastRowNum = sheet.getLastRowNum();
            Row dataRow = sheet.createRow(lastRowNum + 1);
            dataRow.createCell(0).setCellValue(excelPersonRankingDto.getTeamName());
            dataRow.getCell(0).setCellStyle(contentStyle);
            dataRow.createCell(1).setCellValue(excelPersonRankingDto.getUserNo());
            dataRow.getCell(1).setCellStyle(contentStyle);
            dataRow.createCell(2).setCellValue(excelPersonRankingDto.getUserName());
            dataRow.getCell(2).setCellStyle(contentStyle);
            dataRow.createCell(3).setCellValue(excelPersonRankingDto.getUserSex());
            dataRow.getCell(3).setCellStyle(contentStyle);
            dataRow.createCell(4).setCellValue(excelPersonRankingDto.getRank());
            dataRow.getCell(4).setCellStyle(contentStyle);
        }
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode("个人总分排名.xlsx", "utf-8"));
        response.setHeader("Access-Control-Expose-Headers", "content-Disposition");
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @ApiOperation("/导出所有运动员成绩")
    @GetMapping("exportAllPersonScore")
    @RequiresAuthentication
    public void exportAllPersonScore() throws Exception {
        //设置主体风格
        XSSFWorkbook workbook = new XSSFWorkbook();
        String[] columnNames = {"运动员班级", "运动员学号", "姓名", "性别", "参赛项目", "项目地点", "项目分数", "分数单位", "记分员", "分数最后修改时间"};
        Sheet sheet = workbook.createSheet();
        Font titleFont = workbook.createFont();
        titleFont.setFontName("simsun");
        titleFont.setBold(true);
        titleFont.setColor(IndexedColors.BLACK.index);
        XSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFillForegroundColor(IndexedColors.LIME.index);
        titleStyle.setFont(titleFont);
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < columnNames.length; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(columnNames[i]);
            cell.setCellStyle(titleStyle);
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 15 / 10);
        }

        //获取数据
        Page<AthleteScoreDto> page = new Page<AthleteScoreDto>(1, 999999);
        IPage<AthleteScoreDto> dtoList = scoreService.getAthleteScoreDto(page,  new Score());
        List<AthleteScoreDto> athleteScoreDtoList = dtoList.getRecords();

        //设置内容表格格式
        XSSFCellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);


        //创建数据行并写入值
        for (AthleteScoreDto as : athleteScoreDtoList) {
            int lastRowNum = sheet.getLastRowNum();
            Row dataRow = sheet.createRow(lastRowNum + 1);
            if (as.getScore() == null) {
                as.setScore("未计分");
            }
            dataRow.createCell(0).setCellValue(as.getSeasonName());
            dataRow.getCell(0).setCellStyle(contentStyle);
            dataRow.createCell(1).setCellValue(as.getTeamName());
            dataRow.getCell(1).setCellStyle(contentStyle);
            dataRow.createCell(2).setCellValue(as.getUserNo());
            dataRow.getCell(2).setCellStyle(contentStyle);
            dataRow.createCell(3).setCellValue(as.getNickname());
            dataRow.getCell(3).setCellStyle(contentStyle);
            dataRow.createCell(4).setCellValue(as.getUserSex());
            dataRow.getCell(4).setCellStyle(contentStyle);
            dataRow.createCell(5).setCellValue(as.getItemName());
            dataRow.getCell(5).setCellStyle(contentStyle);
            dataRow.createCell(6).setCellValue(as.getScore());
            dataRow.getCell(6).setCellStyle(contentStyle);
            dataRow.createCell(7).setCellValue(as.getIsBreakRecord());
            dataRow.getCell(7).setCellStyle(contentStyle);
            dataRow.createCell(8).setCellValue(as.getScorer());
            dataRow.getCell(8).setCellStyle(contentStyle);
            dataRow.createCell(9).setCellValue(DateFormatterUtil.dateToString(as.getEditTime()));
            dataRow.getCell(9).setCellStyle(contentStyle);
        }


        response.setContentType("application/vnd.ms-excel");
        response.setHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode("运动员成绩.xlsx", "utf-8"));
        response.setHeader("Access-Control-Expose-Headers", "content-Disposition");
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();

    }

    @ApiOperation("/导出指定项目运动员")
    @GetMapping("exportItemAthlete")
    @RequiresRoles(logical = Logical.OR, value = {"1", "2"})
    public void exportItemAthlete(Athlete athlete) throws Exception {
        //设置主体风格
        XSSFWorkbook workbook = new XSSFWorkbook();
        String[] columnNames = {"序号", "参赛项目", "运动员班级", "运动员学号", "姓名", "性别", "项目地点", "记分员"};
        Sheet sheet = workbook.createSheet();
        Font titleFont = workbook.createFont();
        titleFont.setFontName("simsun");
        titleFont.setBold(true);
        titleFont.setColor(IndexedColors.BLACK.index);
        XSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFillForegroundColor(IndexedColors.LIME.index);
        titleStyle.setFont(titleFont);
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < columnNames.length; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(columnNames[i]);
            cell.setCellStyle(titleStyle);
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 15 / 10);
        }

        //获取数据
        Page<Athlete> page = new Page<Athlete>(1, 999999);
        IPage<Athlete> dtoList = athleteService.getAthleteByCondition(page, athlete);
        List<Athlete> AthleteList = dtoList.getRecords();

        //设置内容表格格式
        XSSFCellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //文件名
        String fileName = "";
        int i = 0;
        for (Athlete as : AthleteList) {
            int lastRowNum = sheet.getLastRowNum();
            Row dataRow = sheet.createRow(lastRowNum + 1);
            dataRow.createCell(0).setCellValue(++i);
            dataRow.getCell(0).setCellStyle(contentStyle);
            dataRow.createCell(1).setCellValue(as.getItem().getItemName());
            dataRow.getCell(1).setCellStyle(contentStyle);
            dataRow.createCell(2).setCellValue(as.getUser().getTeam().getTeamName());
            dataRow.getCell(2).setCellStyle(contentStyle);
            dataRow.createCell(3).setCellValue(as.getUser().getUserNo());
            dataRow.getCell(3).setCellStyle(contentStyle);
            dataRow.createCell(4).setCellValue(as.getUser().getNickname());
            dataRow.getCell(4).setCellStyle(contentStyle);
            dataRow.createCell(5).setCellValue(as.getUser().getUserSex());
            dataRow.getCell(5).setCellStyle(contentStyle);
            dataRow.createCell(6).setCellValue(as.getItem().getItemPlace());
            dataRow.getCell(6).setCellStyle(contentStyle);
            dataRow.createCell(7).setCellValue(as.getItem().getUser().getNickname());
            dataRow.getCell(7).setCellStyle(contentStyle);
            fileName = as.getItem().getItemName() + "(" + as.getItem().getItemSex() + ")";
        }
        if (athlete.getItem().getItemId() == 0) {
            fileName = "所有";
        }
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + "参赛运动员.xlsx", "utf-8"));
        response.setHeader("Access-Control-Expose-Headers", "content-Disposition");
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();

    }

    @ApiOperation("/下载批量添加用户文件模板")
    @GetMapping("exportExcelTemplate")
    @RequiresRoles(value = {"1"})
    public void exportExcelTemplate() throws Exception {
        //设置主体风格
        XSSFWorkbook workbook = new XSSFWorkbook();
        String[] columnNames = {"班级名称", "运动员学号", "姓名", "性别", "账号", "密码", "用户类型(管理员、记分员、运动员)", "电话"};
        Sheet sheet = workbook.createSheet();
        Font titleFont = workbook.createFont();
        titleFont.setFontName("simsun");
        titleFont.setBold(true);
        titleFont.setColor(IndexedColors.BLACK.index);
        XSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFillForegroundColor(IndexedColors.LIME.index);
        titleStyle.setFont(titleFont);
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < columnNames.length; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(columnNames[i]);
            cell.setCellStyle(titleStyle);
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 15 / 10);
        }

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode("批量添加用户文件模板.xlsx", "utf-8"));
        response.setHeader("Access-Control-Expose-Headers", "content-Disposition");
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();

    }

    @Transactional
    @ApiOperation("/批量添加用户")
    @PostMapping("importUser")
    @RequiresRoles(value = {"1"})
    public ServerResponse importUser(@RequestParam("file") MultipartFile file) throws IOException {
        List<ExcelUserDto> userDtoList = EasyExcel.read(file.getInputStream(), ExcelUserDto.class, new ModelExcelListener()).sheet().doReadSync();
        for (ExcelUserDto u : userDtoList) {
            // team添加时检查去重（teamName）
            Team team = new Team();
            User user = new User();
            team.setTeamName(u.getTeamName());
            Team tempTeam = teamService.getTeamByCondition(team);
            if (tempTeam == null) {
                teamService.addTeam(team);
                user.setTeam(team);
            } else {
                user.setTeam(tempTeam);
            }
            //  user去重(userNo)
            user.setUserNo(u.getUserNo());

            if (userService.getOne(new QueryWrapper<User>().eq("user_no", user.getUserNo())) != null) {
                continue;
            }

            user.setNickname(u.getNickname());
            user.setUsername(u.getUsername());
            user.setPassword(SecureUtil.md5(u.getPassword()));
            user.setUserSex(u.getUserSex());
            user.setPhone(u.getPhone());
            user.setCreateTime(LocalDateTime.now());
            user.setEditTime(LocalDateTime.now());

            if ("管理员".equals(u.getUserType())) {
                user.setUserType(1);
            } else if ("记分员".equals(u.getUserType())) {
                user.setUserType(2);
            } else {
                user.setUserType(3);
            }
            userService.addUser(user);
        }
        return ServerResponse.createBySuccess(userDtoList);
    }

}

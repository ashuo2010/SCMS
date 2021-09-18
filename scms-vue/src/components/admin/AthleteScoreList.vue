<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>参赛成绩管理</el-breadcrumb-item>
      <el-breadcrumb-item>项目成绩信息</el-breadcrumb-item>
    </el-breadcrumb>

    <!--参数运动员列表主体-->
    <el-card>
      <!--搜索区域-->
      <el-row :gutter="25">
        <el-col :span="5">
          <!--搜索添加-->
          <el-input
              v-model="queryInfo.query"
              clearable
              placeholder="请输入运动员姓名"
              @clear="page"
              @keyup.enter.native="page"
          >
            <!--搜索按钮-->
            <el-button
                slot="append"
                icon="el-icon-search"
                @click="page"
            ></el-button>
          </el-input>
        </el-col>

        <div style="float: left">
          <el-col>
            <el-select
                v-model="selectSeasonId"
                filterable
                placeholder="请选择运动会"
                @change="page(true)"
            >
              <el-option
                  v-for="item in allSeasonOptions"
                  :key="item.seasonId"
                  :label="item.seasonName"
                  :value="item.seasonId"
              >
              </el-option>
            </el-select>
          </el-col>
        </div>

        <div style="float: left">
          <el-col>
            <el-select
                v-model="selectItemId"
                filterable
                placeholder="请选择项目"
                @change="page(true)"
            >
              <el-option
                  v-for="item in itemList"
                  :key="item.itemId"
                  :label="item.itemName"
                  :value="item.itemId"
              >
              </el-option>
            </el-select>
          </el-col>
        </div>
        <el-col :span="4">
          <el-button type="primary" @click="exportExcel()"
          >导出成绩列表
          </el-button
          >
        </el-col>
      </el-row>
      <!--参数运动员列表 stripe隔行变色-->
      <el-table :data="athleteScoreList" border stripe>
        <!--索引列-->

        <el-table-column type="index"></el-table-column>
        <el-table-column label="运动会" prop="seasonName"></el-table-column>
        <el-table-column label="团体名称" prop="teamName"></el-table-column>
        <el-table-column label="学号" prop="userNo"></el-table-column>
        <el-table-column label="姓名" prop="nickname"></el-table-column>
        <el-table-column label="性别" prop="userSex"></el-table-column>
        <el-table-column label="参赛项目" prop="itemName"></el-table-column>
        <el-table-column label="项目分数" prop="score"></el-table-column>
        <el-table-column label="项目是否破纪录" prop="isBreakRecord"></el-table-column>
        <el-table-column label="记分员" prop="scorer"></el-table-column>
        <el-table-column
            label="分数最后修改时间"
            prop="editTime"
        ></el-table-column>
      </el-table>
      <!--分页组件-->
      <div>
        <el-pagination
            :current-page="queryInfo.currentPage"
            :page-size="queryInfo.pageSize"
            :page-sizes="[5, 10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        >
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "AthleteList",
  data() {
    return {
      //成绩列表
      athleteScoreList: [],
      //项目列表
      itemList: [],
      //选择的项目
      selectItemId: "",

      //所有运动会届时列表
      allSeasonOptions: [],
      //选择的届时
      selectSeasonId: "",
      queryInfo: {
        currentPage: 1,
        pageSize: 10,
        query: "",
      },
      total: 0,
    };
  },
  created() {
    this.getSeasons();
  },
  methods: {
    async page(isSelect) {
      if (isSelect === true) {
        this.queryInfo.currentPage = 1;
        this.queryInfo.pageSize = 10;
      }
      const _this = this;
      axios
          .get("/score/queryAthleteScore?athlete.item.season.seasonId=" + _this.selectSeasonId
              + "&athlete.item.itemId=" + _this.selectItemId + "&queryInfo=", {params: _this.queryInfo})
          .then((res) => {
            let data = res.data.data;
            _this.athleteScoreList = data.records;
            _this.athleteScoreList.forEach((item, index) => {
              //分数加上单位
              if (
                  item.itemUnit == "秒" &&
                  item.score > 60
              ) {
                //如果分数为秒，且分数大于60秒，转成分钟显示
                let minute = parseInt(item.score / 60);
                let second = parseInt(item.score % 60);
                item.score = minute + "分" + second + "秒";
              } else {
                item.score += item.itemUnit;
              }


              if (item.isBreakRecord == 1) {
                item.isBreakRecord = "是";
              } else {
                item.isBreakRecord = "否";
              }
            })

            _this.queryInfo.currentPage = data.current;
            _this.total = data.total;
            _this.queryInfo.pageSize = data.size;
          });
    },
//获取运动会届时
    async getSeasons() {
      const _this = this;
      axios
          .get("/season/querySeason?query=&currentPage=1&pageSize=999999999")
          .then((res) => {
            let data = res.data.data.records;
            data.push({
              seasonId: 0,
              seasonStatus: 0,
              seasonName: "所有运动会",
            })
            data.forEach((item, index) => {
              if (item.seasonStatus != 0) {
                _this.selectSeasonId = item.seasonId
              }
            })
            _this.allSeasonOptions = data;
            _this.page();
            _this.getItems();
          });
    },


    //获取项目
    async getItems() {
      const _this = this;
      axios
          .get("/item/queryItem?query=&currentPage=1&pageSize=999999999&season.seasonId=" + _this.selectSeasonId
          )
          .then((res) => {
            let data = res.data.data.records;
            data.push({
              itemId: 0,
              itemName: "所有项目",
            })
            _this.itemList = data;

          });
    },


    async exportExcel() {
      const _this = this;
      const confirmResult = await _this
          .$confirm("确定导出成绩吗？", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          })
          .catch((err) => err);
      if (confirmResult !== "confirm") {
        return;
      }
      axios
          .get("/excel/exportAllPersonScore?athlete.item.season.seasonId=" + _this.selectSeasonId, {
            responseType: "blob", //二进制流
          })
          .then((res) => {
            const filename = res.headers["content-disposition"];
            let blob = new Blob([res.data], {type: "application/vnd.ms-excel"});
            let url = window.URL.createObjectURL(blob);
            const link = document.createElement("a"); // 创建a标签
            link.href = url;
            link.download = decodeURIComponent(filename.split("filename=")[1]); // 重命名文件
            link.click();
            URL.revokeObjectURL(url);
          });
    },

    handleSizeChange(newSize) {
      const _this = this;
      _this.queryInfo.pageSize = newSize;
      _this.page();
    },
    handleCurrentChange(newPage) {
      const _this = this;
      _this.queryInfo.currentPage = newPage;
      _this.page();
    },
  },
};
</script>

<style lang="less" scoped>
.el-breadcrumb {
  margin-bottom: 15px;
  font-size: 17px;
}

.myTable {
  border-collapse: collapse;
  margin: 0 auto;
  text-align: center;
}

.myTable td,
.myTable th {
  border: 1px solid #cad9ea;
  color: #666;
  height: 40px;
}
</style>
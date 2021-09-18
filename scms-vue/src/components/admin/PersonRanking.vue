<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>总分排名管理</el-breadcrumb-item>
      <el-breadcrumb-item>个人总分排名</el-breadcrumb-item>
    </el-breadcrumb>

    <!--项目列表主体-->
    <el-card>
      <!--搜索区域-->
      <el-row :gutter="25">
        <el-col :span="5">
          <!--搜索添加-->
          <el-input
              v-model="queryInfo.query"
              clearable
              placeholder="请输入运动员名称"
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

        <el-col :span="4">
          <el-button type="primary" @click="exportExcel()"
          >导出排名列表
          </el-button
          >
        </el-col>
      </el-row>
      <!--项目列表 stripe隔行变色-->
      <el-table :data="rankingList" border stripe>
        <!--索引列-->

        <el-table-column type="index"></el-table-column>
        <el-table-column label="团体名称" prop="athlete.user.team.teamName"></el-table-column>
        <el-table-column label="运动员" prop="athlete.user.nickname"></el-table-column>
        <el-table-column label="学号" prop="athlete.user.userNo"></el-table-column>
        <el-table-column label="性别" prop="athlete.user.userSex"></el-table-column>
        <el-table-column label="个人总得分" prop="rank"></el-table-column>
        <el-table-column label="操作" prop="state">
          <template slot-scope="scope">
            <!--详情-->
            <el-button
                icon="el-icon-tickets"
                size="mini"
                type="primary"
                @click="
                dialogTableVisible = true;
                getRankingDetail(scope.row.athlete.user.userId);
              "
            >查看详情
            </el-button
            >
          </template>
        </el-table-column>
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

    <el-dialog
        :visible.sync="dialogTableVisible"
        title="分数详情信息"
        width="40%"
    >
      <el-table :data="personScoreDetail" stripe style="width: 100%">
        <el-table-column label="项目名称" prop="athlete.item.itemName" width="180">
        </el-table-column>
        <el-table-column label="项目性别" prop="athlete.item.itemSex" width="180">
        </el-table-column>
        <el-table-column label="运动员" prop="athlete.user.nickname" width="180">
        </el-table-column>
        <el-table-column label="分数" prop="score" width="180">
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "PersonRanking",
  data() {
    return {
      rankingList: [],
      personScoreDetail: [],
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
      // 对话框状态
      dialogTableVisible: false,
      dialogFormVisible: false,
    };
  },
  created() {
    this.getSeasons();
  },
  methods: {
    async page(isSelect) {
      if (this.selectSeasonId == "") {
        return this.$message.info("请先选择运动会");
      }
      if (isSelect === true) {
        this.queryInfo.currentPage = 1;
        this.queryInfo.pageSize = 10;
      }
      const _this = this;
      axios
          .get("/ranking/queryUserRanking?athlete.item.season.seasonId=" + this.selectSeasonId + "&queryInfo=", {
            params: _this.queryInfo,
          })
          .then((res) => {
            let data = res.data.data;
            _this.rankingList = data.records;
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
            data.forEach((item, index) => {
              if (item.seasonStatus != 0) {
                _this.selectSeasonId = item.seasonId
              }
            })
            _this.allSeasonOptions = data;
            _this.page();

          });
    },


    async getRankingDetail(userId) {
      const _this = this;
      axios
          .get(
              "/score/queryScore?currentPage=1&pageSize=999999999&athlete.user.userId=" +
              userId
          )
          .then((res) => {
            let data = res.data.data.records;
            data.forEach((i, index) => {
              //分数加上单位
              i.score += i.athlete.item.itemUnit
            });

            _this.personScoreDetail = data;
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
          .get("/excel/exportPersonRanking?athlete.item.season.seasonId=" +
              this.selectSeasonId, {
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
<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>我的参赛项目</el-breadcrumb-item>
      <el-breadcrumb-item>我的参赛项目</el-breadcrumb-item>
    </el-breadcrumb>

    <!--项目列表主体-->
    <el-card>
      <el-row :gutter="25">
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
      </el-row>

      <!--项目列表 stripe隔行变色-->
      <!--参数运动员列表 stripe隔行变色-->
      <el-table :data="athletelist" border stripe>
        <!--索引列-->

        <el-table-column type="index"></el-table-column>
        <el-table-column label="学号" prop="user.userNo"></el-table-column>
        <el-table-column
            label="参数运动员"
            prop="user.nickname"
        ></el-table-column>
        <el-table-column label="性别" prop="user.userSex"></el-table-column>

        <el-table-column
            label="参赛项目"
            prop="item.itemName"
        ></el-table-column>
        <el-table-column label="地点" prop="item.itemPlace"></el-table-column>
        <el-table-column
            label="开始时间"
            prop="item.startTime"
        ></el-table-column>
        <el-table-column label="结束时间" prop="item.endTime"></el-table-column>

        <el-table-column label="报名时间" prop="signTime"></el-table-column>

        <el-table-column label="操作" prop="state">
          <template slot-scope="scope">
            <!--取消报名-->
            <el-button
                :disabled="scope.row.userIds!=null"
                size="mini"
                type="danger"
                @click="deleteAthlete(scope.row.athleteId)"
            >取消报名
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
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "ItemList",
  data() {
    return {
      athletelist: [],
      scorers: [],
      itemDetail: [],
      //所有运动会届时列表
      allSeasonOptions: [],
      //选择的届时
      selectSeasonId: "",
      userId: JSON.parse(localStorage.getItem("user")).userId,

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
      if (isSelect === true) {
        this.queryInfo.currentPage = 1;
        this.queryInfo.pageSize = 10;
      }
      const _this = this;
      axios
          .get(
              "/athlete/queryAthlete?item.season.seasonId=" + _this.selectSeasonId + "&user.userId=" + this.userId + "&queryInfo=", {params: _this.queryInfo}
          )
          .then((res) => {
            let data = res.data.data;
            _this.athletelist = data.records;
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
          });
    },


    async deleteAthlete(athleteId) {
      const _this = this;
      const confirmResult = await _this
          .$confirm("是否确定取消报名？", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          })
          .catch((err) => err);
      if (confirmResult !== "confirm") {
        return _this.$message.info("已取消操作");
      }
      axios
          .delete("/athlete/deleteAthlete?athleteId=" + athleteId)
          .then((res) => {
            if (res.data.status == 200) {

              _this.$message.success("已取消该项目");
              _this.addDialogVisible = false;
              _this.page();
            } else {
              _this.$message.error(res.data.msg);
            }
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
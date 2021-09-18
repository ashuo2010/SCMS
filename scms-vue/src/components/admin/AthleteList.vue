<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>参数运动员管理</el-breadcrumb-item>
      <el-breadcrumb-item>参数运动员信息</el-breadcrumb-item>
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
        <!-- 选择项目 -->
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
          >导出运动员列表
          </el-button
          >
        </el-col>

        <el-col v-show="systemStatus && currentUser.userType == 1" :span="4">
          <el-button type="danger" @click="switchSystem()"
          >关闭系统报名
          </el-button
          >
        </el-col>

        <el-col v-show="!systemStatus && currentUser.userType == 1" :span="4">
          <el-button type="success" @click="switchSystem()"
          >开启系统报名
          </el-button
          >
        </el-col>
      </el-row>
      <!--参数运动员列表 stripe隔行变色-->
      <el-table :data="athleteList" border stripe>
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

        <el-table-column label="报名时间" prop="signTime"></el-table-column>

        <el-table-column label="操作" prop="state">
          <template slot-scope="scope">
            <!--删除-->
            <el-button
                icon="el-icon-delete"
                size="mini"
                type="danger"
                @click="deleteAthlete(scope.row.athleteId)"
            ></el-button>
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
  name: "AthleteList",
  data() {
    return {
      athleteList: [],

      //项目列表
      itemList: [],
      //选择的项目
      selectItemId: "",

      //所有运动会届时列表
      allSeasonOptions: [],
      //选择的届时
      selectSeasonId: "",

      currentUser: "",

      systemStatus: true,
      queryInfo: {
        currentPage: 1,
        pageSize: 10,
        query: "",
      },
      total: 0,
      // 对话框状态
      addDialogVisible: false,

      FormRules: {
        itemPlace: [
          //   { required: true, message: "请输入用户名", trigger: "blur" },
          {required: true, trigger: "blur"},
        ],
      },

      editDialogVisible: false,

      dialogTableVisible: false,
      dialogFormVisible: false,
    };
  },
  created() {
    this.getItems();
    this.getSeasons();
    this.currentUser = JSON.parse(localStorage.getItem("user"));
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
              "/athlete/queryAthlete?item.season.seasonId=" + _this.selectSeasonId + "&item.parentId=" + _this.selectItemId + "&queryInfo=", {params: _this.queryInfo}
          )
          .then((res) => {
            let data = res.data.data;
            _this.athleteList = data.records;
            _this.queryInfo.currentPage = data.current;
            _this.total = data.total;
            _this.queryInfo.pageSize = data.size;
          });
      axios.post("/syslog/getSystemStatus").then((res) => {
        _this.systemStatus = res.data.data;
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


    //获取项目
    async getItems() {
      const _this = this;
      axios
          .get("/item/queryItemTemplate")
          .then((res) => {
            let data = res.data.data;
            data.push({
              itemId: 0,
              itemName: "所有项目",
            })
            _this.itemList = data;
            _this.page();
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
          .get("/excel/exportItemAthlete?item.season.seasonId=" + _this.selectSeasonId + "&item.parentId=" + _this.selectItemId, {
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

    async switchSystem(id) {
      const _this = this;
      const confirmResult = await _this
          .$confirm("是否关闭/开启系统报名？", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          })
          .catch((err) => err);
      if (confirmResult !== "confirm") {
        return _this.$message.info("已取消");
      }
      axios.post("/syslog/switchSystemStatus").then((res) => {
        if (res.data.status != 200) {
          return _this.$message.error(res.data.msg);
        }
        _this.systemStatus = res.data.data;
        return _this.$message.success("操作成功");
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
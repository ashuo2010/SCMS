<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>参数运动员管理</el-breadcrumb-item>
      <el-breadcrumb-item>参数运动员列表</el-breadcrumb-item>
    </el-breadcrumb>

    <!--参数运动员列表主体-->
    <el-card>
      <!--搜索区域-->
      <el-row :gutter="25">
        <el-col :span="10">
          <!--搜索添加-->
          <el-input
            placeholder="请输入运动员姓名"
            v-model="queryInfo.query"
            clearable
            @keyup.enter.native="page"
            @clear="page"
          >
            <!--搜索按钮-->
            <el-button
              slot="append"
              icon="el-icon-search"
              @click="page"
            ></el-button>
          </el-input>
        </el-col>

        <!-- 选择项目 -->
        <div style="float: left">
          <el-col>
            <el-select
              v-model="selectItemId"
              filterable
              placeholder="请选择项目"
              @change="querySelectedOptions"
            >
              <el-option
                v-for="item in itemList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-col>
        </div>
        <el-col :span="4">
          <el-button type="primary" @click="exportExcel()"
            >导出运动员列表</el-button
          >
        </el-col>

        <el-col :span="4" v-show="systemStatus && currentUser.userType == 1">
          <el-button type="danger" @click="switchSystem()"
            >关闭系统报名</el-button
          >
        </el-col>

        <el-col :span="4" v-show="!systemStatus && currentUser.userType == 1">
          <el-button type="success" @click="switchSystem()"
            >开启系统报名</el-button
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
      </el-table>
      <!--分页组件-->
      <div>
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryInfo.currentPage"
          :page-sizes="[5, 10, 20, 50]"
          :page-size="queryInfo.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
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
      itemList: [
        {
          value: 0,
          label: "所有项目",
        },
      ],

      //选择的项目
      selectItemId: 0,
      currentUser: "",

      systemStatus: true,
      queryInfo: {
        currentPage: 1,
        pageSize: 5,
        query: "",
      },
      total: 0,
      // 对话框状态
      addDialogVisible: false,

      FormRules: {
        itemPlace: [
          //   { required: true, message: "请输入用户名", trigger: "blur" },
          { required: true, trigger: "blur" },
        ],
      },

      editDialogVisible: false,

      dialogTableVisible: false,
      dialogFormVisible: false,
    };
  },
  created() {
    this.page();
    this.getItems();
    this.currentUser = JSON.parse(localStorage.getItem("user"));
  },
  methods: {
    async page() {
      const _this = this;
      axios
        .get("/athlete/queryAthlete?queryInfo=", { params: _this.queryInfo })
        .then((res) => {
          let data = res.data.data;
          _this.athleteList = data.records;
          console.log(this.athleteList);
          _this.queryInfo.currentPage = data.current;
          _this.total = data.total;
          _this.queryInfo.pageSize = data.size;
        });
      console.log(_this.systemStatus);
      axios.post("/syslog/getSystemStatus").then((res) => {
        _this.systemStatus = res.data.data;
        console.log(_this.systemStatus);
      });
    },

    //获取项目列表
    async getItems() {
      const _this = this;
      axios
        .get("/item/queryItem?query=&currentPage=1&pageSize=999999999")
        .then((res) => {
          let data = res.data.data.records;
          data.forEach((item, index) => {
            _this.itemList.push({
              value: item.itemId,
              label: item.itemName + " (" + item.itemSex + ") ",
            });
          });

          console.log(_this.itemList);
        });
    },

    //根据下拉框进行搜索
    async querySelectedOptions() {
      const _this = this;
      axios
        .get(
          "/athlete/queryAthlete?query=&currentPage=1&pageSize=999999999&item.itemId=" +
            _this.selectItemId
        )
        .then((res) => {
          let data = res.data.data;
          _this.athleteList = data.records;
          console.log(this.athleteList);
          _this.queryInfo.currentPage = data.current;
          _this.total = data.total;
          _this.queryInfo.pageSize = data.size;
        });
      console.log(_this.systemStatus);
      axios.post("/syslog/getSystemStatus").then((res) => {
        _this.systemStatus = res.data.data;
        console.log(_this.systemStatus);
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
        .get("/excel/exportItemAthlete?&item.itemId=" + _this.selectItemId, {
          responseType: "blob", //二进制流
        })
        .then((res) => {
          const filename = res.headers["content-disposition"];
          let blob = new Blob([res.data], { type: "application/vnd.ms-excel" });
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
        console.log(res.data);
        if (res.data.status != 200) {
          return _this.$message.error("操作失败" + res.data.msg);
        }
        _this.systemStatus = res.data.data;
        return _this.$message.success("操作成功");
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
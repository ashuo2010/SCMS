<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统管理</el-breadcrumb-item>
      <el-breadcrumb-item>系统操作日志</el-breadcrumb-item>
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
              placeholder="请输入操作用户姓名"
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
      </el-row>
      <!--操作记录列表 stripe隔行变色-->
      <el-table :data="syslogList" border stripe>
        <!--索引列-->

        <el-table-column type="index"></el-table-column>
        <el-table-column
            label="操作时间"
            prop="executionTime"
        ></el-table-column>
        <el-table-column
            label="操作用户"
            prop="executionUser.nickname"
        ></el-table-column>

        <el-table-column
            label="用户角色"
            prop="executionUser.userType"
        ></el-table-column>

        <el-table-column label="执行操作" prop="method"></el-table-column>
        <el-table-column :show-overflow-tooltip="true" label="执行参数"
                         prop="parameter"></el-table-column>
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
  name: "SyslogList",
  data() {
    return {
      syslogList: [],
      userRole: ["", "管理员", "记分员", "运动员"],
      queryInfo: {
        currentPage: 1,
        pageSize: 10,
        query: "",
      },
      total: 0,
      // 对话框状态
      addDialogVisible: false,
    };
  },
  created() {
    this.page();
  },
  methods: {
    async page() {
      const _this = this;
      axios
          .get("/syslog/querySyslog?queryInfo=", {params: _this.queryInfo})
          .then((res) => {
            let data = res.data.data;
            _this.syslogList = data.records;
            _this.queryInfo.currentPage = data.current;
            _this.total = data.total;
            _this.queryInfo.pageSize = data.size;
            //userType->userRole
            _this.syslogList.forEach((item, index) => {
              if (item.executionUser.userType == 1) {
                item.executionUser.userType = "管理员";
              } else if (item.executionUser.userType == 2) {
                item.executionUser.userType = "计分员";
              } else {
                item.executionUser.userType = "运动员";
              }
            });
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
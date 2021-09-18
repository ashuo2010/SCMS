<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>项目记录管理</el-breadcrumb-item>
      <el-breadcrumb-item>项目记录信息</el-breadcrumb-item>
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
                v-model="selectRecordStatus"
                filterable
                placeholder="请选择记录状态"
                @change="page(true)"
            >
              <el-option
                  v-for="item in recordStatusList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
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
          >导出项目记录列表
          </el-button
          >
        </el-col>
      </el-row>
      <!--参数运动员列表 stripe隔行变色-->
      <el-table :data="recordList" border stripe>
        <!--索引列-->

        <el-table-column type="index"></el-table-column>
        <el-table-column label="运动会" prop="athlete.item.season.seasonName"></el-table-column>
        <el-table-column label="项目名称" prop="athlete.item.itemName"></el-table-column>
        <el-table-column label="项目成绩" prop="recordScore"></el-table-column>
        <el-table-column label="项目纪录是否可用" prop="recordStatus"></el-table-column>
        <el-table-column label="运动员姓名" prop="athlete.user.nickname"></el-table-column>
        <el-table-column label="团体名称" prop="athlete.user.team.teamName"></el-table-column>
        <el-table-column label="记录创建时间" prop="createTime"></el-table-column>
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
      //项目记录列表
      recordList: [],
      //项目列表
      itemList: [],
      //选择的项目
      selectItemId: "",

      //记录是否可用下拉
      recordStatusList: [
        {
          value: "1",
          label: "可用",
        },
        {
          value: "0",
          label: "所有",
        },
      ],
      //选择的状态
      selectRecordStatus: "1",
      queryInfo: {
        currentPage: 1,
        pageSize: 10,
        query: "",
      },
      total: 0,
    };
  },
  created() {
    this.getItems();
  },
  methods: {
    async page(isSelect) {
      if (isSelect === true) {
        this.queryInfo.currentPage = 1;
        this.queryInfo.pageSize = 10;
      }
      const _this = this;
      axios
          .get("/record/queryRecord?recordStatus=" + _this.selectRecordStatus + "&athlete.item.parentId=" + _this.selectItemId + "&queryInfo=", {params: _this.queryInfo})
          .then((res) => {
            let data = res.data.data;
            _this.recordList = data.records;
            _this.recordList.forEach((item, index) => {
              //分数加上单位
              if (
                  item.athlete.item.itemUnit == "秒" &&
                  item.recordScore > 60
              ) {
                //如果分数为秒，且分数大于60秒，转成分钟显示
                let minute = parseInt(item.recordScore / 60);
                let second = parseInt(item.recordScore % 60);
                item.recordScore = minute + "分" + second + "秒";
              } else {
                item.recordScore += item.athlete.item.itemUnit;
              }

              if (item.recordStatus == 1) {
                item.recordStatus = "是";
              } else {
                item.recordStatus = "否";
              }
            })
            _this.queryInfo.currentPage = data.current;
            _this.total = data.total;
            _this.queryInfo.pageSize = data.size;
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
          .$confirm("确定导出项目记录吗？", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          })
          .catch((err) => err);
      if (confirmResult !== "confirm") {
        return;
      }
      axios
          .get("/excel/exportItemRecord?recordStatus=1", {
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
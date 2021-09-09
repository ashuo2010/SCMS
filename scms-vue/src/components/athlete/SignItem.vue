<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>参赛项目</el-breadcrumb-item>
      <el-breadcrumb-item>参赛项目列表</el-breadcrumb-item>
    </el-breadcrumb>

    <!--项目列表主体-->
    <el-card>
      <!--搜索区域-->
      <el-row :gutter="25">
        <el-col :span="10">
          <!--搜索添加-->
          <el-input
            placeholder="请输入搜索内容"
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
        <div style="float: left">
          <el-col>
            <el-select
              v-model="selectSeasonId"
              filterable
              placeholder="请选择运动会"
              @change="querySelectedOptions"
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
      <el-table :data="item" border stripe>
        <!--索引列-->

        <el-table-column type="index"></el-table-column>
        <el-table-column label="项目名称" prop="itemName"></el-table-column>
        <el-table-column label="项目性别" prop="itemSex"></el-table-column>
        <el-table-column label="项目地点" prop="itemPlace"></el-table-column>
        <el-table-column
          label="项目记分员"
          prop="user.nickname"
        ></el-table-column>
        <el-table-column
          label="参赛人数"
          prop="athleteAmount"
        ></el-table-column>
        <el-table-column
          label="比赛开始时间"
          prop="startTime"
        ></el-table-column>
        <el-table-column label="比赛结束时间" prop="endTime"></el-table-column>
        <el-table-column label="操作" prop="state">
          <template slot-scope="scope">
            <!--详情-->

            <el-button
              type="primary"
              icon="el-icon-tickets"
              size="mini"
              @click="
                dialogTableVisible = true;
                signItem(scope.row.itemId, scope.row.itemSex);
              "
              >报名</el-button
            >
          </template>
        </el-table-column>
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
  name: "ItemList",
  data() {
    return {
      item: [],
      scorers: [],
      itemDetail: [],
//所有运动会届时列表
      allSeasonOptions:[],
      //选择的届时
      selectSeasonId:"",

      signItemInfo: {
        user: {
          userId: "",
        },
        item: { itemId: "" },
      },

      queryInfo: {
        currentPage: 1,
        pageSize: 5,
        query: "",
      },
      total: 0,
      // 对话框状态
      dialogTableVisible: false,
      dialogFormVisible: false,
    };
  },
  created() {
    this.page();
     this.getSeasons();
  },
  methods: {
    async page() {
      const _this = this;
      axios
        .get("/item/queryItem?queryInfo=", { params: _this.queryInfo })
        .then((res) => {
          let data = res.data.data;
          _this.item = data.records;
          _this.queryInfo.currentPage = data.current;
          _this.total = data.total;
          _this.queryInfo.pageSize = data.size;
        });
    },


//获取运动会届时
    async getSeasons() {
      const _this = this;
      axios
        .get(
          "/season/querySeason?query=&currentPage=1&pageSize=999999999"
        )
        .then((res) => {
          let data = res.data.data.records;
        data.push( {
          seasonId: " ",
          seasonName: "所有运动会",
        })
        _this.allSeasonOptions=data;
     
        });
    },

     async querySelectedOptions() {
      const _this = this;
      if(_this.selectItemId==""){
        _this.selectItemId=0;
      }
      axios
        .get(
          "/item/queryItem?query=&currentPage=1&pageSize=999999999&season.seasonId="+_this.selectSeasonId
        )
        .then((res) => {
          let data = res.data.data;
          _this.item = data.records;
          _this.queryInfo.currentPage = data.current;
          _this.total = data.total;
          _this.queryInfo.pageSize = data.size;
        });
    },




    async getItemDetail(id) {
      const _this = this;
      axios.get("/item/getItem?itemId=" + id).then((res) => {
        let data = res.data.data;
        _this.itemDetail = [];
        _this.itemDetail.push(data);
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

    //项目报名
    async signItem(itemId, itemSex) {
      const _this = this;
      const confirmResult = await _this
        .$confirm("确定报名参赛该项目吗", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
        .catch((err) => err);
      if (confirmResult !== "confirm") {
        return _this.$message.info("已取消报名");
      }

      if (JSON.parse(localStorage.getItem("user")).userSex != itemSex) {
        return _this.$message.info("参赛性别不符合");
      }
      _this.signItemInfo.user.userId = JSON.parse(
        localStorage.getItem("user")
      ).userId;
      _this.signItemInfo.item.itemId = itemId;
      axios.post("/athlete/addAthlete", _this.signItemInfo).then((res) => {
        if (res.data.status != 200) {
          return _this.$message.error("报名失败" + res.data.msg);
        }
        _this.$message.success("报名成功");
        _this.addDialogVisible = false;
        _this.page();
      });
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
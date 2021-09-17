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
      <el-table :data="itemList" border stripe>
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
                signItem(scope.row);
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

    <!-- 多人报名 -->
    <el-dialog
      title="多人项目报名"
      :visible.sync="dialogTableVisible"
      width="50%"
      @close="addDialogClosed"
    >
      <el-form
        :model="addAthletesItemForm"
        ref="addFormRef"
        label-width="70px"
        class="demo-ruleForm"
      >
        <el-form-item label="项目名称">
          <el-input v-model="addAthletesItemForm.itemName" disabled></el-input>
        </el-form-item>
          <el-form-item label="项目性别">
          <el-input v-model="addAthletesItemForm.itemSex" disabled></el-input>
        </el-form-item>

        <el-form-item label="参赛人员">
         
            <el-select
              v-model="addAthletesItemForm.userIds"
              multiple
              filterable
              remote
              reserve-keyword
              placeholder="请输入关键词"
              :remote-method="searchUser"
              :loading="loading">
              <el-option
                v-for="item in userList"
                :key="item.userId"
                :label="item.nickname"
                :value="item.userId">
              </el-option>
            </el-select>

        </el-form-item>

       
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="signItemAthletes">报名</el-button>
        <el-button @click="dialogTableVisible = false">取消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "ItemList",
  data() {
    return {

      
      userList:[],
      // selectUids:[],

      itemList: [],
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

      addAthletesItemForm:{
          item: { itemId: "" },
          user: {userId: "",},
          userIds:"",
      },
        //select远程搜索加载中
       loading: false,
      
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
      if(isSelect===true){
        this.queryInfo.currentPage=1;
        this.queryInfo.pageSize = 10;
      }
      const _this = this;
      axios
         .get( "/item/queryItem?season.seasonId="+_this.selectSeasonId+"&queryInfo=", { params: _this.queryInfo })
        .then((res) => {
          let data = res.data.data;
          _this.itemList = data.records;
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
        data.forEach((item,index)=>{
            if(item.seasonStatus!=0){
            _this.selectSeasonId=item.seasonId 
            }
          })
        _this.allSeasonOptions=data;
        _this.page();

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
    async signItem(row) {
      const _this = this;
      if(row.itemAmount>1){
        _this.addAthletesItemForm.item.itemId=row.itemId;
        _this.addAthletesItemForm.itemName=row.itemName;
        _this.addAthletesItemForm.itemSex=row.itemSex;
        _this.addAthletesItemForm.itemPlace=row.itemPlace;
        
        _this.dialogTableVisible=true;
        return;
      }
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

      if (JSON.parse(localStorage.getItem("user")).userSex != row.itemSex) {
        return _this.$message.info("参赛性别不符合");
      }
      _this.signItemInfo.user.userId = JSON.parse(
        localStorage.getItem("user")
      ).userId;
      _this.signItemInfo.item.itemId = row.itemId;
      axios.post("/athlete/addAthlete", _this.signItemInfo).then((res) => {
        if (res.data.status != 200) {
          return _this.$message.error( res.data.msg);
        }
        _this.$message.success("报名成功");
        _this.page();
      });
    },

  //多人项目报名
    async signItemAthletes() {
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
      _this.addAthletesItemForm.userIds=_this.addAthletesItemForm.userIds.toString();
      _this.addAthletesItemForm.user.userId = JSON.parse( localStorage.getItem("user")).userId;
      axios.post("/athlete/addAthlete", _this.addAthletesItemForm).then((res) => {
        if (res.data.status != 200) {
        _this.addAthletesItemForm.userIds =[];
          return _this.$message.error(res.data.msg);
        }
        _this.$message.success("报名成功");
        _this.dialogTableVisible = false;
        _this.page();
      });
    },
      addDialogClosed() {
      const _this = this;
      _this.$refs.addFormRef.resetFields();
    },

 async searchUser(queryName) {
      const _this = this;
      _this.loading = true;
      axios
        .get("/user/queryUser?currentPage=1&pageSize=999999999&query="+queryName)
        .then((res) => {
          let data = res.data.data.records;
          data.forEach((item,index) => {
            item.nickname=item.nickname+"      "+item.team.teamName;
          });
          _this.userList =data ;
          this.loading = false;
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
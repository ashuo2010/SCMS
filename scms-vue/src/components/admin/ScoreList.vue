<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>参赛成绩管理</el-breadcrumb-item>
      <el-breadcrumb-item>参赛运动员列表</el-breadcrumb-item>
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

        <div style="float: left">
          <!-- 下拉列表选择区域 -->
          <el-col>
            <el-select
              v-model="athlete.item.user.userId"
              filterable
              placeholder="记分员"
              @change="querySelectedOptions"
            >
              <el-option
                v-for="item in scorers"
                :key="item.userId"
                :label="item.nickname"
                :value="item.userId"
              >
              </el-option>
            </el-select>
            &nbsp;
            <el-select
              v-model="athlete.status"
              filterable
              placeholder="是否录入成绩"
              @change="querySelectedOptions"
            >
              <el-option
                v-for="item in status"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-col>
        </div>
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

        <el-table-column
          label="记分员"
          prop="item.user.nickname"
        ></el-table-column>

        <el-table-column label="操作" prop="state">
          <template slot-scope="scope">
            <div>
              <!--录入分数-->
              <el-button
                style="margin-left: 10px"
                v-show="scope.row.status == 0"
                :disabled="scope.row.item.user.userId != currentUser.userId"
                type="primary"
                icon="el-icon-edit"
                size="mini"
                @click="
                  addDialogVisible = true;
                  oneAthlete = scope.row;
                  score.score = '';
                  score.user.userId = scope.row.user.userId;
                  score.item.itemId = scope.row.item.itemId;
                "
                >录入分数</el-button
              >

              <!--查看分数-->
              <el-button
                v-show="scope.row.status == 1"
                type="info"
                icon="el-icon-tickets"
                size="mini"
                @click="
                  dialogTableVisible = true;
                  oneAthlete = scope.row;
                  showScoreDetail(scope.row.user.userId, scope.row.item.itemId);
                "
                >查看分数</el-button
              >

              <!--修改分数-->
              <el-button
                v-show="scope.row.status == 1"
                :disabled="scope.row.item.user.userId != currentUser.userId"
                type="primary"
                icon="el-icon-edit"
                size="mini"
                @click="
                  EditDialogVisible = true;
                  oneAthlete = scope.row;
                  showScoreDetail(scope.row.user.userId, scope.row.item.itemId);
                "
                >修改分数</el-button
              >
            </div>
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
    <!--录入分数区域-->
    <el-dialog
      title="项目记分"
      :visible.sync="addDialogVisible"
      width="50%"
      @close="addDialogClosed"
    >
      <el-form
        :model="oneAthlete"
        ref="addFormRef"
        label-width="70px"
        class="demo-ruleForm"
      >
        <el-form-item label="项目名称" prop="">
          <el-input v-model="oneAthlete.item.itemName" disabled></el-input>
        </el-form-item>
        <el-form-item label="运动员" prop="">
          <el-input v-model="oneAthlete.user.nickname" disabled></el-input>
        </el-form-item>

        <el-form-item
          label="分数"
          prop=""
          v-show="oneAthlete.item.itemUnit != '秒'"
        >
          <el-input v-model="score.score"></el-input>
        </el-form-item>
        <el-form-item
          label="分数单位"
          prop=""
          v-show="oneAthlete.item.itemUnit != '秒'"
        >
          <el-input v-model="oneAthlete.item.itemUnit" disabled></el-input>
        </el-form-item>

        <el-form-item
          label="分数"
          prop=""
          v-show="oneAthlete.item.itemUnit == '秒'"
        >
          <div class="myInput">
            <div style="display: inline-block; width: 50px">
              <el-input v-model="score.minute"></el-input>
            </div>
            <span>分</span>
            <div style="display: inline-block; width: 50px">
              <el-input v-model="score.second"></el-input>
            </div>
            <span>秒</span>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addScore">确定</el-button>
      </span>
    </el-dialog>

    <!--查看分数详情区域-->
    <el-dialog
      title="分数详情"
      :visible.sync="dialogTableVisible"
      width="50%"
      @close="addDialogClosed"
    >
      <el-form
        :model="oneAthlete"
        ref="addFormRef"
        label-width="70px"
        class="demo-ruleForm"
      >
        <el-form-item label="项目名称" prop="">
          <el-input v-model="oneAthlete.item.itemName" disabled></el-input>
        </el-form-item>
        <el-form-item label="运动员" prop="">
          <el-input v-model="oneAthlete.user.nickname" disabled></el-input>
        </el-form-item>

        <el-form-item label="分数" prop="">
          <el-input v-model="score.score" disabled></el-input>
        </el-form-item>

        <el-form-item label="分数单位" prop="">
          <el-input v-model="oneAthlete.item.itemUnit" disabled></el-input>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!--修改分数详情区域-->
    <el-dialog
      title="分数详情"
      :visible.sync="EditDialogVisible"
      width="50%"
      @close="addDialogClosed"
    >
      <el-form
        :model="oneAthlete"
        ref="addFormRef"
        label-width="70px"
        class="demo-ruleForm"
      >
        <el-form-item label="项目名称" prop="">
          <el-input v-model="oneAthlete.item.itemName" disabled></el-input>
        </el-form-item>
        <el-form-item label="运动员" prop="">
          <el-input v-model="oneAthlete.user.nickname" disabled></el-input>
        </el-form-item>

        <el-form-item
          label="分数"
          prop=""
          v-show="oneAthlete.item.itemUnit != '秒'"
        >
          <el-input v-model="score.score"></el-input>
        </el-form-item>
        <el-form-item
          label="分数单位"
          prop=""
          v-show="oneAthlete.item.itemUnit != '秒'"
        >
          <el-input v-model="oneAthlete.item.itemUnit" disabled></el-input>
        </el-form-item>

        <el-form-item
          label="分数"
          prop=""
          v-show="oneAthlete.item.itemUnit == '秒'"
        >
          <div class="myInput">
            <div style="display: inline-block; width: 50px">
              <el-input v-model="score.minute"></el-input>
            </div>
            <span>分</span>
            <div style="display: inline-block; width: 50px">
              <el-input v-model="score.second"></el-input>
            </div>
            <span>秒</span>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="EditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="editScore">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "AthleteList",
  data() {
    return {
      athleteList: [],
      oneAthlete: {
        item: {
          itemName: "",
        },
        user: {
          userName: "",
        },
      },
      //记分员
      scorers: "",
      currentUser: "",
      status: [
        {
          value: "2",
          label: "未录入成绩",
        },
        {
          value: "1",
          label: "已录入成绩",
        },
        {
          value: "0",
          label: "所有成绩",
        },
      ],
      //下拉框选择的条件
      athlete: {
        item: {
          user: {
            userId: "",
          },
        },

        status: "",
      },

      score: {
        item: {
          itemId: "",
        },
        user: {
          userId: "",
        },
        score: "",
        minute: "",
        second: "",
      },
      queryInfo: {
        currentPage: 1,
        pageSize: 5,
        query: "",
      },
      total: 0,
      // 对话框状态
      addDialogVisible: false,

      dialogTableVisible: false,
      EditDialogVisible: false,
      dialogFormVisible: false,
    };
  },
  created() {
    this.currentUser = JSON.parse(localStorage.getItem("user"));
    this.page();
    this.getScorers();
  },
  methods: {
    async page() {
      const _this = this;
      axios
        .get("/athlete/queryAthlete?queryInfo=", {
          params: _this.queryInfo,
        })
        .then((res) => {
          let data = res.data.data;
          _this.athleteList = data.records;
          _this.queryInfo.currentPage = data.current;
          _this.total = data.total;
          _this.queryInfo.pageSize = data.size;
        });
    },

    //获取记分员
    async getScorers() {
      const _this = this;
      axios
        .get(
          "/user/queryUser?userType=2&query=&currentPage=1&pageSize=999999999"
        )
        .then((res) => {
          let data = res.data.data;
          _this.scorers = data.records;
          _this.scorers.push({
            nickname: "所有用户",
            userId: 0,
          });
        });
    },

    //根据下拉框进行搜索
    async querySelectedOptions() {
      const _this = this;
      let getStr =
        "/athlete/queryAthlete?query=&currentPage=1&pageSize=999999999&";
      if (_this.athlete.item.user.userId != "") {
        getStr += "item.user.userId=" + _this.athlete.item.user.userId + "&";
      }
      if (_this.athlete.status != "") {
        getStr += "status=" + _this.athlete.status + "&";
      }
      axios.get(getStr).then((res) => {
        let data = res.data.data;
        _this.athleteList = data.records;
        _this.queryInfo.currentPage = data.current;
        _this.total = data.total;
        _this.queryInfo.pageSize = data.size;
      });
    },

    //添加分数
    async addScore() {
      const _this = this;

      if (_this.score.minute != "" || _this.score.second != "") {
        if (
          parseInt(_this.score.minute) > 60 ||
          parseInt(_this.score.second) > 60
        ) {
          return _this.$message.error("数据错误");
        }
        _this.score.score =
          parseInt(_this.score.minute * 60) + parseInt(_this.score.second);
      }

      axios.post("/score/addScore", _this.score).then((res) => {
        if (res.data.status != 200) {
          return _this.$message.error("操作失败" + res.data.msg);
        }
        _this.$message.success("操作成功");
        _this.addDialogVisible = false;
        _this.page();
      });
    },
    //查看分数详情
    async showScoreDetail(userId, itemId) {
      const _this = this;
      axios
        .get(
          "/score/queryScore?currentPage=1&pageSize=999&item.itemId=" +
            itemId +
            "&user.userId=" +
            userId
        )
        .then((res) => {
          let data = res.data.data;
          _this.score = data.records[0];
        });
    },

    //修改分数
    async editScore() {
      const _this = this;
      if (_this.score.minute || _this.score.second) {
        if (
          parseInt(_this.score.minute) > 60 ||
          parseInt(_this.score.second) > 60
        ) {
          return _this.$message.error("数据错误");
        }
        _this.score.score =
          parseInt(_this.score.minute * 60) + parseInt(_this.score.second);
      }
      axios.put("/score/editScore", _this.score).then((res) => {
        if (res.data.status != 200) {
          return _this.$message.error("操作失败" + res.data.msg);
        }
        _this.$message.success("操作成功");
        _this.EditDialogVisible = false;
        _this.page();
      });
    },

    addDialogClosed() {
      const _this = this;
      _this.$refs.addFormRef.resetFields();
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

.myInput {
  display: flex;
}
</style>
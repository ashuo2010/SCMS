<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>参赛成绩管理</el-breadcrumb-item>
      <el-breadcrumb-item>项目成绩录入</el-breadcrumb-item>
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
          <!-- 下拉列表选择区域 -->
          <el-col>
            <el-select
                v-model="athlete.item.user.userId"
                filterable
                placeholder="记分员"
                @change="page(true)"
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
                v-model="athlete.scoreStatus"
                filterable
                placeholder="是否录入成绩"
                @change="page(true)"
            >
              <el-option
                  v-for="item in statusOptions"
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
        <el-table-column
            label="团体名称"
            prop="user.team.teamName"
        ></el-table-column>

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
                  v-show="scope.row.scoreStatus == 0"
                  :disabled="scope.row.item.user.userId != currentUser.userId"
                  icon="el-icon-edit"
                  size="mini"
                  style="margin-left: 10px"
                  type="primary"
                  @click="
                  addDialogVisible = true;
                  oneAthlete = scope.row;
                  scoreForm.score = '';
                  scoreForm.athlete.athleteId = scope.row.athleteId;
                  scoreForm.athlete.user.userId = scope.row.user.userId;
                  scoreForm.athlete.item.itemId = scope.row.item.itemId;
                  scoreForm.athlete.item.itemUnit = scope.row.item.itemUnit;
                "
              >录入分数
              </el-button
              >

              <!--查看分数-->
              <el-button
                  v-show="scope.row.scoreStatus == 1"
                  icon="el-icon-tickets"
                  size="mini"
                  type="info"
                  @click="
                  dialogTableVisible = true;
                  oneAthlete = scope.row;
                  showScoreDetail(scope.row.user.userId, scope.row.item.itemId,false);
                "
              >查看分数
              </el-button
              >

              <!--修改分数-->
              <el-button
                  v-show="scope.row.scoreStatus == 1"
                  :disabled="scope.row.item.user.userId != currentUser.userId"
                  icon="el-icon-edit"
                  size="mini"
                  type="primary"
                  @click="
                  EditDialogVisible = true;
                  oneAthlete = scope.row;
                  showScoreDetail(scope.row.user.userId, scope.row.item.itemId,true);
                "
              >修改分数
              </el-button
              >
            </div>
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
    <!--录入分数区域-->
    <el-dialog
        :visible.sync="addDialogVisible"
        title="项目记分"
        width="40%"
        @close="addDialogClosed"
    >
      <el-form
          ref="addFormRef"
          :model="oneAthlete"
          class="demo-ruleForm"
          label-width="180px"
      >
        <el-form-item label="项目名称">
          <el-input v-model="oneAthlete.item.itemName" disabled></el-input>
        </el-form-item>
        <el-form-item label="运动员">
          <el-input v-model="oneAthlete.user.nickname" disabled></el-input>
        </el-form-item>

        <el-form-item
            v-show="oneAthlete.item.itemUnit != '秒'"
            label="分数"
        >
          <el-input v-model="scoreForm.score"></el-input>
        </el-form-item>
        <el-form-item
            v-show="oneAthlete.item.itemUnit != '秒'"
            label="分数单位"
        >
          <el-input v-model="oneAthlete.item.itemUnit" disabled></el-input>
        </el-form-item>

        <el-form-item
            v-show="oneAthlete.item.itemUnit == '秒'"
            label="分数"
        >
          <div class="myInput">
            <div style="display: inline-block; width: 50px">
              <el-input v-model="scoreForm.minute"></el-input>
            </div>
            <span>分</span>
            <div style="display: inline-block; width: 50px">
              <el-input v-model="scoreForm.second"></el-input>
            </div>
            <span>秒</span>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="addScore">确定</el-button>
        <el-button @click="addDialogVisible = false">取消</el-button>

      </span>
    </el-dialog>

    <!--查看分数详情区域-->
    <el-dialog
        :visible.sync="dialogTableVisible"
        title="分数详情"
        width="40%"
        @close="addDialogClosed"
    >
      <el-form
          ref="addFormRef"
          :model="oneAthlete"
          class="demo-ruleForm"
          label-width="180px"
      >
        <el-form-item label="项目名称">
          <el-input v-model="oneAthlete.item.itemName" disabled></el-input>
        </el-form-item>
        <el-form-item label="运动员">
          <el-input v-model="oneAthlete.user.nickname" disabled></el-input>
        </el-form-item>

        <el-form-item label="分数">
          <el-input v-model="scoreDetail.score" disabled></el-input>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!--修改分数详情区域-->
    <el-dialog
        :visible.sync="EditDialogVisible"
        title="分数详情"
        width="40%"
        @close="addDialogClosed"
    >
      <el-form
          ref="addFormRef"
          :model="oneAthlete"
          class="demo-ruleForm"
          label-width="180px"
      >
        <el-form-item label="项目名称">
          <el-input v-model="oneAthlete.item.itemName" disabled></el-input>
        </el-form-item>
        <el-form-item label="运动员">
          <el-input v-model="oneAthlete.user.nickname" disabled></el-input>
        </el-form-item>

        <el-form-item
            v-show="oneAthlete.item.itemUnit != '秒'"
            label="分数"
        >
          <el-input v-model="scoreForm.score"></el-input>
        </el-form-item>
        <el-form-item
            v-show="oneAthlete.item.itemUnit != '秒'"
            label="分数单位"
        >
          <el-input v-model="oneAthlete.item.itemUnit" disabled></el-input>
        </el-form-item>

        <el-form-item
            v-show="oneAthlete.item.itemUnit == '秒'"
            label="分数"
        >
          <div class="myInput">
            <div style="display: inline-block; width: 50px">
              <el-input v-model="scoreForm.minute"></el-input>
            </div>
            <span>分</span>
            <div style="display: inline-block; width: 50px">
              <el-input v-model="scoreForm.second"></el-input>
            </div>
            <span>秒</span>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="editScore">确定</el-button>
        <el-button @click="EditDialogVisible = false">取消</el-button>

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
      //所有运动会届时列表
      allSeasonOptions: [],
      //选择的届时
      selectSeasonId: "",
      statusOptions: [
        {
          value: "0",
          label: "未录入成绩",
        },
        {
          value: "1",
          label: "已录入成绩",
        },
        {
          value: "-1",
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

        scoreStatus: "",
      },

      scoreForm: {
        athlete: {
          item: {
            itemId: "",
            itemUnit: "",
          },
          user: {
            userId: "",
          },
        },
        score: "",
        minute: "",
        second: "",
      },
      scoreDetail: {},
      queryInfo: {
        currentPage: 1,
        pageSize: 10,
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
    this.getScorers();
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
              "/athlete/queryAthlete?item.season.seasonId=" +
              _this.selectSeasonId +
              "&item.user.userId=" +
              _this.athlete.item.user.userId +
              "&scoreStatus=" +
              _this.athlete.scoreStatus +
              "&queryInfo=",
              {params: _this.queryInfo}
          )
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
            });
            data.forEach((item, index) => {
              if (item.seasonStatus != 0) {
                _this.selectSeasonId = item.seasonId;
              }
            });
            _this.allSeasonOptions = data;
            _this.page()
          });
    },


    //添加分数
    async addScore() {
      const _this = this;
      _this.scoreHandle();
      if (_this.scoreForm.score !== "") {
        axios.post("/score/addScore", _this.scoreForm).then((res) => {
          if (res.data.status != 200) {
            return _this.$message.error(res.data.msg);
          }
          _this.$message.success("操作成功");
          _this.addDialogVisible = false;
          _this.page();
        });
      }
    },

    //修改分数
    async editScore() {
      const _this = this;
      _this.scoreHandle();
      if (_this.scoreForm.score !== "") {
        axios.put("/score/editScore", _this.scoreForm).then((res) => {
          if (res.data.status != 200) {
            return _this.$message.error(res.data.msg);
          }
          _this.$message.success("操作成功");
          _this.EditDialogVisible = false;
          _this.page();
        });
      }
    },
    //分数处理
    scoreHandle() {
      if (this.scoreForm.athlete.item.itemUnit != "秒") {
        return;
      }
      if (this.scoreForm.minute != "" && this.scoreForm.second != "") {
        if (
            parseInt(this.scoreForm.minute) > 60 ||
            parseInt(this.scoreForm.second) > 60
        ) {
          this.scoreForm.score = "";
          return this.$message.error("数据错误");
        }
        this.scoreForm.score =
            parseInt(this.scoreForm.minute * 60) +
            parseInt(this.scoreForm.second);
      } else {
        this.scoreForm.score = "";
        return this.$message.error("请填写分和秒");
      }
    },

    //查看分数详情
    async showScoreDetail(userId, itemId, isEdit) {
      const _this = this;
      axios
          .get(
              "/score/queryScore?currentPage=1&pageSize=999&athlete.item.itemId=" +
              itemId +
              "&athlete.user.userId=" +
              userId
          )
          .then((res) => {
            let data = res.data.data;
            _this.scoreForm = data.records[0];
            _this.scoreDetail = data.records[0];
            if (isEdit) {
              return;
            }
            //分数加上单位
            if (
                _this.scoreDetail.athlete.item.itemUnit == "秒" &&
                _this.scoreDetail.score > 60
            ) {
              //如果分数为秒，且分数大于60秒，转成分钟显示
              let minute = parseInt(_this.scoreDetail.score / 60);
              let second = parseInt(_this.scoreDetail.score % 60);
              _this.scoreDetail.score = minute + "分" + second + "秒";
            } else {
              _this.scoreDetail.score += _this.scoreDetail.athlete.item.itemUnit;
            }
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
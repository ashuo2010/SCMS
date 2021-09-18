<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>用户信息管理</el-breadcrumb-item>
      <el-breadcrumb-item>用户信息</el-breadcrumb-item>
    </el-breadcrumb>
    <!--用户列表主体-->
    <el-card>
      <!--搜索区域-->
      <el-row :gutter="25">
        <el-col :span="5">
          <!--搜索添加-->
          <el-input
              v-model="queryInfo.query"
              clearable
              placeholder="请输入搜索内容"
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

        <!--添加按钮-->
        <el-col :span="4">
          <el-button type="primary" @click="addDialogVisible = true"
          >添加用户
          </el-button
          >
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="uploadDialogVisible = true"
          >批量添加用户
          </el-button
          >
        </el-col>
      </el-row>
      <!--用户列表 stripe隔行变色-->
      <el-table :data="users" border stripe>
        <!--索引列-->
        <el-table-column type="index"></el-table-column>
        <el-table-column label="学号" prop="userNo"></el-table-column>
        <el-table-column label="用户姓名" prop="nickname"></el-table-column>
        <el-table-column label="性别" prop="userSex"></el-table-column>
        <el-table-column
            label="团体名称"
            prop="team.teamName"
        ></el-table-column>
        <el-table-column label="用户类别" prop="userType"></el-table-column>
        <el-table-column label="电话" prop="phone"></el-table-column>
        <el-table-column label="操作" prop="state">
          <template slot-scope="scope">
            <!--修改-->
            <el-button
                :disabled="scope.row.userId == 1"
                icon="el-icon-edit"
                size="mini"
                type="primary"
                @click="showEditDialog(scope.row.userId)"
            ></el-button>
            <!--删除-->
            <el-button
                :disabled="scope.row.userId == 1"
                icon="el-icon-delete"
                size="mini"
                type="danger"
                @click="deleteUser(scope.row.userId)"
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
    <!--新增用户区域-->
    <el-dialog
        :visible.sync="addDialogVisible"
        title="添加用户"
        width="40%"
        @close="addDialogClosed"
    >
      <el-form
          ref="addFormRef"
          :model="addForm"
          :rules="addFormRules"
          class="demo-ruleForm"
          label-width="80px"
      >
        <el-form-item label="学号">
          <el-input v-model="addForm.userNo"></el-input>
        </el-form-item>
        <el-form-item label="登录账号">
          <el-input v-model="addForm.username"></el-input>
        </el-form-item>
        <el-form-item label="登录密码">
          <el-input v-model="addForm.password" type="password"></el-input>
        </el-form-item>
        <el-form-item label="用户姓名">
          <el-input v-model="addForm.nickname"></el-input>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="addForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="addForm.userSex" filterable placeholder="请选择">
            <el-option
                v-for="item in userSex"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属团体">
          <el-select
              v-model="addForm.team.teamId"
              filterable
              placeholder="请选择"
          >
            <el-option
                v-for="item in teamList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="用户类别">
          <el-select v-model="addForm.userType" filterable placeholder="请选择">
            <el-option
                v-for="item in userRole"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="addUser">确定</el-button>
        <el-button @click="addDialogVisible = false">取消</el-button>

      </span>
    </el-dialog>

    <!--批量新增用户区域-->
    <el-dialog
        :visible.sync="uploadDialogVisible"
        title="批量添加用户"
        width="21%"
    >
      <el-upload
          :before-upload="beforeUpload"
          :http-request="uploadFile"
          :limit="1"
          action=""
          drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传 </em>
          <div>
            <span slot="tip" class="el-upload__tip" style="margin-right: 10px"
            >只能上传excel文件</span
            >
          </div>
          <a slot="tip" class="el-upload__tip" @click="exportExcel()"
          ><em>下载批量添加用户模板</em></a
          >
        </div>


      </el-upload>
    </el-dialog>

    <!--修改用户区域-->
    <el-dialog
        :visible.sync="editDialogVisible"
        title="修改用户"
        width="40%"
        @close="editDialogClosed"
    >
      <el-form
          ref="editFormRef"
          :model="editForm"
          :rules="editFormRules"
          class="demo-ruleForm"
          label-width="80px"
      >
        <el-form-item label="用户ID">
          <el-input v-model="editForm.userId" disabled></el-input>
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="editForm.userNo" disabled></el-input>
        </el-form-item>
        <el-form-item label="登录账号">
          <el-input v-model="editForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="登录密码">
          <el-input v-model="editForm.password"></el-input>
        </el-form-item>
        <el-form-item label="用户姓名">
          <el-input v-model="editForm.nickname"></el-input>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="editForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="editForm.userSex" filterable placeholder="请选择">
            <el-option
                v-for="item in userSex"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属团体">
          <el-select
              v-model="editForm.team.teamId"
              filterable
              placeholder="请选择"
          >
            <el-option
                v-for="item in teamList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="用户类别">
          <el-select
              v-model="editForm.userType"
              filterable
              placeholder="请选择"
          >
            <el-option
                v-for="item in userRole"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="editUser">确定</el-button>
        <el-button @click="editDialogVisible = false">取消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "UserList",
  data() {
    return {
      users: [],

      teamList: [],

      userRole: [
        {
          value: 3,
          label: "运动员",
        },
        {
          value: 2,
          label: "记分员",
        },
        {
          value: 1,
          label: "管理员",
        },
      ],
      queryInfo: {
        currentPage: 1,
        pageSize: 10,
        query: "",
      },
      total: 0,
      // 对话框状态
      addDialogVisible: false,
      editDialogVisible: false,
      uploadDialogVisible: false,

      userSex: [
        {
          value: "男",
          label: "男",
        },
        {
          value: "女",
          label: "女",
        },
      ],

      addForm: {
        userNo: "",
        username: "",
        password: "",
        nickname: "",
        userSex: "",
        team: {
          teamId: "",
        },
        userType: "",
        phone: "",
      },
      editForm: {
        userId: "",
        userNo: "",
        username: "",
        password: "",
        nickname: "",
        userSex: "",
        team: {
          teamId: "",
        },
        userType: "",
        phone: "",
      },

      // 表单验证
      addFormRules: {
        username: [
          //   { required: true, message: "请输入用户名", trigger: "blur" },
          {message: "请输入用户名", trigger: "blur"},
          {
            min: 4,
            max: 30,
            message: "长度在 4 到 30 个字符",
            trigger: "blur",
          },
        ],
        password: [
          {message: "请输入密码", trigger: "blur"},
          {
            min: 6,
            max: 15,
            message: "长度在 6 到 15 个字符",
            trigger: "blur",
          },
        ],
      },
      editFormRules: {
        password: [
          {message: "请输入密码", trigger: "blur"},
          {
            min: 6,
            max: 30,
            message: "长度在 6 到 30 个字符",
            trigger: "blur",
          },
        ],
      },
    };
  },
  created() {
    this.page();
    this.getTeams();
  },
  methods: {
    async page() {
      const _this = this;
      axios
          .get("/user/queryUser?queryInfo=", {params: _this.queryInfo})
          .then((res) => {
            let data = res.data.data;
            _this.users = data.records;
            _this.users.forEach((item, index) => {
              if (item.userType == 3) {
                item.userType = "运动员";
              } else if (item.userType == 2) {
                item.userType = "记分员";
              } else {
                item.userType = "管理员";
              }
            });
            _this.queryInfo.currentPage = data.current;
            _this.total = data.total;
            _this.queryInfo.pageSize = data.size;
          });
    },

    //获取所有团体

    async getTeams() {
      const _this = this;
      axios
          .get("/team/queryTeam?query=&currentPage=1&pageSize=999999999")
          .then((res) => {
            let data = res.data.data.records;
            data.forEach((item, index) => {
              _this.teamList.push({
                value: item.teamId,
                label: item.teamName,
              });
            });
          });
    },

    addUser() {
      const _this = this;
      _this.$refs.addFormRef.validate(async (valid) => {
        if (!valid) return;
        axios.post("/user/addUser", _this.addForm).then((res) => {
          if (res.data.status != 200) {
            return _this.$message.error(res.data.msg);
          }
          _this.$message.success("操作成功");
          _this.addDialogVisible = false;
          _this.page();
        });
      });
    },
    async deleteUser(id) {
      const _this = this;
      const confirmResult = await _this
          .$confirm("此操作将永久删除用户，是否继续？", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          })
          .catch((err) => err);
      if (confirmResult !== "confirm") {
        return _this.$message.info("已取消删除");
      }
      axios.delete("/user/deleteUser?userId=" + id).then((res) => {
        if (res.status == 200) {
          _this.$message.success("删除成功");
          _this.addDialogVisible = false;
          _this.page();
        } else {
          _this.$message.error(res.data.msg);
        }
      });
    },

    async showEditDialog(userId) {
      const _this = this;
      axios
          .get(
              "/user/queryUser??query=&currentPage=1&pageSize=999999999&userId=" +
              userId
          )
          .then((res) => {
            let data = res.data.data.records[0];
            _this.editForm.userId = data.userId;
            _this.editForm.userNo = data.userNo;
            _this.editForm.username = data.username;
            // _this.editForm.password = data.password;
            _this.editForm.nickname = data.nickname;
            _this.editForm.userSex = data.userSex;
            _this.editForm.team = data.team;
            _this.editForm.userType = data.userType;
            _this.editForm.phone = data.phone;
            _this.editDialogVisible = true;
          });
    },

    editUser() {
      const _this = this;
      _this.$refs.editFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        }
        axios.put("/user/editUser", _this.editForm).then((res) => {
          if (res.data.status != 200) {
            return _this.$message.error(res.data.msg);
          }
          _this.$message.success("操作成功");
          _this.editDialogVisible = false;
          _this.page();
        });
      });
    },

    beforeUpload(file) {
      const Xls = file.name.split(".");
      if (Xls[1] === "xls" || Xls[1] === "xlsx" || Xls[1] === "csv") {
        return file;
      } else {
        this.$message.error("请上传excel格式的文件!");
        return false;
      }
    },

    // 上传文件
    uploadFile(file, fileList) {
      const fdata = new FormData();
      fdata.append("file", file.file);

      const _this = this;
      axios
          .post("excel/importUser", fdata)
          .then((res) => {
            _this.$message.success("上传成功");
            window.location.reload();
          })
          .catch((err) => {
            _this.$message.error(res.data.msg);
          });
    },

    async exportExcel() {
      const _this = this;
      const confirmResult = await _this
          .$confirm("确定下载批量添加用户模板吗？", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          })
          .catch((err) => err);
      if (confirmResult !== "confirm") {
        return;
      }
      axios
          .get("/excel/exportExcelTemplate", {
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

    addDialogClosed() {
      const _this = this;
      _this.$refs.addFormRef.resetFields();
    },

    editDialogClosed() {
      const _this = this;
      _this.$refs.editFormRef.resetFields();
    },
  },
};
</script>

<style lang="less" scoped>
.el-breadcrumb {
  margin-bottom: 15px;
  font-size: 17px;
}

em {
  color: #409eff;
  font-style: normal;
  cursor: pointer;
}
</style>
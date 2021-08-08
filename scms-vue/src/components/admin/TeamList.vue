<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>团队信息管理</el-breadcrumb-item>
      <el-breadcrumb-item>团队列表</el-breadcrumb-item>
    </el-breadcrumb>
    <!--团体列表主体-->
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

        <!--添加按钮-->
        <el-col :span="4">
          <el-button type="primary" @click="addDialogVisible = true"
            >添加团体</el-button
          >
        </el-col>
      </el-row>
      <!--团体列表 stripe隔行变色-->
      <el-table :data="team" border stripe>
        <!--索引列-->
        <el-table-column type="index"></el-table-column>
        <el-table-column label="团体名称" prop="teamName"></el-table-column>
        <el-table-column label="创建时间" prop="createTime"></el-table-column>
        <el-table-column
          label="最后一次修改时间"
          prop="editTime"
        ></el-table-column>
        <el-table-column label="操作" prop="state">
          <template slot-scope="scope">
            <!--修改-->
            <el-button
              type="primary"
              icon="el-icon-edit"
              size="mini"
              @click="showEditDialog(scope.row.teamId)"
              :disabled="scope.row.teamId == 1"
            ></el-button>
            <!--删除-->
            <el-button
              type="danger"
              icon="el-icon-delete"
              size="mini"
              @click="deleteTeam(scope.row.teamId)"
              :disabled="scope.row.teamId == 1"
            ></el-button>
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
    <!--新增团体区域-->
    <el-dialog
      title="添加团体"
      :visible.sync="addDialogVisible"
      width="50%"
      @close="addDialogClosed"
    >
      <el-form
        :model="addForm"
        ref="addFormRef"
        label-width="70px"
        class="demo-ruleForm"
      >
        <el-form-item label="团体名称" prop="teamName">
          <el-input v-model="addForm.teamName"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addTeam">确定</el-button>
      </span>
    </el-dialog>
    <!--修改团体区域-->
    <el-dialog
      title="修改团体"
      :visible.sync="editDialogVisible"
      width="50%"
      @close="editDialogClosed"
    >
      <el-form
        :model="editForm"
        ref="editFormRef"
        label-width="70px"
        class="demo-ruleForm"
      >
        <el-form-item label="团体ID" prop="teamId">
          <el-input v-model="editForm.teamId" disabled></el-input>
        </el-form-item>
        <el-form-item label="团体名称" prop="teamName">
          <el-input v-model="editForm.teamName"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="editTeam">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "TeamList",
  data() {
    return {
      team: [],
      queryInfo: {
        currentPage: 1,
        pageSize: 5,
        query: "",
      },
      total: 0,
      // 对话框状态
      addDialogVisible: false,
      addForm: {
        teamName: "",
      },
      editForm: {
        teamId: "",
        teamName: "",
      },
      editDialogVisible: false,
    };
  },
  created() {
    this.page();
  },
  methods: {
    async page() {
      const _this = this;
      axios
        .get("/team/queryTeam?queryInfo=", { params: _this.queryInfo })
        .then((res) => {
          let data = res.data.data;
          _this.team = data.records;
          _this.queryInfo.currentPage = data.current;
          _this.total = data.total;
          _this.queryInfo.pageSize = data.size;
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

    async showEditDialog(id) {
      const _this = this;
      axios.get("/team/getTeam?teamId=" + id).then((res) => {
        let data = res.data.data;
        _this.editForm.teamId = data.teamId;
        _this.editForm.teamName = data.teamName;

        _this.editDialogVisible = true;
      });
    },
    addDialogClosed() {
      const _this = this;
      _this.$refs.addFormRef.resetFields();
    },
    editDialogClosed() {
      const _this = this;
      _this.$refs.editFormRef.resetFields();
    },
    addTeam() {
      const _this = this;
      _this.$refs.addFormRef.validate(async (valid) => {
        if (!valid) return;
        axios.post("/team/addTeam", _this.addForm).then((res) => {
          if (res.data.status != 200) {
            return _this.$message.error("操作失败" + res.data.msg);
          }
          _this.$message.success("操作成功");
          _this.addDialogVisible = false;
          _this.page();
        });
      });
    },
    async deleteTeam(id) {
      const _this = this;
      const confirmResult = await _this
        .$confirm("此操作将永久删除团体，是否继续？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
        .catch((err) => err);
      if (confirmResult !== "confirm") {
        return _this.$message.info("已取消删除");
      }
      axios.delete("/team/deleteTeam?teamId=" + id).then((res) => {
        if (res.status == 200) {
          _this.$message.success("删除成功");
          _this.addDialogVisible = false;
          _this.page();
        } else {
          _this.$message.error("删除失败");
        }
      });
    },
    editTeam() {
      const _this = this;
      _this.$refs.editFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        }
        axios.put("/team/editTeam", _this.editForm).then((res) => {
          if (res.data.status != 200) {
            return _this.$message.error("操作失败");
          }
          _this.$message.success("操作成功");
          _this.editDialogVisible = false;
          _this.page();
        });
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
</style>
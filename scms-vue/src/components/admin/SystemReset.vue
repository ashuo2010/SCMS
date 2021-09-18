<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统管理</el-breadcrumb-item>
      <el-breadcrumb-item>重置系统</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card>
      <el-popover
          content="重置所有数据将清空本系统所有数据，包括用户、项目、分数、排名等信息"
          placement="top-start"
          trigger="hover"
          width="200"
      >
        <el-button slot="reference" type="danger" @click="deleteAllDataConfirm()"
        >重置所有数据
        </el-button
        >
      </el-popover>
    </el-card>


    <el-dialog
        :visible.sync="DialogVisible"
        title="清空系统数据"
        width="40%"
        @close="comfirmPassword=''"
    >
      <el-form
          ref="FormRef"
          label-width="80px"
          @submit.native.prevent
      >
        <el-form-item label="删除密码">
          <el-input v-model="comfirmPassword" placeholder="请输入删除密码" type="password"
                    @keyup.enter.native="deleteAllData"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="deleteAllData">确定</el-button>
        <el-button @click="DialogVisible = false">取消</el-button>

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
      comfirmPassword: "",
      DialogVisible: false
    };
  },
  created() {
  },
  methods: {

    async deleteAllDataConfirm() {
      const _this = this;
      const confirmResult = await _this
          .$confirm("此操作将永久清空所有本系统所有数据，是否继续？", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "confirm",
          })
          .catch((err) => err);
      if (confirmResult !== "confirm") {
        return _this.$message.info("已取消清空");
      }
      _this.DialogVisible = true;
    },

    //重置所有数据
    async deleteAllData() {
      const _this = this;
      axios.delete("/syslog/resetAllData?comfirmPassword=" + this.comfirmPassword).then((res) => {
        if (res.data.status == 200) {
          _this.$message.success("清空成功");
          _this.DialogVisible = false;

        } else {
          _this.$message.error(res.data.msg);
          this.comfirmPassword = "";
        }
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
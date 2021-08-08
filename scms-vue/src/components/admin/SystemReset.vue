<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统管理</el-breadcrumb-item>
      <el-breadcrumb-item>重置系统</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card>
      <el-col :span="5">
        <el-popover
          placement="top-start"
          width="200"
          trigger="hover"
          content="重置运动分数数据可以清空所有报名的参赛运动员信息，项目分数信息，排名信息"
        >
          <el-button
            type="danger"
            slot="reference"
            @click="deleteAllRangkingScoreAthlete()"
            >重置运动分数数据</el-button
          >
        </el-popover></el-col
      >

      <el-popover
        placement="top-start"
        width="200"
        trigger="hover"
        content="重置所有数据将清空本系统所有数据，包括用户、项目、分数等信息"
      >
        <el-button type="danger" slot="reference" @click="deleteAllData()"
          >重置所有数据</el-button
        >
      </el-popover>
    </el-card>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "AthleteList",
  data() {
    return {};
  },
  created() {},
  methods: {
    //重置运动分数数据
    async deleteAllRangkingScoreAthlete(id) {
      const _this = this;
      const confirmResult = await _this
        .$confirm(
          "此操作将永久清空所有项目排名、项目分数、参赛运动员数据，是否继续？",
          "提示",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "confirm",
          }
        )
        .catch((err) => err);
      if (confirmResult !== "confirm") {
        return _this.$message.info("已取消清空");
      }
      const confirmResult2 = await _this
        .$confirm("请再次确认清空所有数据？", "再次确认", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
        .catch((err) => err);
      if (confirmResult2 !== "confirm") {
        return _this.$message.info("已取消清空");
      }
      axios.delete("/syslog/resetRangkingScoreAthlete").then((res) => {
        if (res.status == 200) {
          _this.$message.success("清空成功");
        } else {
          _this.$message.error("清空失败");
        }
      });
    },

    //重置所有数据
    async deleteAllData(id) {
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
      const confirmResult2 = await _this
        .$confirm("请再次确认清空所有数据？", "再次确认", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
        .catch((err) => err);
      if (confirmResult2 !== "confirm") {
        return _this.$message.info("已取消清空");
      }
      axios.delete("/syslog/resetAllData").then((res) => {
        if (res.status == 200) {
          _this.$message.success("清空成功");
        } else {
          _this.$message.error("清空失败");
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
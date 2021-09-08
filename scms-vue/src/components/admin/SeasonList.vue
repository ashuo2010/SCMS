<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>运动会届时管理</el-breadcrumb-item>
      <el-breadcrumb-item>届时列表</el-breadcrumb-item>
    </el-breadcrumb>
    <!--届时列表主体-->
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
            >添加届时</el-button
          >
        </el-col>
      </el-row>
      <!--届时列表 stripe隔行变色-->
      <el-table :data="season" border stripe>
        <!--索引列-->
        <el-table-column type="index"></el-table-column>
        <el-table-column label="运动会名称" prop="seasonName"></el-table-column>
        <el-table-column label="运动会开始时间" prop="seasonBeginTime"></el-table-column>
        <el-table-column label="运动会结束时间" prop="seasonEndTime"></el-table-column>
        <el-table-column label="运动会状态" prop="seasonStatus"></el-table-column>
        <el-table-column label="运动会报名人数" prop="seasonAthleteAmount"></el-table-column>
        <el-table-column label="创建时间" prop="createTime"></el-table-column>
       
        <el-table-column label="操作" prop="state">
          <template slot-scope="scope">
            <!--修改-->
            <el-button
              type="primary"
              icon="el-icon-edit"
              size="mini"
              @click="showEditDialog(scope.row.seasonId)"
            ></el-button>
            <!--删除-->
            <el-button
              type="danger"
              icon="el-icon-delete"
              size="mini"
              @click="deleteSeason(scope.row.seasonId)"
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
    <!--新增届时区域-->
    <el-dialog
      title="添加届时"
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
        <el-form-item label="运动会届时名称" prop="seasonName">
          <el-input v-model="addForm.seasonName"></el-input>
        </el-form-item>
          <el-form-item label="运动会主题描述" prop="seasonTopicDesc">
          <el-input v-model="addForm.seasonTopicDesc"></el-input>
        </el-form-item>

        <el-form-item label="运动会开始时间" prop="seasonBeginTime">
          <el-date-picker
            v-model="addForm.seasonBeginTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择日期时间" >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="运动会结束时间" prop="seasonEndTime">
          <el-date-picker
            v-model="addForm.seasonEndTime"
            value-format="yyyy-MM-dd HH:mm:ss"
            type="datetime"
            placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>


        <el-form-item label="运动会状态" prop="seasonStatus">
          <el-select v-model="addForm.seasonStatus" placeholder="请选择">
            <el-option
              v-for="item in seasonStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>


      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addSeason">确定</el-button>
      </span>
    </el-dialog>
    <!--修改届时区域-->
    <el-dialog
      title="修改届时"
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
       
 <el-form-item label="运动会届时名称" prop="seasonName">
          <el-input v-model="editForm.seasonName"></el-input>
        </el-form-item>
          <el-form-item label="运动会主题描述" prop="seasonTopicDesc">
          <el-input v-model="editForm.seasonTopicDesc"></el-input>
        </el-form-item>

        <el-form-item label="运动会开始时间" prop="seasonBeginTime">
          <el-date-picker
            v-model="editForm.seasonBeginTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择日期时间" >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="运动会结束时间" prop="seasonEndTime">
          <el-date-picker
            v-model="editForm.seasonEndTime"
            value-format="yyyy-MM-dd HH:mm:ss"
            type="datetime"
            placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>


        <el-form-item label="运动会状态" prop="seasonStatus">
          <el-select v-model="editForm.seasonStatus" placeholder="请选择">
            <el-option
              v-for="item in seasonStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>



      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="editSeason">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "SeasonList",
  data() {
    return {
      season: [],
      queryInfo: {
        currentPage: 1,
        pageSize: 5,
        query: "",
      },
      total: 0,
      // 对话框状态
      addDialogVisible: false,
      addForm: {
        seasonName: "",
        seasonTopicDesc:"",
        seasonBeginTime:"",
        seasonEndTime:"",
        seasonStatus:"1",
      },
      editForm: {
        seasonId: "",
        seasonName: "",
        seasonTopicDesc:"",
        seasonBeginTime:"",
        seasonEndTime:"",
        seasonStatus:"1",
      },
      editDialogVisible: false,

      seasonStatusOptions: [
        {
          value: "1",
          label: "启用",
        },
        {
          value: "0",
          label: "禁用",
        },
      
      ],
    };
  },
  created() {
    this.page();
  },
  methods: {
    async page() {
      const _this = this;
      axios
        .get("/season/querySeason?queryInfo=", { params: _this.queryInfo })
        .then((res) => {
          let data = res.data.data;
          _this.season = data.records;
          _this.season.forEach((item,index) => {
            if(item.seasonStatus==1){
              item.seasonStatus="正在举行";
            }else{
              item.seasonStatus="已闭幕";
            }

          }); 

          _this.queryInfo.currentPage = data.current;
          _this.total = data.total;
          _this.queryInfo.pageSize = data.size;
        });
    },

   


   /*  async querySelectedOptions() {
      const _this = this;
      axios
         .get(
          "/season/querySeason?query=&currentPage=1&pageSize=999999999&season.seasonId=" +
            _this.selectItemId
        )
        .then((res) => {
          let data = res.data.data;
          _this.season = data.records;
          _this.season.forEach((item,index) => {
            if(item.seasonStatus==1){
              item.seasonStatus="正在举行";
            }else{
              item.seasonStatus="已闭幕";
            }

          }); 

          _this.queryInfo.currentPage = data.current;
          _this.total = data.total;
          _this.queryInfo.pageSize = data.size;
        });
    }, */


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
      axios.get("/season/getSeason?seasonId=" + id).then((res) => {
        let data = res.data.data;
        _this.editForm= data;
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
    addSeason() {
      const _this = this;
      _this.$refs.addFormRef.validate(async (valid) => {
        if (!valid) return;
        axios.post("/season/addSeason", _this.addForm).then((res) => {
          if (res.data.status != 200) {
            return _this.$message.error("操作失败" + res.data.msg);
          }
          _this.$message.success("操作成功");
          _this.addDialogVisible = false;
          _this.page();
        });
      });
    },
    async deleteSeason(id) {
      const _this = this;
      const confirmResult = await _this
        .$confirm("此操作将永久删除届时，是否继续？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
        .catch((err) => err);
      if (confirmResult !== "confirm") {
        return _this.$message.info("已取消删除");
      }
      axios.delete("/season/deleteSeason?seasonId=" + id).then((res) => {
        if (res.status == 200) {
          _this.$message.success("删除成功");
          _this.addDialogVisible = false;
          _this.page();
        } else {
          _this.$message.error("删除失败");
        }
      });
    },
    editSeason() {
      const _this = this;
      _this.$refs.editFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        }
        axios.put("/season/editSeason", _this.editForm).then((res) => {
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
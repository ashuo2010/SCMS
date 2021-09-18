<template>
  <div>
    <!--导航-->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>项目管理</el-breadcrumb-item>
      <el-breadcrumb-item>项目信息</el-breadcrumb-item>
    </el-breadcrumb>

    <el-dialog
      title="项目详情信息"
      :visible.sync="dialogTableVisible"
      width="80%"
    >
      <el-table :data="itemDetail" stripe style="width: 100%">
        <el-table-column prop="itemName" label="项目名称" width="180">
        </el-table-column>
        <el-table-column prop="itemSex" label="项目性别" width="180">
        </el-table-column>
        <el-table-column prop="itemPlace" label="项目地点" width="180">
        </el-table-column>
        <el-table-column prop="itemUnit" label="项目分数单位" width="180">
        </el-table-column>
        <el-table-column prop="user.nickname" label="项目记分员" width="180">
        </el-table-column>
        <el-table-column prop="startTime" label="项目开始时间" width="180">
        </el-table-column>
        <el-table-column prop="endTime" label="项目结束时间" width="180">
        </el-table-column>
        <el-table-column prop="athleteAmount" label="项目参赛人数" width="180">
        </el-table-column>
        <el-table-column prop="createTime" label="项目创建时间" width="180">
        </el-table-column>
        <el-table-column prop="editTime" label="项目修改时间" width="180">
        </el-table-column>
      </el-table>
    </el-dialog>

    <!--项目列表主体-->
    <el-card>
      <!--搜索区域-->
      <el-row :gutter="25">
        <el-col :span="5">
          <!--搜索添加-->
          <el-input
            placeholder="请输入项目名称"
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
        <!--添加按钮-->
        <el-col :span="4">
          <el-button type="primary" @click="addDialogVisible = true"
            >添加项目</el-button
          >
        </el-col>

        <el-col :span="4">
          <el-button type="primary" @click="templateListDialogVisible = true"
            >项目模板</el-button
          >
        </el-col>



      </el-row>
      <!--项目列表 stripe隔行变色-->
      <el-table :data="item" border stripe>
        <!--索引列-->

        <el-table-column type="index"></el-table-column>
        <el-table-column label="运动会届时" prop="season.seasonName"></el-table-column>
        <el-table-column label="项目名称" prop="itemName"></el-table-column>
        <!-- <el-table-column label="项目性别" prop="itemSex"></el-table-column> -->
        <el-table-column label="项目地点" prop="itemPlace"></el-table-column>
        <el-table-column label="项目分数单位" prop="itemUnit"></el-table-column>

        <el-table-column
          label="项目记分员"
          prop="user.nickname"
        ></el-table-column>
        <el-table-column
          label="参赛人数"
          prop="athleteAmount"
        ></el-table-column>
        <el-table-column label="创建时间" prop="createTime"></el-table-column>
       
        <el-table-column label="操作" prop="state">
          <template slot-scope="scope">
            <!--详情-->
            <el-button
              type="primary"
              icon="el-icon-tickets"
              size="mini"
              style="margin-left: 10px"
              @click="
                dialogTableVisible = true;
                getItemDetail(scope.row.itemId);
              "
            ></el-button>
            <!--修改-->
            <el-button
              type="primary"
              icon="el-icon-edit"
              size="mini"
              @click="showEditDialog(scope.row.itemId)"
            ></el-button>
            <!--删除-->
            <el-button
              type="danger"
              icon="el-icon-delete"
              size="mini"
              @click="deleteItem(scope.row.itemId,'deleteItem')"
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
    <!--新增项目区域-->
    <el-dialog
      title="添加项目"
      :visible.sync="addDialogVisible"
      width="30%"
      @close="addDialogClosed"
    >
      <el-form
        :model="addForm"
        ref="addFormRef"
        label-width="180px"
        class="demo-ruleForm"
      >
        <el-form-item label="运动会项目届时" >
          <el-select v-model="addForm.season.seasonId" filterable placeholder="请选择">
            <el-option
              v-for="item in seasonEnableOptions"
              :key="item.seasonId"
              :label="item.seasonName"
              :value="item.seasonId"
            >
            </el-option>
          </el-select>
        </el-form-item>


        <el-form-item label="项目名称" label-width="170px">
           <el-tooltip class="item" effect="dark" placement="top-start">
                <div slot="content">{{ itemAddTip }}</div>
                <i class="el-icon-info" style="font-size: 15px; color:#409ef0;margin-left:-10px;margin-right:5px"></i>
              </el-tooltip>
          <el-select v-model="addForm.parentId" placeholder="请选择">
            <el-option
              v-for="item in itemTemplateOptions"
              :key="item.itemId"
              :label="item.itemName"
              :value="item.itemId"
            >
            </el-option>
          </el-select>
        </el-form-item>


        <el-form-item label="项目性别">
          <el-select v-model="addForm.itemSex" filterable placeholder="请选择">
            <el-option
              v-for="item in userSex"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="项目记分员">
          <el-select v-model="addForm.user.userId" placeholder="请选择">
            <el-option
              v-for="item in scorers"
              :key="item.userId"
              :label="item.nickname"
              :value="item.userId"
            >
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="项目地点">
          <el-select v-model="addForm.itemPlace" placeholder="请选择">
            <el-option
              v-for="item in itemPlaceOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="项目开始时间" >
          <el-date-picker
            v-model="addForm.startTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择日期时间"
          >
          </el-date-picker>
        </el-form-item>

        <el-form-item label="项目结束时间" >
          <el-date-picker
            v-model="addForm.endTime"
            value-format="yyyy-MM-dd HH:mm:ss"
            type="datetime"
            placeholder="选择日期时间"
          >
          </el-date-picker>
        </el-form-item>


      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="addItem">确定</el-button>
        <el-button @click="addDialogVisible = false">取消</el-button>

      </span>
    </el-dialog>
    <!--修改项目区域-->
    <el-dialog
      title="修改项目"
      :visible.sync="editDialogVisible"
      width="30%"
      @close="editDialogClosed"
    >
      <el-form
        :model="editForm"
        :rules="FormRules"
        ref="editFormRef"
        label-width="180px"
        class="demo-ruleForm"

      >
        <el-form-item label="项目ID">
          <el-input v-model="editForm.itemId" disabled></el-input>
        </el-form-item>
        <el-form-item label="运动会项目届时"  >
          <el-select v-model="editForm.season.seasonId" filterable placeholder="请选择">
            <el-option
              v-for="item in seasonEnableOptions"
              :key="item.seasonId"
              :label="item.seasonName"
              :value="item.seasonId"
            >
            </el-option>
          </el-select>
        </el-form-item>
       <el-form-item label="项目名称" >
          <el-select v-model="editForm.parentId" disabled >
            <el-option
              v-for="item in itemTemplateOptions"
              :key="item.itemId"
              :label="item.itemName"
              :value="item.itemId"
            >
            </el-option>
          </el-select>
        </el-form-item>


        <el-form-item label="项目性别">
          <el-select v-model="editForm.itemSex" filterable placeholder="请选择">
            <el-option
              v-for="item in userSex"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>


      <el-form-item label="项目记分员">
          <el-select v-model="editForm.user.userId" placeholder="请选择">
            <el-option
              v-for="item in scorers"
              :key="item.userId"
              :label="item.nickname"
              :value="item.userId"
            >
            </el-option>
          </el-select>
        </el-form-item>


        <el-form-item label="项目地点" >
          <el-select v-model="editForm.itemPlace" placeholder="请选择">
            <el-option
              v-for="item in itemPlaceOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>


        <el-form-item label="项目开始时间">
          <el-date-picker
            v-model="editForm.startTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择日期时间"
          >
          </el-date-picker>
        </el-form-item>

        <el-form-item label="项目结束时间">
          <el-date-picker
            v-model="editForm.endTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择日期时间"
          >
          </el-date-picker>
        </el-form-item>
       
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="editItem">确定</el-button>
        <el-button @click="editDialogVisible = false">取消</el-button>

      </span>
    </el-dialog>

    <!--新增项目模板区域-->
     <el-dialog
      title="添加项目模板"
      :visible.sync="addTemplateDialogVisible"
      width="30%"
      @close="addDialogClosed"
    >
      <el-form
        :model="addForm"
        ref="addFormRef"
        label-width="100px"
        class="demo-ruleForm"

      >
        <el-form-item label="项目名称" >
          <el-input v-model="addForm.itemName" placeholder="请输入项目名称"></el-input>
        </el-form-item>

        <el-form-item label="项目分数单位" >
          <el-select v-model="addForm.itemUnit" placeholder="请选择">
            <el-option
              v-for="item in itemUnitOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="项目人数" >
           <el-input v-model="addForm.itemAmount" placeholder="请输入项目人数"></el-input>
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="addItem">确定</el-button>
        <el-button @click="addTemplateDialogVisible = false">取消</el-button>

      </span>
    </el-dialog>

  

      <!--项目模板列表区域-->
    <el-dialog
      title="项目模板列表"
      :visible.sync="templateListDialogVisible"
      width="40%">
     <el-row :gutter="25">
        <el-col :span="5">
          <el-button type="primary" @click="addTemplateDialogVisible = true" size="mini"
            >添加项目模板</el-button
          >
        </el-col></el-row>
  <el-table :data="itemTemplateOptions" border stripe>
        <!--索引列-->

        <el-table-column type="index"></el-table-column>
        <el-table-column label="项目模板名称" prop="itemName"></el-table-column>
        <el-table-column label="项目模板分数单位" prop="itemUnit"></el-table-column>
        <el-table-column label="项目模板人数" prop="itemAmount"></el-table-column>
        

        <el-table-column label="操作" prop="state">
          <template slot-scope="scope">
            <el-button
              type="danger"
              icon="el-icon-delete"
              size="mini"
              @click="deleteItem(scope.row.itemId,'deleteItemTemplate')"
            ></el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "ItemList",
  data() {
    return {
      item: [],
      //记分员
      scorers: [],
      //正在举行的届时列表
      seasonEnableOptions: [],
      //所有的届时列表
      allSeasonOptions:[],
      //选择的season
      selectSeasonId:"",
      //项目模板，用于新增修改项目
      itemTemplateOptions:[],

      itemDetail: [],

      itemAddTip:"如果无数据，请先添加项目模板",
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

      itemPlaceOptions: [
        {
          value: "田径场",
          label: "田径场",
        },
        {
          value: "体育馆",
          label: "体育馆",
        },
        {
          value: "跳水馆",
          label: "跳水馆",
        },
      ],

      itemUnitOptions: [
        {
          value: "秒",
          label: "秒",
        },
        {
          value: "米",
          label: "米",
        },
        {
          value: "个",
          label: "个",
        },
        {
          value: "分",
          label: "分数",
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
    addTemplateDialogVisible:false,
    templateListDialogVisible:false,
      addForm: {
        itemName: "",
        parentId:"",
        itemPlace: "",
        itemUnit: "",
        itemAmount:"",
        itemSex: "",
        startTime: "",
        endTime: "",
        user: {
          userId: "",
        },
        season: {
          seasonId: "",
        },
        scorer: [],
      },
      editForm: {
        itemId: "",
        itemName: "",
        parentId:"",
        itemPlace: "",
        itemSex: "",
        itemAmount:"",
        athleteAmount: "",
        startTime: "",
        endTime: "",
        user: {
          userId: "",
        },
        season: {
          seasonId: "",
        },
        scorer: [],
      },

      FormRules: {
        itemPlace: [
          //   { required: true, message: "请输入用户名", trigger: "blur" },
          { required: true, trigger: "blur" },
        ],
      },

      editDialogVisible: false,
      dialogTableVisible: false,
      dialogFormVisible: false,
    };
  },
  created() {
    this.getScorers();
    this.getSeasons();
    this.getTemplateOptions();


  },
  methods: {
    async page(isSelect) {
      if(isSelect===true){
        this.queryInfo.currentPage=1;
        this.queryInfo.pageSize = 10;
      }
      const _this = this;
      axios
         .get("/item/queryItem?season.seasonId="+_this.selectSeasonId+"&queryInfo=", { params: _this.queryInfo })
        .then((res) => {
          let data = res.data.data;
          _this.item = data.records;
          _this.queryInfo.currentPage = data.current;
          _this.total = data.total;
          _this.queryInfo.pageSize = data.size;
        });
    _this.getTemplateOptions();
        
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
        });
    },
//获取所有项目模板
     async getTemplateOptions() {
      const _this = this;
      axios
        .get(
          "/item/queryItemTemplate"
        )
        .then((res) => {
          _this.itemTemplateOptions = res.data.data;
        });
    },
    
   //获取运动会届时
     getSeasons() {
      //获取所有运动会届时
      const _this = this;
      axios
        .get("/season/querySeason?query=&currentPage=1&pageSize=999999999")
        .then((res) => {
        let data = res.data.data.records;
        data.push( {
          seasonId: 0,
          seasonStatus:0,
          seasonName: "所有运动会",
        })
        data.forEach((item,index)=>{
            if(item.seasonStatus!=0){
            _this.selectSeasonId=item.seasonId 
            }
          })

        _this.allSeasonOptions=data;
        _this.page();
        });

      //获取可用运动会届时
        axios
        .get("/season/querySeason?query=&currentPage=1&pageSize=999999999&seasonStatus=1")
        .then((res) => {
        let data = res.data.data.records;
        _this.seasonEnableOptions=data;
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

    async showEditDialog(id) {
      const _this = this;
      axios.get("/item/getItem?itemId=" + id).then((res) => {
        let data = res.data.data;
        _this.editForm = data;
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
    addItem() {
      const _this = this;
      _this.$refs.addFormRef.validate(async (valid) => {
        if (!valid) return;
        axios.post("/item/addItem", _this.addForm).then((res) => {
          if (res.data.status != 200) {
            return _this.$message.error(res.data.msg );
          }
          _this.$message.success("操作成功");
          _this.addDialogVisible = false;
          _this.addTemplateDialogVisible = false;
          _this.templateListDialogVisible=false
          _this.page();
        });
      });
    },

    async deleteItem(id,urlStr) {
      const _this = this;
      const confirmResult = await _this
        .$confirm("此操作将永久删除项目，是否继续？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        })
        .catch((err) => err);
      if (confirmResult !== "confirm") {
        return _this.$message.info("已取消删除");
      }
      axios.delete("/item/"+urlStr+"?itemId=" + id).then((res) => {
        if (res.data.status == 200) {
          _this.$message.success("删除成功");
          _this.page();
        } else {
          _this.$message.error(res.data.msg);
        }
      });
    },
    editItem() {
      const _this = this;
      _this.$refs.editFormRef.validate(async (valid) => {
        if (!valid) {
          return;
        }
        _this.editForm.itemName=_this.editForm.itemName.replace("(男)","")
       _this.editForm.itemName= _this.editForm.itemName.replace("(女)","")
        axios.put("/item/editItem", _this.editForm).then((res) => {
          if (res.data.status != 200) {
            return _this.$message.error(res.data.msg);
          }
          _this.$message.success("操作成功");
        _this.editDialogVisible=false;
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
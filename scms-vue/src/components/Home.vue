<template>
  <!--引入布局-->
  <el-container class="home-container">
    <el-header>
      <div class="div">
        <img class="img" src="../assets/hzu_logo.jpg" alt />
        <span class="span">运动会综合管理系统</span>
      </div>
      <div>
        <el-button size="mini" type="info" @click="editDialogVisible = true"
          >修改密码</el-button
        >
        <el-button size="mini" type="info" @click="logout">安全退出</el-button>
      </div>
    </el-header>
    <el-container>
      <el-aside :width="iscollapse ? '64px' : '200px'">
        <div class="toggle-button" @click="toggleCollapase">
          <i v-show="!this.iscollapse" class="el-icon-d-arrow-left"></i>
          <i v-show="this.iscollapse" class="el-icon-d-arrow-right"></i>
        </div>
        <el-menu
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#409eff"
          :collapse="iscollapse"
          :collapse-transition="false"
          :router="isrouter"
          :default-active="activePath"
        >
          <!--一级菜单 -->
          <el-submenu
            v-show="
              item.mainmenu.type == 0 ||
              currentUser.userType == item.mainmenu.type
            "
            :index="item.mainmenu.path"
            v-for="item in menuList"
            :key="item.mainmenu.mainmenuId"
          >
            <template slot="title">
              <i :class="iconsObj[item.mainmenu.mainmenuId]"></i>
              <span style="margin-left:1em">{{ item.mainmenu.title }}</span>
            </template>
            <!--二级菜单-->
            <el-menu-item
              :index="item.mainmenu.path + sub.path"
              v-for="sub in item.mlist"
              :key="sub.id"
              v-show="sub.type == 0 || currentUser.userType == sub.type"
              @click="savePathState(item.mainmenu.path + sub.path)"
            >
              <template slot="title">
                <!-- <i :class="iconsObj[sub.id]"></i> -->
                <span>{{ sub.title }}</span>
              </template>
            </el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>
      <el-main>
        <transition name="fade" mode="out-in">
          <router-view></router-view>
        </transition>
      </el-main>
    </el-container>

    <!--修改用户区域-->
    <el-dialog
      title="修改密码"
      :visible.sync="editDialogVisible"
      width="40%"
      @close="editDialogClosed"
    >
      <el-form
        :model="editForm"
        ref="editFormRef"
        label-width="180px"
        class="demo-ruleForm"
      >
        <el-form-item label="请输入旧密码" >
          <el-input type="password" v-model="editForm.password"></el-input>
        </el-form-item>
        <el-form-item label="请输入新密码" >
          <el-input type="password" v-model="editForm.newPassword"></el-input>
        </el-form-item>
        <el-form-item label="请再次确认密码">
          <el-input
            type="password"
            v-model="editForm.newPasswordConfirm"
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确定</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>

<script>
export default {
  name: "Home",
  // 页面加载
  data() {
    return {
      // 菜单列表
      menuList: [],

      editForm: {
        username: JSON.parse(localStorage.getItem("user")).username,
        password: "",
        newPassword: "",
        newPasswordConfirm: "",
      },

      editDialogVisible: false,

      //当前用户
      currentUser: "",

      iscollapse: false,
      isrouter: true,
      activePath: "/welcome",
      iconsObj: {
        100: "iconfont icon-tiyuketeshuchuli",
        200: "iconfont icon-bumen",
        300: "iconfont icon-peixunhuodongdongyuandahui",
        400: "iconfont icon-shebeiyanshoudan",
        500: "iconfont icon-yuangong",
        600: "iconfont icon-zhishijingsai",
        700: "iconfont icon-chengjishouji",
        800: "iconfont icon-canjiaxiaowaijingsai",
        900: "iconfont icon-dangtuanzhishijuesai",
        1000: "iconfont icon-xinxibeian",

      },
    };
  },
  created() {
    this.getMenuList();
    this.currentUser = JSON.parse(localStorage.getItem("user"));
    this.activePath = window.sessionStorage.getItem("path");
  },
  methods: {
    logout() {
      const _this = this;
      _this.$http
        .get("/user/logout", {
          headers: {
            Authorization: localStorage.getItem("token"),
          },
        })
        .then((res) => {
          _this.$store.commit("REMOVE_INFO");
          _this.$router.push("/login");
          _this.savePathState("/")
        });
    },

    //修改密码
    changePassword() {
      const _this = this;
      if (
        _this.editForm.password == "" ||
        _this.editForm.newPassword == "" ||
        _this.editForm.newPassword == ""
      ) {
        return _this.$message.info("请输入完整");
      }
      if (_this.editForm.newPassword != _this.editForm.newPasswordConfirm) {
        _this.editForm.newPassword = "";
        _this.editForm.newPasswordConfirm = "";
        return _this.$message.info("两次密码输入不一致");
      }

      _this.$http.put("/user/changePwd", _this.editForm).then((res) => {
        if (res.data.status != 200) {
          _this.editForm.newPassword = "";
          _this.editForm.newPasswordConfirm = "";
          return _this.$message.info("修改失败,请检查密码是否正确");
        }
        _this.$message.info("修改密码成功，请重新登录");
        _this.$store.commit("REMOVE_INFO");
        _this.$router.push("/login");
      });
    },

    //获取导航菜单方法
    async getMenuList() {
      const _this = this;
      _this.$http.get("menu").then((res) => {
        //如果路径为401
        if (window.location.pathname != "/401") {
          if (res.status === 200) {
            _this.menuList = res.data.data;
            _this.$message.success(
              " 欢迎您! " + JSON.parse(localStorage.getItem("user")).nickname
            );
          } else {
            _this.$message.error("获取列表失败");
          }
        }
      });
    },
    // 控制伸缩
    toggleCollapase() {
      const _this = this;
      _this.iscollapse = !_this.iscollapse;
    },
    // 保存路径
    savePathState(activePath) {
      // 放入session
      window.sessionStorage.setItem("path", activePath);
      this.activePath = activePath;
    },

    editDialogClosed() {
      const _this = this;
      _this.$refs.editFormRef.resetFields();
    },
  },
};
</script>

<style lang="less" scoped>
.home-container {
  height: 100%;
}

.el-header {
  background-color: #2b4b6b;
  display: flex;
  justify-content: space-between;
  padding-left: 0%;
  align-items: center;
  color: #ffffff;
  font-size: 20px;
}

.div {
  display: flex;
  align-items: center;
}

.span {
  margin-left: 15px;
}

.el-aside {
  background-color: #444444;
}
.el-menu {
  border-right: none;
}

.el-main {
  background-color: #eaedf1;
}

.img {
  width: 55px;
  height: 55px;
}
/*|||按钮样式*/
.toggle-button {
  background-color: #4a5064;
  font-size: 10px;
  line-height: 24px;
  color: #fff;
  text-align: center;
  letter-spacing: 0.2em;
  cursor: pointer; /*显示小手*/
}

.fade-enter {
  opacity: 0;
}
.fade-leave {
  opacity: 1;
}
.fade-enter-active {
  transition: opacity 0.3s;
}
.fade-leave-active {
  opacity: 0;
  transition: opacity 0.3s;
}
</style>
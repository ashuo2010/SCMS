<template>
  <div :style="bg" class="login_container">
    <!--登录块-->
    <div class="login_box">
      <div class="title">运 动 会 综 合 管 理 系 统</div>
      <!--logo-->
      <!-- <div class="avatar_box">
        <img src="../assets/logo.png" alt />
      </div> -->
      <!--表单-->
      <el-form
          ref="loginForm"
          :model="loginForm"
          :rules="loginRules"
          class="login_form"
          label-width="0"
      >
        <!--用户名-->
        <el-form-item>
          用户名：
          <el-input
              v-model="loginForm.username"
              prefix-icon="iconfont icon-denglu"
          ></el-input>
        </el-form-item>
        <!--密码-->
        <el-form-item>
          密码：
          <el-input
              v-model="loginForm.password"
              prefix-icon="iconfont icon-mima"
              type="password"
              @keyup.enter.native="submitForm('loginForm')"
          ></el-input>
        </el-form-item>
        <!--按钮-->
        <el-form-item class="btns">
          <el-button type="primary" @click="submitForm('loginForm')"
          >登录
          </el-button
          >
          <el-button type="primary" @click="resetForm('loginForm')"
          >重置
          </el-button
          >
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      bg: {
        backgroundImage: "url(" + require("../assets/hzu_gate.jpg") + ")",
        backgroundRepeat: "no-repeat",
        backgroundSize: "cover",
        backgroundPosition: "0 -400px",
      },

      loginForm: {
        username: "",
        password: "",
      },
      loginRules: {
        username: [
          {required: true, message: "请输入用户名", trigger: "blur"},
          {
            min: 3,
            max: 30,
            message: "长度在 3 到 30 个字符",
            trigger: "blur",
          },
        ],
        password: [
          {required: true, message: "请输入密码", trigger: "blur"},
          {
            min: 1,
            max: 99,
            message: "长度在 1 到 99 个字符",
            trigger: "blur",
          },
        ],
      },
    };
  },
  methods: {
    /*处理登录*/
    submitForm(loginForm) {
      /*验证校验规则*/
      this.$refs["loginForm"].validate(async (valid) => {
        if (valid) {
          /*访问后台*/
          const _this = this;
          this.$http.post("/user/login", this.loginForm).then((res) => {
            if (res.data.status === 200) {
              // _this.$message.success("登录成功");
              const jwt = res.headers["authorization"];
              const user = res.data.data;
              _this.$store.commit("SET_TOKEN", jwt);
              _this.$store.commit("SET_USERINFO", user);
              /*路由跳转*/
              _this.$router.push("/home");
            } else {
              _this.$message.error(res.data.msg);
            }
          });
        } else {
          return false;
        }
      });
    },
    resetForm(loginForm) {
      this.loginForm = {
        username: "",
        password: "",
      }
    },
  },
};
</script>

<style lang="less" scoped>
/*根节点样式*/
.login_container {
  //background-color: #2b4b6b;
  height: 100%;
}

/*输入框样式*/
.login_box {
  width: 450px;
  height: 330px;
  background-color: #909399;
  border-radius: 3px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);

  .avatar_box {
    width: 130px;
    height: 130px;
    border: 1px solid #eee;
    border-radius: 50%;
    padding: 5px;
    box-shadow: 0 0 2px #ddd;
    position: absolute;
    left: 50%;
    transform: translate(-50%, -50%);
    background-color: #eee;

    img {
      width: 100%;
      height: 100%;
      border-radius: 50%;
      background-color: #eee;
    }
  }
}

.title {
  font-family: "Helvetica Neue";
  text-align: center;
  font-size: large;
  height: 40px;
  margin-top: 30px;
}

.btns {
  display: flex;
  justify-content: flex-end;
}

.login_form {
  position: absolute;
  bottom: 0%;
  width: 100%;
  padding: 0 10px;
  box-sizing: border-box;
}
</style>
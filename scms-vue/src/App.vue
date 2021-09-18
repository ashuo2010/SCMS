<template>
  <div id="app">
    <transition :name="transitionName">
      <router-view class="router-view"></router-view>
    </transition>
  </div>
</template>

<script>
export default {
  name: "app",
  data() {
    return {
      transitionName: "vux-pop-in",
    };
  },
  watch: {
    $route(to, from) {
      if (to.meta.index > from.meta.index) {
        this.transitionName = "vux-pop-in";
      } else {
        this.transitionName = "vux-pop-out";
      }
    },
  },
};
</script>

<style>
.vux-pop-out-enter-active,
.vux-pop-out-leave-active,
.vux-pop-in-enter-active,
.vux-pop-in-leave-active {
  will-change: transform;
  transition: all 3000ms;
  height: 100%;
  position: absolute;
  backface-visibility: hidden;
  perspective: 1000;
}

.vux-pop-out-enter {
  opacity: 0;
  transform: translate3d(-100%, 0, 0);
}

.vux-pop-out-leave-active {
  opacity: 0;
  transform: translate3d(100%, 0, 0);
}

.vux-pop-in-enter {
  opacity: 0;
  transform: translate3d(100%, 0, 0);
}

.vux-pop-in-leave-active {
  opacity: 0;
  transform: translate3d(-100%, 0, 0);
}

.router-view {
  width: 100%;
  position: absolute;
  -webkit-transition: all 1s cubic-bezier(0.55, 0, 0.1, 1);
  -moz-transition: all 1s cubic-bezier(0.55, 0, 0.1, 1);
  -ms-transition: all 1s cubic-bezier(0.55, 0, 0.1, 1);
  -o-transition: all 1s cubic-bezier(0.55, 0, 0.1, 1);
  transition: all 1s cubic-bezier(0.55, 0, 0.1, 1);
  height: 100%;
}
</style>

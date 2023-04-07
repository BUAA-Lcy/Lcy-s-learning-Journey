<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/>
  </div>
    <div>
        <!-- 根据 isLogin 状态显示不同内容 -->
        <div v-if="isLogin">状态 1（已登录）</div>
        <div v-else>状态 2（未登录）<router-link to="/login">Login</router-link></div>
    </div>
</template>

<script>
// @ is an alias to /src
 import HelloWorld from '@/components/HelloWorld.vue'

export default {
  name: 'HomeView',
  components: {
    HelloWorld
  },
    computed: {
            isLogin() {
                // 从 Vuex store 中获取 isLogin 状态
                return this.$store.state.isLogin;
            }
        }
}
</script>


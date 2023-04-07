<template>
    <div class="about">
        <h1>This is an Login page</h1>
    </div>
    <div>
        <p v-if="isLogin">已登录</p>
        <p v-else>未登录</p>

            <!-- 根据 isLogin 状态显示登录或登出按钮 -->
        <el-button v-if="!isLogin" @click="onLogin">登录</el-button>
        <el-button v-else @click="onLogout">登出</el-button>

    </div>
</template>
<style>
    el-button {
        background-color: #42b983;
        color: black;
        font-size: 20px;
        padding: 10px 20px;

    }
</style>
<script>
import { mapState, mapMutations } from 'vuex'
import store from '@/store' // 导入 Vuex store 对象

export default {
    store, // 将 Vuex store 对象传递给组件的 store 选项

    computed: {
        isLogin() {
            // 从 Vuex store 中获取 isLogin 状态
            return this.$store.state.isLogin;
        }
    },
    methods: {
        onLogin() {
            // 触发 Vuex store 中的 login mutation
            this.$store.commit('login');
        },
        onLogout() {
            // 触发 Vuex store 中的 logout mutation
            this.$store.commit('logout');
        }
    },

}
</script>

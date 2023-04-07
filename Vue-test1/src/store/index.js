
import { createStore } from 'vuex' // 导入 createStore 函数

// 定义状态和 mutations
const store = createStore({
  state: {
    isLogin: false // 初始化 isLogin 状态为 false
  },
  mutations: {
    login(state) {
      state.isLogin = true // 设置 isLogin 为 true
    },
    logout(state) {
      state.isLogin = false // 设置 isLogin 为 false
    }
  }
})

export default store

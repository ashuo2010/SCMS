import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex);
export default new Vuex.Store({
    state: {
        token: '',
        user: JSON.parse(localStorage.getItem("user"))
    },
    mutations: {
        // set
        SET_TOKEN: (state, token) => {
            state.token = token;
            localStorage.setItem("token", token);
        },
        SET_USERINFO: (state, user) => {
            state.user = user;
            localStorage.setItem("user", JSON.stringify(user));
        },
        REMOVE_INFO: (state) => {
            state.token = '';
            state.user = {};
            localStorage.setItem("token", '');
            localStorage.setItem("user", JSON.stringify(''));

        },
    },
    getters: {
        // get
        getUser: state => {
            return state.user;
        }
    },
    actions: {},
    modules: {}
})
import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../components/Login'
import Home from '../components/Home'
import Welcome from "../components/Welcome";
import Page401 from "../components/401"
import SeasonList from "../components/admin/SeasonList";
import TeamList from "../components/admin/TeamList";
import UserList from "../components/admin/UserList";
import ItemList from "../components/admin/ItemList";
import AthleteList from "../components/admin/AthleteList";
import ScoreList from "../components/admin/ScoreList";
import RecordList from "../components/admin/RecordList";
import PersonRanking from "../components/admin/PersonRanking";
import TeamRanking from "../components/admin/TeamRanking";
import SyslogList from "../components/admin/SyslogList";
import SystemReset from "../components/admin/SystemReset";
import SignItem from "../components/athlete/SignItem";
import MyItem from "../components/athlete/MyItem";
import AthleteScoreList from "../components/admin/AthleteScoreList";

Vue.use(VueRouter);

const routes = [{
        path: "/",
        redirect: "/login",
    },
    {
        path: "/login",
        component: Login
    },
    {
        path: "/home",
        component: Home,
        redirect: "welcome",
        children: [{
                path: "/welcome",
                component: Welcome
            },
            {
                path: "/401",
                component: Page401
            },
            {
                path: "/season/seasonlist",
                component: SeasonList
            },
            {
                path: "/team/teamlist",
                component: TeamList
            },
            {
                path: "/user/userlist",
                component: UserList
            },
            {
                path: "/item/itemlist",
                component: ItemList
            },
            {
                path: "/athlete/athletelist",
                component: AthleteList
            },
            {
                path: "/score/scorelist",
                component: ScoreList
            },
            {
                path: "/score/athletescorelist",
                component: AthleteScoreList
            },
            {
                path: "/ranking/personRanking",
                component: PersonRanking
            },
            {
                path: "/ranking/teamRanking",
                component: TeamRanking
            },

            {
                path: "/record/recordlist",
                component: RecordList
            },

            {
                path: "/syslog/sysloglist",
                component: SyslogList
            },
            {
                path: "/syslog/systemreset",
                component: SystemReset
            },


            {
                path: "/athleteItem/signitem",
                component: SignItem
            }, {
                path: "/athleteItem/myitem",
                component: MyItem
            },
        ]
    }
];

const router = new VueRouter({
    mode: 'history',
    routes
});

export default router
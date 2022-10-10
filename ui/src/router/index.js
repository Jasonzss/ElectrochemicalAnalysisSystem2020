import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

import Layout from '@/layout'

export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/auth-redirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/login'
    // redirect: '/usercenter/usermessage',
    // children: [
    //   {
    //     path: 'usermessage',
    //     component: () => import('@/views/usercenter/usermessage'),
    //     name: 'UserMessage',
    //     meta: { title: 'Dashboard', icon: 'kuaijierukou', affix: true }
    //   }
    // ]
  },

  // 个人中心路由注册
  {
    path: '/usercenter',
    component: Layout,
    name: 'UserCenter',
    redirect: '/usercenter/usermessage',
    meta: {
      title: '个人中心',
      icon: 'el-icon-user',
      roles: ['admin', 'editor']
    },
    children: [
      {
        path: 'usermessage',
        component: () => import('@/views/usercenter/usermessage'),
        name: 'userMessage',
        meta: {
          title: '个人信息',
          icon: 'el-icon-place',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'edcenter',
        component: () => import('@/views/usercenter/edcenter'),
        name: 'EDCenter',
        meta: {
          title: '实验数据管理中心',
          icon: 'odbc',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'reportcenter',
        component: () => import('@/views/usercenter/reportcenter'),
        name: 'ReportCenter',
        meta: {
          title: '实验报告管理中心',
          icon: 'example',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'deccenter',
        component: () => import('@/views/usercenter/deccenter'),
        name: 'DECCenter',
        meta: {
          title: '算法管理中心',
          icon: 'shuju4',
          roles: ['admin', 'editor']
        }
      }
    ]
  },
  // 数据分析路由注册
  {
    path: '/analysis',
    component: Layout,
    name: 'Analysis',
    redirect: '/analysis/modelanalysis',
    meta: {
      title: '数据分析',
      icon: 'zhuzhuangtu',
      roles: ['admin', 'editor']
    },
    children: [
      {
        path: 'modelanalysis',
        component: () => import('@/views/analysis/modelanalysis'),
        name: 'ModelAnalysis',
        meta: {
          title: '建模分析',
          icon: 'dianzitijianbaogao',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'waveanalysis',
        component: () => import('@/views/analysis/waveanalysis'),
        name: 'WaveAnalysis',
        meta: {
          title: '波形分析',
          icon: 'fenxi',
          roles: ['admin', 'editor']
        }
      }
    ]
  },
  // 数据管理路由注册
  {
    path: '/datacontrol',
    component: Layout,
    name: 'DataControl',
    redirect: '/datacontrol/mattercontrol',
    meta: {
      title: '数据管理',
      icon: 'kucunfenxi',
      roles: ['admin', 'editor']
    },
    children: [
      {
        path: 'mattercontrol',
        component: () => import('@/views/datacontrol/mattercontrol'),
        name: 'MatterControl',
        meta: {
          title: '物质类型管理',
          icon: 'shuju2',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'deccontrol',
        component: () => import('@/views/datacontrol/deccontrol'),
        name: 'DECControl',
        meta: {
          title: '算法管理',
          icon: 'shuju',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'experimentdata',
        component: () => import('@/views/datacontrol/experimentdata'),
        name: 'ExperimentData',
        meta: {
          title: '实验数据管理',
          icon: 'shuju1',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'experimentreport',
        component: () => import('@/views/datacontrol/experimentreport'),
        name: 'ExperimentReport',
        meta: {
          title: '实验报告管理',
          icon: 'huayandanshibie',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'checkcenter',
        component: () => import('@/views/datacontrol/checkcenter'),
        name: 'CheckCenter',
        meta: {
          title: '审核中心',
          icon: 'gongnenglan-shenhexitong',
          roles: ['admin']
        }
      }
    ]
  },
  // 系统管理路由注册
  {
    path: '/system',
    component: Layout,
    name: 'System',
    redirect: '/system/permisson',
    meta: {
      title: '系统管理',
      icon: 'xitongpeizhi',
      roles: ['admin']
    },
    children: [
      {
        path: 'permisson',
        component: () => import('@/views/system/permisson'),
        name: 'Permisson',
        meta: {
          title: '权限管理',
          icon: 'gongnenglan-kehuguanli',
          roles: ['admin']
        }
      },
      {
        path: 'user',
        component: () => import('@/views/system/user'),
        name: 'User',
        meta: {
          title: '用户管理',
          icon: 'gongnenglan-yonghuguanli',
          roles: ['admin']
        }
      },
      {
        path: 'logcontrol',
        component: () => import('@/views/system/logcontrol'),
        name: 'LogControl',
        meta: {
          title: '日志管理',
          icon: 'shujuchaxun',
          roles: ['admin']
        }
      },
      {
        path: 'backup',
        component: () => import('@/views/system/backup'),
        name: 'Backup',
        meta: {
          title: '备份管理',
          icon: 'skill',
          roles: ['admin']
        }
      }
    ]
  },

  { path: '*', redirect: '/404', hidden: true }
]

export const userRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/auth-redirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/login'
    // redirect: '/usercenter/usermessage',
    // children: [
    //   {
    //     path: 'usermessage',
    //     component: () => import('@/views/usercenter/usermessage'),
    //     name: 'UserMessage',
    //     meta: { title: 'Dashboard', icon: 'kuaijierukou', affix: true }
    //   }
    // ]
  },

  // 个人中心路由注册
  {
    path: '/usercenter',
    component: Layout,
    name: 'UserCenter',
    redirect: '/usercenter/usermessage',
    meta: {
      title: '个人中心',
      icon: 'el-icon-user',
      roles: ['admin', 'editor']
    },
    children: [
      {
        path: 'usermessage',
        component: () => import('@/views/usercenter/usermessage'),
        name: 'userMessage',
        meta: {
          title: '个人信息',
          icon: 'el-icon-place',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'edcenter',
        component: () => import('@/views/usercenter/edcenter'),
        name: 'EDCenter',
        meta: {
          title: '实验数据管理中心',
          icon: 'odbc',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'reportcenter',
        component: () => import('@/views/usercenter/reportcenter'),
        name: 'ReportCenter',
        meta: {
          title: '实验报告管理中心',
          icon: 'example',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'deccenter',
        component: () => import('@/views/usercenter/deccenter'),
        name: 'DECCenter',
        meta: {
          title: '算法管理中心',
          icon: 'shuju4',
          roles: ['admin', 'editor']
        }
      }
    ]
  },
  // 数据分析路由注册
  {
    path: '/analysis',
    component: Layout,
    name: 'Analysis',
    redirect: '/analysis/modelanalysis',
    meta: {
      title: '数据分析',
      icon: 'zhuzhuangtu',
      roles: ['admin', 'editor']
    },
    children: [
      {
        path: 'modelanalysis',
        component: () => import('@/views/analysis/modelanalysis'),
        name: 'ModelAnalysis',
        meta: {
          title: '建模分析',
          icon: 'dianzitijianbaogao',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'waveanalysis',
        component: () => import('@/views/analysis/waveanalysis'),
        name: 'WaveAnalysis',
        meta: {
          title: '波形分析',
          icon: 'fenxi',
          roles: ['admin', 'editor']
        }
      }
    ]
  }
]

export const adminRoutes = userRoutes + [
  // 数据管理路由注册
  {
    path: '/datacontrol',
    component: Layout,
    name: 'DataControl',
    redirect: '/datacontrol/mattercontrol',
    meta: {
      title: '数据管理',
      icon: 'kucunfenxi',
      roles: ['admin', 'editor']
    },
    children: [
      {
        path: 'mattercontrol',
        component: () => import('@/views/datacontrol/mattercontrol'),
        name: 'MatterControl',
        meta: {
          title: '物质类型管理',
          icon: 'shuju2',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'deccontrol',
        component: () => import('@/views/datacontrol/deccontrol'),
        name: 'DECControl',
        meta: {
          title: '算法管理',
          icon: 'shuju',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'experimentdata',
        component: () => import('@/views/datacontrol/experimentdata'),
        name: 'ExperimentData',
        meta: {
          title: '实验数据管理',
          icon: 'shuju1',
          roles: ['admin', 'editor']
        }
      },
      {
        path: 'experimentreport',
        component: () => import('@/views/datacontrol/experimentreport'),
        name: 'ExperimentReport',
        meta: {
          title: '实验报告管理',
          icon: 'huayandanshibie',
          roles: ['admin', 'editor']
        }
      }
    ]
  }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router

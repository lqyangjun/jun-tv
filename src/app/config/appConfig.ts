/**
 * 应用配置文件
 * 统一管理应用的各项配置参数
 */

export const APP_CONFIG = {
  // 应用信息
  app: {
    name: '苹果CMS TV版',
    version: 'v2.0.1',
    description: '专为电视大屏设计的视频点播应用',
  },

  // 焦点导航配置
  focus: {
    // 是否启用焦点导航
    enabled: true,
    // 焦点移动动画时长（ms）
    transitionDuration: 300,
    // 自动滚动到焦点元素
    autoScroll: true,
    // 滚动行为
    scrollBehavior: 'smooth' as ScrollBehavior,
  },

  // 轮播配置
  banner: {
    // 自动轮播间隔（ms）
    autoPlayInterval: 5000,
    // 是否启用自动轮播
    autoPlay: true,
    // 轮播动画时长（ms）
    transitionDuration: 700,
  },

  // 视频详情页配置
  detail: {
    // 自动播放
    autoPlay: true,
    // 全屏倒计时（秒）
    fullscreenCountdown: 3,
    // 是否自动全屏
    autoFullscreen: true,
    // 默认播放源
    defaultPlaySource: 'yn',
  },

  // 历史记录配置
  history: {
    // 最大保存数量
    maxItems: 20,
    // 是否自动保存
    autoSave: true,
  },

  // 搜索配置
  search: {
    // 最小搜索长度
    minLength: 1,
    // 是否实时搜索
    realtime: true,
    // 搜索防抖延迟（ms）
    debounceDelay: 300,
  },

  // 内容展示配置
  content: {
    // 首页每行显示数量
    homeRowItems: 10,
    // 分类页网格列数
    categoryColumns: 6,
    // 搜索结果网格列数
    searchColumns: 4,
    // 历史记录网格列数
    historyColumns: 5,
  },

  // 样式配置
  theme: {
    // 主题色
    primaryColor: 'cyan',
    // 焦点高亮色
    focusColor: '#06B6D4',
    // 背景渐变
    background: {
      from: '#0a1128',
      via: '#0d1a3a',
      to: '#06102a',
    },
    // 字体大小
    fontSize: {
      xs: '0.75rem',
      sm: '0.875rem',
      base: '1rem',
      lg: '1.125rem',
      xl: '1.25rem',
      '2xl': '1.5rem',
      '3xl': '1.875rem',
      '4xl': '2.25rem',
      '5xl': '3rem',
    },
  },

  // 网络配置
  network: {
    // API基础URL（模拟数据时为空）
    apiBaseUrl: '',
    // 请求超时时间（ms）
    timeout: 10000,
    // 重试次数
    retryCount: 3,
  },

  // 播放器配置
  player: {
    // 默认播放内核
    defaultVodCore: 'IJK',
    defaultLiveCore: 'IJK',
    // 默认解码方式
    defaultDecoder: 'auto',
    // 默认显示比例
    defaultAspectRatio: '等比缩放',
    // 跳过片头时间（秒）
    skipHeadTime: 0,
    // 跳过片尾时间（秒）
    skipTailTime: 0,
  },

  // 公告配置
  announcement: {
    // 是否显示公告
    enabled: true,
    // 公告内容
    text: '本应用所调用的内容侵犯其合法权',
    // 公告滚动速度（px/s）
    scrollSpeed: 30,
  },

  // 功能开关
  features: {
    // 是否启用语音搜索
    voiceSearch: false,
    // 是否启用收藏功能
    favorites: true,
    // 是否启用追剧功能
    series: true,
    // 是否启用评分功能
    rating: true,
    // 是否启用评论功能
    comments: false,
    // 是否启用分享功能
    share: false,
  },

  // 分类配置
  categories: [
    { id: 'live', name: '直播', icon: '📡', enabled: true },
    { id: 'tv', name: '电视剧', icon: '📺', enabled: true },
    { id: 'movie', name: '电影', icon: '🎬', enabled: true },
    { id: 'variety', name: '综艺', icon: '🎤', enabled: true },
    { id: 'anime', name: '动漫', icon: '🎨', enabled: true },
    { id: 'documentary', name: '美剧', icon: '🌍', enabled: true },
    { id: 'korean', name: '韩剧', icon: '🇰🇷', enabled: true },
    { id: '4k', name: '4K', icon: '📽️', enabled: true },
    { id: 'kids', name: '少儿', icon: '🧒', enabled: true },
  ],

  // 调试配置
  debug: {
    // 是否启用调试模式
    enabled: false,
    // 是否显示焦点ID
    showFocusId: false,
    // 是否显示键盘指南
    showKeyGuide: false,
    // 是否记录导航日志
    logNavigation: false,
  },
};

/**
 * 获取配置项
 */
export function getConfig<T>(path: string): T {
  const keys = path.split('.');
  let value: any = APP_CONFIG;
  
  for (const key of keys) {
    value = value?.[key];
    if (value === undefined) break;
  }
  
  return value as T;
}

/**
 * 更新配置项（运行时）
 */
export function updateConfig(path: string, value: any): void {
  const keys = path.split('.');
  let obj: any = APP_CONFIG;
  
  for (let i = 0; i < keys.length - 1; i++) {
    obj = obj[keys[i]];
  }
  
  obj[keys[keys.length - 1]] = value;
}

/**
 * 重置配置为默认值
 */
export function resetConfig(): void {
  // 实现配置重置逻辑
  console.log('配置已重置为默认值');
}

export default APP_CONFIG;

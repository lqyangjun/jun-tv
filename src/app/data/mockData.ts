// 模拟数据
export interface Video {
  id: string;
  title: string;
  year: string;
  category: string;
  description: string;
  poster: string;
  featured?: boolean;
  episodes?: number;
  currentEpisode?: number;
  rating?: number;
  tag?: string;
}

export const categories = [
  { id: 'live', name: '直播', icon: '📡' },
  { id: 'tv', name: '电视剧', icon: '📺' },
  { id: 'movie', name: '电影', icon: '🎬' },
  { id: 'variety', name: '综艺', icon: '🎤' },
  { id: 'anime', name: '动漫', icon: '🎨' },
  { id: 'documentary', name: '美剧', icon: '🌍' },
  { id: 'korean', name: '韩剧', icon: '🇰🇷' },
  { id: '4k', name: '4K', icon: '📽️' },
  { id: 'kids', name: '少儿', icon: '🧒' },
];

export const videos: Video[] = [
  {
    id: '1',
    title: '生命树',
    year: '2026',
    category: '电视剧',
    description: '简介：上世纪九十年代，青海高原上的玛沁县度很娜代尔牧业一度深陷贫困，某思迈边防站以墩坡木拉赛尔矿产和野生动物贸易交换，大量的盗挖盗猎分子为抢夺利益而非法取获和盗猎...',
    poster: 'figma:asset/e68471f2bf01a3612f508216b90c0ef44cfa25e2.png',
    featured: true,
    episodes: 32,
    rating: 9.2,
    tag: '热播',
  },
  {
    id: '2',
    title: '成何体统',
    year: '2026',
    category: '电视剧',
    description: '简介，断场场岛主要花香外进入一本穿书文的副剧之中，与同是穿越者的张三相遇...',
    poster: 'https://images.unsplash.com/photo-1673877489519-3016b28cd0d7?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjaGluZXNlJTIwZHJhbWElMjBzZXJpZXN8ZW58MXx8fHwxNzc0ODU1ODAxfDA&ixlib=rb-4.1.0&q=80&w=1080',
    featured: true,
    episodes: 32,
    rating: 8.7,
    tag: '独播',
  },
  {
    id: '3',
    title: '太平年',
    year: '2026',
    category: '电视剧',
    description: '古装历史剧，讲述太平盛世背后的故事...',
    poster: 'https://images.unsplash.com/photo-1675326625745-9333cda1b21b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxoaXN0b3JpY2FsJTIwYW5jaWVudCUyMHBhbGFjZSUyMGRyYW1hfGVufDF8fHx8MTc3NDg1NzQ5N3ww&ixlib=rb-4.1.0&q=80&w=1080',
    featured: true,
    episodes: 40,
    rating: 8.9,
    tag: '古装',
  },
  {
    id: '4',
    title: '非常检控观',
    year: '2026',
    category: '电视剧',
    description: '检察官题材电视剧...',
    poster: 'https://images.unsplash.com/photo-1726452486208-8ecf3dc483b6?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx1cmJhbiUyMGNpdHklMjBuaWdodCUyMGRyYW1hJTIwc2NlbmV8ZW58MXx8fHwxNzc0ODU3NDk3fDA&ixlib=rb-4.1.0&q=80&w=1080',
    featured: true,
    episodes: 24,
    rating: 8.5,
    tag: '悬疑',
  },
  {
    id: '5',
    title: '星际穿越',
    year: '2025',
    category: '电影',
    description: '科幻电影经典之作，讲述宇航员穿越虫洞拯救人类...',
    poster: 'https://images.unsplash.com/photo-1758142369757-dc884f1feba5?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxzY2ktZmklMjBtb3ZpZSUyMHBvc3RlciUyMGRhcmt8ZW58MXx8fHwxNzc0ODU3NDk0fDA&ixlib=rb-4.1.0&q=80&w=1080',
    rating: 9.4,
    tag: '科幻',
  },
  {
    id: '6',
    title: '快乐大本营',
    year: '2026',
    category: '综艺',
    description: '娱乐综艺节目...',
    poster: 'https://images.unsplash.com/photo-1721218212636-8a3a162fd293?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjb25jZXJ0JTIwc3RhZ2UlMjBlbnRlcnRhaW5tZW50JTIwc2hvd3xlbnwxfHx8fDE3NzQ4NTc0OTV8MA&ixlib=rb-4.1.0&q=80&w=1080',
    rating: 7.8,
    tag: '综艺',
  },
  {
    id: '7',
    title: '超能陆战队',
    year: '2024',
    category: '动漫',
    description: '动漫电影，讲述少年与机器人大白的冒险...',
    poster: 'https://images.unsplash.com/photo-1767557125491-b3483567d843?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxhbmltYXRpb24lMjBjYXJ0b29uJTIwY29sb3JmdWx8ZW58MXx8fHwxNzc0NzQ0ODcyfDA&ixlib=rb-4.1.0&q=80&w=1080',
    rating: 8.6,
    tag: '动漫',
  },
  {
    id: '8',
    title: '权力的游戏',
    year: '2023',
    category: '美剧',
    description: '史诗级美剧，七大王国争夺铁王座...',
    poster: 'https://images.unsplash.com/photo-1773518011746-4f1c46ddced1?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmYW50YXN5JTIwYWR2ZW50dXJlJTIwZXBpYyUyMG1vdmllfGVufDF8fHx8MTc3NDg1NzQ5Nnww&ixlib=rb-4.1.0&q=80&w=1080',
    episodes: 73,
    rating: 9.5,
    tag: '美剧',
  },
  {
    id: '9',
    title: '暗夜行者',
    year: '2026',
    category: '电视剧',
    description: '悬疑犯罪剧集，卧底警探深入犯罪组织...',
    poster: 'https://images.unsplash.com/photo-1758525589403-1235e5d6c300?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxkZXRlY3RpdmUlMjBteXN0ZXJ5JTIwdGhyaWxsZXIlMjBmaWxtfGVufDF8fHx8MTc3NDg1NzQ5Nnww&ixlib=rb-4.1.0&q=80&w=1080',
    episodes: 28,
    rating: 8.8,
    tag: '悬疑',
  },
  {
    id: '10',
    title: '武林风云',
    year: '2025',
    category: '电影',
    description: '武侠动作大片，江湖恩怨情仇...',
    poster: 'https://images.unsplash.com/photo-1767465262698-696af3387163?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtYXJ0aWFsJTIwYXJ0cyUyMGFjdGlvbiUyMG1vdmllfGVufDF8fHx8MTc3NDg1NzQ5Nnww&ixlib=rb-4.1.0&q=80&w=1080',
    rating: 8.3,
    tag: '动作',
  },
  {
    id: '11',
    title: '爱在黄昏',
    year: '2026',
    category: '电视剧',
    description: '都市爱情剧，两个性格截然不同的人在工作中相遇...',
    poster: 'https://images.unsplash.com/photo-1608170825938-a8ea0305d46c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxyb21hbmNlJTIwbG92ZSUyMHN0b3J5JTIwY2luZW1hfGVufDF8fHx8MTc3NDg1NzQ5Nnww&ixlib=rb-4.1.0&q=80&w=1080',
    episodes: 36,
    rating: 8.1,
    tag: '爱情',
  },
  {
    id: '12',
    title: '韩流来袭',
    year: '2026',
    category: '韩剧',
    description: '韩国浪漫爱情剧...',
    poster: 'https://images.unsplash.com/photo-1705049316554-e62aa4cfa415?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxhc2lhbiUyMGRyYW1hJTIwdGVsZXZpc2lvbiUyMHNlcmllc3xlbnwxfHx8fDE3NzQ4NTc0OTR8MA&ixlib=rb-4.1.0&q=80&w=1080',
    episodes: 16,
    rating: 8.4,
    tag: '韩剧',
  },
];

export const bannerVideos = videos.filter(v => v.featured);
export const bannerVideo = videos[0];

// 内容分组
export const hotTV = videos.filter(v => v.category === '电视剧');
export const hotMovies = videos.filter(v => v.category === '电影');
export const hotAnime = videos.filter(v => v.category === '动漫' || v.category === '综艺');

// 历史记录
export let watchHistory: Video[] = [];

export const addToHistory = (video: Video) => {
  watchHistory = watchHistory.filter(v => v.id !== video.id);
  watchHistory.unshift(video);
  if (watchHistory.length > 20) {
    watchHistory = watchHistory.slice(0, 20);
  }
};

// 剧集信息
export const getEpisodes = (videoId: string) => {
  const video = videos.find(v => v.id === videoId);
  if (!video || !video.episodes) return [];
  
  return Array.from({ length: video.episodes }, (_, i) => ({
    number: i + 1,
    title: `第${String(i + 1).padStart(2, '0')}集`,
  }));
};

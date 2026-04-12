import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router';
import { useFocus } from '../hooks/useFocus';
import { categories, bannerVideos, videos, hotTV, hotMovies, hotAnime } from '../data/mockData';
import { Search, History, Settings, User, Wifi, BarChart3 } from 'lucide-react';
import { ImageWithFallback } from '../components/figma/ImageWithFallback';
import heroBg from 'figma:asset/e68471f2bf01a3612f508216b90c0ef44cfa25e2.png';

function useCurrentTime() {
  const [time, setTime] = useState(new Date());
  useEffect(() => {
    const timer = setInterval(() => setTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);
  return time;
}

function VideoCard({
  video,
  idPrefix,
  focusedId,
  onClick,
}: {
  video: (typeof videos)[0];
  idPrefix: string;
  focusedId: string | null;
  onClick: () => void;
}) {
  const cardId = `${idPrefix}-${video.id}`;
  const isFocused = focusedId === cardId;

  return (
    <button
      key={cardId}
      id={cardId}
      data-focusable="true"
      onClick={onClick}
      className={`group relative rounded-xl overflow-hidden transition-all duration-300 flex-shrink-0 w-[200px] ${
        isFocused
          ? 'scale-110 shadow-2xl shadow-cyan-500/40 z-10 ring-2 ring-cyan-400'
          : ''
      }`}
    >
      <div className="aspect-[3/4] relative bg-slate-800">
        {video.id === '1' ? (
          <img src={heroBg} alt={video.title} className="w-full h-full object-cover" />
        ) : (
          <ImageWithFallback src={video.poster} alt={video.title} className="w-full h-full object-cover" />
        )}
        {/* 标签 */}
        {video.tag && (
          <div className="absolute top-2 left-2 bg-gradient-to-r from-amber-500 to-orange-500 text-white px-2.5 py-0.5 rounded text-xs">
            {video.tag}
          </div>
        )}
        {/* 评分 */}
        {video.rating && (
          <div className="absolute top-2 right-2 bg-black/70 text-amber-400 px-2 py-0.5 rounded text-xs">
            {video.rating}分
          </div>
        )}
        {/* 集数 */}
        {video.episodes && (
          <div className="absolute bottom-10 right-2 bg-black/70 text-cyan-300 px-2 py-0.5 rounded text-xs">
            全{video.episodes}集
          </div>
        )}
        {/* 底部渐变标题 */}
        <div className="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/90 via-black/40 to-transparent pt-10 pb-3 px-3">
          <h3 className="text-sm line-clamp-1">{video.title}</h3>
        </div>
      </div>
    </button>
  );
}

function ContentRow({
  title,
  items,
  idPrefix,
  focusedId,
  navigate,
}: {
  title: string;
  items: typeof videos;
  idPrefix: string;
  focusedId: string | null;
  navigate: (path: string) => void;
}) {
  const scrollRef = useRef<HTMLDivElement>(null);

  // Auto scroll to focused item
  useEffect(() => {
    if (!focusedId?.startsWith(idPrefix)) return;
    const el = document.getElementById(focusedId);
    if (el && scrollRef.current) {
      el.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' });
    }
  }, [focusedId, idPrefix]);

  return (
    <section className="mb-8">
      <div className="flex items-center justify-between mb-4 px-2">
        <h2 className="text-xl">{title}</h2>
        <span className="text-slate-500 text-sm">更多 &gt;</span>
      </div>
      <div ref={scrollRef} className="flex gap-4 overflow-x-auto pb-2 scrollbar-hide">
        {items.map((video) => (
          <VideoCard
            key={video.id}
            video={video}
            idPrefix={idPrefix}
            focusedId={focusedId}
            onClick={() => navigate(`/detail/${video.id}`)}
          />
        ))}
      </div>
    </section>
  );
}

export default function Home() {
  const navigate = useNavigate();
  const { focusedId } = useFocus('search-btn', () => {});
  const now = useCurrentTime();
  const [bannerIndex, setBannerIndex] = useState(0);
  const [activeNavId, setActiveNavId] = useState('');

  // No longer auto-track
  // useEffect(() => {
  //   if (focusedId && focusedId.startsWith('nav-')) {
  //     setActiveNavId(focusedId);
  //   }
  // }, [focusedId]);

  // Auto-rotate banner
  useEffect(() => {
    const timer = setInterval(() => {
      setBannerIndex((prev) => (prev + 1) % bannerVideos.length);
    }, 5000);
    return () => clearInterval(timer);
  }, []);

  const currentBanner = bannerVideos[bannerIndex];

  const focusClass = (id: string) => {
    const isFocused = focusedId === id;
    const isActiveNav = id.startsWith('nav-') && id === activeNavId;
    if (isFocused && isActiveNav) return 'bg-cyan-600 scale-105 shadow-lg shadow-cyan-500/50 text-white';
    if (isFocused) return 'ring-2 ring-cyan-400 scale-105 shadow-lg shadow-cyan-500/50 text-white bg-slate-800/60';
    if (isActiveNav) return 'bg-cyan-600 text-white';
    return 'bg-slate-800/60 text-slate-200';
  };

  return (
    <div className="h-screen overflow-hidden bg-gradient-to-b from-[#0a0e27] via-[#0d1333] to-[#060a1f] text-white flex flex-col">
      {/* 顶部导航栏 */}
      <header className="flex-shrink-0 px-8 pt-3 pb-1 bg-gradient-to-b from-black/70 to-transparent z-40">
        {/* 第一行：功能按钮 + 公告 + 状态 */}
        <div className="flex items-center justify-between mb-2">
          <div className="flex items-center gap-3">
            <button
              id="search-btn"
              data-focusable="true"
              onClick={() => navigate('/search')}
              className={`flex items-center gap-1.5 px-4 py-1.5 rounded-full text-sm transition-all duration-200 ${focusClass('search-btn')}`}
            >
              <Search className="w-4 h-4 text-orange-400" />
              <span>搜索</span>
            </button>
            <button
              id="history-btn"
              data-focusable="true"
              onClick={() => navigate('/history')}
              className={`flex items-center gap-1.5 px-4 py-1.5 rounded-full text-sm transition-all duration-200 ${focusClass('history-btn')}`}
            >
              <History className="w-4 h-4 text-green-400" />
              <span>历史</span>
            </button>
            <button
              id="settings-btn"
              data-focusable="true"
              onClick={() => navigate('/settings')}
              className={`flex items-center gap-1.5 px-4 py-1.5 rounded-full text-sm transition-all duration-200 ${focusClass('settings-btn')}`}
            >
              <Settings className="w-4 h-4 text-orange-400" />
              <span>设置</span>
            </button>
            <button
              id="user-btn"
              data-focusable="true"
              onClick={() => navigate('/profile')}
              className={`flex items-center gap-1.5 px-4 py-1.5 rounded-full text-sm transition-all duration-200 ${focusClass('user-btn')}`}
            >
              <User className="w-4 h-4 text-orange-400" />
              <span>我的</span>
            </button>
            <div className="ml-2 flex items-center gap-2">
              <span className="bg-orange-500 text-white px-2 py-0.5 rounded text-xs">公告：</span>
              <span className="text-slate-300 text-xs truncate max-w-[280px]">本应用所调用的内容侵犯其合法权</span>
            </div>
          </div>

          <div className="flex items-center gap-4 text-xs text-slate-300 flex-shrink-0">
            <button
              id="cache-btn"
              data-focusable="true"
              onClick={() => navigate('/clear-cache')}
              className={`flex items-center gap-1.5 text-sm px-3 py-1 rounded-full transition-all duration-200 ${focusClass('cache-btn')}`}
            >
              <BarChart3 className="w-4 h-4 text-cyan-400" />
              <span>清除缓存</span>
            </button>
            <Wifi className="w-4 h-4 text-green-400" />
            <span className="text-sm">{now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}</span>
          </div>
        </div>

        {/* 第二行：分类导航 */}
        <div className="flex items-center gap-2">
          {categories.map((cat) => {
            const navId = `nav-${cat.id}`;
            return (
              <button
                key={navId}
                id={navId}
                data-focusable="true"
                onClick={() => {
                  if (activeNavId === navId) {
                    // Already selected -> navigate to category
                    navigate(`/category/${cat.id}`);
                  } else {
                    // First click -> select/highlight
                    setActiveNavId(navId);
                  }
                }}
                className={`px-5 py-1.5 rounded-lg text-base transition-all duration-200 whitespace-nowrap ${focusClass(navId)}`}
              >
                {cat.name}
              </button>
            );
          })}
        </div>
      </header>

      {/* 主内容区 - 可滚动 */}
      <main className="flex-1 overflow-y-auto px-8 pb-8">
        {/* Hero Banner */}
        <section className="relative h-[380px] rounded-2xl overflow-hidden mb-8 mt-2">
          {/* Banner图 */}
          {currentBanner.id === '1' ? (
            <img src={heroBg} alt={currentBanner.title} className="w-full h-full object-cover transition-all duration-700" />
          ) : (
            <ImageWithFallback src={currentBanner.poster} alt={currentBanner.title} className="w-full h-full object-cover transition-all duration-700" />
          )}
          {/* 渐变覆盖 */}
          <div className="absolute inset-0 bg-gradient-to-r from-black/85 via-black/50 to-transparent" />
          <div className="absolute inset-0 bg-gradient-to-t from-[#0a0e27] via-transparent to-transparent" />

          {/* Banner内容 */}
          <div className="absolute bottom-10 left-10 max-w-xl">
            <div className="flex items-center gap-3 mb-3">
              {currentBanner.tag && (
                <span className="bg-gradient-to-r from-cyan-500 to-blue-500 px-3 py-1 rounded text-xs">
                  {currentBanner.tag}
                </span>
              )}
              {currentBanner.rating && (
                <span className="text-amber-400 text-sm">★ {currentBanner.rating}</span>
              )}
            </div>
            <h1 className="text-5xl mb-3">{currentBanner.title}</h1>
            <div className="flex items-center gap-3 text-sm text-slate-300 mb-4">
              <span>{currentBanner.category}</span>
              <span className="text-slate-600">|</span>
              <span>{currentBanner.year}</span>
              {currentBanner.episodes && (
                <>
                  <span className="text-slate-600">|</span>
                  <span>全{currentBanner.episodes}集</span>
                </>
              )}
            </div>
            <p className="text-slate-400 text-sm line-clamp-2 mb-6">{currentBanner.description}</p>
            <div className="flex gap-3">
              <button
                id="play-btn"
                data-focusable="true"
                onClick={() => navigate(`/detail/${currentBanner.id}`)}
                className={`flex items-center gap-2 px-7 py-3 rounded-lg transition-all duration-200 ${
                  focusedId === 'play-btn'
                    ? 'bg-cyan-400 scale-110 shadow-lg shadow-cyan-500/50'
                    : 'bg-cyan-600'
                }`}
              >
                <span>▶</span>
                <span>立即播放</span>
              </button>
              <button
                id="collect-btn"
                data-focusable="true"
                className={`flex items-center gap-2 px-7 py-3 rounded-lg transition-all duration-200 ${
                  focusedId === 'collect-btn'
                    ? 'bg-slate-500 scale-110 shadow-lg'
                    : 'bg-slate-700/80'
                }`}
              >
                <span>♥</span>
                <span>收藏</span>
              </button>
            </div>
          </div>

          {/* Banner指示器 */}
          <div className="absolute bottom-4 right-10 flex gap-2">
            {bannerVideos.map((_, i) => (
              <button
                key={`dot-${i}`}
                onClick={() => setBannerIndex(i)}
                className={`h-1 rounded-full transition-all duration-300 ${
                  i === bannerIndex ? 'w-8 bg-cyan-400' : 'w-4 bg-slate-600'
                }`}
              />
            ))}
          </div>
        </section>

        {/* 热播电视剧 */}
        <ContentRow
          title="🔥 热播电视剧"
          items={hotTV}
          idPrefix="tv"
          focusedId={focusedId}
          navigate={navigate}
        />

        {/* 电影推荐 */}
        <ContentRow
          title="🎬 电影推荐"
          items={hotMovies}
          idPrefix="movie"
          focusedId={focusedId}
          navigate={navigate}
        />

        {/* 综艺动漫 */}
        <ContentRow
          title="🎭 综艺 · 动漫"
          items={hotAnime}
          idPrefix="anime"
          focusedId={focusedId}
          navigate={navigate}
        />

        {/* 猜你喜欢 - 全部视频 */}
        <ContentRow
          title="💡 猜你喜欢"
          items={videos}
          idPrefix="guess"
          focusedId={focusedId}
          navigate={navigate}
        />
      </main>
    </div>
  );
}
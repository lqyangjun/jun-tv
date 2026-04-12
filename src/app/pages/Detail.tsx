import { useState, useEffect, useRef } from 'react';
import { useNavigate, useParams } from 'react-router';
import { useFocus } from '../hooks/useFocus';
import { videos, getEpisodes, addToHistory } from '../data/mockData';
import { ImageWithFallback } from '../components/figma/ImageWithFallback';
import heroBg from 'figma:asset/e68471f2bf01a3612f508216b90c0ef44cfa25e2.png';

export default function Detail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [selectedEpisode, setSelectedEpisode] = useState(1);
  const [isPlaying, setIsPlaying] = useState(true); // Auto-play on entry
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [countdown, setCountdown] = useState(3);
  const [episodeOrder, setEpisodeOrder] = useState<'asc' | 'desc'>('asc');
  const [activeSource, setActiveSource] = useState('yn');
  const videoContainerRef = useRef<HTMLDivElement>(null);
  const { focusedId } = useFocus('fullscreen-btn', () => navigate('/'));

  const video = videos.find((v) => v.id === id);
  const episodes = getEpisodes(id || '');
  const displayEpisodes = episodeOrder === 'desc' ? [...episodes].reverse() : episodes;
  const relatedVideos = videos.filter((v) => v.id !== id).slice(0, 8);

  useEffect(() => {
    if (video) addToHistory(video);
  }, [video]);

  // Countdown + auto fullscreen
  useEffect(() => {
    if (isPlaying && !isFullscreen && countdown > 0) {
      const timer = setTimeout(() => setCountdown((c) => c - 1), 1000);
      return () => clearTimeout(timer);
    }
    if (isPlaying && !isFullscreen && countdown === 0) {
      setIsFullscreen(true);
    }
  }, [isPlaying, isFullscreen, countdown]);

  // Reset countdown when starting play
  const startPlay = () => {
    setIsPlaying(true);
    setCountdown(3);
  };

  // ESC to exit fullscreen
  useEffect(() => {
    const handleKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isFullscreen) {
        setIsFullscreen(false);
        setIsPlaying(false);
      }
    };
    window.addEventListener('keydown', handleKey);
    return () => window.removeEventListener('keydown', handleKey);
  }, [isFullscreen]);

  const fc = (itemId: string) =>
    focusedId === itemId
      ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/40 text-white'
      : 'bg-slate-700/60 text-slate-200';

  if (!video) {
    return (
      <div className="h-screen bg-slate-950 text-white flex items-center justify-center">
        <p className="text-2xl">视频不存在</p>
      </div>
    );
  }

  const posterSrc = video.id === '1' ? heroBg : video.poster;
  const episodeCount = video.episodes || episodes.length;

  return (
    <div className="h-screen bg-gradient-to-br from-[#0a1128] via-[#0d1a3a] to-[#06102a] text-white flex flex-col overflow-hidden">
      {/* Fullscreen player overlay */}
      {isFullscreen && (
        <div className="fixed inset-0 z-50 bg-black flex items-center justify-center">
          <button
            onClick={() => { setIsFullscreen(false); setIsPlaying(false); }}
            className="absolute inset-0 flex items-center justify-center bg-gradient-to-br from-slate-900 to-slate-950 cursor-pointer"
          >
            <div className="text-center">
              <div className="text-9xl mb-6">{isPlaying ? '⏸️' : '▶️'}</div>
              <p className="text-3xl text-slate-400">1280x536</p>
            </div>
          </button>
          <button
            onClick={() => { setIsFullscreen(false); setIsPlaying(false); }}
            className="absolute top-8 right-8 bg-black/70 px-8 py-4 rounded-lg text-xl"
          >
            退出全屏 (ESC)
          </button>
        </div>
      )}

      {/* Main content */}
      <div className="flex-1 overflow-y-auto p-6 space-y-5">
        {/* Top section: Player + Info side by side */}
        <div className="flex gap-6">
          {/* Left: Video player */}
          <div className="w-[55%] flex-shrink-0">
            <div
              ref={videoContainerRef}
              className="relative aspect-video bg-black rounded-xl overflow-hidden border-2 border-slate-600 cursor-pointer"
              onClick={() => setIsPlaying(!isPlaying)}
            >
              {video.id === '1' ? (
                <img src={heroBg} alt={video.title} className="w-full h-full object-cover" />
              ) : (
                <ImageWithFallback src={video.poster} alt={video.title} className="w-full h-full object-cover" />
              )}
              {/* Overlay with play state */}
              <div className="absolute inset-0 bg-black/40 flex items-center justify-center">
                <div className="text-center">
                  <p className="text-slate-300 text-sm mb-2">1280x536</p>
                  {!isPlaying && <div className="text-6xl">▶️</div>}
                  {isPlaying && !isFullscreen && (
                    <div className="mt-3">
                      <div className="w-16 h-16 rounded-full border-4 border-cyan-400 flex items-center justify-center mx-auto mb-2">
                        <span className="text-2xl text-cyan-400">{countdown}</span>
                      </div>
                      <p className="text-cyan-400 animate-pulse text-sm">{countdown}秒后自动全屏</p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>

          {/* Right: Video info */}
          <div className="flex-1 min-w-0">
            <div className="mb-3">
              <div className="flex items-baseline gap-3 mb-2">
                <h1 className="text-3xl">{video.title}</h1>
                <span className="text-slate-400 text-base">{video.year}</span>
                <span className="text-orange-400 text-base">更新至{episodeCount}集</span>
              </div>
              <div className="space-y-1.5 text-sm text-slate-300">
                <p><span className="text-slate-500">导演: </span>李雪</p>
                <p><span className="text-slate-500">主演: </span>杨紫,胡歌,李光洁,张哲华,梅婷,袁弘,杨烁,周游</p>
                <p><span className="text-slate-500">类型: </span>{video.category}</p>
                <p><span className="text-slate-500">地区: </span></p>
              </div>
            </div>
            <p className="text-slate-400 text-sm leading-relaxed line-clamp-3 mb-4">{video.description}</p>

            {/* Action buttons */}
            <div className="flex flex-wrap gap-3">
              <button
                id="fullscreen-btn"
                data-focusable="true"
                onClick={() => { setIsPlaying(true); setIsFullscreen(true); }}
                className={`flex items-center gap-2 px-6 py-2.5 rounded-lg transition-all duration-200 ${fc('fullscreen-btn')}`}
              >
                <span>📺</span>
                <span>全屏</span>
              </button>
              <button
                id="fav-btn"
                data-focusable="true"
                className={`flex items-center gap-2 px-6 py-2.5 rounded-lg transition-all duration-200 ${fc('fav-btn')}`}
              >
                <span>☆</span>
                <span>收藏</span>
              </button>
              <button
                id="yn-play-btn"
                data-focusable="true"
                className={`flex items-center gap-2 px-6 py-2.5 rounded-lg transition-all duration-200 ${
                  focusedId === 'yn-play-btn'
                    ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/40 text-white'
                    : 'bg-green-700/80 text-green-100'
                }`}
              >
                <span>YN蓝光</span>
                <span className="text-green-300">⇄</span>
              </button>
              <button
                id="ijk-play-btn"
                data-focusable="true"
                className={`flex items-center gap-2 px-6 py-2.5 rounded-lg transition-all duration-200 ${
                  focusedId === 'ijk-play-btn'
                    ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/40 text-white'
                    : 'bg-green-700/80 text-green-100'
                }`}
              >
                <span>IJK</span>
                <span className="text-green-300">⇄</span>
              </button>
            </div>
          </div>
        </div>

        {/* Episode section */}
        <div>
          <div className="flex items-center gap-4 mb-3">
            <span className="text-lg">剧集列表</span>
            <span className="text-slate-400 text-sm">选集 ({episodeCount}集)</span>
            <button
              id="order-btn"
              data-focusable="true"
              onClick={() => setEpisodeOrder(o => o === 'asc' ? 'desc' : 'asc')}
              className={`px-4 py-1 rounded-lg text-sm transition-all duration-200 ${fc('order-btn')}`}
            >
              剧集{episodeOrder === 'asc' ? '正序' : '倒序'}
            </button>
          </div>

          {/* Source tabs */}
          <div className="flex gap-3 mb-3">
            <button
              id="source-yn"
              data-focusable="true"
              onClick={() => setActiveSource('yn')}
              className={`px-5 py-2 rounded-lg text-base transition-all duration-200 ${
                focusedId === 'source-yn'
                  ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/40'
                  : activeSource === 'yn'
                  ? 'bg-blue-700 ring-1 ring-blue-400'
                  : 'bg-slate-700/60'
              }`}
            >
              YN蓝光 ({episodeCount}集)
            </button>
          </div>

          {/* Episode grid */}
          <div className="flex flex-wrap gap-2.5">
            {displayEpisodes.slice(0, 20).map((ep) => {
              const epId = `ep-${ep.number}`;
              const isSelected = selectedEpisode === ep.number;
              return (
                <button
                  key={epId}
                  id={epId}
                  data-focusable="true"
                  onClick={() => { setSelectedEpisode(ep.number); startPlay(); }}
                  className={`px-5 py-2 rounded-lg text-sm transition-all duration-200 ${
                    focusedId === epId
                      ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/40 text-white'
                      : isSelected
                      ? 'bg-blue-600 text-white ring-1 ring-blue-400'
                      : 'bg-slate-700/50 text-slate-300'
                  }`}
                >
                  {ep.title}
                </button>
              );
            })}
          </div>
        </div>

        {/* Related recommendations */}
        <div>
          <h3 className="text-lg mb-3">相关推荐</h3>
          <div className="flex gap-4 overflow-x-auto pb-2">
            {relatedVideos.map((rv) => {
              const rid = `rel-${rv.id}`;
              const isFocused = focusedId === rid;
              return (
                <button
                  key={rid}
                  id={rid}
                  data-focusable="true"
                  onClick={() => navigate(`/detail/${rv.id}`)}
                  className={`flex-shrink-0 w-[140px] rounded-xl overflow-hidden transition-all duration-200 ${
                    isFocused ? 'scale-110 ring-2 ring-cyan-400 shadow-lg shadow-cyan-500/40 z-10' : ''
                  }`}
                >
                  <div className="aspect-[3/4] relative bg-slate-800">
                    {rv.id === '1' ? (
                      <img src={heroBg} alt={rv.title} className="w-full h-full object-cover" />
                    ) : (
                      <ImageWithFallback src={rv.poster} alt={rv.title} className="w-full h-full object-cover" />
                    )}
                    <div className="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/90 to-transparent pt-8 pb-2 px-2">
                      <p className="text-xs line-clamp-1">{rv.title}</p>
                    </div>
                  </div>
                </button>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
}
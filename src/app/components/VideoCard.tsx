/**
 * 通用视频卡片组件
 * 用于首页、搜索页、分类页、历史记录等
 */

import { ImageWithFallback } from './figma/ImageWithFallback';
import heroBg from 'figma:asset/e68471f2bf01a3612f508216b90c0ef44cfa25e2.png';

interface Video {
  id: string;
  title: string;
  year: string;
  category?: string;
  poster: string;
  episodes?: number;
  rating?: number;
  tag?: string;
  currentEpisode?: number;
}

interface VideoCardProps {
  video: Video;
  id: string;
  isFocused: boolean;
  onClick: () => void;
  size?: 'sm' | 'md' | 'lg';
  showProgress?: boolean;
  showSvip?: boolean;
}

const sizeClasses = {
  sm: 'w-[140px]',
  md: 'w-[200px]',
  lg: 'w-[240px]',
};

export function VideoCard({
  video,
  id,
  isFocused,
  onClick,
  size = 'md',
  showProgress = false,
  showSvip = false,
}: VideoCardProps) {
  return (
    <button
      id={id}
      data-focusable="true"
      onClick={onClick}
      className={`group relative rounded-xl overflow-hidden transition-all duration-300 flex-shrink-0 ${sizeClasses[size]} ${
        isFocused
          ? 'scale-110 shadow-2xl shadow-cyan-500/40 z-10 ring-2 ring-cyan-400'
          : ''
      }`}
    >
      <div className="aspect-[3/4] relative bg-slate-800">
        {/* 海报图片 */}
        {video.id === '1' ? (
          <img
            src={heroBg}
            alt={video.title}
            className="w-full h-full object-cover"
          />
        ) : (
          <ImageWithFallback
            src={video.poster}
            alt={video.title}
            className="w-full h-full object-cover"
          />
        )}

        {/* SVIP标签 */}
        {showSvip && (
          <div className="absolute top-2 left-2 bg-amber-600 text-white px-3 py-1 rounded-full text-xs font-bold flex items-center gap-1">
            <span>👑</span>
            <span>SVIP</span>
          </div>
        )}

        {/* 标签（热播、独播等） */}
        {video.tag && !showSvip && (
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

        {/* 播放进度（历史记录） */}
        {showProgress && video.currentEpisode && (
          <div className="absolute bottom-10 left-2 bg-cyan-500 text-black px-2 py-1 rounded text-xs font-bold">
            看到第{video.currentEpisode}集
          </div>
        )}

        {/* 底部渐变标题 */}
        <div className="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/90 via-black/40 to-transparent pt-10 pb-3 px-3">
          <h3 className={`line-clamp-1 ${size === 'sm' ? 'text-xs' : 'text-sm'}`}>
            {video.title}
          </h3>
          {size !== 'sm' && (
            <p className="text-xs text-slate-400 mt-1">{video.year}</p>
          )}
        </div>
      </div>
    </button>
  );
}

/**
 * 视频卡片行组件
 * 用于横向滚动的内容行
 */
interface VideoRowProps {
  title: string;
  videos: Video[];
  idPrefix: string;
  focusedId: string | null;
  onVideoClick: (videoId: string) => void;
  size?: 'sm' | 'md' | 'lg';
  showMore?: boolean;
  onMoreClick?: () => void;
}

export function VideoRow({
  title,
  videos,
  idPrefix,
  focusedId,
  onVideoClick,
  size = 'md',
  showMore = false,
  onMoreClick,
}: VideoRowProps) {
  return (
    <section className="mb-8">
      <div className="flex items-center justify-between mb-4 px-2">
        <h2 className="text-xl">{title}</h2>
        {showMore && (
          <button
            onClick={onMoreClick}
            className="text-slate-500 text-sm hover:text-cyan-400 transition-colors"
          >
            更多 &gt;
          </button>
        )}
      </div>
      <div className="flex gap-4 overflow-x-auto pb-2 scrollbar-hide">
        {videos.map((video) => {
          const cardId = `${idPrefix}-${video.id}`;
          return (
            <VideoCard
              key={cardId}
              video={video}
              id={cardId}
              isFocused={focusedId === cardId}
              onClick={() => onVideoClick(video.id)}
              size={size}
            />
          );
        })}
      </div>
    </section>
  );
}

/**
 * 视频网格组件
 * 用于分类页、搜索结果等
 */
interface VideoGridProps {
  videos: Video[];
  idPrefix: string;
  focusedId: string | null;
  onVideoClick: (videoId: string) => void;
  columns?: 4 | 5 | 6;
  showSvip?: boolean;
}

export function VideoGrid({
  videos,
  idPrefix,
  focusedId,
  onVideoClick,
  columns = 6,
  showSvip = false,
}: VideoGridProps) {
  const gridCols = {
    4: 'grid-cols-4',
    5: 'grid-cols-5',
    6: 'grid-cols-6',
  };

  return (
    <div className={`grid ${gridCols[columns]} gap-6`}>
      {videos.map((video) => {
        const cardId = `${idPrefix}-${video.id}`;
        return (
          <VideoCard
            key={cardId}
            video={video}
            id={cardId}
            isFocused={focusedId === cardId}
            onClick={() => onVideoClick(video.id)}
            showSvip={showSvip}
          />
        );
      })}
    </div>
  );
}

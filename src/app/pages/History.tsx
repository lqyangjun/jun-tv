import { useNavigate } from 'react-router';
import { useFocus } from '../hooks/useFocus';
import { watchHistory } from '../data/mockData';
import { ArrowLeft } from 'lucide-react';
import { ImageWithFallback } from '../components/figma/ImageWithFallback';
import heroBg from 'figma:asset/e68471f2bf01a3612f508216b90c0ef44cfa25e2.png';

export default function History() {
  const navigate = useNavigate();
  const { focusedId } = useFocus('back-btn', () => navigate('/'));

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#0a1128] via-[#0d1a3a] to-[#06102a] text-white p-8">
      <div className="max-w-7xl mx-auto">
        {/* 顶部栏 */}
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-4">
            <button
              id="back-btn"
              data-focusable="true"
              onClick={() => navigate('/')}
              className={`flex items-center gap-2 px-6 py-3 rounded-lg transition-all ${
                focusedId === 'back-btn'
                  ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/50'
                  : 'bg-slate-800/50'
              }`}
            >
              <ArrowLeft className="w-5 h-5" />
              <span>返回</span>
            </button>
            <h1 className="text-4xl font-bold">历史记录</h1>
          </div>
          <p className="text-slate-300 text-lg">选中按某单键或长按OK键</p>
        </div>

        {/* 历史记录列表 */}
        {watchHistory.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-[500px]">
            <div className="text-6xl mb-6">📺</div>
            <p className="text-3xl text-slate-400">小伙伴你还没有观看记录哦!!</p>
          </div>
        ) : (
          <div className="grid grid-cols-5 gap-6">
            {watchHistory.map((video) => (
              <button
                key={`history-${video.id}`}
                id={`history-${video.id}`}
                data-focusable="true"
                onClick={() => navigate(`/detail/${video.id}`)}
                className={`group relative rounded-xl overflow-hidden transition-all ${
                  focusedId === `history-${video.id}`
                    ? 'scale-110 shadow-2xl shadow-cyan-500/50 z-10 ring-4 ring-cyan-500'
                    : 'hover:scale-105'
                }`}
              >
                <div className="aspect-[3/4] bg-slate-800">
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
                  <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-transparent to-transparent">
                    <div className="absolute bottom-4 left-4 right-4">
                      <h3 className="font-bold text-lg mb-1">{video.title}</h3>
                      <p className="text-sm text-slate-300">{video.year}</p>
                      {video.currentEpisode && (
                        <div className="mt-2">
                          <div className="bg-cyan-500 text-black px-2 py-1 rounded text-xs inline-block">
                            看到第{video.currentEpisode}集
                          </div>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
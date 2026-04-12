import { useState } from 'react';
import { useNavigate } from 'react-router';
import { useFocus } from '../hooks/useFocus';

const cacheItems = [
  { id: 'personality', label: '个性设置' },
  { id: 'favorites', label: '收藏记录' },
  { id: 'playback', label: '播放记录' },
  { id: 'series', label: '追剧记录' },
  { id: 'all', label: '全部清除' },
];

export default function ClearCache() {
  const navigate = useNavigate();
  const { focusedId } = useFocus('clear-personality', () => navigate('/'));
  const [cleared, setCleared] = useState<Record<string, boolean>>({});

  const handleClear = (id: string) => {
    setCleared((prev) => ({ ...prev, [id]: true }));
    if (id === 'all') {
      const allCleared: Record<string, boolean> = {};
      cacheItems.forEach((item) => (allCleared[item.id] = true));
      setCleared(allCleared);
    }
  };

  return (
    <div className="h-screen bg-gradient-to-br from-[#0a1128] via-[#0d1a3a] to-[#06102a] text-white flex flex-col p-10">
      {/* Title */}
      <div className="flex items-center gap-3 mb-12">
        <h1 className="text-3xl">记录清理</h1>
        <div className="w-1 h-8 bg-amber-500 rounded-full" />
      </div>

      {/* Cache items */}
      <div className="max-w-2xl mx-auto w-full space-y-4">
        {cacheItems.map((item) => {
          const btnId = `clear-${item.id}`;
          const isFocused = focusedId === btnId;
          const isCleared = cleared[item.id];
          return (
            <div
              key={item.id}
              id={btnId}
              data-focusable="true"
              onClick={() => handleClear(item.id)}
              className={`flex items-center justify-between px-8 py-4 rounded-xl transition-all duration-200 cursor-pointer ${
                isFocused
                  ? 'bg-blue-600/70 ring-2 ring-cyan-400 scale-[1.02]'
                  : 'bg-[#132952]/80 border border-[#1e3a6e]/50'
              }`}
            >
              <span className="text-base">{item.label}</span>
              <button
                className={`px-5 py-1.5 rounded-lg text-sm transition-all ${
                  isCleared
                    ? 'bg-green-600/60 text-green-200'
                    : isFocused
                    ? 'bg-cyan-500 text-white'
                    : 'bg-[#1a3f70] text-slate-300 border border-[#2a5090]/50'
                }`}
              >
                {isCleared ? '已清除' : '清除'}
              </button>
            </div>
          );
        })}
      </div>
    </div>
  );
}

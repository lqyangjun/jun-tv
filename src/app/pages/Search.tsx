import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router';
import { useFocus } from '../hooks/useFocus';
import { videos } from '../data/mockData';
import { ArrowLeft } from 'lucide-react';
import { ImageWithFallback } from '../components/figma/ImageWithFallback';
import heroBg from 'figma:asset/e68471f2bf01a3612f508216b90c0ef44cfa25e2.png';

const KEYBOARD_LETTERS = [
  ['A', 'B', 'C', 'D', 'E'],
  ['F', 'G', 'H', 'I', 'J'],
  ['K', 'L', 'M', 'N', 'O'],
  ['P', 'Q', 'R', 'S', 'T'],
  ['U', 'V', 'W', 'X', 'Y'],
  ['Z', '0', '1', '2', '3'],
  ['4', '5', '6', '7', '8'],
  ['9', '拼', '搜索', '清空', '⌫'],
];

export default function Search() {
  const navigate = useNavigate();
  const [searchText, setSearchText] = useState('');
  const [searchResults, setSearchResults] = useState(videos);
  const { focusedId } = useFocus('back-btn', () => navigate('/'));

  const handleKeyPress = useCallback((key: string) => {
    if (key === '搜索') {
      // 执行搜索
      const results = videos.filter(v =>
        v.title.toLowerCase().includes(searchText.toLowerCase())
      );
      setSearchResults(results);
    } else if (key === '清空') {
      setSearchText('');
      setSearchResults(videos);
    } else if (key === '⌫') {
      setSearchText(prev => prev.slice(0, -1));
    } else if (key === '拼') {
      // 拼音输入模式切换
    } else {
      setSearchText(prev => prev + key);
    }
  }, [searchText]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#0a1128] via-[#0d1a3a] to-[#06102a] text-white">
      <div className="flex h-screen">
        {/* 左侧键盘区 */}
        <div className="w-[300px] bg-slate-950/30 p-6 flex flex-col backdrop-blur-sm border-r border-slate-700/30">
          <button
            id="back-btn"
            data-focusable="true"
            onClick={() => navigate('/')}
            className={`flex items-center gap-2 mb-6 px-4 py-3 rounded-lg transition-all ${
              focusedId === 'back-btn'
                ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/50'
                : 'bg-slate-800/50'
            }`}
          >
            <ArrowLeft className="w-5 h-5" />
            <span>返回</span>
          </button>

          <div className="mb-6">
            <input
              type="text"
              value={searchText}
              readOnly
              placeholder="请输入搜索项"
              className="w-full bg-slate-800/50 border border-slate-700 rounded-lg px-4 py-3 text-white placeholder-slate-400"
            />
            <button
              id="voice-input-btn"
              data-focusable="true"
              className={`mt-3 w-full px-4 py-3 rounded-lg transition-all ${
                focusedId === 'voice-input-btn'
                  ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/50'
                  : 'bg-slate-800/50'
              }`}
            >
              🎤 语音输入
            </button>
          </div>

          {/* 字母键盘 */}
          <div className="flex-1 overflow-auto">
            <div className="grid grid-cols-5 gap-2">
              {KEYBOARD_LETTERS.flat().map((letter, index) => (
                <button
                  key={`key-${index}`}
                  id={`key-${index}`}
                  data-focusable="true"
                  onClick={() => handleKeyPress(letter)}
                  className={`aspect-square flex items-center justify-center rounded-lg transition-all text-sm ${
                    focusedId === `key-${index}`
                      ? 'bg-cyan-500 scale-110 shadow-lg shadow-cyan-500/50'
                      : 'bg-slate-800/70 hover:bg-slate-700/70'
                  } ${
                    letter === '搜索' || letter === '清空'
                      ? 'col-span-1 bg-blue-600 hover:bg-blue-700'
                      : ''
                  }`}
                >
                  {letter}
                </button>
              ))}
            </div>
          </div>

          <p className="mt-4 text-sm text-slate-400 text-center">
            快速/特准搜索: 太极 = TJ / TAIJI
            <br />
            多音字: 娘 = XUAN 或 HUAN
          </p>
        </div>

        {/* 右侧搜索结果区 */}
        <div className="flex-1 p-12">
          <div className="mb-8">
            <h1 className="text-4xl font-bold mb-2">搜索</h1>
            <p className="text-slate-300 text-lg">
              片名(中文)/演员(中文)/拼音/首字母 逗号请注意多音字
            </p>
          </div>

          {searchText ? (
            <div>
              <h2 className="text-2xl font-bold mb-6">
                搜索结果 ({searchResults.length})
              </h2>
              <div className="grid grid-cols-4 gap-6">
                {searchResults.map((video) => (
                  <button
                    key={`result-${video.id}`}
                    id={`result-${video.id}`}
                    data-focusable="true"
                    onClick={() => navigate(`/detail/${video.id}`)}
                    className={`group relative rounded-xl overflow-hidden transition-all ${
                      focusedId === `result-${video.id}`
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
                      <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent">
                        <div className="absolute bottom-4 left-4 right-4">
                          <h3 className="font-bold text-lg mb-1">{video.title}</h3>
                          <p className="text-sm text-slate-300">{video.year}</p>
                        </div>
                      </div>
                    </div>
                  </button>
                ))}
              </div>
            </div>
          ) : (
            <div className="flex items-center justify-center h-[400px]">
              <p className="text-2xl text-slate-400">请输入搜索内容</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
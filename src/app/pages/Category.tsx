import { useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { useFocus } from '../hooks/useFocus';
import { videos, categories } from '../data/mockData';
import { ArrowLeft } from 'lucide-react';
import { ImageWithFallback } from '../components/figma/ImageWithFallback';
import heroBg from 'figma:asset/e68471f2bf01a3612f508216b90c0ef44cfa25e2.png';

const filterOptions = {
  type: ['全部', '都市', '古装', '战争', '青春', '偶像', '喜剧'],
  year: ['全部', '2024', '2023', '2022', '2021', '2020', '2019'],
  region: ['全部', '内地', '香港', '台湾', '美国', '韩国', '日本'],
  sort: ['全部', '热度优先', '评分最高', '最近更新'],
};

export default function Category() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [selectedType, setSelectedType] = useState('全部');
  const [selectedYear, setSelectedYear] = useState('全部');
  const [selectedRegion, setSelectedRegion] = useState('全部');
  const [selectedSort, setSelectedSort] = useState('全部');

  const { focusedId } = useFocus('back-btn', () => navigate('/'));

  const category = categories.find((c) => c.id === id);
  const filteredVideos = videos; // 实际应该根据筛选条件过滤

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#0a1128] via-[#0d1a3a] to-[#06102a] text-white p-8">
      <div className="max-w-[1600px] mx-auto">
        {/* 返回按钮 */}
        <button
          id="back-btn"
          data-focusable="true"
          onClick={() => navigate('/')}
          className={`flex items-center gap-2 mb-6 px-6 py-3 rounded-lg transition-all ${
            focusedId === 'back-btn'
              ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/50'
              : 'bg-slate-800/50'
          }`}
        >
          <ArrowLeft className="w-5 h-5" />
          <span>返回</span>
        </button>

        {/* 标题 */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold">
            {category?.name || '分类'} <span className="text-slate-400">共{filteredVideos.length}部</span>
          </h1>
        </div>

        {/* 筛选区域 */}
        <div className="space-y-4 mb-8">
          {/* 类型 */}
          <div className="flex items-center gap-4">
            <span className="text-lg min-w-[80px]">类型：</span>
            <div className="flex flex-wrap gap-3">
              {filterOptions.type.map((type) => (
                <button
                  key={`type-${type}`}
                  id={`type-${type}`}
                  data-focusable="true"
                  onClick={() => setSelectedType(type)}
                  className={`px-6 py-2 rounded-lg transition-all ${
                    selectedType === type
                      ? 'bg-cyan-500 text-white'
                      : focusedId === `type-${type}`
                      ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/50'
                      : 'bg-slate-800/50 hover:bg-slate-700/50'
                  }`}
                >
                  {type}
                </button>
              ))}
            </div>
          </div>

          {/* 年份 */}
          <div className="flex items-center gap-4">
            <span className="text-lg min-w-[80px]">年份：</span>
            <div className="flex flex-wrap gap-3">
              {filterOptions.year.map((year) => (
                <button
                  key={`year-${year}`}
                  id={`year-${year}`}
                  data-focusable="true"
                  onClick={() => setSelectedYear(year)}
                  className={`px-6 py-2 rounded-lg transition-all ${
                    selectedYear === year
                      ? 'bg-cyan-500 text-white'
                      : focusedId === `year-${year}`
                      ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/50'
                      : 'bg-slate-800/50 hover:bg-slate-700/50'
                  }`}
                >
                  {year}
                </button>
              ))}
            </div>
          </div>

          {/* 地区 */}
          <div className="flex items-center gap-4">
            <span className="text-lg min-w-[80px]">地区：</span>
            <div className="flex flex-wrap gap-3">
              {filterOptions.region.map((region) => (
                <button
                  key={`region-${region}`}
                  id={`region-${region}`}
                  data-focusable="true"
                  onClick={() => setSelectedRegion(region)}
                  className={`px-6 py-2 rounded-lg transition-all ${
                    selectedRegion === region
                      ? 'bg-cyan-500 text-white'
                      : focusedId === `region-${region}`
                      ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/50'
                      : 'bg-slate-800/50 hover:bg-slate-700/50'
                  }`}
                >
                  {region}
                </button>
              ))}
            </div>
          </div>

          {/* 排序 */}
          <div className="flex items-center gap-4">
            <span className="text-lg min-w-[80px]">排序：</span>
            <div className="flex flex-wrap gap-3">
              {filterOptions.sort.map((sort) => (
                <button
                  key={`sort-${sort}`}
                  id={`sort-${sort}`}
                  data-focusable="true"
                  onClick={() => setSelectedSort(sort)}
                  className={`px-6 py-2 rounded-lg transition-all ${
                    selectedSort === sort
                      ? 'bg-cyan-500 text-white'
                      : focusedId === `sort-${sort}`
                      ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/50'
                      : 'bg-slate-800/50 hover:bg-slate-700/50'
                  }`}
                >
                  {sort}
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* 视频网格 */}
        <div className="grid grid-cols-6 gap-6">
          {filteredVideos.map((video) => (
            <button
              key={`cat-video-${video.id}`}
              id={`cat-video-${video.id}`}
              data-focusable="true"
              onClick={() => navigate(`/detail/${video.id}`)}
              className={`group relative rounded-xl overflow-hidden transition-all ${
                focusedId === `cat-video-${video.id}`
                  ? 'scale-110 shadow-2xl shadow-cyan-500/50 z-10 ring-4 ring-cyan-500'
                  : 'hover:scale-105'
              }`}
            >
              <div className="aspect-[3/4] relative bg-slate-800">
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
                <div className="absolute top-2 left-2 bg-amber-600 text-white px-3 py-1 rounded-full text-xs font-bold flex items-center gap-1">
                  <span>👑</span>
                  <span>SVIP</span>
                </div>

                {/* 集数标签 */}
                {video.episodes && (
                  <div className="absolute bottom-2 right-2 bg-black/80 text-amber-400 px-3 py-1 rounded text-sm font-bold">
                    全{video.episodes}集
                  </div>
                )}

                {/* 标题覆盖层 */}
                <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-transparent to-transparent">
                  <div className="absolute bottom-4 left-3 right-3">
                    <h3 className="font-bold text-base line-clamp-2">{video.title}</h3>
                  </div>
                </div>
              </div>
            </button>
          ))}
        </div>

        {/* 分页提示���如果需要） */}
        {filteredVideos.length > 0 && (
          <div className="mt-8 text-center text-slate-400">
            <p>共{filteredVideos.length}部作品</p>
          </div>
        )}
      </div>
    </div>
  );
}
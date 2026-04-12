import { useState } from 'react';
import { useNavigate } from 'react-router';
import { useFocus } from '../hooks/useFocus';
import { UserCircle, Film, Heart, Clock, Shield } from 'lucide-react';

export default function Profile() {
  const navigate = useNavigate();
  const [activeMenu, setActiveMenu] = useState('account');
  const { focusedId } = useFocus('menu-account', () => navigate('/'));

  const menuItems = [
    { id: 'account', label: '我的账户', icon: UserCircle, gradient: 'from-cyan-500 to-blue-500' },
    { id: 'series', label: '我的追剧', icon: Film, gradient: 'from-pink-500 to-red-500' },
    { id: 'favorite', label: '我的收藏', icon: Heart, gradient: 'from-orange-500 to-red-500' },
    { id: 'history', label: '历史播放', icon: Clock, gradient: 'from-red-500 to-pink-500', nav: '/history' },
    { id: 'auth', label: '授权中心', icon: Shield, gradient: 'from-blue-500 to-cyan-500' },
  ];

  return (
    <div className="h-screen bg-gradient-to-br from-[#0a1128] via-[#0d1a3a] to-[#06102a] text-white flex overflow-hidden">
      {/* 左侧菜单 */}
      <aside className="w-64 flex-shrink-0 p-6 space-y-3">
        {menuItems.map((menu) => {
          const Icon = menu.icon;
          const isActive = activeMenu === menu.id;
          const isFocused = focusedId === `menu-${menu.id}`;
          return (
            <button
              key={menu.id}
              id={`menu-${menu.id}`}
              data-focusable="true"
              onClick={() => menu.nav ? navigate(menu.nav) : setActiveMenu(menu.id)}
              className={`w-full flex items-center gap-3 px-5 py-3 rounded-xl transition-all duration-200 text-base ${
                isFocused
                  ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/50'
                  : isActive
                  ? `bg-gradient-to-r ${menu.gradient} shadow-lg`
                  : 'bg-slate-800/30'
              }`}
            >
              <Icon className="w-5 h-5" />
              <span>{menu.label}</span>
            </button>
          );
        })}
      </aside>

      {/* 右侧内容区 */}
      <main className="flex-1 p-8 overflow-hidden">
        {activeMenu === 'account' && (
          <div className="h-full flex flex-col">
            <h1 className="text-3xl mb-6">我的账户</h1>

            {/* 用户信息 + 统计 横向排列 */}
            <div className="flex gap-6 flex-1 min-h-0">
              {/* 左侧用户卡片 */}
              <div className="bg-slate-800/30 rounded-2xl p-6 backdrop-blur-sm flex-1 flex flex-col justify-between">
                <div>
                  <div className="flex items-center gap-4 mb-6">
                    <div className="w-16 h-16 bg-gradient-to-br from-amber-500 to-orange-500 rounded-full flex items-center justify-center text-3xl">
                      👑
                    </div>
                    <div>
                      <h2 className="text-xl mb-1">be162e6657d8c129</h2>
                      <span className="inline-block bg-gradient-to-r from-pink-500 to-purple-500 px-4 py-1 rounded-full text-sm">
                        SVIP会员
                      </span>
                    </div>
                  </div>
                  <div className="space-y-3 text-slate-300 text-sm">
                    <div className="flex justify-between bg-slate-700/30 px-4 py-2.5 rounded-lg">
                      <span className="text-slate-400">用户ID</span>
                      <span>be162e6657d8c129</span>
                    </div>
                    <div className="flex justify-between bg-slate-700/30 px-4 py-2.5 rounded-lg">
                      <span className="text-slate-400">VIP有效期</span>
                      <span className="text-amber-400">永久会员</span>
                    </div>
                    <div className="flex justify-between bg-slate-700/30 px-4 py-2.5 rounded-lg">
                      <span className="text-slate-400">绑定邮箱</span>
                      <span className="text-red-400">未绑定</span>
                    </div>
                  </div>
                </div>
                <button
                  id="logout-btn"
                  data-focusable="true"
                  className={`mt-4 w-full py-3 rounded-xl text-center transition-all duration-200 ${
                    focusedId === 'logout-btn'
                      ? 'bg-red-500 scale-[1.02] shadow-lg'
                      : 'bg-red-900/40 text-red-400'
                  }`}
                >
                  注销登录
                </button>
              </div>

              {/* 右侧统计网格 */}
              <div className="flex-1 grid grid-cols-2 gap-4">
                <div className="bg-slate-800/30 rounded-2xl p-5 backdrop-blur-sm flex flex-col items-center justify-center">
                  <span className="text-3xl mb-2">⏱️</span>
                  <span className="text-3xl mb-1">0min</span>
                  <span className="text-slate-400 text-sm">累计观影</span>
                </div>
                <div className="bg-slate-800/30 rounded-2xl p-5 backdrop-blur-sm flex flex-col items-center justify-center">
                  <span className="text-3xl mb-2">📺</span>
                  <span className="text-3xl mb-1">0</span>
                  <span className="text-slate-400 text-sm">追剧数量</span>
                </div>
                <div className="bg-slate-800/30 rounded-2xl p-5 backdrop-blur-sm flex flex-col items-center justify-center">
                  <span className="text-3xl mb-2">❤️</span>
                  <span className="text-3xl mb-1">0</span>
                  <span className="text-slate-400 text-sm">收藏数量</span>
                </div>
                <div className="bg-slate-800/30 rounded-2xl p-5 backdrop-blur-sm flex flex-col items-center justify-center">
                  <span className="text-3xl mb-2">📱</span>
                  <span className="text-3xl mb-1">1</span>
                  <span className="text-slate-400 text-sm">授权设备</span>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeMenu === 'series' && (
          <div className="h-full flex flex-col">
            <h1 className="text-3xl mb-6">我的追剧</h1>
            <div className="flex-1 flex items-center justify-center">
              <p className="text-xl text-slate-400">暂无追剧记录</p>
            </div>
          </div>
        )}

        {activeMenu === 'favorite' && (
          <div className="h-full flex flex-col">
            <h1 className="text-3xl mb-6">我的收藏</h1>
            <div className="flex-1 flex items-center justify-center">
              <p className="text-xl text-slate-400">暂无收藏记录</p>
            </div>
          </div>
        )}

        {activeMenu === 'auth' && (
          <div className="h-full flex flex-col">
            <h1 className="text-3xl mb-6">授权中心</h1>
            <div className="flex-1 flex items-center justify-center">
              <p className="text-xl text-slate-400">暂无授权设备</p>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

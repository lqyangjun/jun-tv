import { useState } from 'react';
import { useNavigate } from 'react-router';
import { useFocus } from '../hooks/useFocus';
import { Play, AlertCircle, UserCircle, ChevronLeft, ChevronRight } from 'lucide-react';

interface SettingOption {
  id: string;
  label: string;
  values: string[];
  defaultIndex: number;
}

const playbackSettings: SettingOption[] = [
  { id: 'vod-core', label: '点播内核', values: ['IJK', 'EXO', '系统'], defaultIndex: 0 },
  { id: 'live-core', label: '直播内核', values: ['IJK', 'EXO', '系统'], defaultIndex: 0 },
  { id: 'elder-mode', label: '长辈直播模式', values: ['关闭', '开启'], defaultIndex: 0 },
  { id: 'decoder', label: '播放器解码', values: ['硬解码', '软解码', '自动'], defaultIndex: 0 },
  { id: 'aspect-ratio', label: '播放显示比列', values: ['等比缩放', '拉伸全屏', '16:9', '4:3', '原始'], defaultIndex: 0 },
  { id: 'skip-head', label: '跳过片头时间', values: ['0秒', '15秒', '30秒', '45秒', '60秒', '90秒'], defaultIndex: 0 },
  { id: 'skip-tail', label: '跳片过尾时间', values: ['0秒', '15秒', '30秒', '45秒', '60秒', '90秒'], defaultIndex: 0 },
  { id: 'theme', label: '更换主题', values: ['无界', '默认', '简约', '暗黑'], defaultIndex: 0 },
];

const menus = [
  { id: 'playback', label: '播放设置', icon: Play },
  { id: 'network', label: '网络测试', icon: AlertCircle },
  { id: 'about', label: '关于设备', icon: UserCircle },
];

export default function Settings() {
  const navigate = useNavigate();
  const { focusedId } = useFocus('menu-playback', () => navigate('/'));
  const [activeMenu, setActiveMenu] = useState('playback');
  const [settingValues, setSettingValues] = useState<Record<string, number>>(
    Object.fromEntries(playbackSettings.map(s => [s.id, s.defaultIndex]))
  );

  const changeValue = (id: string, dir: -1 | 1) => {
    const setting = playbackSettings.find(s => s.id === id);
    if (!setting) return;
    setSettingValues(prev => {
      const cur = prev[id] ?? 0;
      const next = (cur + dir + setting.values.length) % setting.values.length;
      return { ...prev, [id]: next };
    });
  };

  return (
    <div className="h-screen bg-gradient-to-br from-[#0a1128] via-[#0d1a3a] to-[#06102a] text-white flex overflow-hidden">
      {/* 左侧菜单 */}
      <aside className="w-[220px] flex-shrink-0 p-6 flex flex-col">
        <h1 className="text-2xl mb-8 px-2">设置中心</h1>
        <div className="space-y-3">
          {menus.map((menu) => {
            const Icon = menu.icon;
            const isActive = activeMenu === menu.id;
            const isFocused = focusedId === `menu-${menu.id}`;
            return (
              <button
                key={menu.id}
                id={`menu-${menu.id}`}
                data-focusable="true"
                onClick={() => setActiveMenu(menu.id)}
                className={`w-full flex items-center gap-3 px-5 py-3 rounded-xl transition-all duration-200 text-base ${
                  isFocused
                    ? 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/50'
                    : isActive
                    ? 'bg-slate-700/80'
                    : 'bg-slate-800/40'
                }`}
              >
                <Icon className="w-5 h-5" />
                <span>{menu.label}</span>
              </button>
            );
          })}
        </div>
      </aside>

      {/* 右侧内容 */}
      <main className="flex-1 p-6 overflow-y-auto">
        {activeMenu === 'playback' && (
          <div>
            <h2 className="text-2xl mb-6">播放设置</h2>
            <div className="space-y-3">
              {playbackSettings.map((setting) => {
                const isFocused = focusedId === `setting-${setting.id}`;
                const currentValue = setting.values[settingValues[setting.id] ?? 0];
                return (
                  <div
                    key={setting.id}
                    id={`setting-${setting.id}`}
                    data-focusable="true"
                    className={`flex items-center justify-between px-6 py-4 rounded-xl transition-all duration-200 ${
                      isFocused
                        ? 'bg-blue-600/60 ring-2 ring-cyan-400 scale-[1.01]'
                        : 'bg-slate-800/40'
                    }`}
                  >
                    <span className="text-base">{setting.label}</span>
                    <div className="flex items-center gap-3">
                      <button
                        onClick={() => changeValue(setting.id, -1)}
                        className="w-8 h-8 flex items-center justify-center rounded bg-slate-700/80 hover:bg-slate-600 transition-colors"
                      >
                        <ChevronLeft className="w-5 h-5" />
                      </button>
                      <span className="min-w-[80px] text-center text-base">{currentValue}</span>
                      <button
                        onClick={() => changeValue(setting.id, 1)}
                        className="w-8 h-8 flex items-center justify-center rounded bg-slate-700/80 hover:bg-slate-600 transition-colors"
                      >
                        <ChevronRight className="w-5 h-5" />
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {activeMenu === 'network' && (
          <div>
            <h2 className="text-2xl mb-6">网络测试</h2>
            <div className="space-y-4">
              <div className="bg-slate-800/40 rounded-xl p-6">
                <div className="flex items-center justify-between mb-4">
                  <span>网络状态</span>
                  <span className="text-green-400">已连接</span>
                </div>
                <div className="flex items-center justify-between mb-4">
                  <span>连接类型</span>
                  <span className="text-slate-300">Wi-Fi</span>
                </div>
                <div className="flex items-center justify-between mb-4">
                  <span>IP地址</span>
                  <span className="text-slate-300">192.168.1.100</span>
                </div>
                <div className="flex items-center justify-between">
                  <span>DNS</span>
                  <span className="text-slate-300">8.8.8.8</span>
                </div>
              </div>
              <button
                id="test-speed-btn"
                data-focusable="true"
                className={`w-full py-4 rounded-xl text-center transition-all duration-200 ${
                  focusedId === 'test-speed-btn'
                    ? 'bg-cyan-500 scale-[1.01] shadow-lg shadow-cyan-500/50'
                    : 'bg-blue-700/60'
                }`}
              >
                开始测速
              </button>
            </div>
          </div>
        )}

        {activeMenu === 'about' && (
          <div>
            <h2 className="text-2xl mb-6">关于设备</h2>
            <div className="bg-slate-800/40 rounded-xl p-6 space-y-4">
              <div className="flex items-center justify-between">
                <span>应用名称</span>
                <span className="text-slate-300">苹果CMS TV版</span>
              </div>
              <div className="flex items-center justify-between">
                <span>版本号</span>
                <span className="text-slate-300">v2.0.1</span>
              </div>
              <div className="flex items-center justify-between">
                <span>设备型号</span>
                <span className="text-slate-300">Smart TV</span>
              </div>
              <div className="flex items-center justify-between">
                <span>系统版本</span>
                <span className="text-slate-300">Android TV 13</span>
              </div>
              <div className="flex items-center justify-between">
                <span>存储空间</span>
                <span className="text-slate-300">已用 2.3GB / 共 16GB</span>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

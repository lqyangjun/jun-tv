/**
 * 键盘快捷键说明组件
 * 显示TV遥控器操作指南
 */

interface KeyGuideProps {
  show?: boolean;
  position?: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right';
}

export function KeyboardGuide({ show = true, position = 'bottom-right' }: KeyGuideProps) {
  if (!show) return null;

  const positionClasses = {
    'top-left': 'top-4 left-4',
    'top-right': 'top-4 right-4',
    'bottom-left': 'bottom-4 left-4',
    'bottom-right': 'bottom-4 right-4',
  };

  const keys = [
    { label: '方向键', desc: '切换焦点', icon: '⬆️⬇️⬅️➡️' },
    { label: 'Enter', desc: '确认', icon: '⏎' },
    { label: 'ESC', desc: '返回', icon: '⎋' },
  ];

  return (
    <div
      className={`fixed ${positionClasses[position]} bg-black/70 backdrop-blur-sm rounded-xl p-4 text-white border border-slate-700/50 z-50`}
    >
      <div className="space-y-2">
        {keys.map((key, i) => (
          <div key={i} className="flex items-center gap-3 text-xs">
            <span className="text-cyan-400 text-base">{key.icon}</span>
            <span className="text-slate-300 min-w-[50px]">{key.label}</span>
            <span className="text-slate-500">-</span>
            <span className="text-slate-400">{key.desc}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

/**
 * 迷你版键盘提示（只显示图标）
 */
export function MiniKeyGuide({ show = true }: { show?: boolean }) {
  if (!show) return null;

  return (
    <div className="fixed bottom-4 right-4 flex items-center gap-2 bg-black/50 backdrop-blur-sm rounded-full px-4 py-2 text-white border border-slate-700/50 z-50">
      <span className="text-cyan-400 text-sm">⬆️⬇️⬅️➡️</span>
      <span className="text-slate-600">|</span>
      <span className="text-cyan-400 text-sm">⏎</span>
      <span className="text-slate-600">|</span>
      <span className="text-cyan-400 text-sm">⎋</span>
    </div>
  );
}

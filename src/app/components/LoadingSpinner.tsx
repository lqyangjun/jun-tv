/**
 * 加载动画组件
 * TV风格的加载提示
 */

interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg' | 'xl';
  text?: string;
  fullScreen?: boolean;
}

export function LoadingSpinner({
  size = 'md',
  text = '加载中...',
  fullScreen = false,
}: LoadingSpinnerProps) {
  const sizeClasses = {
    sm: 'w-8 h-8 border-2',
    md: 'w-12 h-12 border-3',
    lg: 'w-16 h-16 border-4',
    xl: 'w-24 h-24 border-4',
  };

  const textSizes = {
    sm: 'text-sm',
    md: 'text-base',
    lg: 'text-lg',
    xl: 'text-2xl',
  };

  const spinner = (
    <div className="flex flex-col items-center justify-center gap-4">
      <div
        className={`${sizeClasses[size]} border-cyan-500 border-t-transparent rounded-full animate-spin`}
      />
      {text && (
        <p className={`${textSizes[size]} text-slate-300 animate-pulse`}>
          {text}
        </p>
      )}
    </div>
  );

  if (fullScreen) {
    return (
      <div className="fixed inset-0 bg-gradient-to-br from-[#0a1128] via-[#0d1a3a] to-[#06102a] flex items-center justify-center z-50">
        {spinner}
      </div>
    );
  }

  return spinner;
}

/**
 * 骨架屏加载组件
 */
export function SkeletonCard() {
  return (
    <div className="relative rounded-xl overflow-hidden bg-slate-800/50 aspect-[3/4] animate-pulse">
      <div className="absolute inset-0 bg-gradient-to-br from-slate-700/50 to-slate-800/50" />
      <div className="absolute bottom-0 left-0 right-0 p-4 space-y-2">
        <div className="h-4 bg-slate-700/70 rounded w-3/4" />
        <div className="h-3 bg-slate-700/50 rounded w-1/2" />
      </div>
    </div>
  );
}

/**
 * 视频卡片骨架屏行
 */
export function SkeletonRow({ count = 6 }: { count?: number }) {
  return (
    <div className="flex gap-4 mb-8">
      {Array.from({ length: count }).map((_, i) => (
        <div key={i} className="w-[200px] flex-shrink-0">
          <SkeletonCard />
        </div>
      ))}
    </div>
  );
}

/**
 * 点状加载动画
 */
export function DotLoader({ color = 'cyan' }: { color?: string }) {
  return (
    <div className="flex items-center gap-2">
      <div
        className={`w-2 h-2 bg-${color}-400 rounded-full animate-bounce`}
        style={{ animationDelay: '0ms' }}
      />
      <div
        className={`w-2 h-2 bg-${color}-400 rounded-full animate-bounce`}
        style={{ animationDelay: '150ms' }}
      />
      <div
        className={`w-2 h-2 bg-${color}-400 rounded-full animate-bounce`}
        style={{ animationDelay: '300ms' }}
      />
    </div>
  );
}

/**
 * 进度条加载组件
 */
export function ProgressBar({ progress = 0 }: { progress: number }) {
  return (
    <div className="w-full bg-slate-700/50 rounded-full h-2 overflow-hidden">
      <div
        className="h-full bg-gradient-to-r from-cyan-500 to-blue-500 transition-all duration-300 ease-out"
        style={{ width: `${Math.min(100, Math.max(0, progress))}%` }}
      />
    </div>
  );
}

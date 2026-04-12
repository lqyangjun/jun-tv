/**
 * TV焦点样式工具函数
 * 统一管理所有可聚焦元素的样式
 */

/**
 * 基础焦点样式
 * @param isFocused - 是否聚焦
 * @param baseClass - 基础样式类
 * @param focusClass - 自定义焦点样式（可选）
 */
export const getFocusClass = (
  isFocused: boolean,
  baseClass = 'bg-slate-800/60 text-slate-200',
  focusClass = 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/50 text-white'
): string => {
  return isFocused ? focusClass : baseClass;
};

/**
 * 按钮焦点样式（带选中状态）
 * @param isFocused - 是否聚焦
 * @param isSelected - 是否选中
 * @param baseClass - 基础样式
 * @param focusClass - 聚焦样式
 * @param selectedClass - 选中样式
 */
export const getButtonFocusClass = (
  isFocused: boolean,
  isSelected = false,
  baseClass = 'bg-slate-800/60 text-slate-200',
  focusClass = 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/50 text-white',
  selectedClass = 'bg-cyan-600 text-white'
): string => {
  if (isFocused && isSelected) {
    return `${focusClass} ${selectedClass}`;
  }
  if (isFocused) return focusClass;
  if (isSelected) return selectedClass;
  return baseClass;
};

/**
 * 卡片焦点样式（视频卡片）
 * @param isFocused - 是否聚焦
 */
export const getCardFocusClass = (isFocused: boolean): string => {
  return isFocused
    ? 'scale-110 shadow-2xl shadow-cyan-500/50 z-10 ring-4 ring-cyan-500'
    : '';
};

/**
 * 菜单项焦点样式
 * @param isFocused - 是否聚焦
 * @param isActive - 是否激活
 * @param gradient - 渐变样式（激活时）
 */
export const getMenuFocusClass = (
  isFocused: boolean,
  isActive: boolean,
  gradient = 'from-cyan-500 to-blue-500'
): string => {
  if (isFocused) {
    return 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/50';
  }
  if (isActive) {
    return `bg-gradient-to-r ${gradient} shadow-lg`;
  }
  return 'bg-slate-800/30';
};

/**
 * 设置项焦点样式
 * @param isFocused - 是否聚焦
 */
export const getSettingFocusClass = (isFocused: boolean): string => {
  return isFocused
    ? 'bg-blue-600/60 ring-2 ring-cyan-400 scale-[1.01]'
    : 'bg-slate-800/40';
};

/**
 * 筛选按钮焦点样式
 * @param isFocused - 是否聚焦
 * @param isSelected - 是否选中
 */
export const getFilterFocusClass = (
  isFocused: boolean,
  isSelected: boolean
): string => {
  if (isSelected) return 'bg-cyan-500 text-white';
  if (isFocused) return 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/50';
  return 'bg-slate-800/50 hover:bg-slate-700/50';
};

/**
 * 键盘按键焦点样式
 * @param isFocused - 是否聚焦
 * @param isSpecial - 是否特殊按键（搜索、清空等）
 */
export const getKeyboardFocusClass = (
  isFocused: boolean,
  isSpecial = false
): string => {
  const baseClass = isSpecial
    ? 'bg-blue-600 hover:bg-blue-700'
    : 'bg-slate-800/70 hover:bg-slate-700/70';
  
  return isFocused
    ? 'bg-cyan-500 scale-110 shadow-lg shadow-cyan-500/50'
    : baseClass;
};

/**
 * 剧集按钮焦点样式
 * @param isFocused - 是否聚焦
 * @param isSelected - 是否选中（当前播放集）
 */
export const getEpisodeFocusClass = (
  isFocused: boolean,
  isSelected: boolean
): string => {
  if (isFocused) {
    return 'bg-cyan-500 scale-105 shadow-lg shadow-cyan-500/40 text-white';
  }
  if (isSelected) {
    return 'bg-blue-600 text-white ring-1 ring-blue-400';
  }
  return 'bg-slate-700/50 text-slate-300';
};

/**
 * 全局背景渐变样式
 */
export const TV_BACKGROUND = 'bg-gradient-to-br from-[#0a1128] via-[#0d1a3a] to-[#06102a]';

/**
 * 首页特殊背景渐变
 */
export const HOME_BACKGROUND = 'bg-gradient-to-b from-[#0a0e27] via-[#0d1333] to-[#060a1f]';

/**
 * 详情页背景渐变
 */
export const DETAIL_BACKGROUND = 'bg-gradient-to-br from-[#0a1128] via-[#0d1a3a] to-[#06102a]';

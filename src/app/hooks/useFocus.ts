import { useEffect, useCallback, useState, useRef } from 'react';

export interface FocusableElement extends HTMLElement {
  dataset: DOMStringMap & {
    focusable?: string;
    focusGroup?: string;
  };
}

// TV遥控器键盘代码
export const TV_KEYS = {
  UP: ['ArrowUp'],
  DOWN: ['ArrowDown'],
  LEFT: ['ArrowLeft'],
  RIGHT: ['ArrowRight'],
  OK: ['Enter', ' '],
  BACK: ['Escape', 'Backspace'],
};

export const useFocus = (
  initialFocus?: string,
  onBack?: () => void
) => {
  const [focusedId, setFocusedId] = useState<string | null>(initialFocus || null);
  const focusedRef = useRef<string | null>(null);

  useEffect(() => {
    focusedRef.current = focusedId;
  }, [focusedId]);

  const getAllFocusableElements = useCallback((): FocusableElement[] => {
    const elements = document.querySelectorAll<FocusableElement>('[data-focusable="true"]');
    return Array.from(elements).filter(el => {
      const style = window.getComputedStyle(el);
      return style.display !== 'none' && style.visibility !== 'hidden';
    });
  }, []);

  const getElementRect = (el: FocusableElement) => {
    const rect = el.getBoundingClientRect();
    return {
      top: rect.top,
      bottom: rect.bottom,
      left: rect.left,
      right: rect.right,
      centerX: rect.left + rect.width / 2,
      centerY: rect.top + rect.height / 2,
    };
  };

  const findNextElement = useCallback(
    (direction: 'up' | 'down' | 'left' | 'right') => {
      const elements = getAllFocusableElements();
      const currentElement = elements.find(el => el.dataset.focusable === 'true' && el.id === focusedRef.current);
      
      if (!currentElement || elements.length === 0) {
        return elements[0]?.id || null;
      }

      const currentRect = getElementRect(currentElement);
      let bestElement: FocusableElement | null = null;
      let bestDistance = Infinity;

      elements.forEach(el => {
        if (el === currentElement) return;

        const rect = getElementRect(el);
        let isValidDirection = false;
        let distance = 0;

        switch (direction) {
          case 'up':
            isValidDirection = rect.centerY < currentRect.centerY;
            distance = Math.sqrt(
              Math.pow(rect.centerX - currentRect.centerX, 2) +
              Math.pow(rect.centerY - currentRect.centerY, 2)
            );
            break;
          case 'down':
            isValidDirection = rect.centerY > currentRect.centerY;
            distance = Math.sqrt(
              Math.pow(rect.centerX - currentRect.centerX, 2) +
              Math.pow(rect.centerY - currentRect.centerY, 2)
            );
            break;
          case 'left':
            isValidDirection = rect.centerX < currentRect.centerX;
            distance = Math.sqrt(
              Math.pow(rect.centerX - currentRect.centerX, 2) +
              Math.pow(rect.centerY - currentRect.centerY, 2)
            );
            break;
          case 'right':
            isValidDirection = rect.centerX > currentRect.centerX;
            distance = Math.sqrt(
              Math.pow(rect.centerX - currentRect.centerX, 2) +
              Math.pow(rect.centerY - currentRect.centerY, 2)
            );
            break;
        }

        if (isValidDirection && distance < bestDistance) {
          bestDistance = distance;
          bestElement = el;
        }
      });

      return bestElement?.id || null;
    },
    [getAllFocusableElements]
  );

  const handleKeyDown = useCallback(
    (e: KeyboardEvent) => {
      if (TV_KEYS.UP.includes(e.key)) {
        e.preventDefault();
        const nextId = findNextElement('up');
        if (nextId) setFocusedId(nextId);
      } else if (TV_KEYS.DOWN.includes(e.key)) {
        e.preventDefault();
        const nextId = findNextElement('down');
        if (nextId) setFocusedId(nextId);
      } else if (TV_KEYS.LEFT.includes(e.key)) {
        e.preventDefault();
        const nextId = findNextElement('left');
        if (nextId) setFocusedId(nextId);
      } else if (TV_KEYS.RIGHT.includes(e.key)) {
        e.preventDefault();
        const nextId = findNextElement('right');
        if (nextId) setFocusedId(nextId);
      } else if (TV_KEYS.OK.includes(e.key)) {
        e.preventDefault();
        const currentElement = document.getElementById(focusedRef.current || '');
        currentElement?.click();
      } else if (TV_KEYS.BACK.includes(e.key)) {
        e.preventDefault();
        onBack?.();
      }
    },
    [findNextElement, onBack]
  );

  useEffect(() => {
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [handleKeyDown]);

  // 初始化焦点
  useEffect(() => {
    if (!focusedId) {
      const elements = getAllFocusableElements();
      if (elements.length > 0) {
        setFocusedId(elements[0].id);
      }
    }
  }, [focusedId, getAllFocusableElements]);

  // 滚动到焦点元素
  useEffect(() => {
    if (focusedId) {
      const element = document.getElementById(focusedId);
      if (element) {
        element.scrollIntoView({
          behavior: 'smooth',
          block: 'center',
          inline: 'center',
        });
      }
    }
  }, [focusedId]);

  return {
    focusedId,
    setFocusedId,
  };
};

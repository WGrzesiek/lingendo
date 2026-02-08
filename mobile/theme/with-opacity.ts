/**
 * Adds opacity to a hex color
 * @param color - Hex color string (e.g., '#FF0000')
 * @param opacity - Opacity value between 0 and 1
 * @returns Hex color with alpha channel
 */
export function withOpacity(color: string, opacity: number): string {
  const alpha = Math.round(opacity * 255)
    .toString(16)
    .padStart(2, '0');
  return `${color}${alpha}`;
}

import { useState } from "react";
import "./MiniPieChart.css";

// A small, dependency-free donut chart for comparing a couple of values
// (e.g. Completed vs Cancelled, Collected vs Due). Each slice in `slices`
// should be `{ label, value, color }`. `formatValue` controls the number
// shown in the legend and center tooltip.
export default function MiniPieChart({ slices, formatValue = (v) => v }) {
  const [hoveredIndex, setHoveredIndex] = useState(null);

  const total = slices.reduce((sum, s) => sum + (Number(s.value) || 0), 0);
  const size = 140;
  const radius = 52;
  const strokeWidth = 22;
  const center = size / 2;
  const circumference = 2 * Math.PI * radius;

  let cumulativePct = 0;

  const activeSlice = hoveredIndex != null ? slices[hoveredIndex] : null;

  return (
    <div className="mini-pie-chart">
      <div className="mini-pie-svg-wrap">
        <svg
          width={size}
          height={size}
          viewBox={`0 0 ${size} ${size}`}
          className="mini-pie-svg"
        >
          {total <= 0 ? (
            <circle
              cx={center}
              cy={center}
              r={radius}
              fill="none"
              stroke="var(--border)"
              strokeWidth={strokeWidth}
            />
          ) : (
            slices.map((slice, index) => {
              const value = Number(slice.value) || 0;
              const pct = value / total;
              const dashArray = `${pct * circumference} ${circumference}`;
              const dashOffset = -cumulativePct * circumference;
              cumulativePct += pct;
              const isHovered = hoveredIndex === index;
              const isDimmed = hoveredIndex != null && !isHovered;

              return (
                <circle
                  key={slice.label}
                  cx={center}
                  cy={center}
                  r={radius}
                  fill="none"
                  stroke={slice.color}
                  strokeWidth={isHovered ? strokeWidth + 4 : strokeWidth}
                  strokeDasharray={dashArray}
                  strokeDashoffset={dashOffset}
                  transform={`rotate(-90 ${center} ${center})`}
                  className="mini-pie-slice"
                  style={{ opacity: isDimmed ? 0.35 : 1 }}
                  onMouseEnter={() => setHoveredIndex(index)}
                  onMouseLeave={() => setHoveredIndex(null)}
                />
              );
            })
          )}
        </svg>

        <div className="mini-pie-center">
          <span className="mini-pie-center-value">
            {activeSlice ? formatValue(activeSlice.value) : formatValue(total)}
          </span>
          <span className="mini-pie-center-label">
            {activeSlice ? activeSlice.label : "Total"}
          </span>
        </div>
      </div>

      <div className="mini-pie-legend">
        {slices.map((slice, index) => (
          <div
            key={slice.label}
            className="mini-pie-legend-item"
            onMouseEnter={() => setHoveredIndex(index)}
            onMouseLeave={() => setHoveredIndex(null)}
            style={{ opacity: hoveredIndex != null && hoveredIndex !== index ? 0.5 : 1 }}
          >
            <span
              className="mini-pie-legend-dot"
              style={{ background: slice.color }}
            />
            <span className="mini-pie-legend-label">{slice.label}</span>
            <span className="mini-pie-legend-value">
              {formatValue(slice.value)}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}

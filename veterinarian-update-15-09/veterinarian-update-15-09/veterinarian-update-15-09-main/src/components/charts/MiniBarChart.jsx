import { useState } from "react";
import "./MiniBarChart.css";

// A small, dependency-free bar chart for comparing two values
// (e.g. Completed vs Cancelled, Collected vs Due). `hoverColor` sets the
// accent color bars turn on hover; `formatValue` controls the label shown
// above each bar and in the tooltip.
export default function MiniBarChart({ bars, hoverColor, formatValue = (v) => v }) {
  const [hoveredIndex, setHoveredIndex] = useState(null);
  const max = Math.max(1, ...bars.map((b) => Number(b.value) || 0));

  return (
    <div className="mini-bar-chart">
      {bars.map((bar, index) => {
        const pct = Math.max(4, (Number(bar.value) || 0) / max * 100);
        const isHovered = hoveredIndex === index;
        return (
          <div
            key={bar.label}
            className="mini-bar-col"
            onMouseEnter={() => setHoveredIndex(index)}
            onMouseLeave={() => setHoveredIndex(null)}
          >
            <span className="mini-bar-value">
              {formatValue(bar.value)}
            </span>
            <div className="mini-bar-track">
              <div
                className="mini-bar-fill"
                style={{
                  height: `${pct}%`,
                  background: isHovered ? hoverColor : (bar.color || "var(--primary)"),
                  boxShadow: isHovered ? `0 0 0 4px ${hoverColor}33` : "none",
                }}
              />
            </div>
            <span className="mini-bar-label">{bar.label}</span>
          </div>
        );
      })}
    </div>
  );
}

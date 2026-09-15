import { useState } from "react";
import "./AppointmentsTrendChart.css";

// Responsive line chart for appointment counts, one point per day, built
// from real appointment records (no static/fake data — the caller passes
// `data` = [{ date: "YYYY-MM-DD", count }], already grouped from the
// appointments the dashboard fetched from the backend).
//
// The SVG uses a fixed logical viewBox and scales to 100% of its
// container's width via CSS, so it stays responsive and never needs
// horizontal scrolling regardless of how many days are plotted.
export default function AppointmentsTrendChart({ data, formatDateLabel }) {
  const [hoveredIndex, setHoveredIndex] = useState(null);

  const viewW = 640;
  const viewH = 220;
  const padX = 28;
  const padTop = 20;
  const padBottom = 34;

  const maxValue = Math.max(1, ...data.map((d) => d.count));
  const plotW = viewW - padX * 2;
  const plotH = viewH - padTop - padBottom;

  const points = data.map((d, index) => {
    const x =
      data.length === 1
        ? padX + plotW / 2
        : padX + (index / (data.length - 1)) * plotW;
    const y = padTop + plotH - (d.count / maxValue) * plotH;
    return { ...d, x, y };
  });

  const linePath = points
    .map((p, i) => `${i === 0 ? "M" : "L"} ${p.x} ${p.y}`)
    .join(" ");

  const areaPath =
    points.length > 0
      ? `${linePath} L ${points[points.length - 1].x} ${padTop + plotH} L ${points[0].x} ${padTop + plotH} Z`
      : "";

  const hovered = hoveredIndex != null ? points[hoveredIndex] : null;

  // Skip some x-axis labels if there are many days, so they don't overlap.
  const labelEvery = Math.max(1, Math.ceil(points.length / 8));

  if (!points.length) {
    return (
      <div className="appt-trend-empty">
        No appointment data yet.
      </div>
    );
  }

  return (
    <div className="appt-trend-chart">
      <div className="appt-trend-svg-wrap" style={{ aspectRatio: `${viewW} / ${viewH}` }}>
        <svg
          viewBox={`0 0 ${viewW} ${viewH}`}
          preserveAspectRatio="none"
          className="appt-trend-svg"
        >
          {/* horizontal gridlines */}
          {[0, 0.5, 1].map((f) => (
            <line
              key={f}
              x1={padX}
              x2={viewW - padX}
              y1={padTop + plotH * f}
              y2={padTop + plotH * f}
              className="appt-trend-grid"
            />
          ))}

          {areaPath && <path d={areaPath} className="appt-trend-area" />}
          <path d={linePath} className="appt-trend-line" />

          {points.map((p, index) => (
            <g key={p.date}>
              {/* wide invisible hit target for easier hover */}
              <rect
                x={p.x - (plotW / Math.max(points.length - 1, 1)) / 2}
                y={padTop}
                width={plotW / Math.max(points.length - 1, 1)}
                height={plotH}
                fill="transparent"
                onMouseEnter={() => setHoveredIndex(index)}
                onMouseLeave={() => setHoveredIndex(null)}
              />
              <circle
                cx={p.x}
                cy={p.y}
                r={hoveredIndex === index ? 5.5 : 3.5}
                className="appt-trend-dot"
                style={{ pointerEvents: "none" }}
              />
            </g>
          ))}
        </svg>

        {hovered && (
          <div
            className="appt-trend-tooltip"
            style={{
              left: `${(hovered.x / viewW) * 100}%`,
              top: `${(hovered.y / viewH) * 100}%`,
            }}
          >
            <span className="appt-trend-tooltip-value">{hovered.count}</span>
            <span className="appt-trend-tooltip-label">
              {formatDateLabel ? formatDateLabel(hovered.date) : hovered.date}
            </span>
          </div>
        )}
      </div>

      <div className="appt-trend-axis">
        {points.map((p, index) =>
          index % labelEvery === 0 || index === points.length - 1 ? (
            <span
              key={p.date}
              className="appt-trend-axis-label"
              style={{ left: `${(p.x / viewW) * 100}%` }}
            >
              {formatDateLabel ? formatDateLabel(p.date) : p.date}
            </span>
          ) : null
        )}
      </div>
    </div>
  );
}

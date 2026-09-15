import clsx from "clsx";

const TONE_MAP = {
  teal: "tone-primary",
  sky: "tone-info",
  amber: "tone-warning",
  emerald: "tone-success",
  gold: "tone-accent",

  primary: "tone-primary",
  accent: "tone-accent",
  info: "tone-info",
  warning: "tone-warning",
  success: "tone-success",
  danger: "tone-danger",
};

const SUB_TONE_MAP = {
  "text-emerald-500": "up",
  "text-amber-500": "warn",
  "text-red-500": "danger",
};

export function StatCard({
  icon: Icon,
  iconImageSrc,
  label,
  value,
  sub,
  subColor,
  iconBg = "teal",
  hoverColor,
  onClick,
}) {
  const tone = TONE_MAP[iconBg] || "tone-primary";
  const subTone = SUB_TONE_MAP[subColor] || "";

  return (
    <div
      className={clsx(
        "stat-card",
        onClick && "stat-card-clickable"
      )}
      style={
        hoverColor
          ? { "--stat-hover": hoverColor }
          : undefined
      }
      onClick={onClick}
      role={onClick ? "button" : undefined}
      tabIndex={onClick ? 0 : undefined}
      onKeyDown={
        onClick
          ? (e) => {
              if (e.key === "Enter" || e.key === " ") {
                e.preventDefault();
                onClick(e);
              }
            }
          : undefined
      }
    >
      <div className="stat-card-content">

        {/* =====================================
            LEFT SIDE - CONTENT
        ====================================== */}

        <div className="stat-card-info">

          <span className="stat-card-label" >
            {label }
          </span>

          <h2 className="stat-card-value">
            {value}
          </h2>

          {sub && (
            <p
              className={clsx(
                "stat-card-sub",
                
                subTone
              )}
            >
              {sub}
            </p>
          )}

        </div>


        {iconImageSrc ? (
          <div className={clsx("icon-tile", tone)}>
            <img
              src={iconImageSrc}
              alt=""
              style={{
                width: "20px",
                height: "20px",
                objectFit: "contain",
              }}
            />
          </div>
        ) : (
          Icon && (
            <div className={clsx("icon-tile", tone)}>
              <Icon size={19} strokeWidth={2.2} />
            </div>
          )
        )}
      </div>
    </div>
  );
}


/* ============================================
   NORMAL CARD
============================================ */

export default function Card({
  className = "",
  children,
  ...props
}) {
  return (
    <div
      className={clsx(
        "card",
        className
      )}
      {...props}
    >
      {children}
    </div>
  );
}
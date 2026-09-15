import clsx from "clsx";

const VARIANTS = {
  success: "badge-success",
  warning: "badge-warning",
  danger: "badge-danger",
  info: "badge-info",
  teal: "badge-teal",
  slate: "badge-slate",
  navy: "badge-navy",
  accent: "badge-accent",
  gold: "badge-accent",
};

export default function Badge({ variant = "slate", children, className = "" }) {
  return (
    <span className={clsx("badge", VARIANTS[variant] || VARIANTS.slate, className)}>
      {children}
    </span>
  );
}

import clsx from "clsx";

/* Field Wrapper */

export function Field({ label, required = false, className = "", children }) {
  return (
    <label className={clsx("field", className)}>
      {label && (
        <span className="field-label">
          {label}
          {required && <span className="field-required">*</span>}
        </span>
      )}
      {children}
    </label>
  );
}

/* Input */

export default function Input({ className = "", ...props }) {
  return <input className={clsx("input", className)} {...props} />;
}

/* Select */

export function Select({ className = "", children, ...props }) {
  return (
    <select className={clsx("select", className)} {...props}>
      {children}
    </select>
  );
}

/* Textarea */

export function Textarea({ className = "", rows = 4, ...props }) {
  return (
    <textarea rows={rows} className={clsx("textarea", className)} {...props} />
  );
}

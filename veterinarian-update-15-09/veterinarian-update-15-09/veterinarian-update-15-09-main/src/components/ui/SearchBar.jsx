import { FiSearch } from "react-icons/fi";
import clsx from "clsx";

export default function SearchBar({
  placeholder = "Search...",
  value,
  onChange,
  className = "",
}) {
  return (
    <div className={clsx("search-bar compact", className)}>
      <FiSearch size={18} />
      <input
        type="text"
        value={value}
        onChange={onChange}
        placeholder={placeholder}
      />
    </div>
  );
}

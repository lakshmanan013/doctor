import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FiSearch, FiX } from "react-icons/fi";

import useDebounce from "../../hooks/useDebounce";
import useSearch from "../../hooks/useSearch";

export default function GlobalSearch() {
  const [query, setQuery] = useState("");
  const [open, setOpen] = useState(false);
  const [activeIndex, setActiveIndex] = useState(-1);

  const wrapRef = useRef(null);
  const navigate = useNavigate();

  const debouncedQuery = useDebounce(query, 200);

  /*
   * Global search results.
   *
   * useSearch is responsible for searching:
   * - Pet name
   * - Pet ID
   * - Owner
   * - Breed
   * - Medical records
   * - Prescriptions
   * etc.
   */
  const results = useSearch(debouncedQuery);

  // =====================================================
  // CLOSE WHEN CLICKING OUTSIDE
  // =====================================================

  useEffect(() => {
    function handleClickOutside(event) {
      if (
        wrapRef.current &&
        !wrapRef.current.contains(event.target)
      ) {
        setOpen(false);
      }
    }

    document.addEventListener(
      "mousedown",
      handleClickOutside
    );

    return () => {
      document.removeEventListener(
        "mousedown",
        handleClickOutside
      );
    };
  }, []);

  // =====================================================
  // RESET ACTIVE RESULT
  // =====================================================

  useEffect(() => {
    setActiveIndex(-1);
  }, [debouncedQuery]);

  // =====================================================
  // OPEN RESULT
  // =====================================================

  function goTo(result) {
    if (!result?.path) {
      return;
    }

    navigate(result.path);

    setOpen(false);
    setQuery("");
    setActiveIndex(-1);
  }

  // =====================================================
  // KEYBOARD NAVIGATION
  // =====================================================

  function handleKeyDown(event) {
    // ESC
    if (event.key === "Escape") {
      setOpen(false);
      setActiveIndex(-1);

      event.currentTarget.blur();

      return;
    }

    if (
      !open ||
      results.length === 0
    ) {
      return;
    }

    // ARROW DOWN
    if (event.key === "ArrowDown") {
      event.preventDefault();

      setActiveIndex(
        (index) =>
          (index + 1) % results.length
      );

      return;
    }

    // ARROW UP
    if (event.key === "ArrowUp") {
      event.preventDefault();

      setActiveIndex(
        (index) =>
          (index - 1 + results.length) %
          results.length
      );

      return;
    }

    // ENTER
    if (
      event.key === "Enter" &&
      activeIndex >= 0
    ) {
      event.preventDefault();

      goTo(
        results[activeIndex]
      );
    }
  }

  // =====================================================
  // SHOW SEARCH PANEL
  // =====================================================

  const showPanel =
    open &&
    debouncedQuery.trim().length > 0;

  // =====================================================
  // RENDER
  // =====================================================

  const inputRef = useRef(null);

  useEffect(() => {
    const handleGlobalKeyDown = (e) => {
      if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === "k") {
        e.preventDefault();
        inputRef.current?.focus();
        setOpen(true);
      }
    };
    window.addEventListener("keydown", handleGlobalKeyDown);
    return () => window.removeEventListener("keydown", handleGlobalKeyDown);
  }, []);

  return (
    <div
      className="global-search"
      ref={wrapRef}
    >
      {/* SEARCH ICON */}
      <FiSearch size={18} className="search-icon" />

      {/* SEARCH INPUT */}
      <input
        ref={inputRef}
        type="text"
        placeholder="Search pet, Pet ID, owner, RX..."
        value={query}
        onChange={(event) => {
          setQuery(event.target.value);
          setOpen(true);
        }}
        onFocus={() => {
          if (query.trim()) {
            setOpen(true);
          }
        }}
        onKeyDown={handleKeyDown}
        aria-label="Global search"
        autoComplete="off"
      />

      {!query && (
        <span className="search-shortcut-badge">⌘K</span>
      )}

      {/* CLEAR */}

      {query && (
        <button
          type="button"
          className="search-clear"
          aria-label="Clear search"
          onClick={() => {
            setQuery("");
            setOpen(false);
            setActiveIndex(-1);
          }}
        >
          <FiX size={14} />
        </button>
      )}

      {/* =================================================
          SEARCH RESULTS
      ================================================= */}

      {showPanel && (
        <div className="search-results">

          {results.length === 0 ? (

            <p className="search-empty">
              No results for "{debouncedQuery}"
            </p>

          ) : (

            results.map((result, index) => (

              <button
                type="button"
                key={`${result.type}-${result.id}`}
                className={
                  index === activeIndex
                    ? "search-result-item active"
                    : "search-result-item"
                }
                onMouseEnter={() =>
                  setActiveIndex(index)
                }
                onClick={() =>
                  goTo(result)
                }
              >

                {/* ICON */}

                <span className="search-result-icon">
                  {result.icon || "🐾"}
                </span>

                {/* RESULT TEXT */}

                <span className="search-result-text">

                  <span className="search-result-title">
                    {result.title}
                  </span>

                  <span className="search-result-subtitle">
                    {result.subtitle}
                  </span>

                </span>

                {/* TYPE */}

                <span className="search-result-type">
                  {result.type}
                </span>

              </button>

            ))
          )}

        </div>
      )}
    </div>
  );
}
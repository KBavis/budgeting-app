import React, { useEffect, useContext } from "react";
import ReactDOM from "react-dom";
import { FaTimes } from "react-icons/fa";
import { ThemeContext } from "../../context/theme/ThemeContext";

/**
 * Standardized Application Modal Component with ThemeContext Light/Dark Mode support.
 * Uses ReactDOM.createPortal to append directly to document.body, ensuring fixed overlays
 * never get trapped inside parent CSS transform/backdrop-filter containers.
 * 
 * @param {boolean} isOpen - Whether modal is open
 * @param {function} onClose - Callback to close modal
 * @param {string} title - Optional title header
 * @param {string} size - Modal size: 'sm', 'md', 'lg', 'xl' (default: 'md')
 * @param {ReactNode} children - Content inside modal
 */
const Modal = ({ isOpen = true, onClose, title, size = "md", children }) => {
  const { theme } = useContext(ThemeContext);
  const isDark = theme === "dark";

  // Handle ESC key press to dismiss modal
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === "Escape" && onClose) {
        onClose();
      }
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [onClose]);

  // Prevent background body scroll when modal is open
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = "hidden";
    }
    return () => {
      document.body.style.overflow = "unset";
    };
  }, [isOpen]);

  if (!isOpen) return null;

  const sizeClasses = {
    sm: "max-w-md",
    md: "max-w-xl",
    lg: "max-w-3xl",
    xl: "max-w-5xl",
  };

  const modalContent = (
    <div
      className={`fixed inset-0 z-[2000] flex items-center justify-center p-4 sm:p-6 overflow-y-auto backdrop-blur-md animate-fade-in ${
        isDark ? "bg-slate-950/75 text-slate-100" : "bg-slate-900/40 text-slate-800"
      }`}
      onClick={(e) => {
        if (e.target === e.currentTarget && onClose) {
          onClose();
        }
      }}
    >
      <div
        className={`relative w-full ${sizeClasses[size] || sizeClasses.md} border rounded-2xl shadow-2xl overflow-hidden animate-modal-enter flex flex-col max-h-[90vh] ${
          isDark
            ? "bg-slate-900 border-slate-800 text-white"
            : "bg-white border-slate-200 text-slate-900"
        }`}
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className={`flex items-center justify-between px-6 py-4 border-b ${
          isDark ? "border-slate-800/80 bg-slate-900/50" : "border-slate-100 bg-slate-50/50"
        }`}>
          {title ? (
            <h3 className={`text-xl font-bold tracking-tight ${isDark ? "text-slate-100" : "text-slate-900"}`}>
              {title}
            </h3>
          ) : (
            <div />
          )}
          {onClose && (
            <button
              onClick={onClose}
              className={`p-2 rounded-full transition-colors duration-200 focus:outline-none ${
                isDark
                  ? "text-slate-400 hover:text-white hover:bg-slate-800"
                  : "text-slate-400 hover:text-slate-700 hover:bg-slate-100"
              }`}
              title="Close modal (Esc)"
            >
              <FaTimes className="w-5 h-5" />
            </button>
          )}
        </div>

        {/* Body */}
        <div className={`p-6 overflow-y-auto flex-1 ${isDark ? "text-slate-200" : "text-slate-700"}`}>
          {children}
        </div>
      </div>
    </div>
  );

  return ReactDOM.createPortal(modalContent, document.body);
};

export default Modal;

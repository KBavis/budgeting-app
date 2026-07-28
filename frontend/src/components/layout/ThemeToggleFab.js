import React, { useContext } from "react";
import { FaSun, FaMoon } from "react-icons/fa";
import { ThemeContext } from "../../context/theme/ThemeContext";

/**
 * Animated Floating Light/Dark Mode Toggle Slider
 * Fixed at bottom-6 right-6 z-[999] with smooth spring sliding animation,
 * glassmorphism, and clear active state indicators.
 */
const ThemeToggleFab = () => {
   const { theme, toggleTheme, isThemeEnabled } = useContext(ThemeContext);

   if (!isThemeEnabled) {
      return null;
   }

   const isDark = theme === "dark";

   return (
      <div
         onClick={toggleTheme}
         role="button"
         tabIndex={0}
         onKeyDown={(e) => {
            if (e.key === "Enter" || e.key === " ") {
               toggleTheme();
            }
         }}
         className={`fixed bottom-6 right-6 z-[999] w-20 h-10 rounded-full border p-1 shadow-2xl backdrop-blur-md cursor-pointer transition-all duration-300 hover:scale-105 select-none ${isDark
               ? "bg-slate-900/90 border-slate-700/80 hover:border-indigo-500/50 shadow-indigo-950/30"
               : "bg-white/90 border-slate-300/90 hover:border-amber-400/60 shadow-slate-300/50"
            }`}
         title={isDark ? "Switch to Light Mode" : "Switch to Dark Mode"}
         aria-label="Toggle light/dark theme"
      >
         {/* Background Track Icons */}
         <div className="relative w-full h-full flex items-center justify-between px-2 text-xs pointer-events-none">
            <FaSun
               className={`transition-all duration-300 ${isDark ? "text-slate-600 opacity-40 scale-90" : "text-amber-500 opacity-0 scale-75"
                  }`}
            />
            <FaMoon
               className={`transition-all duration-300 ${isDark ? "text-indigo-400 opacity-0 scale-75" : "text-slate-400 opacity-40 scale-90"
                  }`}
            />
         </div>

         {/* Animated Sliding Thumb Knob */}
         <div
            className={`absolute top-1 left-1 w-8 h-8 rounded-full flex items-center justify-center shadow-md transition-transform duration-500 cubic-bezier-[0.34,1.56,0.64,1] ${isDark
                  ? "translate-x-10 bg-slate-800 border border-slate-600 text-indigo-400 shadow-indigo-950/50"
                  : "translate-x-0 bg-amber-400 border border-amber-300 text-amber-950 shadow-amber-500/30"
               }`}
         >
            {isDark ? (
               <FaMoon className="w-4 h-4 transition-transform duration-300 rotate-0" />
            ) : (
               <FaSun className="w-4 h-4 transition-transform duration-300 rotate-0" />
            )}
         </div>
      </div>
   );
};

export default ThemeToggleFab;

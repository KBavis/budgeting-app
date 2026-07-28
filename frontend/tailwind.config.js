/** @type {import('tailwindcss').Config} */

module.exports = {
   darkMode: 'class',
   content: ["./src/**/*.{js,jsx,ts,tsx}"],
   theme: {
      extend: {
         fontFamily: {
            roboto: ["Roboto", "sans-serif"],
            sans: ["Inter", "system-ui", "sans-serif"],
         },
         scale: {
            1025: "1.025",
         },
         fontSize: {
            xxs: "0.65rem",
         },
         screens: {
            xs: "300px",
            xl: "1790px",
            md: "750px",
            "md-xl": "1100px",
            xxl: "2300px",
         },
         colors: {
            budget: {
               safe: "#10B981",      // emerald green (< 80%)
               caution: "#F59E0B",   // amber yellow (80-99%)
               warning: "#F97316",   // orange (100-115%)
               danger: "#EF4444",    // red (> 115%)
            },
            brand: {
               50: "#EEF2FF",
               100: "#E0E7FF",
               200: "#C7D2FE",
               300: "#A5B4FC",
               400: "#818CF8",
               500: "#6366F1",
               600: "#4F46E5",
               700: "#4338CA",
               800: "#3730A3",
               900: "#312E81",
            },
            surface: {
               primary: "#0F172A",
               secondary: "#1E293B",
               card: "rgba(30, 41, 59, 0.8)",
            },
         },
         keyframes: {
            modalEnter: {
               '0%': { opacity: '0', transform: 'scale(0.95) translateY(10px)' },
               '100%': { opacity: '1', transform: 'scale(1) translateY(0)' },
            },
            modalExit: {
               '0%': { opacity: '1', transform: 'scale(1) translateY(0)' },
               '100%': { opacity: '0', transform: 'scale(0.95) translateY(10px)' },
            },
            fadeIn: {
               '0%': { opacity: '0' },
               '100%': { opacity: '1' },
            },
            slideUp: {
               '0%': { opacity: '0', transform: 'translateY(20px)' },
               '100%': { opacity: '1', transform: 'translateY(0)' },
            },
         },
         animation: {
            'modal-enter': 'modalEnter 0.25s ease-out forwards',
            'modal-exit': 'modalExit 0.2s ease-in forwards',
            'fade-in': 'fadeIn 0.25s ease-out forwards',
            'slide-up': 'slideUp 0.3s ease-out forwards',
         },
      },
   },
   plugins: [],
};

/** @type {import('tailwindcss').Config} */

module.exports = {
   content: ["./src/**/*.{js,jsx,ts,tsx}"],
   theme: {
      extend: {
         fontFamily: {
            roboto: ["Roboto", "sans-serif"],
         },
         scale: {
            1025: "1.025",
         },
         screens: {
            xl: "1790px",
            "md-xl": "1100px",
            xxl: "2300px",
         },
      },
   },
   plugins: [],
};

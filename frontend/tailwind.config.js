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
         fontSize: {
            xxs: "0.65rem",
         },
         screens: {
            xs: "300px",
            xl: "1790px",
            "md-xl": "1100px",
            xxl: "2300px",
         },
      },
   },
   plugins: [],
};

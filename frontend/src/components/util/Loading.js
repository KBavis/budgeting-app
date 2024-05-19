import React from "react";

/**
 * Util Component to simulate our application loading while making REST API calls
 */
const Loading = () => {
   return (
      <div className="flex items-center justify-center min-h-screen">
         <div className="w-16 h-16 border-4 border-dashed rounded-full animate-spin border-green-500"></div>
      </div>
   );
};

export default Loading;

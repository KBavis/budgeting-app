import React from "react";

/**
 * Util Component to simulate our application loading while making REST API calls
 */
const Loading = () => {
   return (
      <div className="flex justify-center h-screen items-center">
         <div className="border-t-4 border-white rounded-full animate-spin w-16 h-16 text-white"></div>
      </div>
   );
};

export default Loading;

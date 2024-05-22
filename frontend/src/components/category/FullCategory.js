// Category.js
import React from "react";

const FullCategory = ({ category }) => {
   return (
      <div className="bg-white rounded-lg shadow-md p-4 w-1/3 mx-2">
         <h3 className="text-2xl font-bold mb-2">{category.name}</h3>
         <p className="text-lg">Details for {category.name} go here.</p>
      </div>
   );
};

export default FullCategory;

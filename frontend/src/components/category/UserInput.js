import React, { useState } from "react";

const UserInput = ({ onSubmit }) => {
  const [category, setCategory] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (category.trim() !== "") {
      onSubmit(category);
      setCategory("");
    }
  };

  return (
    <div className="mb-8">
      <h2 className="text-2xl font-bold mb-4 text-white">
        Add Your Own Category
      </h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          placeholder="Enter a category"
          className="border border-gray-300 rounded p-2 w-full"
        />
        <button
          type="submit"
          className="mt-4 font-bold py-2 px-4 rounded text-white bg-indigo-600 hover:bg-indigo-700 transition-colors duration-300"
        >
          Add Category
        </button>
      </form>
    </div>
  );
};

export default UserInput;

import React, { useContext, useEffect, useState } from "react";
import Breakdown from "./Breakdown";
import Top3Merchants from "./Top3Merchants";
import categoryContext from "../../../context/category/categoryContext";
import { ThemeContext } from "../../../context/theme/ThemeContext";
import { FaFolderOpen, FaTag } from "react-icons/fa";

const CategoryPerformance = ({ performance }) => {
    const [categoryName, setCategoryName] = useState("");
    const { categories } = useContext(categoryContext);
    const { theme } = useContext(ThemeContext);

    const isDark = theme === "dark";

    // update the category name based on current CategoryPerformance
    useEffect(() => {
        if (categories && performance) {
            let category = categories.find((cat) => cat.categoryId == performance.categoryId);
            if (category) {
                setCategoryName(category.name);
            } else if (performance.categoryName) {
                setCategoryName(performance.categoryName);
            }
        }
    }, [performance, categories]);

    const totalSpend = typeof performance?.totalSpend === 'number' ? performance.totalSpend : 0;
    const totalAllocated = typeof performance?.totalAmountAllocated === 'number' ? performance.totalAmountAllocated : 0;

    return (
        <div className={`flex flex-col items-center w-full my-6 p-6 rounded-3xl border shadow-xl transition-all duration-300 ${
            isDark
                ? "bg-slate-900/90 border-slate-800/90 text-slate-100 shadow-indigo-950/20"
                : "bg-white border-slate-200 text-slate-800 shadow-slate-200/50"
        }`}>
            {/* Card Header */}
            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between w-full mb-6 pb-4 border-b border-slate-700/30 gap-3">
                <div className="flex items-center gap-3">
                    <div className={`p-3 rounded-2xl border shadow-sm ${
                        isDark
                            ? "bg-gradient-to-br from-indigo-500/20 to-purple-500/20 border-indigo-500/30 text-indigo-400"
                            : "bg-gradient-to-br from-indigo-50 to-indigo-100/50 border-indigo-200 text-indigo-600"
                    }`}>
                        <FaFolderOpen size={20} />
                    </div>
                    <div>
                        <h2 className={`font-black text-2xl tracking-tight ${isDark ? "text-white" : "text-slate-900"}`}>
                            {categoryName || "Category Performance"}
                        </h2>
                        <p className={`text-xs font-semibold ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                            Budget Allocation: ${totalAllocated.toFixed(2)}
                        </p>
                    </div>
                </div>

                <div className="flex items-center gap-2">
                    <span className={`text-xs font-extrabold px-4 py-2 rounded-2xl border shadow-sm flex items-center gap-2 ${
                        isDark
                            ? "bg-slate-800/90 border-slate-700 text-slate-200"
                            : "bg-slate-100/80 border-slate-200 text-slate-800"
                    }`}>
                        <FaTag size={10} className="text-indigo-400" /> Total Spent:{" "}
                        <span className={`font-black ${isDark ? "text-emerald-400" : "text-emerald-600"}`}>
                            ${totalSpend.toFixed(2)}
                        </span>
                    </span>
                </div>
            </div>

            {/* Grid for Breakdown & Top 3 Merchants */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 w-full items-start">
                <Breakdown performance={performance} />
                <Top3Merchants performance={performance} />
            </div>
        </div>
    );
};

export default CategoryPerformance;
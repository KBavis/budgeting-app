import React, { useContext, useEffect, useState, useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";
import categoryTypeContext from "../context/category/types/categoryTypeContext";
import CategoryPerformanceContext from "../context/category/performances/categoryPerformanceContext";
import Loading from "../components/util/Loading";
import CategoryPerformance from "../components/category/performances/CategoryPerformance";
import categoryContext from "../context/category/categoryContext";
import { ThemeContext } from "../context/theme/ThemeContext";
import { FaArrowLeft, FaChartLine, FaRegFrownOpen, FaWallet, FaReceipt, FaFolderOpen } from "react-icons/fa";

const SpendingAnalysisPage = () => {
    const { categoryTypes, fetchCategoryTypes } = useContext(categoryTypeContext);
    const { category_performances, fetchCategoryPerformances, loading } = useContext(CategoryPerformanceContext);
    const { categories, fetchCategories } = useContext(categoryContext);
    const { theme } = useContext(ThemeContext);

    const isDark = theme === "dark";

    const [currentType, setCurrentType] = useState(null);
    const [filteredPerformances, setFilteredPerformances] = useState([]);
    const navigate = useNavigate();
    const { type, month, year } = useParams();

    // determine the relevant Category Type the user selected
    useEffect(() => {
        if (categoryTypes && categoryTypes.length > 0 && type) {
            const matchedType = categoryTypes.find((t) => t.name.toLowerCase() === type.toLowerCase());
            if (matchedType) {
                setCurrentType(matchedType);
            }
        }
    }, [categoryTypes, type]);

    // fetch user categories if page is refreshed
    useEffect(() => {
        if (!categories || categories.length === 0) {
            fetchCategories();
        }
    }, [categories]);

    // fetch category types if page is refreshed
    useEffect(() => {
        if (!categoryTypes || categoryTypes.length === 0) {
            fetchCategoryTypes();
        }
    }, [categoryTypes]);

    // fetch category performances when currentType is set or updated
    useEffect(() => {
        if (currentType && month && year) {
            const monthYear = { month: month.toUpperCase(), year: parseInt(year, 10) };
            fetchCategoryPerformances([currentType.categoryTypeId], monthYear);
        }
    }, [currentType, month, year]);

    // filter category performances based on current category type, sort based on highest spend
    useEffect(() => {
        if (currentType && category_performances && category_performances.length > 0) {
            let currPerformances = category_performances
                .filter((curr) => !curr.categoryTypeId || String(curr.categoryTypeId) === String(currentType.categoryTypeId))
                .filter((curr) => (curr.totalSpend || 0) > 0)
                .sort((a, b) => b.totalSpend - a.totalSpend);
            setFilteredPerformances(currPerformances);
        } else {
            setFilteredPerformances([]);
        }
    }, [currentType, category_performances]);

    // Aggregate summary statistics across all categories for this category type
    const totals = useMemo(() => {
        let totalSpend = 0;
        let totalAllocated = 0;
        let totalTx = 0;
        filteredPerformances.forEach((p) => {
            totalSpend += p.totalSpend || 0;
            totalAllocated += p.totalAmountAllocated || 0;
            totalTx += p.transactionCount || 0;
        });
        return { totalSpend, totalAllocated, totalTx, count: filteredPerformances.length };
    }, [filteredPerformances]);

    const capitalizeFirstLetter = (word) => {
        if (!word) return "";
        return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
    };

    return (
        <div className={`flex flex-col min-h-screen relative ${
            isDark
                ? "bg-gradient-to-br from-gray-900 via-slate-900 to-indigo-950 text-slate-100"
                : "bg-gradient-to-br from-slate-100 via-indigo-50/50 to-slate-100 text-slate-800"
        }`}>
            {/* Top Navigation Bar */}
            <div className="w-full max-w-5xl mx-auto pt-16 px-4 md:px-0">
                <button
                    onClick={() => navigate(-1)}
                    className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs font-black uppercase tracking-wider transition-all duration-200 shadow-md hover:scale-105 active:scale-95 ${
                        isDark
                            ? "bg-slate-800/90 border border-slate-700 text-slate-200 hover:bg-slate-700 hover:text-white"
                            : "bg-white/90 border border-slate-200 text-slate-700 hover:bg-slate-50"
                    }`}
                >
                    <FaArrowLeft size={11} />
                    Back to Budget Overview
                </button>
            </div>

            {/* Main Content Area */}
            <div className="flex flex-col items-center justify-center flex-1 mt-6 px-4 pb-16 w-full max-w-5xl mx-auto">
                {!loading && (
                    <>
                        {/* Page Header */}
                        <div className="text-center mb-8">
                            <h1 className={`text-4xl md:text-5xl font-black mb-2 ${isDark ? "text-white" : "text-slate-900"}`}>
                                {capitalizeFirstLetter(type)} Analysis
                            </h1>
                            <p className={`text-sm font-semibold ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                                Detailed category utilization and top merchant breakdowns for {capitalizeFirstLetter(month)} {year}
                            </p>
                        </div>

                        {/* Summary Metrics Hero Banner */}
                        {filteredPerformances.length > 0 && (
                            <div className={`w-full mb-8 p-6 rounded-3xl border shadow-xl backdrop-blur-md transition-all ${
                                isDark
                                    ? "bg-gradient-to-r from-slate-900/90 via-indigo-950/40 to-slate-900/90 border-slate-800"
                                    : "bg-gradient-to-r from-white via-indigo-50/60 to-white border-slate-200"
                            }`}>
                                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-center">
                                    <div className="flex flex-col items-center">
                                        <div className="flex items-center gap-1.5 text-xs font-bold uppercase tracking-wider mb-1 text-emerald-400">
                                            <FaWallet size={12} /> Total Spent
                                        </div>
                                        <p className={`text-2xl font-black ${isDark ? "text-emerald-400" : "text-emerald-600"}`}>
                                            ${totals.totalSpend.toFixed(2)}
                                        </p>
                                    </div>

                                    <div className="flex flex-col items-center">
                                        <div className="flex items-center gap-1.5 text-xs font-bold uppercase tracking-wider mb-1 text-indigo-400">
                                            <FaWallet size={12} /> Total Budgeted
                                        </div>
                                        <p className={`text-2xl font-black ${isDark ? "text-white" : "text-slate-900"}`}>
                                            ${totals.totalAllocated.toFixed(2)}
                                        </p>
                                    </div>

                                    <div className="flex flex-col items-center">
                                        <div className="flex items-center gap-1.5 text-xs font-bold uppercase tracking-wider mb-1 text-violet-400">
                                            <FaFolderOpen size={12} /> Categories
                                        </div>
                                        <p className={`text-2xl font-black ${isDark ? "text-slate-200" : "text-slate-800"}`}>
                                            {totals.count}
                                        </p>
                                    </div>

                                    <div className="flex flex-col items-center">
                                        <div className="flex items-center gap-1.5 text-xs font-bold uppercase tracking-wider mb-1 text-sky-400">
                                            <FaReceipt size={12} /> Transactions
                                        </div>
                                        <p className={`text-2xl font-black ${isDark ? "text-slate-200" : "text-slate-800"}`}>
                                            {totals.totalTx}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        )}
                    </>
                )}

                {/* Performance Cards List */}
                <div className="w-full flex flex-col items-center">
                    {!loading ? (
                        filteredPerformances.length > 0 ? (
                            filteredPerformances.map((performance) => (
                                <CategoryPerformance
                                    key={performance.categoryPerformanceId || performance.categoryId}
                                    performance={performance}
                                />
                            ))
                        ) : (
                            <div className={`w-full max-w-md my-8 p-10 border rounded-2xl text-center flex flex-col items-center gap-3 ${
                                isDark ? "bg-slate-900/60 border-slate-800 text-slate-400" : "bg-white border-slate-200 text-slate-500 shadow-sm"
                            }`}>
                                <FaRegFrownOpen size={32} className="text-indigo-400 opacity-60" />
                                <h3 className={`text-lg font-extrabold ${isDark ? "text-slate-200" : "text-slate-800"}`}>
                                    No Spending Analysis Found
                                </h3>
                                <p className="text-xs">
                                    No category spending records exist for <span className="font-bold text-indigo-400">{capitalizeFirstLetter(type)}</span> in <span className="font-bold text-indigo-400">{capitalizeFirstLetter(month)} {year}</span>.
                                </p>
                            </div>
                        )
                    ) : (
                        <div className="py-20 flex flex-col items-center gap-3">
                            <Loading />
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default SpendingAnalysisPage;
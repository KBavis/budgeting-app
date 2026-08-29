import React, { useContext, useEffect, useState } from "react";
import { ThemeContext } from "../../../context/theme/ThemeContext";
import { getBudgetStatus } from "../../../utils/budgetColors";
import { FaChartPie, FaWallet, FaReceipt, FaPercentage, FaCheckCircle, FaExclamationTriangle } from "react-icons/fa";

const Breakdown = ({ performance }) => {
    const { theme } = useContext(ThemeContext);
    const isDark = theme === "dark";

    const [percentUtilized, setPercentUtilized] = useState(0.0);

    const totalSpend = typeof performance?.totalSpend === 'number' ? performance.totalSpend : 0;
    const totalAmountAllocated = typeof performance?.totalAmountAllocated === 'number' ? performance.totalAmountAllocated : 0;
    const transactionCount = performance?.transactionCount || 0;
    const rawUtilization = typeof performance?.categoryPercentUtilization === 'number' ? performance.categoryPercentUtilization : 0;

    useEffect(() => {
        setPercentUtilized(rawUtilization * 100);
    }, [performance, rawUtilization]);

    // Use standardized budget status utility matching home page & dashboard
    const budgetStatus = getBudgetStatus(totalSpend, totalAmountAllocated);
    const barWidth = Math.min(percentUtilized, 100);

    return (
        <div className={`border rounded-2xl shadow-md flex flex-col p-6 transition-all ${
            isDark
                ? "bg-slate-800/80 border-slate-700/80 text-slate-100"
                : "bg-white border-slate-200 text-slate-800"
        }`}>
            {/* Card Header */}
            <div className="flex items-center justify-between mb-5 pb-3 border-b border-slate-700/30">
                <div className="flex items-center gap-2">
                    <div className={`p-1.5 rounded-lg ${isDark ? "bg-indigo-500/20 text-indigo-400" : "bg-indigo-100 text-indigo-600"}`}>
                        <FaChartPie size={14} />
                    </div>
                    <h3 className={`text-base font-extrabold ${isDark ? "text-white" : "text-slate-900"}`}>
                        Spending Highlights
                    </h3>
                </div>
                
                {/* Dashboard-Consistent Status Pill */}
                <span className={`flex items-center gap-1.5 text-[11px] font-black px-2.5 py-1 rounded-full border shadow-sm ${
                    isDark
                        ? `${budgetStatus.textClass} bg-slate-900/60 border-slate-700`
                        : `${budgetStatus.textClass} bg-slate-50 border-slate-200`
                }`}>
                    {rawUtilization <= 1.0 ? <FaCheckCircle size={10} /> : <FaExclamationTriangle size={10} />}
                    <span>{rawUtilization <= 1.0 ? 'On Track' : 'Over Budget'}</span>
                </span>
            </div>

            {/* Utilization Bar matching Dashboard Colors */}
            <div className="mb-6">
                <div className="flex justify-between text-xs font-bold mb-2">
                    <span className={isDark ? "text-slate-400" : "text-slate-500"}>Budget Utilization</span>
                    <span className={`font-black ${budgetStatus.textClass}`}>{percentUtilized.toFixed(1)}%</span>
                </div>
                <div className={`w-full rounded-full h-3 p-0.5 ${isDark ? "bg-slate-900/60" : "bg-slate-100"}`}>
                    <div
                        className={`h-2 rounded-full transition-all duration-500 ${budgetStatus.colorClass}`}
                        style={{ width: `${barWidth}%` }}
                    />
                </div>
            </div>

            {/* Compact 2x2 Metric Tiles Grid (no empty vertical stretching) */}
            <div className="grid grid-cols-2 gap-3.5 w-full">
                {/* Total Spend Tile */}
                <div className={`p-3.5 rounded-xl border flex flex-col justify-between ${
                    isDark ? "bg-slate-900/40 border-slate-700/50" : "bg-slate-50/80 border-slate-200/80"
                }`}>
                    <div className="flex items-center gap-1.5 mb-2">
                        <FaWallet className="text-emerald-500" size={12} />
                        <span className={`text-[11px] font-bold uppercase tracking-wider ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                            Total Spend
                        </span>
                    </div>
                    <p className={`font-black text-lg ${isDark ? "text-emerald-400" : "text-emerald-600"}`}>
                        ${totalSpend.toFixed(2)}
                    </p>
                </div>

                {/* Budget Allocated Tile */}
                <div className={`p-3.5 rounded-xl border flex flex-col justify-between ${
                    isDark ? "bg-slate-900/40 border-slate-700/50" : "bg-slate-50/80 border-slate-200/80"
                }`}>
                    <div className="flex items-center gap-1.5 mb-2">
                        <FaWallet className="text-indigo-400" size={12} />
                        <span className={`text-[11px] font-bold uppercase tracking-wider ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                            Budgeted
                        </span>
                    </div>
                    <p className={`font-black text-lg ${isDark ? "text-white" : "text-slate-900"}`}>
                        ${totalAmountAllocated.toFixed(2)}
                    </p>
                </div>

                {/* Transactions Count Tile */}
                <div className={`p-3.5 rounded-xl border flex flex-col justify-between ${
                    isDark ? "bg-slate-900/40 border-slate-700/50" : "bg-slate-50/80 border-slate-200/80"
                }`}>
                    <div className="flex items-center gap-1.5 mb-2">
                        <FaReceipt className="text-violet-400" size={12} />
                        <span className={`text-[11px] font-bold uppercase tracking-wider ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                            Transactions
                        </span>
                    </div>
                    <p className={`font-black text-lg ${isDark ? "text-slate-200" : "text-slate-800"}`}>
                        {transactionCount}
                    </p>
                </div>

                {/* Remaining Budget / Saved Tile */}
                <div className={`p-3.5 rounded-xl border flex flex-col justify-between ${
                    isDark ? "bg-slate-900/40 border-slate-700/50" : "bg-slate-50/80 border-slate-200/80"
                }`}>
                    <div className="flex items-center gap-1.5 mb-2">
                        <FaPercentage className="text-sky-400" size={12} />
                        <span className={`text-[11px] font-bold uppercase tracking-wider ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                            Remaining
                        </span>
                    </div>
                    <p className={`font-black text-lg ${
                        (totalAmountAllocated - totalSpend) >= 0
                            ? isDark ? "text-emerald-400" : "text-emerald-600"
                            : isDark ? "text-red-400" : "text-red-600"
                    }`}>
                        ${(totalAmountAllocated - totalSpend).toFixed(2)}
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Breakdown;

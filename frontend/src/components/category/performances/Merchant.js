import React, { useContext } from 'react';
import { ThemeContext } from '../../../context/theme/ThemeContext';
import { FaCrown, FaMedal, FaReceipt, FaMoneyBillWave } from 'react-icons/fa';

const Merchant = ({ merchant, categoryTotalSpend = 0, rank = 1 }) => {
    const { theme } = useContext(ThemeContext);
    const isDark = theme === "dark";

    if (!merchant) return null;

    const merchantName = merchant.merchantName || merchant.name || "Unknown Merchant";
    const totalSpent = typeof merchant.totalSpent === 'number' ? merchant.totalSpent : 0;
    const transactionCount = merchant.transactionCount || 0;
    
    // Calculate merchant percentage of overall category spend
    const merchantSpendPct = categoryTotalSpend > 0 ? Math.min((totalSpent / categoryTotalSpend) * 100, 100) : 0;

    // Safely calculate avgTransactionAmount if missing or null
    let avgAmount = merchant.avgTransactionAmount;
    if (typeof avgAmount !== 'number' || avgAmount === null) {
        avgAmount = transactionCount > 0 ? totalSpent / transactionCount : totalSpent;
    }

    const rankConfig = {
        1: {
            badge: isDark
                ? "bg-amber-500/20 text-amber-300 border-amber-500/40"
                : "bg-amber-100 text-amber-950 border-amber-300 font-extrabold shadow-sm",
            icon: <FaCrown className={isDark ? "text-amber-400" : "text-amber-600"} size={11} />,
            label: "#1 Top Spender",
            barBg: isDark ? "bg-amber-400" : "bg-amber-500"
        },
        2: {
            badge: isDark
                ? "bg-slate-700/60 text-slate-300 border-slate-600"
                : "bg-slate-200 text-slate-800 border-slate-300 font-extrabold",
            icon: <FaMedal className={isDark ? "text-slate-300" : "text-slate-600"} size={11} />,
            label: "#2 Spender",
            barBg: isDark ? "bg-slate-400" : "bg-slate-500"
        },
        3: {
            badge: isDark
                ? "bg-orange-900/20 text-orange-300 border-orange-700/40"
                : "bg-orange-100 text-orange-950 border-orange-300 font-extrabold",
            icon: <FaMedal className={isDark ? "text-orange-400" : "text-orange-600"} size={11} />,
            label: "#3 Spender",
            barBg: isDark ? "bg-orange-500" : "bg-orange-600"
        }
    };

    const currentRank = rankConfig[rank] || rankConfig[3];

    return (
        <div className={`group rounded-2xl border p-4 transition-all duration-300 shadow-sm hover:shadow-md ${
            isDark
                ? "bg-slate-800/70 border-slate-700/70 text-slate-100 hover:border-indigo-500/50 hover:bg-slate-800"
                : "bg-gradient-to-br from-slate-50 to-white border-slate-200 text-slate-800 hover:border-indigo-300 hover:shadow-indigo-500/5"
        }`}>
            {/* Merchant Header */}
            <div className={`flex items-center justify-between mb-3 pb-2.5 border-b ${
                isDark ? "border-slate-700/60" : "border-slate-100"
            }`}>
                <div className="flex items-center gap-3">
                    {merchant.merchantLogoUrl ? (
                        <img
                            src={merchant.merchantLogoUrl}
                            alt="Merchant Logo"
                            className="w-8 h-8 rounded-full flex-shrink-0 border border-indigo-500/30 object-cover shadow-sm group-hover:scale-105 transition-transform"
                        />
                    ) : (
                        <div className="w-8 h-8 rounded-full flex-shrink-0 bg-indigo-600/20 text-indigo-400 font-black flex items-center justify-center text-xs border border-indigo-500/30 shadow-sm group-hover:scale-105 transition-transform">
                            {merchantName.charAt(0).toUpperCase()}
                        </div>
                    )}
                    <div>
                        <p className={`font-extrabold text-sm leading-snug ${isDark ? "text-white" : "text-slate-900"}`}>
                            {merchantName}
                        </p>
                        {categoryTotalSpend > 0 && (
                            <p className={`text-[11px] font-semibold ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                                {merchantSpendPct.toFixed(1)}% of category spend
                            </p>
                        )}
                    </div>
                </div>

                <span className={`flex items-center gap-1.5 text-[10px] font-black px-2.5 py-1 rounded-lg border uppercase tracking-wider ${currentRank.badge}`}>
                    {currentRank.icon}
                    <span>{currentRank.label}</span>
                </span>
            </div>

            {/* Merchant Spend Progress Bar */}
            {categoryTotalSpend > 0 && (
                <div className="w-full mb-3">
                    <div className={`w-full rounded-full h-1.5 ${isDark ? "bg-slate-700/60" : "bg-slate-100"}`}>
                        <div
                            className={`h-1.5 rounded-full transition-all duration-500 ${currentRank.barBg}`}
                            style={{ width: `${merchantSpendPct}%` }}
                        />
                    </div>
                </div>
            )}
            
            {/* Merchant Metrics Grid */}
            <div className="grid grid-cols-3 gap-2 text-center text-xs">
                <div className={`p-2 rounded-xl border ${isDark ? "bg-slate-900/40 border-slate-700/40" : "bg-slate-50 border-slate-100"}`}>
                    <p className={`text-[10px] font-bold uppercase tracking-wider mb-0.5 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                        Total Spend
                    </p>
                    <p className={`font-black text-xs ${isDark ? "text-emerald-400" : "text-emerald-600"}`}>
                        ${totalSpent.toFixed(2)}
                    </p>
                </div>

                <div className={`p-2 rounded-xl border ${isDark ? "bg-slate-900/40 border-slate-700/40" : "bg-slate-50 border-slate-100"}`}>
                    <p className={`text-[10px] font-bold uppercase tracking-wider mb-0.5 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                        Avg Spend
                    </p>
                    <p className={`font-extrabold text-xs ${isDark ? "text-indigo-300" : "text-indigo-700"}`}>
                        ${avgAmount.toFixed(2)}
                    </p>
                </div>

                <div className={`p-2 rounded-xl border ${isDark ? "bg-slate-900/40 border-slate-700/40" : "bg-slate-50 border-slate-100"}`}>
                    <p className={`text-[10px] font-bold uppercase tracking-wider mb-0.5 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                        Txns
                    </p>
                    <p className={`font-extrabold text-xs ${isDark ? "text-slate-200" : "text-slate-800"}`}>
                        {transactionCount}
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Merchant;
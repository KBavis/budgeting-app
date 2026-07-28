import React, { useContext, useEffect, useState } from "react";
import Merchant from "./Merchant";
import { ThemeContext } from "../../../context/theme/ThemeContext";
import { FaStore, FaCrown } from "react-icons/fa";

const Top3Merchants = ({ performance }) => {
    const { theme } = useContext(ThemeContext);
    const isDark = theme === "dark";

    const [merchants, setMerchants] = useState([]);

    useEffect(() => {
        let top3Merchants = performance?.topMerchants || [];
        setMerchants(top3Merchants);
    }, [performance]);

    const categoryTotalSpend = typeof performance?.totalSpend === 'number' ? performance.totalSpend : 0;

    return (
        <div className={`border rounded-2xl shadow-md flex flex-col p-6 h-full transition-all ${
            isDark
                ? "bg-slate-800/80 border-slate-700/80 text-slate-100"
                : "bg-white border-slate-200 text-slate-800"
        }`}>
            {/* Card Header */}
            <div className="flex items-center justify-between mb-5 pb-3 border-b border-slate-700/30">
                <div className="flex items-center gap-2">
                    <div className={`p-1.5 rounded-lg ${isDark ? "bg-amber-500/20 text-amber-400" : "bg-amber-100 text-amber-600"}`}>
                        <FaCrown size={14} />
                    </div>
                    <h3 className={`text-base font-extrabold ${isDark ? "text-white" : "text-slate-900"}`}>
                        Top Spenders
                    </h3>
                </div>
                <span className={`text-[11px] font-extrabold px-2.5 py-1 rounded-full border ${
                    isDark ? "bg-slate-900/60 border-slate-700 text-slate-300" : "bg-slate-100 border-slate-200 text-slate-700"
                }`}>
                    {merchants.length} Merchant{merchants.length !== 1 ? 's' : ''}
                </span>
            </div>

            {/* Merchant List */}
            <div className="flex-1 space-y-3.5">
                {merchants.length > 0 ? (
                    merchants.map((merchant, idx) => (
                        <Merchant
                            key={merchant.merchantRank || merchant.merchantName || idx}
                            merchant={merchant}
                            rank={merchant.merchantRank || (idx + 1)}
                            categoryTotalSpend={categoryTotalSpend}
                        />
                    ))
                ) : (
                    <div className="flex flex-col items-center justify-center py-10 text-xs text-slate-400 gap-2">
                        <FaStore size={24} className="text-slate-500 opacity-40" />
                        <span>No merchant breakdown recorded</span>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Top3Merchants;
import React, { useContext } from 'react';
import { FaTrashAlt, FaUniversity } from 'react-icons/fa';
import { ThemeContext } from '../../context/theme/ThemeContext';

/**
 * Account type badge styling for both light and dark modes
 */
const ACCOUNT_TYPE_BADGES = {
    CHECKING: {
        label: 'Checking',
        dark: 'bg-blue-500/20 text-blue-300 border-blue-400/30',
        light: 'bg-blue-50 text-blue-700 border-blue-200',
    },
    SAVING: {
        label: 'Savings',
        dark: 'bg-emerald-500/20 text-emerald-300 border-emerald-400/30',
        light: 'bg-emerald-50 text-emerald-700 border-emerald-200',
    },
    CREDIT: {
        label: 'Credit',
        dark: 'bg-amber-500/20 text-amber-300 border-amber-400/30',
        light: 'bg-amber-50 text-amber-700 border-amber-200',
    },
    LOAN: {
        label: 'Loan',
        dark: 'bg-red-500/20 text-red-300 border-red-400/30',
        light: 'bg-red-50 text-red-700 border-red-200',
    },
    INVESTMENT: {
        label: 'Investment',
        dark: 'bg-purple-500/20 text-purple-300 border-purple-400/30',
        light: 'bg-purple-50 text-purple-700 border-purple-200',
    },
};

/**
 * Compact Account card component with full Light/Dark mode support.
 * Designed as a compact, button-like card with zero overlap between amount and trash icon.
 */
const Account = ({ account, handleShowConfirmationModal }) => {
    const { theme } = useContext(ThemeContext);
    const isDark = theme === 'dark';

    const badgeDef = ACCOUNT_TYPE_BADGES[account.accountType] || {
        label: account.accountType || 'Account',
        dark: 'bg-slate-500/20 text-slate-300 border-slate-400/30',
        light: 'bg-slate-100 text-slate-600 border-slate-200',
    };
    const badgeColor = isDark ? badgeDef.dark : badgeDef.light;
    const isNegative = account.balance < 0;

    return (
        <div className={`relative border rounded-xl px-4 py-3 w-full transition-all duration-200 hover:scale-[1.01] group ${
            isDark
                ? "bg-slate-800/80 border-slate-600/50 hover:bg-slate-700/80"
                : "bg-white border-slate-200 hover:bg-slate-50 shadow-sm"
        }`}>
            {/* Inner Content with pr-8 to prevent trash icon overlap */}
            <div className="flex items-center gap-3 pr-8">
                {/* Institution Icon */}
                <div className={`flex-shrink-0 w-9 h-9 rounded-lg flex items-center justify-center ${
                    isDark
                        ? "bg-indigo-500/20 border border-indigo-400/20"
                        : "bg-indigo-50 border border-indigo-100"
                }`}>
                    <FaUniversity className={`text-sm ${isDark ? "text-indigo-300" : "text-indigo-500"}`} />
                </div>

                {/* Account Info */}
                <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2">
                        <h3 className={`text-sm font-bold ${
                            isDark ? "text-white" : "text-slate-900"
                        }`}>
                            {account.accountName}
                        </h3>
                        <span className={`inline-flex items-center px-1.5 py-0.5 rounded-full text-[9px] font-semibold border ${badgeColor}`}>
                            {badgeDef.label}
                        </span>
                    </div>
                    <p className={`text-xs ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                        {account.institutionName || 'Financial Institution'} {account.mask ? `•••${account.mask}` : ''}
                    </p>
                </div>

                {/* Balance - Right aligned */}
                <div className="flex-shrink-0 text-right">
                    <p className={`text-base font-extrabold ${
                        isNegative
                            ? (isDark ? 'text-red-400' : 'text-red-600')
                            : (isDark ? 'text-emerald-400' : 'text-emerald-600')
                    }`}>
                        {isNegative ? '-' : ''}${Math.abs(account.balance).toFixed(2)}
                    </p>
                </div>
            </div>

            {/* Delete button positioned absolute right, clear of balance text */}
            <button
                className={`absolute top-1/2 -translate-y-1/2 right-3 p-1.5 rounded-lg transition duration-200 opacity-0 group-hover:opacity-100 ${
                    isDark ? "text-slate-400 hover:text-red-400 bg-slate-700/80 hover:bg-red-500/20" : "text-slate-400 hover:text-red-600 bg-slate-100 hover:bg-red-50"
                }`}
                onClick={() => handleShowConfirmationModal(account)}
                title="Remove Account"
            >
                <FaTrashAlt size={13} />
            </button>
        </div>
    );
};

export default Account;

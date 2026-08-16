import React, { useContext } from 'react';
import { FaTrashAlt, FaUniversity, FaEdit } from 'react-icons/fa';
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
 * Designed as a compact, button-like card with zero overlap between amount and action buttons.
 */
const Account = ({ account, handleShowConfirmationModal, handleOpenEditModal }) => {
    const { theme } = useContext(ThemeContext);
    const isDark = theme === 'dark';

    const badgeDef = ACCOUNT_TYPE_BADGES[account.accountType] || {
        label: account.accountType || 'Account',
        dark: 'bg-slate-500/20 text-slate-300 border-slate-400/30',
        light: 'bg-slate-100 text-slate-600 border-slate-200',
    };
    const badgeColor = isDark ? badgeDef.dark : badgeDef.light;
    const isLiability = account.accountType === 'CREDIT' || account.accountType === 'LOAN';
    const isNegative = account.balance < 0 || isLiability;

    return (
        <div className={`relative border rounded-xl p-3.5 sm:p-4 w-full transition-all duration-200 hover:scale-[1.01] group ${
            isDark
                ? "bg-slate-800/80 border-slate-600/50 hover:bg-slate-700/80"
                : "bg-white border-slate-200 hover:bg-slate-50 shadow-sm"
        }`}>
            {/* Action buttons (Edit & Delete) - Positioned Top Right & Always Visible */}
            <div className="absolute top-3.5 right-3.5 flex items-center gap-1 z-10 opacity-100">
                {handleOpenEditModal && (
                    <button
                        className={`p-1.5 rounded-lg transition duration-200 ${
                            isDark ? "text-slate-400 hover:text-indigo-300 bg-slate-700/60 hover:bg-indigo-500/20" : "text-slate-400 hover:text-indigo-600 bg-slate-100 hover:bg-indigo-50"
                        }`}
                        onClick={() => handleOpenEditModal(account)}
                        title="Edit Account"
                    >
                        <FaEdit size={12} />
                    </button>
                )}
                <button
                    className={`p-1.5 rounded-lg transition duration-200 ${
                        isDark ? "text-slate-400 hover:text-red-400 bg-slate-700/60 hover:bg-red-500/20" : "text-slate-400 hover:text-red-600 bg-slate-100 hover:bg-red-50"
                    }`}
                    onClick={() => handleShowConfirmationModal(account)}
                    title="Remove Account"
                >
                    <FaTrashAlt size={12} />
                </button>
            </div>

            {/* Main content container (pr-16/pr-20 clears always-visible top-right action icons) */}
            <div className="flex items-center gap-3 pr-16 sm:pr-20">
                {/* Institution Icon */}
                <div className={`flex-shrink-0 w-9 h-9 sm:w-10 sm:h-10 rounded-xl flex items-center justify-center ${
                    isLiability
                        ? (isDark ? "bg-rose-500/20 border border-rose-400/20" : "bg-rose-50 border border-rose-100")
                        : (isDark ? "bg-indigo-500/20 border border-indigo-400/20" : "bg-indigo-50 border border-indigo-100")
                }`}>
                    <FaUniversity className={`text-sm sm:text-base ${
                        isLiability
                            ? (isDark ? "text-rose-300" : "text-rose-500")
                            : (isDark ? "text-indigo-300" : "text-indigo-500")
                    }`} />
                </div>

                {/* Account Details & Balance Area */}
                <div className="flex-1 min-w-0 flex flex-col justify-center gap-0.5">
                    {/* Top Row: Account Name (left) & Balance Amount (right, aligned on top row!) */}
                    <div className="flex items-center justify-between gap-2 min-w-0">
                        <h3 className={`text-sm sm:text-base font-bold truncate ${
                            isDark ? "text-white" : "text-slate-900"
                        }`} title={account.accountName}>
                            {account.accountName}
                        </h3>

                        <div className="flex-shrink-0 text-right whitespace-nowrap">
                            <span className={`text-sm sm:text-base font-black tracking-tight ${
                                isLiability
                                    ? (isDark ? 'text-rose-400' : 'text-rose-600')
                                    : isNegative
                                    ? (isDark ? 'text-red-400' : 'text-red-600')
                                    : (isDark ? 'text-emerald-400' : 'text-emerald-600')
                            }`}>
                                {isLiability ? '-' : (isNegative ? '-' : '')}${Math.abs(account.balance).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                            </span>
                        </div>
                    </div>

                    {/* Subtitle Row: Badge + Institution details */}
                    <div className="flex flex-wrap items-center gap-1.5 min-w-0">
                        <span className={`inline-flex items-center px-1.5 py-0.5 rounded-md text-[10px] font-bold border ${badgeColor}`}>
                            {badgeDef.label}
                        </span>
                        <span className={`text-xs ${isDark ? "text-slate-400" : "text-slate-500"} truncate`}>
                            {account.institutionName || 'Financial Institution'} {account.mask ? `•••${account.mask}` : ''}
                        </span>
                        {isLiability && (
                            <span className={`ml-auto text-[10px] font-extrabold uppercase tracking-wider ${isDark ? 'text-rose-400/80' : 'text-rose-500'}`}>
                                Owed
                            </span>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Account;

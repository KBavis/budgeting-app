import React, { useContext, useEffect, useRef, useState, useMemo } from 'react';
import SummaryContext from "../context/summary/summaryContext";
import BudgetOverview from "../components/summary/BudgetOverview";
import FinancialGrowthSummary from "../components/summary/FinancialGrowthSummary";
import { FaArrowLeft, FaArrowRight, FaCalendarAlt, FaChevronRight } from "react-icons/fa";
import CategoryPerformanceContext from '../context/category/performances/categoryPerformanceContext';
import categoryTypeContext from '../context/category/types/categoryTypeContext';
import { ThemeContext } from '../context/theme/ThemeContext';
import { getBudgetStatus } from '../utils/budgetColors';

const monthOrder = {
    JANUARY: 1, FEBRUARY: 2, MARCH: 3, APRIL: 4,
    MAY: 5, JUNE: 6, JULY: 7, AUGUST: 8,
    SEPTEMBER: 9, OCTOBER: 10, NOVEMBER: 11, DECEMBER: 12,
};

const convertToNormalCase = (str) => {
    if (!str) return '';
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
};

const BudgetSummaryPage = () => {
    const { summaries, fetchBudgetSummaries, setLoading, setPrev, prev } = useContext(SummaryContext);
    const { fetchCategoryPerformances } = useContext(CategoryPerformanceContext);
    const { categoryTypes, fetchCategoryTypes } = useContext(categoryTypeContext);
    const { theme } = useContext(ThemeContext);

    const isDark = theme === "dark";

    const [selectedYear, setSelectedYear] = useState('');
    const [selectedMonth, setSelectedMonth] = useState('ALL');
    const [selectedSummary, setSelectedSummary] = useState(null);

    const initialFetchRef = useRef(false);
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 4;

    const getBudgetSummariesData = async () => {
        setLoading();
        await fetchBudgetSummaries();
    };

    useEffect(() => {
        if (!initialFetchRef.current || !summaries) {
            getBudgetSummariesData();
            initialFetchRef.current = true;
        }
    }, [summaries]);

    useEffect(() => {
        if (!categoryTypes || categoryTypes.length === 0) {
            fetchCategoryTypes();
        }
    }, [categoryTypes, fetchCategoryTypes]);

    // Exclude current month and sort descending by year and month
    const historicalSummaries = useMemo(() => {
        if (!summaries) return [];
        const now = new Date();
        const currentYear = now.getFullYear();
        const currentMonthIdx = now.getMonth();

        return summaries.filter((s) => {
            const sYear = s.id.monthYear.year;
            const sMonthNum = monthOrder[s.id.monthYear.month];
            if (sYear < currentYear) return true;
            if (sYear === currentYear && sMonthNum < currentMonthIdx + 1) return true;
            return false;
        }).sort((a, b) => {
            if (a.id.monthYear.year !== b.id.monthYear.year) {
                return b.id.monthYear.year - a.id.monthYear.year;
            }
            return monthOrder[b.id.monthYear.month] - monthOrder[a.id.monthYear.month];
        });
    }, [summaries]);

    // Unique available years
    const availableYears = useMemo(() => {
        const years = [...new Set(historicalSummaries.map((s) => s.id.monthYear.year))];
        return years.sort((a, b) => b - a);
    }, [historicalSummaries]);

    // Restore previously selected summary or default to first available year
    useEffect(() => {
        if (prev && prev.id && prev.id.monthYear) {
            const yearStr = prev.id.monthYear.year.toString();
            const monthStr = prev.id.monthYear.month;
            setSelectedYear(yearStr);
            setSelectedMonth(monthStr);
            setSelectedSummary(prev);
        } else if (availableYears.length > 0 && !selectedYear) {
            setSelectedYear(availableYears[0].toString());
        }
    }, [availableYears, prev]);

    // Available months for selected year
    const availableMonthsForYear = useMemo(() => {
        if (!selectedYear) return [];
        const yearNum = parseInt(selectedYear, 10);
        const yearMatches = historicalSummaries.filter((s) => s.id.monthYear.year === yearNum);
        const months = [...new Set(yearMatches.map((s) => s.id.monthYear.month))];
        return months.sort((a, b) => monthOrder[a] - monthOrder[b]);
    }, [historicalSummaries, selectedYear]);

    // Fetch Category Performances whenever selectedSummary changes
    useEffect(() => {
        if (selectedSummary && categoryTypes && categoryTypes.length > 0) {
            const categoryTypeIds = categoryTypes.map((ct) => ct.categoryTypeId);
            fetchCategoryPerformances(categoryTypeIds, selectedSummary.id.monthYear);
        }
    }, [selectedSummary, categoryTypes]);

    const handleYearSelect = (yearStr) => {
        setSelectedYear(yearStr);
        setSelectedMonth('ALL');
        setSelectedSummary(null);
        setPrev(null);
        setCurrentPage(1);
    };

    const handleMonthSelect = (monthStr) => {
        setSelectedMonth(monthStr);
        setCurrentPage(1);

        if (monthStr === 'ALL') {
            setSelectedSummary(null);
            setPrev(null);
        } else {
            const yearNum = parseInt(selectedYear, 10);
            const match = historicalSummaries.find(
                (s) => s.id.monthYear.year === yearNum && s.id.monthYear.month === monthStr
            );
            if (match) {
                setSelectedSummary(match);
                setPrev(match);
            }
        }
    };

    const handleSummaryCardClick = (summary) => {
        setSelectedMonth(summary.id.monthYear.month);
        setSelectedSummary(summary);
        setPrev(summary);
    };

    // Summaries to display when ALL is selected
    const displaySummaries = useMemo(() => {
        if (!selectedYear) return [];
        const yearNum = parseInt(selectedYear, 10);
        return historicalSummaries.filter((s) => {
            if (s.id.monthYear.year !== yearNum) return false;
            if (selectedMonth !== 'ALL' && s.id.monthYear.month !== selectedMonth) return false;
            return true;
        });
    }, [historicalSummaries, selectedYear, selectedMonth]);

    const totalPages = Math.ceil(displaySummaries.length / itemsPerPage);
    const indexOfLastItem = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentSummaries = displaySummaries.slice(indexOfFirstItem, indexOfLastItem);

    const handleNextPage = () => {
        if (currentPage < totalPages) {
            setCurrentPage(currentPage + 1);
        }
    };

    const handlePrevPage = () => {
        if (currentPage > 1) {
            setCurrentPage(currentPage - 1);
        }
    };

    return (
        <div className={`flex flex-col min-h-screen relative ${
            isDark
                ? "bg-gradient-to-br from-gray-900 via-slate-900 to-indigo-950 text-slate-100"
                : "bg-gradient-to-br from-slate-100 via-indigo-50/50 to-slate-100 text-slate-800"
        }`}>
            <div className="flex flex-col items-center px-4 md:px-12 pt-16 pb-16 w-full">

                {/* Header Title */}
                <div className="max-w-3xl text-center mt-4 mb-6">
                    <h1 className={`text-4xl md:text-5xl font-black mb-2 ${isDark ? "text-white" : "text-slate-900"}`}>
                        Budget Overview
                    </h1>
                    <p className={`text-sm ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                        Review historical spending and budget allocations across previous months
                    </p>
                </div>

                {availableYears.length > 0 ? (
                    <>
                        {/* Compact, Infinite Scalable Period Toolbar: Year Dropdown & Month Dropdown */}
                        <div className={`w-full max-w-xl mb-8 p-3.5 sm:p-4 rounded-2xl border shadow-xl flex flex-col sm:flex-row items-center justify-center gap-3 sm:gap-6 backdrop-blur-md ${
                            isDark ? "bg-slate-900/90 border-slate-800" : "bg-white/90 border-slate-200"
                        }`}>
                            {/* Scalable Year Dropdown */}
                            <div className="flex items-center gap-2">
                                <span className={`text-xs font-extrabold uppercase tracking-wider flex items-center gap-1.5 ${isDark ? "text-slate-300" : "text-slate-500"}`}>
                                    <FaCalendarAlt size={12} className="text-indigo-400" />
                                    Year:
                                </span>
                                <select
                                    value={selectedYear}
                                    onChange={(e) => handleYearSelect(e.target.value)}
                                    style={{ colorScheme: isDark ? 'dark' : 'light' }}
                                    className={`px-3 py-1.5 rounded-xl text-xs font-extrabold transition-all border cursor-pointer focus:outline-none focus:ring-2 focus:ring-indigo-500/50 ${
                                        isDark
                                            ? 'bg-slate-800 border-slate-700 text-white hover:bg-slate-700'
                                            : 'bg-slate-100 border-slate-300 text-slate-800 hover:bg-slate-200'
                                    }`}
                                >
                                    {availableYears.map((year) => (
                                        <option
                                            key={year}
                                            value={year.toString()}
                                            className={isDark ? "bg-slate-900 text-white" : "bg-white text-slate-900"}
                                        >
                                            {year}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            {/* Divider */}
                            <div className={`hidden sm:block h-6 w-px ${isDark ? "bg-slate-800" : "bg-slate-200"}`} />

                            {/* Scalable Month Dropdown */}
                            <div className="flex items-center gap-2">
                                <span className={`text-xs font-extrabold uppercase tracking-wider ${isDark ? "text-slate-300" : "text-slate-500"}`}>
                                    Month:
                                </span>
                                <select
                                    value={selectedMonth}
                                    onChange={(e) => handleMonthSelect(e.target.value)}
                                    style={{ colorScheme: isDark ? 'dark' : 'light' }}
                                    className={`px-3 py-1.5 sm:px-3.5 rounded-xl text-xs font-extrabold transition-all border cursor-pointer focus:outline-none focus:ring-2 focus:ring-indigo-500/50 max-w-[200px] sm:max-w-none ${
                                        selectedMonth !== 'ALL'
                                            ? 'bg-indigo-600 border-indigo-500 text-white shadow-md'
                                            : isDark
                                                ? 'bg-slate-800 border-slate-700 text-white hover:bg-slate-700'
                                                : 'bg-slate-100 border-slate-300 text-slate-800 hover:bg-slate-200'
                                    }`}
                                >
                                    <option value="ALL" className={isDark ? "bg-slate-900 text-white" : "bg-white text-slate-900"}>
                                        All Months ({availableMonthsForYear.length})
                                    </option>
                                    {availableMonthsForYear.map((month) => (
                                        <option
                                            key={month}
                                            value={month}
                                            className={isDark ? "bg-slate-900 text-white" : "bg-white text-slate-900"}
                                        >
                                            {convertToNormalCase(month)}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        {/* Content Area */}
                        {selectedSummary ? (
                            /* Financial Growth Summary + Needs/Wants Spending */
                            <div className="flex flex-col items-center w-full max-w-5xl animate-fade-in">
                                <div className="flex flex-col sm:flex-row items-center justify-between w-full max-w-4xl mb-6 px-2 gap-3">
                                    <button
                                        onClick={() => { setSelectedSummary(null); setSelectedMonth('ALL'); setPrev(null); }}
                                        className={`text-xs font-bold hover:underline flex items-center gap-1.5 px-3 py-1.5 rounded-xl border transition-all ${
                                            isDark
                                                ? "bg-slate-800/80 border-slate-700 text-indigo-400 hover:bg-slate-800"
                                                : "bg-white border-slate-300 text-indigo-600 hover:bg-slate-50"
                                        }`}
                                    >
                                        ← Back to All {selectedYear} Overviews
                                    </button>
                                    <span className={`text-sm font-extrabold px-4 py-1.5 rounded-xl border ${
                                        isDark ? "bg-slate-800 border-slate-700 text-slate-200" : "bg-white border-slate-300 text-slate-800"
                                    }`}>
                                        {convertToNormalCase(selectedSummary.id.monthYear.month)} {selectedSummary.id.monthYear.year}
                                    </span>
                                </div>

                                {/* Financial Growth Summary — replaces General + Investments overviews */}
                                <FinancialGrowthSummary summary={selectedSummary} month={selectedSummary.id.monthYear.month} year={selectedSummary.id.monthYear.year} />

                                {/* Needs, Wants & Investments Spending Overviews */}
                                <BudgetOverview overview={selectedSummary.needsOverview} month={selectedSummary.id.monthYear.month} year={selectedSummary.id.monthYear.year} />
                                <BudgetOverview overview={selectedSummary.wantsOverview} month={selectedSummary.id.monthYear.month} year={selectedSummary.id.monthYear.year} />
                                <BudgetOverview overview={selectedSummary.investmentOverview} month={selectedSummary.id.monthYear.month} year={selectedSummary.id.monthYear.year} />
                            </div>
                        ) : displaySummaries.length > 0 ? (
                            /* Summary Cards Grid for ALL Months */
                            <div className="w-full max-w-5xl flex flex-col items-center">
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 w-full mb-8">
                                    {currentSummaries.map((summary) => {
                                        const budgeted = summary.generalOverview?.totalAmountAllocated || 0;
                                        const spent = summary.generalOverview?.totalSpent || 0;
                                        const usagePct = budgeted > 0 ? Math.min((spent / budgeted) * 100, 100) : 0;
                                        const pctRaw = budgeted > 0 ? ((spent / budgeted) * 100).toFixed(1) : '0';
                                        const status = getBudgetStatus(spent, budgeted);
                                        const investedAmount = summary.investmentOverview?.totalSpent || 0;
                                        const netCashFlow = budgeted - spent;
                                        const netWealthBuilt = investedAmount + netCashFlow;
                                        const isNetWealthNegative = netWealthBuilt < 0;

                                        return (
                                            <div
                                                key={`${summary.id.monthYear.month}-${summary.id.monthYear.year}`}
                                                onClick={() => handleSummaryCardClick(summary)}
                                                className={`p-5 sm:p-6 border rounded-2xl cursor-pointer transition-all duration-300 shadow-md hover:shadow-xl hover:scale-[1.02] flex flex-col justify-between ${
                                                    isDark
                                                        ? "bg-slate-900/80 border-slate-800 text-white hover:border-indigo-500/60"
                                                        : "bg-white border-slate-200 text-slate-900 hover:border-indigo-400"
                                                }`}
                                            >
                                                <div>
                                                    <div className="flex flex-wrap items-center justify-between gap-2 mb-3">
                                                        <h3 className="text-lg sm:text-xl font-extrabold tracking-tight">
                                                            {convertToNormalCase(summary.id.monthYear.month)} {summary.id.monthYear.year}
                                                        </h3>
                                                        <div className="flex items-center gap-2">
                                                            <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[10px] font-extrabold uppercase tracking-wider border ${status.bg}/20 ${status.text} border-${status.color}/30 shadow-sm`}>
                                                                {status.label}
                                                            </span>
                                                            <span className={`text-xs font-bold px-2.5 py-1 rounded-full border flex items-center gap-1 transition-colors ${
                                                                isDark
                                                                    ? "bg-indigo-500/20 text-indigo-300 border-indigo-500/30"
                                                                    : "bg-indigo-50 text-indigo-700 border-indigo-200"
                                                            }`}>
                                                                Overview <FaChevronRight size={9} />
                                                            </span>
                                                        </div>
                                                    </div>

                                                    {/* Spent / Budgeted Header Info */}
                                                    <div className="flex justify-between items-baseline mb-2">
                                                        <span className={`text-xs font-semibold ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                                                            Budget Utilization
                                                        </span>
                                                        <span className={`text-xs font-bold ${status.textClass}`}>
                                                            {pctRaw}%
                                                        </span>
                                                    </div>

                                                    {/* Dynamic Status Progress Bar */}
                                                    <div className={`w-full rounded-full h-2.5 mb-4 overflow-hidden ${isDark ? "bg-slate-800" : "bg-slate-100"}`}>
                                                        <div
                                                            className={`h-2.5 rounded-full transition-all duration-500 ease-in-out ${status.colorClass}`}
                                                            style={{ width: `${usagePct}%` }}
                                                        />
                                                    </div>
                                                </div>

                                                {/* 3-Column Financial Breakdown */}
                                                <div className={`grid grid-cols-3 gap-2 pt-3 border-t text-center ${isDark ? "border-slate-800" : "border-slate-100"}`}>
                                                    <div>
                                                        <p className={`text-[10px] font-bold uppercase tracking-wider mb-0.5 ${isDark ? "text-slate-400" : "text-slate-500"}`}>Budgeted</p>
                                                        <p className="font-extrabold text-sm sm:text-base">${budgeted.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 })}</p>
                                                    </div>
                                                    <div>
                                                        <p className={`text-[10px] font-bold uppercase tracking-wider mb-0.5 ${isDark ? "text-slate-400" : "text-slate-500"}`}>Spent</p>
                                                        <p className={`font-extrabold text-sm sm:text-base ${status.textClass}`}>${spent.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 })}</p>
                                                    </div>
                                                    <div>
                                                        <p className={`text-[10px] font-bold uppercase tracking-wider mb-0.5 ${
                                                            isNetWealthNegative
                                                                ? "text-red-500 dark:text-red-400"
                                                                : "text-teal-600 dark:text-teal-400"
                                                        }`}>
                                                            {isNetWealthNegative ? "Net Loss" : "Net Wealth"}
                                                        </p>
                                                        <p className={`font-extrabold text-sm sm:text-base ${
                                                            isNetWealthNegative
                                                                ? "text-red-500 dark:text-red-400"
                                                                : "text-teal-600 dark:text-teal-400"
                                                        }`}>
                                                            {isNetWealthNegative ? "-" : ""}${Math.abs(netWealthBuilt).toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 0 })}
                                                        </p>
                                                    </div>
                                                </div>
                                            </div>
                                        );
                                    })}
                                </div>

                                {totalPages > 1 && (
                                    <div className="flex items-center gap-4">
                                        <button
                                            onClick={handlePrevPage}
                                            disabled={currentPage === 1}
                                            className={`p-3 rounded-full border transition-all ${
                                                currentPage === 1
                                                    ? 'opacity-40 cursor-not-allowed'
                                                    : 'hover:scale-110'
                                            } ${isDark ? "bg-slate-800 border-slate-700 text-white" : "bg-white border-slate-300 text-slate-800"}`}
                                        >
                                            <FaArrowLeft />
                                        </button>
                                        <span className={`text-sm font-bold ${isDark ? "text-slate-300" : "text-slate-700"}`}>
                                            Page {currentPage} of {totalPages}
                                        </span>
                                        <button
                                            onClick={handleNextPage}
                                            disabled={currentPage === totalPages}
                                            className={`p-3 rounded-full border transition-all ${
                                                currentPage === totalPages
                                                    ? 'opacity-40 cursor-not-allowed'
                                                    : 'hover:scale-110'
                                            } ${isDark ? "bg-slate-800 border-slate-700 text-white" : "bg-white border-slate-300 text-slate-800"}`}
                                        >
                                            <FaArrowRight />
                                        </button>
                                    </div>
                                )}
                            </div>
                        ) : (
                            <div className="max-w-md text-center py-12">
                                <h2 className={`text-lg font-bold ${isDark ? "text-slate-500" : "text-slate-400"}`}>
                                    No budget overview available for {selectedMonth !== 'ALL' ? `${convertToNormalCase(selectedMonth)} ` : ''}{selectedYear}.
                                </h2>
                            </div>
                        )}
                    </>
                ) : (
                    /* Blank state when no history exists at all */
                    <div className={`w-full max-w-md my-12 p-10 border rounded-2xl text-center flex flex-col items-center gap-2 ${
                        isDark ? "bg-slate-900/50 border-slate-800 text-slate-400" : "bg-white border-slate-200 text-slate-500 shadow-sm"
                    }`}>
                        <h2 className={`text-xl font-bold ${isDark ? "text-slate-300" : "text-slate-800"}`}>
                            No Budget History Available
                        </h2>
                        <p className="text-xs">
                            Retrospective budget performance summaries will appear here once prior monthly data exists.
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default BudgetSummaryPage;

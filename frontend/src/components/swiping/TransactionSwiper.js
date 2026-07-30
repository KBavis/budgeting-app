import React, { useState, useEffect, useContext, useCallback } from 'react';
import './styles.css';
import ConfirmationModal from '../layout/ConfirmationModal';
import transactionContext from '../../context/transaction/transactionContext';
import AlertContext from '../../context/alert/alertContext';
import { ThemeContext } from '../../context/theme/ThemeContext';
import {
  FaCheck,
  FaTrash,
  FaPen,
  FaTimes,
  FaArrowRight,
  FaMagic,
  FaLayerGroup,
  FaCalendarAlt,
  FaUndo
} from 'react-icons/fa';

const TransactionSwiper = ({ transactions = [], categories = [], categoryTypes = [], onClose }) => {
  const { deleteTransaction, renameTransaction, reduceTransactionAmount, updateCategory } = useContext(transactionContext);
  const { setAlert } = useContext(AlertContext);
  const { theme } = useContext(ThemeContext);

  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedCategoryTypeId, setSelectedCategoryTypeId] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [cardAnimation, setCardAnimation] = useState('');
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [isEditingName, setIsEditingName] = useState(false);
  const [isEditingAmount, setIsEditingAmount] = useState(false);
  const [editedName, setEditedName] = useState('');
  const [editedAmount, setEditedAmount] = useState('');
  const [originalAmount, setOriginalAmount] = useState(0);
  const [suggestionDenied, setSuggestionDenied] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const amountHasChanged = parseFloat(editedAmount) !== originalAmount && !isNaN(parseFloat(editedAmount));

  const currentTransaction = transactions[currentIndex];
  const suggestedCategory = currentTransaction?.suggestedCategory;
  const showSuggestion = suggestedCategory && !suggestionDenied;

  const isPreviousMonth = (() => {
    if (!currentTransaction?.date) return false;
    const txDate = new Date(currentTransaction.date);
    const now = new Date();
    const firstOfCurrentMonth = new Date(now.getFullYear(), now.getMonth(), 1);
    return txDate < firstOfCurrentMonth;
  })();

  const prevMonthLabel = (() => {
    if (!currentTransaction?.date) return '';
    return new Date(currentTransaction.date).toLocaleDateString(undefined, {
      month: 'long',
      year: 'numeric'
    });
  })();

  // Synchronize local state when active transaction changes
  useEffect(() => {
    if (currentTransaction) {
      setEditedName(currentTransaction.name || '');
      setEditedAmount(currentTransaction.amount?.toString() || '');
      setOriginalAmount(currentTransaction.amount || 0);
      setIsEditingName(false);
      setIsEditingAmount(false);
      setSuggestionDenied(false);
      setIsSubmitting(false);

      // Auto-select suggested category if available, otherwise null
      if (currentTransaction.suggestedCategory) {
        setSelectedCategory(currentTransaction.suggestedCategory);
        if (currentTransaction.suggestedCategory.categoryType?.categoryTypeId) {
          setSelectedCategoryTypeId(currentTransaction.suggestedCategory.categoryType.categoryTypeId);
        }
      } else {
        setSelectedCategory(null);
        if (categoryTypes && categoryTypes.length > 0) {
          setSelectedCategoryTypeId(categoryTypes[0].categoryTypeId);
        }
      }
    }
  }, [currentIndex, currentTransaction, categoryTypes]);

  // Helper to deduplicate categories for a given CategoryType
  const getCategoriesForType = useCallback((ctId) => {
    if (!ctId) return [];
    const ct = (categoryTypes || []).find(c => c.categoryTypeId === ctId);
    if (!ct) return [];

    const globalCats = (categories || []).filter(
      (c) => c.categoryType && (c.categoryType.categoryTypeId === ct.categoryTypeId || c.categoryType.name === ct.name)
    );
    const typeCats = ct.categories || [];
    const map = new Map();
    [...typeCats, ...globalCats].forEach((c) => {
      if (c && c.categoryId) map.set(c.categoryId, c);
    });
    return Array.from(map.values());
  }, [categories, categoryTypes]);

  // --- Card transition logic ---
  const advanceToNext = () => {
    if (currentIndex < transactions.length - 1) {
      setCardAnimation('card-exit-left');
    } else {
      setCurrentIndex(transactions.length);
    }
  };

  const handleAnimationEnd = () => {
    if (cardAnimation === 'card-exit-left') {
      if (currentIndex < transactions.length - 1) {
        setCurrentIndex(currentIndex + 1);
      } else {
        setCurrentIndex(transactions.length);
      }
      setCardAnimation('card-enter-right');
    }
  };

  // --- SUBMIT TRANSACTION (Persists Name, Amount, & Category updates to backend) ---
  const handleCategorizeSubmit = async () => {
    if (!currentTransaction || !selectedCategory || isSubmitting) return;
    setIsSubmitting(true);

    try {
      // 1. Persist Name update if edited
      if (editedName.trim() && editedName.trim() !== currentTransaction.name) {
        await renameTransaction(currentTransaction.transactionId, editedName.trim());
      }

      // 2. Persist Amount update if reduced
      const newAmount = parseFloat(editedAmount);
      if (!isNaN(newAmount) && newAmount > 0 && newAmount < originalAmount) {
        await reduceTransactionAmount(currentTransaction.transactionId, newAmount);
      }

      // 3. Persist Category assignment
      await updateCategory(currentTransaction.transactionId, selectedCategory.categoryId, isPreviousMonth);

      setAlert(`Saved & assigned to ${selectedCategory.name}`, 'success');
      advanceToNext();
    } catch (err) {
      console.error(err);
      setAlert('Failed to update transaction', 'danger');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleAcceptSuggestion = () => {
    if (suggestedCategory) {
      setSelectedCategory(suggestedCategory);
    }
  };

  const handleDenySuggestion = () => {
    setSuggestionDenied(true);
    setSelectedCategory(null);
  };

  // --- Skip (no action, move forward) ---
  const handleSkip = () => {
    advanceToNext();
  };

  // --- Delete ---
  const handleDelete = () => {
    setShowConfirmation(true);
  };

  const confirmDelete = async () => {
    await deleteTransaction(currentTransaction.transactionId);
    setShowConfirmation(false);
    advanceToNext();
  };

  // --- Inline Name Editing ---
  const handleNameSave = () => {
    if (editedName.trim()) {
      setIsEditingName(false);
    }
  };

  // --- Amount Editing ---
  const handleAmountSave = () => {
    const newAmount = parseFloat(editedAmount);
    if (isNaN(newAmount) || newAmount <= 0) {
      setAlert('Amount must be greater than $0.00', 'danger');
      return;
    }
    if (newAmount > originalAmount) {
      setAlert(`Amount cannot be greater than original amount ($${originalAmount.toFixed(2)})`, 'danger');
      return;
    }
    setEditedAmount(newAmount.toString());
    setIsEditingAmount(false);
  };

  const handleQuickReduce = (percentage) => {
    if (!currentTransaction) return;
    const baseAmount = parseFloat(editedAmount) || originalAmount;
    const newAmount = Math.round((baseAmount * (1 - percentage)) * 100) / 100;
    if (newAmount <= 0) {
      setAlert('Amount must be greater than $0.00', 'danger');
      return;
    }
    setEditedAmount(newAmount.toString());
  };

  const handleResetAmount = () => {
    setEditedAmount(originalAmount.toString());
    setIsEditingAmount(false);
  };

  // --- Keyboard shortcuts ---
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (isEditingName || isEditingAmount) return;

      if (e.key === 'Escape') {
        onClose();
      } else if (e.key === 'ArrowRight') {
        handleSkip();
      } else if (e.key === 'Enter' && selectedCategory && !isSubmitting) {
        e.preventDefault();
        handleCategorizeSubmit();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isEditingName, isEditingAmount, selectedCategory, isSubmitting, currentIndex]);

  useEffect(() => {
    if (transactions.length > 0 && currentIndex < transactions.length) {
      setCardAnimation('card-enter-right');
    }
  }, []);

  // --- Completion View ---
  if (!currentTransaction || currentIndex >= transactions.length) {
    return (
      <div className="swiper-backdrop no-scrollbar">
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-8 max-w-md w-full text-center shadow-2xl backdrop-blur-xl">
          <div className="w-16 h-16 bg-emerald-500/20 text-emerald-400 rounded-full flex items-center justify-center mx-auto mb-4 border border-emerald-500/30 text-3xl">
            🎉
          </div>
          <h2 className="text-2xl font-black text-white mb-2">All Caught Up!</h2>
          <p className="text-slate-400 text-sm mb-6">
            You have categorized all remaining transactions.
          </p>
          <button
            onClick={onClose}
            className="w-full py-3 bg-brand-600 hover:bg-brand-500 text-white font-bold rounded-xl shadow-lg transition-all"
          >
            Done & Return to Dashboard
          </button>
        </div>
      </div>
    );
  }

  const currentCategoryList = getCategoriesForType(selectedCategoryTypeId);
  const progressPercent = transactions.length > 0
    ? Math.round((currentIndex / transactions.length) * 100)
    : 0;

  return (
    <div className="swiper-backdrop no-scrollbar">
      <div className="swiper-card-workspace">

        {/* ─── Top Header & Progress ─── */}
        <div className="flex flex-wrap items-center justify-between gap-3 mb-4 px-1">
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-2 bg-indigo-500/20 border border-indigo-500/30 px-3 py-1.5 rounded-xl text-indigo-300 text-xs font-bold">
              <FaLayerGroup className="w-3.5 h-3.5" />
              <span>Smart Swiper</span>
            </div>
            <span className="text-xs font-bold text-slate-300">
              {currentIndex + 1} / {transactions.length}
            </span>
          </div>

          <button
            onClick={onClose}
            className="p-2 rounded-xl bg-slate-800/80 hover:bg-red-500/20 text-slate-400 hover:text-red-400 border border-slate-700/80 hover:border-red-500/40 transition-all"
            title="Close Swiper (Esc)"
          >
            <FaTimes className="w-4 h-4" />
          </button>
        </div>

        {/* Progress Bar */}
        <div className="w-full bg-slate-800/80 h-1.5 rounded-full overflow-hidden mb-5 border border-slate-700/50">
          <div
            className="bg-gradient-to-r from-indigo-500 to-purple-500 h-full transition-all duration-300"
            style={{ width: `${progressPercent}%` }}
          />
        </div>

        {/* ─── Main Workspace Card ─── */}
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl shadow-2xl overflow-hidden backdrop-blur-xl grid grid-cols-1 md:grid-cols-12 gap-0">

          {/* ─── LEFT: Transaction Details & Edits ─── */}
          <div className="md:col-span-5 p-5 md:p-6 border-b md:border-b-0 md:border-r border-slate-800/80 flex flex-col justify-between bg-slate-950/40">
            <div
              className={`flex flex-col gap-4 ${cardAnimation}`}
              onAnimationEnd={handleAnimationEnd}
            >
              {/* Previous Month Badge */}
              {isPreviousMonth && (
                <div className="bg-amber-500/15 border border-amber-500/30 text-amber-300 text-xs font-bold px-3 py-1.5 rounded-xl flex items-center justify-center gap-2">
                  <FaCalendarAlt className="w-3 h-3 text-amber-400" />
                  <span>Previous Month ({prevMonthLabel})</span>
                </div>
              )}

              {/* Merchant Logo & Info */}
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-center gap-3 flex-1 min-w-0">
                  <img
                    src={currentTransaction.logoUrl || 'https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg'}
                    alt="merchant logo"
                    className="w-12 h-12 rounded-2xl object-cover border border-slate-700/80 shadow-md shrink-0"
                  />
                  <div className="flex flex-col min-w-0 flex-1">
                    {isEditingName ? (
                      <div className="flex items-center gap-2">
                        <input
                          type="text"
                          value={editedName}
                          onChange={(e) => setEditedName(e.target.value)}
                          onKeyDown={(e) => e.key === 'Enter' && handleNameSave()}
                          className="bg-slate-800 border border-indigo-500 text-white text-sm font-bold rounded-lg px-2.5 py-1 w-full focus:outline-none focus:ring-2 focus:ring-indigo-400/50"
                          autoFocus
                        />
                        <button
                          onClick={handleNameSave}
                          className="p-1.5 bg-emerald-600 text-white rounded-lg hover:bg-emerald-500 shrink-0"
                        >
                          <FaCheck className="w-3 h-3" />
                        </button>
                        <button
                          onClick={() => { setIsEditingName(false); setEditedName(currentTransaction.name || ''); }}
                          className="p-1.5 bg-slate-700 text-slate-300 rounded-lg hover:bg-slate-600 shrink-0"
                        >
                          <FaTimes className="w-3 h-3" />
                        </button>
                      </div>
                    ) : (
                      <div
                        className="flex items-center gap-1.5 group cursor-pointer"
                        onClick={() => setIsEditingName(true)}
                      >
                        <h3 className="text-lg font-bold text-white truncate group-hover:text-indigo-300 transition-colors">
                          {editedName}
                        </h3>
                        <FaPen className="w-3 h-3 text-slate-500 opacity-0 group-hover:opacity-100 transition-opacity shrink-0" />
                      </div>
                    )}
                    <span className="text-xs text-slate-400 font-medium mt-0.5">
                      {new Date(currentTransaction.date).toLocaleDateString(undefined, {
                        year: 'numeric',
                        month: 'short',
                        day: 'numeric'
                      })}
                    </span>
                  </div>
                </div>

                <button
                  onClick={handleDelete}
                  className="p-2 rounded-xl text-slate-500 hover:text-red-400 hover:bg-red-500/10 border border-slate-800 hover:border-red-500/30 transition-all shrink-0"
                  title="Delete Transaction"
                >
                  <FaTrash className="w-4 h-4" />
                </button>
              </div>

              {/* Amount Display & Direct Edit */}
              <div className="py-4 bg-slate-800/40 border border-slate-800 rounded-2xl text-center">
                {isEditingAmount ? (
                  <div className="flex flex-col items-center gap-2 px-4">
                    <div className="flex items-center gap-2 w-full max-w-[220px]">
                      <span className="text-xl font-bold text-slate-400">$</span>
                      <input
                        type="number"
                        step="0.01"
                        value={editedAmount}
                        onChange={(e) => setEditedAmount(e.target.value)}
                        onKeyDown={(e) => e.key === 'Enter' && handleAmountSave()}
                        className="bg-slate-800 border border-indigo-500 text-white text-2xl font-black rounded-lg px-2 py-1 w-full text-center focus:outline-none focus:ring-2 focus:ring-indigo-400/50"
                        autoFocus
                      />
                      <button
                        onClick={handleAmountSave}
                        className="p-2 bg-emerald-600 text-white rounded-lg hover:bg-emerald-500 shrink-0"
                      >
                        <FaCheck className="w-3 h-3" />
                      </button>
                      <button
                        onClick={() => { setIsEditingAmount(false); }}
                        className="p-2 bg-slate-700 text-slate-300 rounded-lg hover:bg-slate-600 shrink-0"
                      >
                        <FaTimes className="w-3 h-3" />
                      </button>
                    </div>
                    <span className="text-[10px] text-slate-500 font-medium">
                      Original: ${originalAmount.toFixed(2)}
                    </span>
                  </div>
                ) : (
                  <div
                    onClick={() => setIsEditingAmount(true)}
                    className="inline-flex items-center justify-center gap-2 cursor-pointer group"
                    title="Click to edit amount"
                  >
                    <span className="text-3xl sm:text-4xl font-black text-white group-hover:text-indigo-300 transition-colors">
                      ${parseFloat(editedAmount || 0).toFixed(2)}
                    </span>
                    <FaPen className="w-3.5 h-3.5 text-slate-500 opacity-0 group-hover:opacity-100 transition-opacity" />
                  </div>
                )}
              </div>

              {/* Quick Reduce Buttons */}
              <div className="flex flex-col gap-2">
                <div className="flex items-center justify-between">
                  <span className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">
                    Quick Reduce
                  </span>
                  {amountHasChanged && (
                    <button
                      type="button"
                      onClick={handleResetAmount}
                      className="text-[11px] font-medium text-slate-400 hover:text-slate-200 transition-colors flex items-center gap-1 opacity-80 hover:opacity-100"
                      title="Reset to original amount"
                    >
                      <FaUndo className="w-2.5 h-2.5" />
                      <span>Reset</span>
                    </button>
                  )}
                </div>
                <div className="grid grid-cols-3 gap-2">
                  {[0.10, 0.25, 0.50].map((pct) => (
                    <button
                      key={pct}
                      type="button"
                      onClick={() => handleQuickReduce(pct)}
                      className="py-2 bg-slate-800/80 hover:bg-indigo-600/30 text-slate-300 hover:text-white rounded-xl border border-slate-700/60 hover:border-indigo-500/50 text-xs font-bold transition-all"
                      title={`Reduce amount by ${(pct * 100).toFixed(0)}%`}
                    >
                      -{(pct * 100).toFixed(0)}%
                    </button>
                  ))}
                </div>
              </div>
            </div>

            {/* Bottom Left: Skip Button */}
            <div className="flex items-center justify-start pt-5 mt-5 border-t border-slate-800/80">
              <button
                onClick={handleSkip}
                disabled={isSubmitting}
                className="px-4 py-2 rounded-xl bg-slate-800/80 hover:bg-slate-700 border border-slate-700 text-slate-300 text-xs font-bold flex items-center gap-2 transition-all"
              >
                <span>Skip</span>
                <FaArrowRight className="w-3 h-3" />
              </button>
            </div>
          </div>

          {/* ─── RIGHT: Suggestion + Category Selection + Submission ─── */}
          <div className="md:col-span-7 p-5 md:p-6 flex flex-col justify-between gap-5">
            <div className="flex flex-col gap-4">
              {/* ML Suggested Category (Accept / Reject) */}
              {showSuggestion && (
                <div className="bg-gradient-to-br from-indigo-950/70 via-purple-950/50 to-slate-900/60 border border-indigo-500/40 rounded-2xl p-4 shadow-lg ai-recommend-glow">
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      <FaMagic className="w-3.5 h-3.5 text-amber-400" />
                      <span className="text-xs font-extrabold text-indigo-300 uppercase tracking-wider">
                        ML Suggested Category
                      </span>
                    </div>
                    {selectedCategory?.categoryId === suggestedCategory.categoryId && (
                      <span className="text-[10px] font-bold text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded-full border border-emerald-500/20">
                        Selected
                      </span>
                    )}
                  </div>

                  <div className="bg-slate-900/70 rounded-xl border border-indigo-500/20 p-3 mb-3">
                    <span className="text-[11px] font-bold text-indigo-300/70 uppercase tracking-wider block mb-0.5">
                      {suggestedCategory.categoryType?.name || 'Category'}
                    </span>
                    <span className="text-xl font-black text-white block">
                      {suggestedCategory.name}
                    </span>
                  </div>

                  <div className="flex items-center gap-2.5">
                    <button
                      onClick={handleAcceptSuggestion}
                      className={`flex-1 py-2.5 font-extrabold text-sm rounded-xl shadow-md transition-all flex items-center justify-center gap-2 border ${
                        selectedCategory?.categoryId === suggestedCategory.categoryId
                          ? 'bg-emerald-600 border-emerald-500 text-white ring-2 ring-emerald-400/40'
                          : 'bg-indigo-600/80 hover:bg-indigo-600 border-indigo-500 text-white'
                      }`}
                    >
                      <FaCheck className="w-3.5 h-3.5" />
                      <span>{selectedCategory?.categoryId === suggestedCategory.categoryId ? 'Selected' : 'Use ML Suggestion'}</span>
                    </button>
                    <button
                      onClick={handleDenySuggestion}
                      className="py-2.5 px-4 bg-slate-800/80 hover:bg-slate-700 text-slate-300 hover:text-white font-bold text-sm rounded-xl border border-slate-700 transition-all flex items-center justify-center gap-2"
                    >
                      <FaTimes className="w-3.5 h-3.5" />
                      <span>Choose Other</span>
                    </button>
                  </div>
                </div>
              )}

              {/* Manual Category Selection — shown when no suggestion or after rejecting / browsing */}
              {(!showSuggestion || suggestionDenied) && (
                <div className="flex flex-col gap-3 flex-1">
                  <h4 className="text-xs font-extrabold uppercase tracking-wider text-slate-400">
                    Select a Category
                  </h4>

                  {/* Category Type Tabs */}
                  <div className="flex items-center gap-2 overflow-x-auto no-scrollbar pb-1">
                    {categoryTypes.map((ct) => {
                      const isSelected = selectedCategoryTypeId === ct.categoryTypeId;
                      return (
                        <button
                          key={ct.categoryTypeId}
                          onClick={() => setSelectedCategoryTypeId(ct.categoryTypeId)}
                          className={`px-3.5 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition-all border ${
                            isSelected
                              ? 'bg-indigo-600 border-indigo-500 text-white shadow-md'
                              : 'bg-slate-800/80 border-slate-700/70 text-slate-300 hover:bg-slate-700 hover:text-white'
                          }`}
                        >
                          {ct.name}
                        </button>
                      );
                    })}
                  </div>

                  {/* Category Grid */}
                  <div className="grid grid-cols-2 sm:grid-cols-3 gap-2.5 max-h-[280px] overflow-y-auto no-scrollbar">
                    {currentCategoryList.map((category) => {
                      const isCategorySelected = selectedCategory?.categoryId === category.categoryId;
                      return (
                        <button
                          key={category.categoryId}
                          onClick={() => setSelectedCategory(category)}
                          className={`p-3 rounded-2xl text-xs font-bold text-left transition-all border flex items-center justify-between gap-1.5 ${
                            isCategorySelected
                              ? 'bg-indigo-600 border-indigo-400 text-white shadow-lg ring-2 ring-indigo-400/50'
                              : 'bg-slate-800/60 border-slate-700/60 text-slate-200 hover:bg-indigo-600/60 hover:border-indigo-500 hover:text-white'
                          }`}
                        >
                          <span className="truncate">{category.name}</span>
                          {isCategorySelected && <FaCheck className="w-3 h-3 text-white shrink-0" />}
                        </button>
                      );
                    })}
                  </div>
                </div>
              )}
            </div>

            {/* Submission Section */}
            <div className="flex flex-col gap-3 pt-4 border-t border-slate-800/80">
              <button
                onClick={handleCategorizeSubmit}
                disabled={!selectedCategory || isSubmitting}
                className={`w-full py-3.5 px-5 rounded-2xl text-sm font-extrabold flex items-center justify-between shadow-lg transition-all duration-200 ${
                  selectedCategory && !isSubmitting
                    ? 'bg-gradient-to-r from-emerald-500 to-teal-500 hover:from-emerald-400 hover:to-teal-400 text-white shadow-emerald-500/20 hover:shadow-emerald-500/30 hover:-translate-y-0.5 active:translate-y-0 active:scale-[0.99] cursor-pointer'
                    : 'bg-slate-800/50 border border-slate-700/50 text-slate-400 cursor-not-allowed opacity-75'
                }`}
              >
                <div className="flex items-center gap-2.5">
                  <div className={`p-1.5 rounded-xl ${selectedCategory ? 'bg-white/20 text-white' : 'bg-slate-700/50 text-slate-500'}`}>
                    <FaCheck className="w-3.5 h-3.5" />
                  </div>
                  <span className="font-extrabold tracking-wide">
                    {isSubmitting
                      ? 'Saving...'
                      : selectedCategory
                      ? 'Save & Continue'
                      : 'Select a Category'}
                  </span>
                </div>

                {selectedCategory && (
                  <span className="bg-black/20 text-emerald-100 text-xs font-bold px-3 py-1 rounded-xl truncate max-w-[160px] border border-white/10">
                    {selectedCategory.name}
                  </span>
                )}
              </button>

              {/* Keyboard Shortcut Hints (desktop only) */}
              <div className="hidden md:flex items-center justify-between text-[11px] text-slate-500 px-1">
                <div className="flex items-center gap-1.5">
                  <kbd className="px-1.5 py-0.5 rounded bg-slate-800 border border-slate-700 text-slate-400 font-mono text-[10px]">Enter</kbd>
                  <span>Save & Continue</span>
                </div>
                <div className="flex items-center gap-1.5">
                  <kbd className="px-1.5 py-0.5 rounded bg-slate-800 border border-slate-700 text-slate-400 font-mono text-[10px]">→</kbd>
                  <span>Skip</span>
                </div>
                <div className="flex items-center gap-1.5">
                  <kbd className="px-1.5 py-0.5 rounded bg-slate-800 border border-slate-700 text-slate-400 font-mono text-[10px]">Esc</kbd>
                  <span>Exit</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Confirmation Modal */}
      {showConfirmation && (
        <ConfirmationModal
          question="Are you sure you want to delete this transaction?"
          onConfirm={confirmDelete}
          onClose={() => setShowConfirmation(false)}
        />
      )}
    </div>
  );
};

export default TransactionSwiper;
import React, { useState, useEffect, useContext } from 'react';
import './styles.css';
import CategorySlider from './CategorySlider';
import ConfirmationModal from '../layout/ConfirmationModal';
import SplitTransactionModal from '../transaction/SplitTransaction';
import transactionContext from '../../context/transaction/transactionContext';
import AlertContext from '../../context/alert/alertContext';
import { ThemeContext } from '../../context/theme/ThemeContext';

const TransactionSwiper = ({ transactions, categories, categoryTypes, onClose }) => {
  const { deleteTransaction, renameTransaction, reduceTransactionAmount, updateCategory } = useContext(transactionContext);
  const { setAlert } = useContext(AlertContext);
  const { theme } = useContext(ThemeContext);
  const isDark = theme === "dark";

  const [currentIndex, setCurrentIndex] = useState(0);
  const [showCategorySlider, setShowCategorySlider] = useState(false);
  const [selectedCategoryType, setSelectedCategoryType] = useState(null);
  const [cardAnimation, setCardAnimation] = useState('');
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [isEditingName, setIsEditingName] = useState(false);
  const [isEditingAmount, setIsEditingAmount] = useState(false);
  const [editedName, setEditedName] = useState('');
  const [editedAmount, setEditedAmount] = useState('');
  const [suggestionDenied, setSuggestionDenied] = useState(false);
  const [activePresetSplits, setActivePresetSplits] = useState(null);
  const [showSplitModal, setShowSplitModal] = useState(false);

  const currentTransaction = transactions[currentIndex];
  const hasSuggestedCategory = currentTransaction?.suggestedCategory && !suggestionDenied;

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

  useEffect(() => {
    if (currentTransaction) {
      setEditedName(currentTransaction.name);
      setEditedAmount(currentTransaction.amount);
      setSuggestionDenied(false);
    }
  }, [currentTransaction]);

  const handleAcceptSuggestion = () => {
    const { suggestedCategory } = currentTransaction;
    updateCategory(currentTransaction.transactionId, suggestedCategory.categoryId, false);
    setAlert(`Transaction assigned to ${suggestedCategory.name}`, 'success');
    setCardAnimation('card-exit-left');
  };

  const handleDenySuggestion = () => {
    setSuggestionDenied(true);
  };

  const getCategoriesForType = (ct) => {
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
  };

  const handleCategoryTypeClick = (categoryType) => {
    setSelectedCategoryType(categoryType);
    setShowCategorySlider(true);
  };

  const handleCategorySelect = (category) => {
    updateCategory(currentTransaction.transactionId, category.categoryId, false);
    setAlert(`Transaction assigned to ${category.name}`, 'success');
    setShowCategorySlider(false);
    setCardAnimation('card-exit-left');
  };

  const handleAnimationEnd = () => {
    if (cardAnimation === 'card-exit-left') {
      setCardAnimation('card-enter-right');
      if (currentIndex < transactions.length - 1) {
        setCurrentIndex(currentIndex + 1);
      } else {
        onClose();
      }
    }
  };

  const handleDelete = () => {
    setShowConfirmation(true);
  };

  const confirmDelete = () => {
    deleteTransaction(currentTransaction.transactionId);
    setShowConfirmation(false);
    setCardAnimation('card-exit-left');
  };

  const handleNameSave = () => {
    renameTransaction(currentTransaction.transactionId, editedName);
    setIsEditingName(false);
    setAlert('Transaction name updated', 'success');
  };

  const handleAmountSave = () => {
    const newAmount = parseFloat(editedAmount);
    if (isNaN(newAmount) || newAmount <= 0) {
        setAlert('Please enter a valid amount', 'danger');
        return;
    }
    if (newAmount >= currentTransaction.amount) {
        setAlert('Amount must be less than original', 'danger');
        return;
    }
    reduceTransactionAmount(currentTransaction.transactionId, newAmount);
    setIsEditingAmount(false);
    setAlert('Transaction amount updated', 'success');
  };

  const handleQuickReduce = (percentage) => {
    if (!currentTransaction) return;
    const newAmount = Math.round((currentTransaction.amount * (1 - percentage)) * 100) / 100;
    if (newAmount <= 0) {
      setAlert('Reduced amount must be greater than 0', 'danger');
      return;
    }
    reduceTransactionAmount(currentTransaction.transactionId, newAmount);
    setEditedAmount(newAmount.toString());
    setAlert(`Amount reduced by ${(percentage * 100).toFixed(0)}% to $${newAmount}`, 'success');
  };

  const handleQuickSplitPreset = (ratio1, ratio2) => {
    if (!currentTransaction) return;
    const amt = parseFloat(currentTransaction.amount);
    const amt1 = Math.round(amt * ratio1 * 100) / 100;
    const amt2 = Math.round((amt - amt1) * 100) / 100;

    setActivePresetSplits([
      { name: `${currentTransaction.name} (Part 1)`, amount: amt1 },
      { name: `${currentTransaction.name} (Part 2)`, amount: amt2 }
    ]);
    setShowSplitModal(true);
  };

  useEffect(() => {
    if (transactions.length > 0) {
      setCardAnimation('card-enter-right');
    }
  }, [transactions]);

  if (!currentTransaction) {
    return null;
  }

  return (
    <div className="swiper-container">
      {/* Transaction Card */}
      <div className="card-container">
        <div
          className={`card ${cardAnimation}`}
          onAnimationEnd={handleAnimationEnd}
        >
            {isPreviousMonth && (
              <div className="bg-amber-500/20 text-amber-300 text-xs font-bold px-3 py-1 text-center rounded-t-lg border-b border-amber-500/30 flex items-center justify-center gap-1">
                <span>📅</span> Previous Month ({prevMonthLabel})
              </div>
            )}
            <div className="card-header">
                <div className="transaction-info">
                    <img src={currentTransaction.logoUrl || 'https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg'} alt="logo" className="transaction-logo" />
                    {isEditingName ? (
                    <div className="edit-container">
                        <input type="text" value={editedName} onChange={(e) => setEditedName(e.target.value)} className="edit-input" />
                        <button onClick={handleNameSave} className="save-button">Save</button>
                    </div>
                    ) : (
                    <div className="transaction-name-container">
                      <h3 onClick={() => setIsEditingName(true)}>
                        {editedName} <span className="edit-indicator">✏️</span>
                      </h3>
                      <span className="transaction-date-inline">
                        {new Date(currentTransaction.date).toLocaleDateString(undefined, {
                          year: 'numeric',
                          month: 'short',
                          day: 'numeric'
                        })}
                      </span>
                    </div>
                    )}
                </div>
                <button onClick={handleDelete} className="delete-button">🗑️</button>
            </div>
            <div className="card-body">
                {isEditingAmount ? (
                    <div className="edit-container">
                        <input type="text" value={editedAmount} onChange={(e) => setEditedAmount(e.target.value)} className="edit-input" />
                        <button onClick={handleAmountSave} className="save-button">Save</button>
                    </div>
                ) : (
                    <p onClick={() => setIsEditingAmount(true)}>${editedAmount} <span className="edit-indicator">✏️</span></p>
                )}
            </div>

            {/* Quick Actions - Integrated Into Card */}
            <div className="flex items-center justify-center gap-1.5 mt-1 flex-wrap px-1">
              {[0.10, 0.25, 0.50].map((pct) => (
                <button
                  key={pct}
                  type="button"
                  onClick={() => handleQuickReduce(pct)}
                  className="px-2 py-0.5 text-[10px] font-semibold bg-white/10 hover:bg-white/20 text-white/80 hover:text-white rounded-md border border-white/20 transition-colors"
                  title={`Reduce by ${(pct * 100).toFixed(0)}%`}
                >
                  -{(pct * 100).toFixed(0)}%
                </button>
              ))}
              <span className="text-white/30 mx-0.5">|</span>
              {[{ label: '50/50', r1: 0.5, r2: 0.5 }, { label: '70/30', r1: 0.7, r2: 0.3 }].map(({ label, r1, r2 }) => (
                <button
                  key={label}
                  onClick={() => handleQuickSplitPreset(r1, r2)}
                  className="px-2 py-0.5 text-[10px] font-semibold bg-indigo-500/20 hover:bg-indigo-500/40 text-indigo-200 hover:text-white rounded-md border border-indigo-400/30 transition-colors"
                >
                  ✂ {label}
                </button>
              ))}
            </div>
        </div>
      </div>

      {/* Suggested Category Section */}
      {hasSuggestedCategory && (
        <div className="suggestion-container">
          <div className="suggestion-header">
            <h3>Suggested Category</h3>
          </div>
          <div className="suggestion-content">
            <div className="suggested-category">
              <span className="category-type">{currentTransaction.suggestedCategory.categoryType?.name}</span>
              <span className="category-name">{currentTransaction.suggestedCategory.name}</span>
            </div>
            <div className="suggestion-buttons">
              <button onClick={handleAcceptSuggestion} className="accept-button">✓ Accept</button>
              <button onClick={handleDenySuggestion} className="deny-button">✗ Choose Different</button>
            </div>
          </div>
        </div>
      )}

      {/* Manual Category Selection */}
      {(!hasSuggestedCategory || showCategorySlider) && (
        <div className="flex flex-wrap justify-center gap-2.5 mt-5 max-w-md px-2">
          {categoryTypes.map(ct => (
            <button
              key={ct.categoryTypeId}
              onClick={() => handleCategoryTypeClick(ct)}
              className={`px-4 py-2.5 rounded-xl text-sm font-bold transition-all shadow-md ${
                isDark
                  ? "bg-slate-800 border border-slate-700 text-slate-100 hover:bg-brand-600 hover:border-brand-500 hover:text-white"
                  : "bg-white border-2 border-slate-300 text-slate-900 hover:bg-brand-600 hover:border-brand-600 hover:text-white shadow-sm"
              }`}
            >
              {ct.name}
            </button>
          ))}
        </div>
      )}

      {showCategorySlider && selectedCategoryType && (
        <CategorySlider
          categoryType={selectedCategoryType}
          categories={getCategoriesForType(selectedCategoryType)}
          onCategorySelect={handleCategorySelect}
          onCancel={() => setShowCategorySlider(false)}
        />
      )}
      {showConfirmation && (
        <ConfirmationModal
          question="Are you sure you want to delete this transaction?"
          onConfirm={confirmDelete}
          onClose={() => setShowConfirmation(false)}
        />
      )}
      {showSplitModal && (
        <SplitTransactionModal
          transaction={currentTransaction}
          presetSplits={activePresetSplits}
          onClose={() => {
            setShowSplitModal(false);
            setActivePresetSplits(null);
          }}
        />
      )}
      <button onClick={onClose} className="close-button">
        Close
      </button>
    </div>
  );
};

export default TransactionSwiper;
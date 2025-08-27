import React, { useState, useEffect, useContext } from 'react';
import './styles.css';
import CategorySlider from './CategorySlider';
import ConfirmationModal from '../layout/ConfirmationModal';
import transactionContext from '../../context/transaction/transactionContext';
import AlertContext from '../../context/alert/alertContext';

const TransactionSwiper = ({ transactions, categories, categoryTypes, onClose }) => {
  const { deleteTransaction, renameTransaction, reduceTransactionAmount, updateCategory } = useContext(transactionContext);
  const { setAlert } = useContext(AlertContext);

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

  const currentTransaction = transactions[currentIndex];
  const hasSuggestedCategory = currentTransaction?.suggestedCategory && !suggestionDenied;

  useEffect(() => {
    if (currentTransaction) {
      setEditedName(currentTransaction.name);
      setEditedAmount(currentTransaction.amount);
      // Reset suggestion denied state when moving to new transaction
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
    setAlert('Choose a category manually', 'info');
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
      <div className="card-container">
        <div
          className={`card ${cardAnimation}`}
          onAnimationEnd={handleAnimationEnd}
        >
            <div className="card-header">
                <div className="transaction-info">
                    <img src={currentTransaction.logoUrl || 'https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg'} alt="logo" className="transaction-logo" />
                    {isEditingName ? (
                    <div className="edit-container">
                        <input type="text" value={editedName} onChange={(e) => setEditedName(e.target.value)} className="edit-input" />
                        <button onClick={handleNameSave} className="save-button">Save</button>
                    </div>
                    ) : (
                    <h3 onClick={() => setIsEditingName(true)}>{editedName} <span className="edit-indicator">✏️</span></h3>
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

      {/* Manual Category Selection - Show when no suggestion, denied, or category slider is open */}
      {(!hasSuggestedCategory || showCategorySlider) && (
        <div className="buttons">
          {categoryTypes.map(ct => <button key={ct.categoryTypeId} onClick={() => handleCategoryTypeClick(ct)}>{ct.name}</button>)}
        </div>
      )}

      {showCategorySlider && (
        <CategorySlider
          categoryType={selectedCategoryType}
          categories={selectedCategoryType.categories}
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
      <button onClick={onClose} className="close-button">
        Close
      </button>
    </div>
  );
};

export default TransactionSwiper;
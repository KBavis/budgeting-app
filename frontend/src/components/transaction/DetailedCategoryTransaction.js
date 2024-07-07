import React, { useContext, useState } from "react";
import { FaEllipsisV } from "react-icons/fa";
import { Dropdown, DropdownButton } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import transactionContext from "../../context/transaction/transactionContext";

/**
 * Component used to represent an Individual Transaction in a DetailedCategory
 *
 * @param transaction
 * - Transaction to generate Component for
 * @param handleShowSplitTransactionModal
 *       - Functionality to display SplitTransaction modal
 * @param handleShowRenameTransactionModal
 *       - Functionality to display RenameTransaction modal
 * @param handleShowReduceTransactionModal
 *       - Functionality to display ReduceTransaction modal
 * @param handleShowAssignCategoryModal
 *       - Functionality to display AssignCategory modal
 */
const DetailedCategoryTransaction = ({
  transaction,
  handleShowSplitTransactionModal,
  handleShowReduceTransactionModal,
  handleShowRenameTransactionModal,
  handleShowAssignCategoryModal,
}) => {
  // Determine rounded amount for Transaction
  const roundedAmount = Math.round(transaction.amount);

  //Local State
  const [dropdownVisible, setDropdownVisible] = useState(false);

  //Glboal State
  const { deleteTransaction } = useContext(transactionContext);

  const handleDropdownToggle = (isOpen) => {
    setDropdownVisible(isOpen);
  };

  //Handle removing a Transaction
  const handleDeleteTransaction = () => {
    deleteTransaction(transaction.transactionId);
    setDropdownVisible(false);
  };

  //Handle splitting a Transaction
  const handleSplitTransaction = () => {
    handleShowSplitTransactionModal(transaction);
    setDropdownVisible(false);
  };

  //Handle re-assinging Transaction
  const handleReassignTransaction = () => {
    handleShowAssignCategoryModal(transaction);
    setDropdownVisible(false);
  };

  //Handle re-naming Transaction
  const handleRenameTransaction = () => {
    handleShowRenameTransactionModal(transaction);
    setDropdownVisible(false);
  };

  //Handle reducing Transaction
  const handleReduceTransaction = () => {
    handleShowReduceTransactionModal(transaction);
    setDropdownVisible(false);
  };

  return (
    <div className="bg-indigo-900 rounded-lg p-3 shadow-md flex items-center justify-between mb-3">
      <div className="flex items-center space-x-2 flex-grow overflow-hidden">
        {transaction.logoUrl ? (
          <img
            src={transaction.logoUrl}
            alt="Transaction Logo"
            className="w-8 h-8 rounded-full flex-shrink-0"
          />
        ) : (
          <img
            src="https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
            className="w-8 h-8 rounded-full flex-shrink-0"
            alt="Default Avatar"
          />
        )}
        <div className="text-white text-left flex-grow">
          <p className="text-sm font-bold break-words">{transaction.name}</p>
          <p className="text-sm">${roundedAmount}</p>
        </div>
      </div>
      <Dropdown show={dropdownVisible} onToggle={handleDropdownToggle}>
        <Dropdown.Toggle
          as="button"
          className="text-white bg-transparent border-none p-0 m-0"
        >
          <FaEllipsisV size={20} />
        </Dropdown.Toggle>

        <Dropdown.Menu
          align="end"
          className="bg-white text-black text-sm text-center"
        >
          <Dropdown.Item
            className="dropdown-item font-semibold"
            onClick={handleDeleteTransaction}
          >
            Delete
          </Dropdown.Item>
          <Dropdown.Item
            className="dropdown-item font-semibold"
            onClick={handleSplitTransaction}
          >
            Split
          </Dropdown.Item>
          <Dropdown.Item
            className="dropdown-item font-semibold"
            onClick={handleReassignTransaction}
          >
            Re-Assign Category
          </Dropdown.Item>
          <Dropdown.Item
            className="dropdown-item font-semibold"
            onClick={handleRenameTransaction}
          >
            Rename
          </Dropdown.Item>
          <Dropdown.Item
            className="dropdown-item font-semibold"
            onClick={handleReduceTransaction}
          >
            Reduce
          </Dropdown.Item>
        </Dropdown.Menu>
      </Dropdown>
    </div>
  );
};

export default DetailedCategoryTransaction;

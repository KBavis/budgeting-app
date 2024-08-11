import React from 'react';
import { FaTrashAlt } from 'react-icons/fa';

/**
 * Component to represent a users Account
 *
 * @param account
 *          - account to generate component for
 * @param handleShowConfirmationModal
 *          - functionality to display confirmation modal
 */
const Account = ({ account, handleShowConfirmationModal }) => {
    return (
        <div className="relative bg-gradient-to-r from-indigo-800 to-indigo-400 rounded-xl shadow-lg p-8 mb-8 w-full max-w-3xl text-center">
            <div
                className="absolute top-4 right-4 text-white cursor-pointer hover:text-red-500 transition duration-200"
                onClick={() => handleShowConfirmationModal(account)}
                title="Delete Account"
            >
                <span className="z-5 duration-500 hover:text-red-500 "><FaTrashAlt size={24} /> </span>
            </div>

            <div className="flex justify-center items-center mb-4">
                <h3 className="text-4xl font-bold text-white">{account.accountName}</h3>
            </div>
            <p className="text-2xl text-white mb-4">
                Balance: <span className="font-semibold">${account.balance.toFixed(2)}</span>
            </p>
        </div>
    );
};

export default Account;


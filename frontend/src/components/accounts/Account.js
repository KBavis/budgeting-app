/**
 *  Account component to represent a users account
 */
import React from 'react';

/**
 *  Account component to represent a user's account
 */
const Account = ({ account }) => {
    return (
        <div className="bg-gradient-to-r from-indigo-800 to-indigo-400 rounded-xl shadow-lg p-8 mb-8 w-full max-w-3xl text-center transform transition duration-300 hover:scale-105">
            <div className="flex justify-center items-center mb-4">
                <h3 className="text-4xl font-bold text-white">{account.accountName} </h3>
            </div>
            <p className="text-2xl text-white mb-4">Balance: <span className="font-semibold">${account.balance.toFixed(2)}</span></p>
        </div>
    );
};

export default Account;

import React, { useContext, useEffect, useRef, useState, useMemo } from 'react';
import alertContext from "../context/alert/alertContext";
import authContext from "../context/auth/authContext";
import accountContext from "../context/account/accountContext";
import { FaPlus } from "react-icons/fa";
import { PlaidLink } from "react-plaid-link";
import Account from "../components/accounts/Account";
import ConfirmationModal from "../components/layout/ConfirmationModal";
import transactionContext from "../context/transaction/transactionContext";
import { ThemeContext } from "../context/theme/ThemeContext";

/**
 * Page to display the current user's connected Accounts with full Light/Dark mode support
 */
const AccountsPage = () => {
    const { accounts, fetchAccounts, setLoading, createAccount, removeAccount, error } = useContext(accountContext);
    const { setAlert } = useContext(alertContext);
    const { refreshLinkToken, user } = useContext(authContext);
    const { fetchTransactions } = useContext(transactionContext);
    const { theme } = useContext(ThemeContext);

    const isDark = theme === "dark";

    const initialFetchRef = useRef(false);
    const isTokenRefreshed = useRef(false);
    const [accountAdded, setAccountAdded] = useState(null);
    const [plaidKey, setPlaidKey] = useState(Date.now());
    const [showConfirmationModal, setShowConfirmationModal] = useState(false);
    const [accountToDelete, setAccountToDelete] = useState(null);

    const handleShowConfirmationModal = (account) => {
        setAccountToDelete(account);
        setShowConfirmationModal(true);
    };

    const handleCloseConfirmationModal = () => {
        setShowConfirmationModal(false);
        setAccountToDelete(null);
    };

    const handleConfirm = async () => {
        await removeAccount(accountToDelete.accountId);
        await fetchTransactions();
        handleCloseConfirmationModal();
    };

    const handleOnSuccess = (publicToken, metadata) => {
        if (accountAdded && accountAdded.plaidAccountId === metadata.account_id) {
            setAlert("Account already added", "danger");
            return;
        }

        const accountData = {
            institutionName: metadata.institution.name,
            accountName: metadata.account.name,
            accountType: metadata.account.type.toUpperCase(),
            accountSubtype: metadata.account.subtype.toUpperCase(),
            mask: metadata.account.mask,
            plaidAccountId: metadata.account_id,
            plaidPublicToken: publicToken
        };

        createAccount(accountData);
        setAccountAdded(accountData);
        setPlaidKey(Date.now());
        setAlert("Successfully linked financial institution!", "success");
    };

    const handleOnExit = (error, metadata) => {
        if (error) {
            console.error("Plaid link error: ", error);
        }
    };

    const getAccountsData = async () => {
        setLoading();
        await fetchAccounts();
    };

    useEffect(() => {
        if (!initialFetchRef.current || !accounts) {
            getAccountsData();
            initialFetchRef.current = true;
        }
    }, [accounts]);

    useEffect(() => {
        if (user && user.linkToken.expired && !isTokenRefreshed.current) {
            isTokenRefreshed.current = true;
            refreshLinkToken();
        }
    }, [user, refreshLinkToken]);

    useEffect(() => {
        if(error) { setAlert(error, "danger"); }
    }, [error]);

    const netWorth = useMemo(() => {
        if (!accounts) return 0;
        return accounts.reduce((total, account) => {
            const type = account.accountType;
            if (type === 'CREDIT' || type === 'LOAN') {
                return total - Math.abs(account.balance);
            }
            return total + account.balance;
        }, 0);
    }, [accounts]);

    return accounts && (
        <div className={`flex flex-col min-h-screen relative ${
            isDark
                ? "bg-gradient-to-br from-gray-900 via-slate-900 to-indigo-950 text-slate-100"
                : "bg-gradient-to-br from-slate-100 via-indigo-50/50 to-slate-100 text-slate-800"
        }`}>
            <div className="flex flex-col items-center px-4 md:px-12 h-full pt-16">
                {/* Header */}
                <div className="max-w-xl w-full text-center mb-6 mt-5">
                    <h2 className={`text-4xl md:text-5xl font-black mb-2 ${isDark ? "text-white" : "text-slate-900"}`}>
                        Your Accounts
                    </h2>
                    <p className={`text-sm ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                        View and manage linked financial accounts
                    </p>
                </div>

                {/* Net Worth Banner */}
                {accounts.length > 0 && (
                    <div className={`w-full max-w-xl border rounded-2xl p-5 mb-6 shadow-lg ${
                        isDark
                            ? "bg-slate-800/70 border-slate-600/50"
                            : "bg-white border-slate-200"
                    }`}>
                        <div className="text-center">
                            <p className={`text-xs font-bold uppercase tracking-wider mb-1 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                                Net Worth
                            </p>
                            <p className={`text-3xl font-black ${netWorth >= 0 ? (isDark ? 'text-emerald-400' : 'text-emerald-600') : (isDark ? 'text-red-400' : 'text-red-600')}`}>
                                {netWorth >= 0 ? '' : '-'}${Math.abs(netWorth).toFixed(2)}
                            </p>
                            <p className={`text-xs mt-1 ${isDark ? "text-slate-500" : "text-slate-400"}`}>
                                Across {accounts.length} linked account{accounts.length !== 1 ? 's' : ''}
                            </p>
                        </div>
                    </div>
                )}

                {/* Account List */}
                <div className="w-full max-w-xl flex flex-col gap-2.5 pb-6">
                    {accounts.length > 0 ? (
                        accounts.map((account) => (
                            <Account
                                key={account.accountId}
                                account={account}
                                handleShowConfirmationModal={handleShowConfirmationModal}
                            />
                        ))
                    ) : (
                        <div className={`p-8 text-center border rounded-2xl ${
                            isDark ? "bg-slate-800/60 border-slate-700 text-slate-500" : "bg-white border-slate-200 text-slate-400 shadow-sm"
                        }`}>
                            No accounts connected yet. Link your first account below!
                        </div>
                    )}
                </div>

                {/* Link Account Button - at bottom */}
                <div className="flex justify-center mb-20 w-full max-w-xl">
                    <PlaidLink
                        key={plaidKey}
                        token={user?.linkToken.token}
                        onSuccess={handleOnSuccess}
                        onExit={handleOnExit}
                        className="plaid-link-wrapper-class"
                    >
                        <button className="flex items-center gap-2 bg-brand-600 hover:bg-brand-500 text-white rounded-xl px-6 py-3 text-sm font-bold transition-all duration-300 shadow-lg hover:scale-105">
                            <FaPlus size={12} />
                            Link Bank Account
                        </button>
                    </PlaidLink>
                </div>
            </div>

            {/* Confirmation Modal */}
            {showConfirmationModal && accountToDelete && (
                <ConfirmationModal
                    question="Are you sure you want to remove the following account?"
                    accountName={accountToDelete.accountName}
                    onConfirm={handleConfirm}
                    onClose={handleCloseConfirmationModal}
                />
            )}
        </div>
    );
};

export default AccountsPage;

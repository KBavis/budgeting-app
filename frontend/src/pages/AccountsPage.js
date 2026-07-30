import React, { useContext, useEffect, useRef, useState, useMemo } from 'react';
import alertContext from "../context/alert/alertContext";
import authContext from "../context/auth/authContext";
import accountContext from "../context/account/accountContext";
import { FaPlus, FaWallet, FaCreditCard, FaArrowUp, FaArrowDown } from "react-icons/fa";
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
        const accountsList = (metadata.accounts && metadata.accounts.length > 0)
            ? metadata.accounts
            : [metadata.account];

        accountsList.forEach((acc) => {
            const accountData = {
                institutionName: metadata.institution.name,
                accountName: acc.name,
                accountType: acc.type ? acc.type.toUpperCase() : 'CHECKING',
                accountSubtype: acc.subtype ? acc.subtype.toUpperCase() : '',
                mask: acc.mask,
                plaidAccountId: acc.id || metadata.account_id,
                plaidPublicToken: publicToken
            };
            createAccount(accountData);
        });

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

    const { assetAccounts, liabilityAccounts, totalAssets, totalLiabilities, netWorth } = useMemo(() => {
        if (!accounts) return { assetAccounts: [], liabilityAccounts: [], totalAssets: 0, totalLiabilities: 0, netWorth: 0 };
        
        const assets = [];
        const liabilities = [];
        let assetSum = 0;
        let liabilitySum = 0;

        accounts.forEach((acc) => {
            const type = acc.accountType;
            if (type === 'CREDIT' || type === 'LOAN') {
                liabilities.push(acc);
                liabilitySum += Math.abs(acc.balance);
            } else {
                assets.push(acc);
                assetSum += acc.balance;
            }
        });

        return {
            assetAccounts: assets,
            liabilityAccounts: liabilities,
            totalAssets: assetSum,
            totalLiabilities: liabilitySum,
            netWorth: assetSum - liabilitySum
        };
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

                {/* Net Worth & Assets / Liabilities Summary Banner */}
                {accounts.length > 0 && (
                    <div className={`w-full max-w-xl border rounded-3xl p-6 mb-6 shadow-xl backdrop-blur-sm ${
                        isDark
                            ? "bg-slate-800/80 border-slate-700/60"
                            : "bg-white/90 border-slate-200 shadow-slate-200/50"
                    }`}>
                        {/* Primary Net Worth */}
                        <div className="text-center pb-5 border-b border-slate-700/40">
                            <p className={`text-xs font-extrabold uppercase tracking-widest mb-1 ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                                Total Net Worth
                            </p>
                            <p className={`text-4xl md:text-5xl font-black tracking-tight ${
                                netWorth >= 0 ? (isDark ? 'text-emerald-400' : 'text-emerald-600') : (isDark ? 'text-rose-400' : 'text-rose-600')
                            }`}>
                                {netWorth >= 0 ? '' : '-'}${Math.abs(netWorth).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                            </p>
                        </div>

                        {/* Breakdown Grid */}
                        <div className="grid grid-cols-2 gap-4 pt-4 text-center">
                            {/* Assets */}
                            <div className={`p-3 rounded-2xl border ${
                                isDark ? "bg-emerald-950/30 border-emerald-800/40" : "bg-emerald-50/70 border-emerald-200/80"
                            }`}>
                                <div className="flex items-center justify-center gap-1.5 mb-1">
                                    <div className="p-1 rounded-full bg-emerald-500/20 text-emerald-400">
                                        <FaArrowUp className="w-2.5 h-2.5" />
                                    </div>
                                    <span className={`text-xs font-bold uppercase tracking-wider ${isDark ? "text-emerald-300" : "text-emerald-800"}`}>
                                        Assets
                                    </span>
                                </div>
                                <p className={`text-lg font-black ${isDark ? "text-emerald-400" : "text-emerald-700"}`}>
                                    ${totalAssets.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                                </p>
                                <p className={`text-[10px] mt-0.5 ${isDark ? "text-emerald-400/60" : "text-emerald-600"}`}>
                                    {assetAccounts.length} account{assetAccounts.length !== 1 ? 's' : ''}
                                </p>
                            </div>

                            {/* Liabilities */}
                            <div className={`p-3 rounded-2xl border ${
                                isDark ? "bg-rose-950/30 border-rose-800/40" : "bg-rose-50/70 border-rose-200/80"
                            }`}>
                                <div className="flex items-center justify-center gap-1.5 mb-1">
                                    <div className="p-1 rounded-full bg-rose-500/20 text-rose-400">
                                        <FaArrowDown className="w-2.5 h-2.5" />
                                    </div>
                                    <span className={`text-xs font-bold uppercase tracking-wider ${isDark ? "text-rose-300" : "text-rose-800"}`}>
                                        Liabilities
                                    </span>
                                </div>
                                <p className={`text-lg font-black ${isDark ? "text-rose-400" : "text-rose-700"}`}>
                                    ${totalLiabilities.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                                </p>
                                <p className={`text-[10px] mt-0.5 ${isDark ? "text-rose-400/60" : "text-rose-600"}`}>
                                    {liabilityAccounts.length} account{liabilityAccounts.length !== 1 ? 's' : ''}
                                </p>
                            </div>
                        </div>
                    </div>
                )}

                {/* Account List Split by Category */}
                <div className="w-full max-w-xl flex flex-col gap-6 pb-6">
                    {accounts.length > 0 ? (
                        <>
                            {/* Assets Section */}
                            {assetAccounts.length > 0 && (
                                <div>
                                    <div className="flex items-center gap-2 mb-2 px-1">
                                        <FaWallet className={`text-sm ${isDark ? "text-emerald-400" : "text-emerald-600"}`} />
                                        <h3 className={`text-xs font-extrabold uppercase tracking-wider ${isDark ? "text-slate-300" : "text-slate-600"}`}>
                                            Assets & Cash ({assetAccounts.length})
                                        </h3>
                                    </div>
                                    <div className="flex flex-col gap-2.5">
                                        {assetAccounts.map((account) => (
                                            <Account
                                                key={account.accountId}
                                                account={account}
                                                handleShowConfirmationModal={handleShowConfirmationModal}
                                            />
                                        ))}
                                    </div>
                                </div>
                            )}

                            {/* Liabilities Section */}
                            {liabilityAccounts.length > 0 && (
                                <div>
                                    <div className="flex items-center gap-2 mb-2 px-1">
                                        <FaCreditCard className={`text-sm ${isDark ? "text-rose-400" : "text-rose-600"}`} />
                                        <h3 className={`text-xs font-extrabold uppercase tracking-wider ${isDark ? "text-slate-300" : "text-slate-600"}`}>
                                            Liabilities & Credit ({liabilityAccounts.length})
                                        </h3>
                                    </div>
                                    <div className="flex flex-col gap-2.5">
                                        {liabilityAccounts.map((account) => (
                                            <Account
                                                key={account.accountId}
                                                account={account}
                                                handleShowConfirmationModal={handleShowConfirmationModal}
                                            />
                                        ))}
                                    </div>
                                </div>
                            )}
                        </>
                    ) : (
                        <div className={`p-8 text-center border rounded-2xl ${
                            isDark ? "bg-slate-800/60 border-slate-700 text-slate-500" : "bg-white border-slate-200 text-slate-400 shadow-sm"
                        }`}>
                            No accounts connected yet. Link your first account below!
                        </div>
                    )}
                </div>

                {/* Link Account Button */}
                <div className="flex justify-center mb-16 w-full max-w-xl">
                    <PlaidLink
                        key={plaidKey}
                        token={user?.linkToken?.token}
                        onSuccess={handleOnSuccess}
                        onExit={handleOnExit}
                        className="plaid-link-wrapper-class w-full"
                        style={{ background: 'transparent', border: 'none', padding: 0 }}
                    >
                        <button className="w-full flex items-center justify-center gap-2.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-2xl px-6 py-3.5 text-sm font-extrabold transition-all duration-300 shadow-lg shadow-indigo-500/25 hover:shadow-indigo-500/40 hover:scale-[1.01] active:scale-[0.99]">
                            <FaPlus className="w-3.5 h-3.5" />
                            <span>Link Bank Account</span>
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

import React, { useContext, useEffect, useRef, useState } from 'react';
import alertContext from "../context/alert/alertContext";
import authContext from "../context/auth/authContext";
import accountContext from "../context/account/accountContext";
import { FaArrowLeft } from "react-icons/fa";
import { PlaidLink } from "react-plaid-link";
import { useNavigate } from "react-router-dom";
import Account from "../components/accounts/Account";
import ConfirmationModal from "../components/layout/ConfirmationModal";
import transactionContext from "../context/transaction/transactionContext";

/**
 * Page to display the current users connected Accounts
 *
 */
const AccountsPage = () => {
    //Global State
    const { accounts, fetchAccounts, setLoading, createAccount, removeAccount, error } = useContext(accountContext);
    const { setAlert } = useContext(alertContext);
    const { refreshLinkToken, user } = useContext(authContext);
    const { fetchTransactions } = useContext(transactionContext);

    //Local State
    const initialFetchRef = useRef(false);
    const isTokenRefreshed = useRef(false);
    const navigate = useNavigate();
    const [accountAdded, setAccountAdded] = useState(null);
    const [plaidKey, setPlaidKey] = useState(Date.now());
    const [showConfirmationModal, setShowConfirmationModal] = useState(false);
    const [accountToDelete, setAccountToDelete] = useState(null);

    //Functionality to show confirmation modal
    const handleShowConfirmationModal = (account) => {
        setAccountToDelete(account)
        setShowConfirmationModal(true);
    }

    //Functionality to close confirmation modal
    const handleCloseConfirmationModal = () => {
        setShowConfirmationModal(false);
        setAccountToDelete(null);
    }

    //Navigation back to Hoem page
    const handleBackClick = () => {
        navigate("/home");
    };

    const handleConfirm = async () => {
        await removeAccount(accountToDelete.accountId); //remove Account from backend & frontend
        await fetchTransactions();
        handleCloseConfirmationModal()
    }

    // Functionality to handle a user's successful connection of their financial institution via Plaid
    const handleOnSuccess = (publicToken, metadata) => {
        if (accountAdded && accountAdded.plaidAccountId === metadata.account_id) {
            setAlert("Account already added", "danger");
            return;
        }

        // Create payload to send to backend
        const accountData = {
            plaidAccountId: metadata.account_id,
            accountName: metadata.institution.name,
            publicToken,
            accountType: mapAccountType(metadata.account.type, metadata.account.subtype),
        };
        console.log(accountData);
        createAccount(accountData);

        setAccountAdded(accountData);
        setAlert("Account added successfully", "SUCCESS");
        setPlaidKey(Date.now()); // Force PlaidLink to re-render by updating key
    };

    // Functionality to map a given account type to our backend enum value
    const mapAccountType = (type, subtype) => {
        switch (type) {
            case "depository":
                return subtype === "checking" ? "CHECKING" : "SAVING";
            case "credit":
                return "CREDIT";
            case "loan":
                return "LOAN";
            case "investment":
                return "INVESTMENT";
            default:
                return null;
        }
    };

    // Functionality to handle a user closing Plaid Link
    const handleOnExit = (err, metadata) => {
        console.log("Error:", err);
        console.log("Metadata:", metadata);
    };

    // Functionality to determine whether a User's link token is expired or not
    const isTokenExpired = (expirationDateTimeString) => {
        const now = new Date();
        const expiration = new Date(expirationDateTimeString);
        return now > expiration;
    };

    // Refresh User's Link Token if Needed to enable them to add additional accounts
    useEffect(() => {
        if (!isTokenRefreshed.current && user?.linkToken && isTokenExpired(user.linkToken.expiration)) {
            console.log("Refreshing user's link token due to link token being expired");
            isTokenRefreshed.current = true; // Set the flag to true to prevent future refreshes
            refreshLinkToken(); // Refresh user's link token
        }
    }, [user, refreshLinkToken]);

    // Fetch All Accounts
    const getAccounts = async () => {
        if (!accounts || accounts.length === 0) {
            console.log("Fetching accounts...");
            setLoading();
            await fetchAccounts();
        }
    };

    // Fetch All Entities from Backend on initial Component Mount
    useEffect(() => {
        console.log(`Component Mounted! Initial Fetch Value: ${initialFetchRef.current}`);
        if (!initialFetchRef.current || !accounts) {
            getAccounts();
            initialFetchRef.current = true;
        }
    }, [accounts]);

    useEffect(() => {
        if(error) { setAlert(error, "danger"); }
    }, [error]);

    return accounts && (
        <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800">
            <FaArrowLeft
                className="text-4xl text-white ml-5 mt-5 hover:scale-110 hover:text-gray-200 cursor-pointer z-[500]"
                onClick={handleBackClick}
            />
            <div className="flex flex-col items-center px-8 md:px-12 h-full">
                <div className="max-w-3xl text-center mb-8 mt-5">
                    <h2 className="text-4xl md:text-5xl font-bold text-white mb-8">Your Accounts</h2>
                </div>
                <PlaidLink
                    key={plaidKey} // Add key prop to force re-render
                    token={user?.linkToken.token}
                    onSuccess={handleOnSuccess}
                    onExit={handleOnExit}
                    className="plaid-link-wrapper-class mb-10"
                >
                    {<button className="bg-indigo-600 text-white rounded-3/4 px-6 py-3 text-xl font-bold rounded-full border-indigo-600 border-2 hover:bg-transparent hover:duration-500">
                        Add Account
                    </button>}
                </PlaidLink>
                <div className="flex flex-col items-center w-full h-3/5 overflow-y-auto scrollbar-hide">
                    {accounts.map((account, index) => (
                        <Account key={index} account={account} handleShowConfirmationModal={handleShowConfirmationModal} />
                    ))}
                </div>
            </div>
            {showConfirmationModal && (
                <div
                    className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-50 flex justify-center items-center">
                    <ConfirmationModal
                        account={accountToDelete}
                        onConfirm={handleConfirm}
                        onClose={handleCloseConfirmationModal}
                        question="Are you sure you want to remove the following account?"
                        accountName={accountToDelete.accountName}
                        confirmText="Yes"
                        cancelText="No"
                    />
                </div>

            )}
        </div>
    );
};

export default AccountsPage;

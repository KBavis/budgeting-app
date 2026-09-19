import React, { useContext, useEffect, useState } from "react";
import { usePlaidLink } from "react-plaid-link";
import { FaLink } from "react-icons/fa";
import accountContext from "../../context/account/accountContext";
import AlertContext from "../../context/alert/alertContext";

/**
 * Opens Plaid Link in "update mode" as soon as it is ready. Rendered only once we have a Link Token.
 */
const PlaidUpdateLauncher = ({ token, onSuccess, onExit }) => {
   const { open, ready } = usePlaidLink({ token, onSuccess, onExit });

   useEffect(() => {
      if (ready) {
         open();
      }
   }, [ready, open]);

   return null;
};

/**
 * Button allowing a User to log in to their financial institution again for an Account that requires it
 * (Plaid reported ITEM_LOGIN_REQUIRED).
 *
 * Flow: request an update mode Link Token for the Account -> User logs in via Plaid Link -> we tell the server the
 * Account was reconnected, which clears its error, so the "needs login" indicators disappear right away.
 *
 * This intentionally does NOT sync: the User syncs manually when they want to. (If the connection is somehow still
 * broken, the next sync flags the Account again.)
 *
 * @param {string} accountId - Account to re-authenticate
 * @param {string} [accountName] - display name used in messages
 * @param {function} [onReconnected] - called with (accountId) once the Account was reconnected
 * @param {string} [className] - styling override
 */
const ReconnectAccountButton = ({ accountId, accountName, onReconnected, className }) => {
   const { getReauthLinkToken, completeReauthentication } = useContext(accountContext);
   const { setAlert } = useContext(AlertContext);

   const [linkToken, setLinkToken] = useState(null);
   const [loading, setLoading] = useState(false);

   const label = accountName || "account";

   const handleClick = async (e) => {
      e.stopPropagation();
      setLoading(true);
      try {
         setLinkToken(await getReauthLinkToken(accountId));
      } catch (err) {
         console.error(err);
         setAlert(
            (err.response && err.response.data && err.response.data.error) ||
               "Unable to start reconnecting this account. Please try again.",
            "danger"
         );
         setLoading(false);
      }
   };

   // Plaid Link succeeded: nothing to exchange in update mode, so just record it (clears the error & indicators)
   const handleSuccess = async () => {
      setLinkToken(null);
      try {
         await completeReauthentication(accountId);
         setAlert(`${label} reconnected! Press Sync Transactions when you're ready to pull in the latest transactions.`, "success");
         if (onReconnected) {
            onReconnected(accountId);
         }
      } catch (err) {
         console.error(err);
         setAlert(
            `You logged in to ${label}, but we could not update its status. Press Sync Transactions to confirm it is working.`,
            "danger"
         );
      } finally {
         setLoading(false);
      }
   };

   const handleExit = (err) => {
      setLinkToken(null);
      setLoading(false);
      if (err) {
         console.error("Plaid Link Exit Error:", err);
         setAlert("Reconnecting was interrupted. Please try again.", "danger");
      }
   };

   return (
      <>
         <button
            type="button"
            onClick={handleClick}
            disabled={loading}
            className={
               className ||
               "inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-bold border transition-all bg-amber-500/10 hover:bg-amber-500/20 text-amber-700 dark:text-amber-300 border-amber-500/30 disabled:opacity-60 disabled:cursor-not-allowed"
            }
            title={`Log in to your bank again to restore syncing for ${label}`}
         >
            <FaLink className="w-3 h-3" />
            <span>{loading ? "Reconnecting..." : "Reconnect"}</span>
         </button>

         {linkToken && <PlaidUpdateLauncher token={linkToken} onSuccess={handleSuccess} onExit={handleExit} />}
      </>
   );
};

export default ReconnectAccountButton;

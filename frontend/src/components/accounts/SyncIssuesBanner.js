import React from "react";
import { FaExclamationTriangle } from "react-icons/fa";
import ReconnectAccountButton from "./ReconnectAccountButton";

const NEEDS_LOGIN_MESSAGE =
   "Your bank needs you to log in again before this account can be synced. Choose Reconnect to log in and restore access.";

/**
 * Builds the list of accounts that need the User's attention:
 *  - the failures reported by the most recent sync (which explain WHY each account failed), plus
 *  - any account the server still has flagged as needing a login (so the notice survives a page reload)
 *
 * @param {Array} failures - failedAccounts returned by the last sync
 * @param {Array} accounts - the User's accounts
 */
export const buildSyncIssues = (failures, accounts) => {
   const issues = [...(failures || [])];
   (accounts || [])
      .filter((account) => account.requiresReauth)
      .forEach((account) => {
         if (!issues.some((issue) => issue.accountId === account.accountId)) {
            issues.push({
               accountId: account.accountId,
               accountName: account.accountName,
               errorCode: account.connectionErrorCode,
               message: NEEDS_LOGIN_MESSAGE,
               requiresReauth: true,
            });
         }
      });
   return issues;
};

/**
 * Persistent notice listing every account that could not be synced, why, and (when a login is needed) a Reconnect button
 *
 * @param {Array} issues - result of buildSyncIssues
 * @param {function} [onReconnected] - called with (accountId) after an account was reconnected
 * @param {function} [onDismiss] - hide the notice (flagged accounts reappear on the next reload)
 */
const SyncIssuesBanner = ({ issues, onReconnected, onDismiss }) => {
   if (!issues || issues.length === 0) {
      return null;
   }

   return (
      <div className="w-full max-w-3xl mx-auto mb-6 text-left rounded-2xl border border-amber-500/40 bg-amber-50/90 dark:bg-amber-500/10 backdrop-blur-md shadow-md p-4">
         <div className="flex items-start justify-between gap-3">
            <div className="flex items-center gap-2 text-amber-800 dark:text-amber-300 font-bold text-sm">
               <FaExclamationTriangle className="w-4 h-4 flex-shrink-0" />
               <span>
                  {issues.length === 1
                     ? "1 account could not be synced"
                     : `${issues.length} accounts could not be synced`}
               </span>
            </div>
            {onDismiss && (
               <button
                  type="button"
                  onClick={onDismiss}
                  className="text-xs font-semibold text-amber-800/70 dark:text-amber-300/70 hover:text-amber-900 dark:hover:text-amber-200"
               >
                  Dismiss
               </button>
            )}
         </div>

         <ul className="mt-3 flex flex-col gap-2.5">
            {issues.map((issue) => (
               <li
                  key={issue.accountId}
                  className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 rounded-xl bg-white/70 dark:bg-slate-900/50 border border-amber-500/20 px-3 py-2.5"
               >
                  <div className="min-w-0">
                     <div className="text-sm font-bold text-slate-900 dark:text-white truncate">
                        {issue.accountName || issue.accountId}
                     </div>
                     <div className="text-xs text-slate-600 dark:text-slate-300">{issue.message}</div>
                     {issue.errorCode && (
                        <div className="text-[10px] mt-0.5 font-mono text-slate-400 dark:text-slate-500">
                           {issue.errorCode}
                        </div>
                     )}
                  </div>
                  {issue.requiresReauth && (
                     <div className="flex-shrink-0">
                        <ReconnectAccountButton
                           accountId={issue.accountId}
                           accountName={issue.accountName}
                           onReconnected={onReconnected}
                        />
                     </div>
                  )}
               </li>
            ))}
         </ul>
      </div>
   );
};

export default SyncIssuesBanner;

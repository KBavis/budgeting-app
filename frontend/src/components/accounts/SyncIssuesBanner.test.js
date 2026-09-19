import "@testing-library/jest-dom";
import React from "react";
import { render, screen } from "@testing-library/react";
import SyncIssuesBanner, { buildSyncIssues } from "./SyncIssuesBanner";

// the real button needs Plaid + several contexts; here we only care about WHEN it is offered
jest.mock("./ReconnectAccountButton", () => ({ accountId }) => (
   <button data-testid={`reconnect-${accountId}`}>Reconnect</button>
));

describe("buildSyncIssues", () => {
   test("returns nothing when there are no failures and no flagged accounts", () => {
      expect(buildSyncIssues([], [{ accountId: "a", requiresReauth: false }])).toEqual([]);
      expect(buildSyncIssues(undefined, undefined)).toEqual([]);
   });

   test("keeps the failures reported by the last sync", () => {
      const failures = [{ accountId: "a", accountName: "Chase", message: "boom", requiresReauth: false }];
      expect(buildSyncIssues(failures, [])).toEqual(failures);
   });

   test("adds accounts the server still flags as needing a login, so the notice survives a page reload", () => {
      const issues = buildSyncIssues(
         [],
         [
            { accountId: "healthy", accountName: "Chase", requiresReauth: false },
            { accountId: "discover", accountName: "Discover", requiresReauth: true, connectionErrorCode: "ITEM_LOGIN_REQUIRED" },
         ]
      );

      expect(issues).toHaveLength(1);
      expect(issues[0]).toMatchObject({
         accountId: "discover",
         accountName: "Discover",
         errorCode: "ITEM_LOGIN_REQUIRED",
         requiresReauth: true,
      });
      expect(issues[0].message).toMatch(/log in again/i);
   });

   test("does not list an account twice when it is both a fresh failure and flagged", () => {
      const failures = [{ accountId: "discover", accountName: "Discover", message: "from sync", requiresReauth: true }];
      const issues = buildSyncIssues(failures, [{ accountId: "discover", requiresReauth: true }]);

      expect(issues).toHaveLength(1);
      expect(issues[0].message).toBe("from sync");
   });
});

describe("SyncIssuesBanner", () => {
   test("renders nothing without issues", () => {
      const { container } = render(<SyncIssuesBanner issues={[]} />);
      expect(container).toBeEmptyDOMElement();
   });

   test("calls out each failed account and why", () => {
      render(
         <SyncIssuesBanner
            issues={[
               { accountId: "a", accountName: "Chase - Credit Card", message: "Plaid was unable to sync this account: boom", errorCode: "INTERNAL_SERVER_ERROR", requiresReauth: false },
               { accountId: "b", accountName: "Discover", message: "Your bank needs you to log in again", errorCode: "ITEM_LOGIN_REQUIRED", requiresReauth: true },
            ]}
         />
      );

      expect(screen.getByText("2 accounts could not be synced")).toBeInTheDocument();
      expect(screen.getByText("Chase - Credit Card")).toBeInTheDocument();
      expect(screen.getByText("Plaid was unable to sync this account: boom")).toBeInTheDocument();
      expect(screen.getByText("Discover")).toBeInTheDocument();
      expect(screen.getByText("ITEM_LOGIN_REQUIRED")).toBeInTheDocument();
   });

   test("only offers Reconnect for accounts that can be fixed by logging in again", () => {
      render(
         <SyncIssuesBanner
            issues={[
               { accountId: "a", accountName: "Chase", message: "boom", requiresReauth: false },
               { accountId: "b", accountName: "Discover", message: "log in", requiresReauth: true },
            ]}
         />
      );

      expect(screen.queryByTestId("reconnect-a")).not.toBeInTheDocument();
      expect(screen.getByTestId("reconnect-b")).toBeInTheDocument();
   });

   test("falls back to the account id when the account name is unknown", () => {
      render(<SyncIssuesBanner issues={[{ accountId: "acct-123", message: "unexpected", requiresReauth: false }]} />);
      expect(screen.getByText("acct-123")).toBeInTheDocument();
      expect(screen.getByText("1 account could not be synced")).toBeInTheDocument();
   });
});

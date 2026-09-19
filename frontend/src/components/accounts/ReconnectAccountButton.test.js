import "@testing-library/jest-dom";
import React from "react";
import { render, screen, waitFor, fireEvent, act } from "@testing-library/react";
import ReconnectAccountButton from "./ReconnectAccountButton";
import accountContext from "../../context/account/accountContext";
import transactionContext from "../../context/transaction/transactionContext";
import AlertContext from "../../context/alert/alertContext";

// Plaid Link itself is not under test; capture how it is launched so the tests can simulate the User finishing/cancelling
const mockOpen = jest.fn();
let mockPlaidOptions;
jest.mock("react-plaid-link", () => ({
   usePlaidLink: (options) => {
      mockPlaidOptions = options;
      return { open: mockOpen, ready: true };
   },
}));

const renderButton = ({
   getReauthLinkToken = jest.fn().mockResolvedValue("update-link-token"),
   completeReauthentication = jest.fn().mockResolvedValue({ accountId: "acct-1", requiresReauth: false }),
   syncTransactions = jest.fn(),
   setAlert = jest.fn(),
   onReconnected = jest.fn(),
} = {}) => {
   render(
      <AlertContext.Provider value={{ setAlert }}>
         <accountContext.Provider value={{ getReauthLinkToken, completeReauthentication }}>
            <transactionContext.Provider value={{ syncTransactions }}>
               <ReconnectAccountButton accountId="acct-1" accountName="Discover" onReconnected={onReconnected} />
            </transactionContext.Provider>
         </accountContext.Provider>
      </AlertContext.Provider>
   );
   return { getReauthLinkToken, completeReauthentication, syncTransactions, setAlert, onReconnected };
};

const clickReconnectAndWaitForPlaidLink = async () => {
   fireEvent.click(screen.getByRole("button", { name: /reconnect/i }));
   await waitFor(() => expect(mockOpen).toHaveBeenCalled());
};

beforeEach(() => {
   mockOpen.mockClear();
   mockPlaidOptions = undefined;
});

describe("ReconnectAccountButton", () => {
   test("requests an update mode Link Token for the account and opens Plaid Link with it", async () => {
      const { getReauthLinkToken } = renderButton();

      await clickReconnectAndWaitForPlaidLink();

      expect(getReauthLinkToken).toHaveBeenCalledWith("acct-1");
      expect(mockPlaidOptions.token).toBe("update-link-token");
      // shows progress and can't be double-clicked while Plaid Link is open
      expect(screen.getByRole("button", { name: /reconnecting/i })).toBeDisabled();
   });

   test("once the User re-authenticates, the account is marked reconnected (indicators clear) and NO sync is started", async () => {
      const { completeReauthentication, syncTransactions, setAlert, onReconnected } = renderButton();
      await clickReconnectAndWaitForPlaidLink();

      await act(async () => {
         await mockPlaidOptions.onSuccess("public-token", {});
      });

      expect(completeReauthentication).toHaveBeenCalledWith("acct-1");
      expect(onReconnected).toHaveBeenCalledWith("acct-1");
      expect(syncTransactions).not.toHaveBeenCalled(); // syncing stays a manual step
      expect(setAlert).toHaveBeenCalledWith(expect.stringMatching(/reconnected.*Sync Transactions/i), "success");
      expect(screen.getByRole("button", { name: "Reconnect" })).toBeEnabled();
   });

   test("cancelling Plaid Link changes nothing", async () => {
      const { completeReauthentication, syncTransactions, onReconnected, setAlert } = renderButton();
      await clickReconnectAndWaitForPlaidLink();

      await act(async () => {
         mockPlaidOptions.onExit(null, {});
      });

      expect(completeReauthentication).not.toHaveBeenCalled();
      expect(syncTransactions).not.toHaveBeenCalled();
      expect(onReconnected).not.toHaveBeenCalled();
      expect(setAlert).not.toHaveBeenCalled();
      expect(screen.getByRole("button", { name: "Reconnect" })).toBeEnabled();
   });

   test("tells the User when the Link Token could not be created, without opening Plaid Link", async () => {
      const getReauthLinkToken = jest.fn().mockRejectedValue({ response: { data: { error: "Unable to locate Account" } } });
      const { setAlert } = renderButton({ getReauthLinkToken });

      fireEvent.click(screen.getByRole("button", { name: /reconnect/i }));

      await waitFor(() => expect(setAlert).toHaveBeenCalledWith("Unable to locate Account", "danger"));
      expect(mockOpen).not.toHaveBeenCalled();
      expect(screen.getByRole("button", { name: "Reconnect" })).toBeEnabled();
   });

   test("if recording the reconnection fails, the User is told and the notice is not dismissed", async () => {
      const completeReauthentication = jest.fn().mockRejectedValue(new Error("network down"));
      const { onReconnected, setAlert } = renderButton({ completeReauthentication });
      await clickReconnectAndWaitForPlaidLink();

      jest.spyOn(console, "error").mockImplementation(() => {});
      await act(async () => {
         await mockPlaidOptions.onSuccess("public-token", {});
      });
      console.error.mockRestore();

      expect(onReconnected).not.toHaveBeenCalled();
      expect(setAlert).toHaveBeenCalledWith(expect.stringMatching(/could not update its status/i), "danger");
      expect(screen.getByRole("button", { name: "Reconnect" })).toBeEnabled();
   });
});

import React, { useState, useEffect, useContext, useCallback } from "react";
import axios from "axios";
import apiUrl from "../utils/url";
import alertContext from "../context/alert/alertContext";
import authContext from "../context/auth/authContext";
import { ThemeContext } from "../context/theme/ThemeContext";
import { FaEnvelope, FaCheck, FaCopy, FaToggleOn, FaToggleOff, FaQuestionCircle, FaChevronDown, FaChevronUp } from "react-icons/fa";

/**
 * Venmo Automation Settings Page
 * 
 * Allows users to enable/disable Venmo email forwarding automation,
 * view their unique forwarding address, and see setup instructions.
 */
const VenmoAutomationPage = () => {
    const { setAlert } = useContext(alertContext);
    const { user } = useContext(authContext);
    const { theme } = useContext(ThemeContext);
    const isDark = theme === "dark";

    const [settings, setSettings] = useState(null);
    const [loading, setLoading] = useState(true);
    const [enabling, setEnabling] = useState(false);
    const [copied, setCopied] = useState(false);
    const [showGmailSteps, setShowGmailSteps] = useState(false);
    const [showOutlookSteps, setShowOutlookSteps] = useState(false);

    const fetchSettings = useCallback(async () => {
        try {
            const res = await axios.get(`${apiUrl}/venmo/automation`);
            if (res.status === 204) {
                setSettings(null);
            } else {
                setSettings(res.data);
            }
        } catch (err) {
            console.error("Failed to fetch Venmo automation settings", err);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchSettings();
    }, [fetchSettings]);

    // Only poll while waiting for the email verification step to complete
    const isWaitingForVerification = settings?.enabled && !settings?.verificationCode && !settings?.verificationLink && !settings?.lastProcessedAt;

    useEffect(() => {
        if (!isWaitingForVerification) return;

        const interval = setInterval(() => {
            fetchSettings();
        }, 5000);

        return () => clearInterval(interval);
    }, [isWaitingForVerification, fetchSettings]);

    const handleEnable = async () => {
        setEnabling(true);
        try {
            const res = await axios.post(`${apiUrl}/venmo/automation/enable`);
            setSettings(res.data);
            setAlert("Venmo automation enabled! Follow the setup instructions below.", "success");
        } catch (err) {
            console.error("Failed to enable Venmo automation", err);
            setAlert("Failed to enable Venmo automation", "danger");
        } finally {
            setEnabling(false);
        }
    };

    const handleDisable = async () => {
        try {
            await axios.delete(`${apiUrl}/venmo/automation/disable`);
            setSettings({ ...settings, enabled: false });
            setAlert("Venmo automation disabled", "success");
        } catch (err) {
            console.error("Failed to disable Venmo automation", err);
            setAlert("Failed to disable Venmo automation", "danger");
        }
    };

    const handleReEnable = async () => {
        try {
            const res = await axios.post(`${apiUrl}/venmo/automation/enable`);
            setSettings(res.data);
            setAlert("Venmo automation enabled!", "success");
        } catch (err) {
            console.error("Failed to enable Venmo automation", err);
            setAlert("Failed to enable Venmo automation", "danger");
        }
    };

    const handleVerify = async () => {
        try {
            const res = await axios.post(`${apiUrl}/venmo/automation/verify`);
            setSettings(res.data);
            setAlert("Venmo automation verified!", "success");
        } catch (err) {
            console.error("Failed to verify Venmo automation", err);
        }
    };

    const copyToClipboard = () => {
        if (settings?.ingestEmail) {
            navigator.clipboard.writeText(settings.ingestEmail);
            setCopied(true);
            setTimeout(() => setCopied(false), 2000);
        }
    };

    if (loading) {
        return (
            <div className={`flex flex-col min-h-screen items-center justify-center ${
                isDark
                    ? "bg-gradient-to-br from-gray-900 via-slate-900 to-indigo-950 text-slate-100"
                    : "bg-gradient-to-br from-slate-100 via-indigo-50/50 to-slate-100 text-slate-800"
            }`}>
                <div className="animate-pulse flex flex-col items-center gap-3">
                    <div className={`w-12 h-12 rounded-full ${isDark ? "bg-slate-700" : "bg-slate-200"}`} />
                    <div className={`h-4 w-40 rounded ${isDark ? "bg-slate-700" : "bg-slate-200"}`} />
                </div>
            </div>
        );
    }

    return (
        <div className={`flex flex-col min-h-screen ${
            isDark
                ? "bg-gradient-to-br from-gray-900 via-slate-900 to-indigo-950 text-slate-100"
                : "bg-gradient-to-br from-slate-100 via-indigo-50/50 to-slate-100 text-slate-800"
        }`}>
            <div className="flex flex-col items-center px-4 md:px-12 h-full pt-16">
                {/* Header */}
                <div className="max-w-xl w-full text-center mb-6 mt-5">
                    <h2 className={`text-4xl md:text-5xl font-black mb-2 ${isDark ? "text-white" : "text-slate-900"}`}>
                        Venmo Automation
                    </h2>
                    <p className={`text-sm ${isDark ? "text-slate-400" : "text-slate-500"}`}>
                        Automatically enrich Venmo transaction descriptions
                    </p>
                </div>

                {/* Main Content */}
                <div className="w-full max-w-xl flex flex-col gap-5 pb-16">

                    {/* Status Card */}
                    {!settings ? (
                        /* Not yet enabled — onboarding card */
                        <div className={`border rounded-3xl p-6 shadow-xl backdrop-blur-sm ${
                            isDark
                                ? "bg-slate-800/80 border-slate-700/60"
                                : "bg-white/90 border-slate-200 shadow-slate-200/50"
                        }`}>
                            {/* Venmo Problem Explainer */}
                            <div className="flex items-start gap-3 mb-5">
                                <div className={`p-2.5 rounded-xl flex-shrink-0 ${
                                    isDark ? "bg-blue-500/15 text-blue-400" : "bg-blue-50 text-blue-600"
                                }`}>
                                    <FaEnvelope className="w-5 h-5" />
                                </div>
                                <div>
                                    <h3 className={`text-lg font-bold mb-1 ${isDark ? "text-white" : "text-slate-900"}`}>
                                        Why is this needed?
                                    </h3>
                                    <p className={`text-sm leading-relaxed ${isDark ? "text-slate-400" : "text-slate-600"}`}>
                                        When Plaid syncs your Venmo transactions, they all appear as just
                                        <span className={`font-mono font-bold mx-1 px-1.5 py-0.5 rounded ${
                                            isDark ? "bg-slate-700 text-amber-400" : "bg-amber-50 text-amber-700 border border-amber-200"
                                        }`}>"Venmo"</span>
                                        with no description of what the payment was actually for.
                                    </p>
                                    <p className={`text-sm leading-relaxed mt-2 ${isDark ? "text-slate-400" : "text-slate-600"}`}>
                                        By forwarding Venmo notification emails to your unique address below,
                                        we can automatically extract the description and counterparty name,
                                        turning <span className="font-semibold">"Venmo"</span> into something like
                                        <span className={`font-mono font-bold mx-1 px-1.5 py-0.5 rounded ${
                                            isDark ? "bg-slate-700 text-emerald-400" : "bg-emerald-50 text-emerald-700 border border-emerald-200"
                                        }`}>"Rent split (John)"</span>.
                                    </p>
                                </div>
                            </div>

                            {/* Enable Button */}
                            <button
                                onClick={handleEnable}
                                disabled={enabling}
                                className="w-full flex items-center justify-center gap-2.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-2xl px-6 py-3.5 text-sm font-extrabold transition-all duration-300 shadow-lg shadow-indigo-500/25 hover:shadow-indigo-500/40 hover:scale-[1.01] active:scale-[0.99] disabled:opacity-60 disabled:cursor-not-allowed"
                            >
                                {enabling ? (
                                    <>
                                        <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                                        <span>Setting up...</span>
                                    </>
                                ) : (
                                    <>
                                        <FaEnvelope className="w-3.5 h-3.5" />
                                        <span>Enable Venmo Automation</span>
                                    </>
                                )}
                            </button>
                        </div>
                    ) : (
                        <>
                            {/* Enabled & Verified Status Card */}
                            <div className={`border rounded-3xl p-6 shadow-xl backdrop-blur-sm ${
                                settings.enabled
                                    ? isDark
                                        ? "bg-emerald-950/30 border-emerald-800/40"
                                        : "bg-emerald-50/70 border-emerald-200/80"
                                    : isDark
                                        ? "bg-slate-800/80 border-slate-700/60"
                                        : "bg-white/90 border-slate-200"
                            }`}>
                                <div className="flex items-center justify-between mb-4">
                                    <div className="flex items-center gap-3">
                                        <div className={`w-10 h-10 rounded-2xl flex items-center justify-center ${
                                            settings.enabled && settings.verified
                                                ? "bg-emerald-500/20 text-emerald-400"
                                                : settings.enabled
                                                    ? "bg-amber-500/20 text-amber-400"
                                                    : "bg-slate-700 text-slate-400"
                                        }`}>
                                            <FaCheck className="w-5 h-5" />
                                        </div>
                                        <div>
                                            <div className="flex items-center gap-2">
                                                <span className={`text-base font-extrabold ${
                                                    settings.enabled
                                                        ? isDark ? "text-emerald-300" : "text-emerald-700"
                                                        : isDark ? "text-slate-400" : "text-slate-600"
                                                }`}>
                                                    {settings.enabled
                                                        ? settings.verified
                                                            ? "Verified & Connected"
                                                            : "Forwarding Configured"
                                                        : "Automation Disabled"}
                                                </span>
                                            </div>
                                            <p className={`text-xs opacity-75 ${isDark ? "text-slate-400" : "text-slate-600"}`}>
                                                {settings.enabled
                                                    ? settings.verified
                                                        ? "Venmo emails are automatically parsed and enriched."
                                                        : "Complete forwarding setup in Gmail to start automatic enrichment."
                                                    : "Re-enable automation at any time to resume enrichment."}
                                            </p>
                                        </div>
                                    </div>

                                    {/* Toggle Button */}
                                    <button
                                        onClick={settings.enabled ? handleDisable : handleReEnable}
                                        className={`flex items-center gap-1.5 px-3.5 py-2 rounded-xl text-xs font-extrabold transition-all ${
                                            settings.enabled
                                                ? isDark
                                                    ? "text-slate-400 hover:text-rose-400 bg-slate-800 hover:bg-rose-950/50"
                                                    : "text-slate-600 hover:text-rose-600 bg-slate-100 hover:bg-rose-50"
                                                : "text-white bg-indigo-600 hover:bg-indigo-500 shadow-md"
                                        }`}
                                    >
                                        {settings.enabled ? (
                                            <><FaToggleOn className="w-4 h-4" /> Disable</>
                                        ) : (
                                            <><FaToggleOff className="w-4 h-4" /> Enable</>
                                        )}
                                    </button>
                                </div>

                                {/* Stats Row (only shown when enabled) */}
                                {settings.enabled && (
                                    <div className="grid grid-cols-2 gap-3">
                                        <div className={`p-3 rounded-xl ${
                                            isDark ? "bg-slate-800/80" : "bg-white/80 border border-slate-100"
                                        }`}>
                                            <p className={`text-[10px] font-bold uppercase tracking-wider mb-0.5 ${
                                                isDark ? "text-slate-500" : "text-slate-400"
                                            }`}>Enriched</p>
                                            <p className={`text-xl font-black ${isDark ? "text-white" : "text-slate-900"}`}>
                                                {settings.enrichedCount}
                                            </p>
                                        </div>
                                        <div className={`p-3 rounded-xl ${
                                            isDark ? "bg-slate-800/80" : "bg-white/80 border border-slate-100"
                                        }`}>
                                            <p className={`text-[10px] font-bold uppercase tracking-wider mb-0.5 ${
                                                isDark ? "text-slate-500" : "text-slate-400"
                                            }`}>Last Processed</p>
                                            <p className={`text-sm font-bold ${isDark ? "text-white" : "text-slate-900"}`}>
                                                {settings.lastProcessedAt
                                                    ? new Date(settings.lastProcessedAt).toLocaleDateString()
                                                    : "Never"}
                                            </p>
                                        </div>
                                    </div>
                                )}
                            </div>

                            {/* Forwarding Address Card (Shown only during onboarding) */}
                            {settings.enabled && !settings.verified && (
                                <div className={`border rounded-3xl p-5 shadow-xl backdrop-blur-sm ${
                                    isDark
                                        ? "bg-slate-800/80 border-slate-700/60"
                                        : "bg-white/90 border-slate-200 shadow-slate-200/50"
                                }`}>
                                    <h3 className={`text-xs font-extrabold uppercase tracking-widest mb-3 ${
                                        isDark ? "text-slate-400" : "text-slate-500"
                                    }`}>
                                        Your Forwarding Address
                                    </h3>

                                    {/* Email Address Display */}
                                    <div className={`flex items-center gap-2 p-3 rounded-xl mb-4 ${
                                        isDark ? "bg-slate-900 border border-slate-700" : "bg-slate-50 border border-slate-200"
                                    }`}>
                                        <FaEnvelope className={`w-3.5 h-3.5 flex-shrink-0 ${
                                            isDark ? "text-indigo-400" : "text-indigo-600"
                                        }`} />
                                        <code className={`text-sm font-mono font-bold flex-1 truncate ${
                                            isDark ? "text-indigo-300" : "text-indigo-700"
                                        }`}>
                                            {settings.ingestEmail}
                                        </code>
                                        <button
                                            onClick={copyToClipboard}
                                            className={`flex items-center gap-1 px-2.5 py-1.5 rounded-lg text-xs font-bold transition-all ${
                                                copied
                                                    ? "bg-emerald-500/20 text-emerald-400"
                                                    : isDark
                                                        ? "bg-slate-700 hover:bg-slate-600 text-slate-300"
                                                        : "bg-slate-200 hover:bg-slate-300 text-slate-700"
                                            }`}
                                        >
                                            {copied ? <><FaCheck className="w-2.5 h-2.5" /> Copied!</> : <><FaCopy className="w-2.5 h-2.5" /> Copy</>}
                                        </button>
                                    </div>

                                    <p className={`text-xs leading-relaxed ${isDark ? "text-slate-500" : "text-slate-400"}`}>
                                        This is your unique, private forwarding address. Set up a filter in your email
                                        to forward Venmo payment notifications to this address.
                                    </p>
                                </div>
                            )}

                            {/* Gmail Verification Banner (Shown only when NOT verified and link received) */}
                            {settings.enabled && !settings.verified && settings.verificationLink && (
                                <div className={`border rounded-3xl p-5 shadow-xl backdrop-blur-sm ${
                                    isDark
                                        ? "bg-amber-950/40 border-amber-500/50 text-amber-200"
                                        : "bg-amber-50 border-amber-300 text-amber-900"
                                }`}>
                                    <div className="flex items-center gap-2 mb-2">
                                        <span className="text-lg">📧</span>
                                        <h3 className="text-sm font-extrabold uppercase tracking-wide">
                                            Gmail Verification Email Received!
                                        </h3>
                                    </div>
                                    <p className="text-xs leading-relaxed mb-4 opacity-90">
                                        Gmail sent a confirmation email to your forwarding address.
                                        Click the button below to confirm forwarding in Google:
                                    </p>

                                    <a
                                        href={settings.verificationLink}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                        onClick={handleVerify}
                                        className="w-full flex items-center justify-center gap-2 bg-gradient-to-r from-amber-500 to-orange-500 text-slate-950 font-bold text-xs py-3 px-4 rounded-xl hover:brightness-110 transition-all shadow-md"
                                    >
                                        Confirm Forwarding in Gmail ↗
                                    </a>
                                </div>
                            )}

                            {/* Setup Instructions (Shown only during onboarding) */}
                            {settings.enabled && !settings.verified && (
                                <div className={`border rounded-3xl p-5 shadow-xl backdrop-blur-sm ${
                                    isDark
                                        ? "bg-slate-800/80 border-slate-700/60"
                                        : "bg-white/90 border-slate-200 shadow-slate-200/50"
                                }`}>
                                    <div className="flex items-center gap-2 mb-4">
                                        <FaQuestionCircle className={`w-3.5 h-3.5 ${isDark ? "text-indigo-400" : "text-indigo-600"}`} />
                                        <h3 className={`text-xs font-extrabold uppercase tracking-widest ${
                                            isDark ? "text-slate-400" : "text-slate-500"
                                        }`}>
                                            Setup Instructions
                                        </h3>
                                    </div>

                                    {/* Gmail Instructions */}
                                    <button
                                        onClick={() => setShowGmailSteps(!showGmailSteps)}
                                        className={`w-full flex items-center justify-between p-3 rounded-xl mb-2 transition-colors ${
                                            isDark
                                                ? "bg-slate-900/80 hover:bg-slate-900 border border-slate-700"
                                                : "bg-slate-50 hover:bg-slate-100 border border-slate-200"
                                        }`}
                                    >
                                        <span className={`text-sm font-bold ${isDark ? "text-white" : "text-slate-900"}`}>
                                            📧 Gmail Setup
                                        </span>
                                        {showGmailSteps ? <FaChevronUp className="w-3 h-3" /> : <FaChevronDown className="w-3 h-3" />}
                                    </button>
                                    {showGmailSteps && (
                                        <div className={`p-4 rounded-xl mb-3 ${
                                            isDark ? "bg-slate-900/60 border border-slate-700/50" : "bg-slate-50/80 border border-slate-200"
                                        }`}>
                                            <ol className={`text-sm space-y-2.5 list-decimal list-inside ${
                                                isDark ? "text-slate-300" : "text-slate-700"
                                            }`}>
                                                <li>Open <span className="font-bold">Gmail Settings</span> → <span className="font-bold">Forwarding and POP/IMAP</span></li>
                                                <li>Click <span className="font-bold">"Add a forwarding address"</span> and paste your unique address above</li>
                                                <li>Confirm the forwarding address via the verification email</li>
                                                <li>Go to <span className="font-bold">Settings</span> → <span className="font-bold">Filters and Blocked Addresses</span></li>
                                                <li>Click <span className="font-bold">"Create a new filter"</span></li>
                                                <li>Set <span className="font-bold">From:</span> to <code className={`px-1 py-0.5 rounded text-xs ${
                                                    isDark ? "bg-slate-800 text-indigo-300" : "bg-indigo-50 text-indigo-700"
                                                }`}>venmo@venmo.com</code></li>
                                                <li>Set <span className="font-bold">Subject:</span> to <code className={`px-1 py-0.5 rounded text-xs ${
                                                    isDark ? "bg-slate-800 text-indigo-300" : "bg-indigo-50 text-indigo-700"
                                                }`}>You paid</code></li>
                                                <li>Click <span className="font-bold">"Create filter"</span> → check <span className="font-bold">"Forward it to"</span> your unique address</li>
                                                <li>Click <span className="font-bold">"Create filter"</span> to save</li>
                                            </ol>
                                        </div>
                                    )}

                                    {/* Outlook Instructions */}
                                    <button
                                        onClick={() => setShowOutlookSteps(!showOutlookSteps)}
                                        className={`w-full flex items-center justify-between p-3 rounded-xl mb-2 transition-colors ${
                                            isDark
                                                ? "bg-slate-900/80 hover:bg-slate-900 border border-slate-700"
                                                : "bg-slate-50 hover:bg-slate-100 border border-slate-200"
                                        }`}
                                    >
                                        <span className={`text-sm font-bold ${isDark ? "text-white" : "text-slate-900"}`}>
                                            📬 Outlook / Hotmail Setup
                                        </span>
                                        {showOutlookSteps ? <FaChevronUp className="w-3 h-3" /> : <FaChevronDown className="w-3 h-3" />}
                                    </button>
                                    {showOutlookSteps && (
                                        <div className={`p-4 rounded-xl mb-3 ${
                                            isDark ? "bg-slate-900/60 border border-slate-700/50" : "bg-slate-50/80 border border-slate-200"
                                        }`}>
                                            <ol className={`text-sm space-y-2.5 list-decimal list-inside ${
                                                isDark ? "text-slate-300" : "text-slate-700"
                                            }`}>
                                                <li>Go to <span className="font-bold">Settings</span> → <span className="font-bold">Mail</span> → <span className="font-bold">Rules</span></li>
                                                <li>Click <span className="font-bold">"Add new rule"</span></li>
                                                <li>Name it <span className="font-bold">"Venmo Automation"</span></li>
                                                <li>Add condition: <span className="font-bold">From</span> → <code className={`px-1 py-0.5 rounded text-xs ${
                                                    isDark ? "bg-slate-800 text-indigo-300" : "bg-indigo-50 text-indigo-700"
                                                }`}>venmo@venmo.com</code></li>
                                                <li>Add condition: <span className="font-bold">Subject contains</span> → <code className={`px-1 py-0.5 rounded text-xs ${
                                                    isDark ? "bg-slate-800 text-indigo-300" : "bg-indigo-50 text-indigo-700"
                                                }`}>You paid</code></li>
                                                <li>Add action: <span className="font-bold">Forward to</span> → paste your unique address</li>
                                                <li>Click <span className="font-bold">Save</span></li>
                                            </ol>
                                        </div>
                                    )}

                                    {/* Security Note */}
                                    <div className={`flex items-start gap-2 mt-3 p-3 rounded-xl ${
                                        isDark ? "bg-amber-950/20 border border-amber-800/30" : "bg-amber-50/80 border border-amber-200/80"
                                    }`}>
                                        <span className="text-sm flex-shrink-0 mt-0.5">🔒</span>
                                        <p className={`text-xs leading-relaxed ${isDark ? "text-amber-300/80" : "text-amber-800"}`}>
                                            Your forwarding address contains a unique token that identifies your account.
                                            Only emails from <code className="font-mono font-bold">@venmo.com</code> are processed —
                                            all other emails are silently ignored. Don't share this address publicly.
                                        </p>
                                    </div>
                                </div>
                            )}
                        </>
                    )}
                </div>
            </div>
        </div>
    );
};

export default VenmoAutomationPage;

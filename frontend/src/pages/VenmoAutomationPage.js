import React, { useState, useEffect, useContext, useCallback } from "react";
import axios from "axios";
import apiUrl from "../utils/url";
import alertContext from "../context/alert/alertContext";
import authContext from "../context/auth/authContext";
import { ThemeContext } from "../context/theme/ThemeContext";
import { FaEnvelope, FaCheck, FaCopy, FaToggleOn, FaToggleOff, FaArrowRight, FaClock, FaCheckCircle, FaSlidersH, FaExternalLinkAlt } from "react-icons/fa";

/**
 * Venmo Automation Settings Page
 * 
 * Implements a two-phase onboarding workflow:
 * Phase 1: Forwarding address verification (required for Gmail; auto-skipped for others)
 * Phase 2: Email filter rule creation for subject "You paid"
 */
const VenmoAutomationPage = () => {
    const { setAlert } = useContext(alertContext);
    const { theme } = useContext(ThemeContext);
    const isDark = theme === "dark";

    const [settings, setSettings] = useState(null);
    const [loading, setLoading] = useState(true);
    const [enabling, setEnabling] = useState(false);
    const [completingPhase2, setCompletingPhase2] = useState(false);
    const [copied, setCopied] = useState(false);
    const [selectedProvider, setSelectedProvider] = useState("GMAIL");

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

    // Poll while waiting for Gmail forwarding verification link if in FORWARDING_VERIFICATION phase
    const isWaitingForVerification = settings?.enabled && 
        settings?.setupPhase === "FORWARDING_VERIFICATION" && 
        !settings?.verificationLink;

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
            const res = await axios.post(`${apiUrl}/venmo/automation/enable`, null, {
                params: { emailProvider: selectedProvider }
            });
            setSettings(res.data);
            setAlert("Venmo automation initialized! Follow the setup steps below.", "success");
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
            const res = await axios.post(`${apiUrl}/venmo/automation/enable`, null, {
                params: { emailProvider: settings?.emailProvider || "GMAIL" }
            });
            setSettings(res.data);
            setAlert("Venmo automation re-enabled!", "success");
        } catch (err) {
            console.error("Failed to enable Venmo automation", err);
            setAlert("Failed to enable Venmo automation", "danger");
        }
    };

    const handleVerifyGmail = async () => {
        try {
            const res = await axios.post(`${apiUrl}/venmo/automation/complete-forwarding-verification`);
            setSettings(res.data);
            setAlert("Gmail forwarding verified! Proceed to Step 2: Filter setup.", "success");
        } catch (err) {
            console.error("Failed to verify Venmo automation", err);
        }
    };

    const handleCompleteFilterSetup = async () => {
        setCompletingPhase2(true);
        try {
            const res = await axios.post(`${apiUrl}/venmo/automation/complete-filter-setup`);
            setSettings(res.data);
            setAlert("Setup complete! Venmo automation is now fully connected.", "success");
        } catch (err) {
            console.error("Failed to complete filter setup", err);
            setAlert("Failed to update setup status", "danger");
        } finally {
            setCompletingPhase2(false);
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

    const currentPhase = settings?.setupPhase || "FORWARDING_VERIFICATION";
    const isComplete = currentPhase === "COMPLETE" && settings?.enabled;
    const provider = settings?.emailProvider || selectedProvider;

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
                        Automatically extract payment notes and counterparty names
                    </p>
                </div>

                {/* Main Content Container */}
                <div className="w-full max-w-xl flex flex-col gap-5 pb-16">

                    {/* Case 1: Automation Not Configured Yet */}
                    {!settings ? (
                        <div className={`border rounded-3xl p-6 shadow-xl backdrop-blur-sm ${
                            isDark
                                ? "bg-slate-800/80 border-slate-700/60"
                                : "bg-white/90 border-slate-200 shadow-slate-200/50"
                        }`}>
                            {/* Explainer */}
                            <div className="flex items-start gap-3 mb-5">
                                <div className={`p-2.5 rounded-xl flex-shrink-0 ${
                                    isDark ? "bg-blue-500/15 text-blue-400" : "bg-blue-50 text-blue-600"
                                }`}>
                                    <FaEnvelope className="w-5 h-5" />
                                </div>
                                <div>
                                    <h3 className={`text-lg font-bold mb-1 ${isDark ? "text-white" : "text-slate-900"}`}>
                                        Why setup Venmo Automation?
                                    </h3>
                                    <p className={`text-sm leading-relaxed ${isDark ? "text-slate-400" : "text-slate-600"}`}>
                                        Venmo transaction syncs from Plaid show up as uninformative
                                        <span className={`font-mono font-bold mx-1 px-1.5 py-0.5 rounded ${
                                            isDark ? "bg-slate-700 text-amber-400" : "bg-amber-50 text-amber-700 border border-amber-200"
                                        }`}>"Venmo"</span> items.
                                    </p>
                                    <p className={`text-sm leading-relaxed mt-2 ${isDark ? "text-slate-400" : "text-slate-600"}`}>
                                        By setting up email forwarding, transaction descriptions like
                                        <span className={`font-mono font-bold mx-1 px-1.5 py-0.5 rounded ${
                                            isDark ? "bg-slate-700 text-emerald-400" : "bg-emerald-50 text-emerald-700 border border-emerald-200"
                                        }`}>"Dinner split (Alex)"</span> are automatically parsed and enriched.
                                    </p>
                                </div>
                            </div>

                            {/* Email Provider Selector */}
                            <div className="mb-6">
                                <label className={`block text-xs font-extrabold uppercase tracking-wider mb-2 ${
                                    isDark ? "text-slate-400" : "text-slate-600"
                                }`}>
                                    Select Your Email Provider
                                </label>
                                <div className="grid grid-cols-2 gap-2.5">
                                    {[
                                        { id: "GMAIL", name: "Gmail", icon: "📧" },
                                        { id: "OUTLOOK", name: "Outlook / Hotmail", icon: "📬" },
                                        { id: "YAHOO", name: "Yahoo Mail", icon: "✉️" },
                                        { id: "OTHER", name: "Other Email", icon: "🌐" }
                                    ].map((prov) => (
                                        <button
                                            key={prov.id}
                                            type="button"
                                            onClick={() => setSelectedProvider(prov.id)}
                                            className={`flex items-center gap-2 p-3 rounded-2xl border text-xs font-bold transition-all ${
                                                selectedProvider === prov.id
                                                    ? isDark
                                                        ? "bg-indigo-600/30 border-indigo-500 text-white shadow-lg shadow-indigo-500/20"
                                                        : "bg-indigo-50 border-indigo-500 text-indigo-900 shadow-sm"
                                                    : isDark
                                                        ? "bg-slate-900/50 border-slate-700 text-slate-400 hover:border-slate-600"
                                                        : "bg-slate-50 border-slate-200 text-slate-600 hover:border-slate-300"
                                            }`}
                                        >
                                            <span className="text-base">{prov.icon}</span>
                                            <span>{prov.name}</span>
                                        </button>
                                    ))}
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
                                        <span>Initialising...</span>
                                    </>
                                ) : (
                                    <>
                                        <FaEnvelope className="w-3.5 h-3.5" />
                                        <span>Start Venmo Setup</span>
                                    </>
                                )}
                            </button>
                        </div>
                    ) : (
                        <>
                            {/* Enabled Status Header Card */}
                            <div className={`border rounded-3xl p-6 shadow-xl backdrop-blur-sm ${
                                settings.enabled
                                    ? isComplete
                                        ? isDark ? "bg-emerald-950/30 border-emerald-800/40" : "bg-emerald-50/70 border-emerald-200/80"
                                        : isDark ? "bg-indigo-950/30 border-indigo-800/40" : "bg-indigo-50/70 border-indigo-200/80"
                                    : isDark ? "bg-slate-800/80 border-slate-700/60" : "bg-white/90 border-slate-200"
                            }`}>
                                <div className="flex items-center justify-between mb-4">
                                    <div className="flex items-center gap-3">
                                        <div className={`w-10 h-10 rounded-2xl flex items-center justify-center ${
                                            settings.enabled && isComplete
                                                ? "bg-emerald-500/20 text-emerald-400"
                                                : settings.enabled
                                                    ? "bg-indigo-500/20 text-indigo-400"
                                                    : "bg-slate-700 text-slate-400"
                                        }`}>
                                            {isComplete ? <FaCheck className="w-5 h-5" /> : <FaSlidersH className="w-4 h-4" />}
                                        </div>
                                        <div>
                                            <span className={`text-base font-extrabold block ${
                                                settings.enabled
                                                    ? isComplete
                                                        ? isDark ? "text-emerald-300" : "text-emerald-700"
                                                        : isDark ? "text-indigo-300" : "text-indigo-700"
                                                    : isDark ? "text-slate-400" : "text-slate-600"
                                            }`}>
                                                {settings.enabled
                                                    ? isComplete
                                                        ? "Verified & Fully Connected"
                                                        : `Setup In Progress (${currentPhase === "FORWARDING_VERIFICATION" ? "Phase 1" : "Phase 2"})`
                                                    : "Automation Disabled"}
                                            </span>
                                            <p className={`text-xs opacity-75 ${isDark ? "text-slate-400" : "text-slate-600"}`}>
                                                {settings.enabled
                                                    ? isComplete
                                                        ? "Venmo payment emails are parsed and enriched automatically."
                                                        : "Complete the setup steps below to finish setup."
                                                    : "Re-enable automation at any time to resume enrichment."}
                                            </p>
                                        </div>
                                    </div>

                                    {/* Enable / Disable Toggle */}
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

                                {/* Operational Stats (Always visible when enabled) */}
                                {settings.enabled && (
                                    <div className="grid grid-cols-2 gap-3 mt-4">
                                        <div className={`p-3 rounded-xl ${
                                            isDark ? "bg-slate-800/80" : "bg-white/80 border border-slate-100"
                                        }`}>
                                            <p className={`text-[10px] font-bold uppercase tracking-wider mb-0.5 ${
                                                isDark ? "text-slate-500" : "text-slate-400"
                                            }`}>Enriched Count</p>
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

                            {/* TWO-PHASE ONBOARDING FLOW (Hidden once setup phase is COMPLETE) */}
                            {settings.enabled && !isComplete && (
                                <div className={`border rounded-3xl p-6 shadow-xl backdrop-blur-sm ${
                                    isDark ? "bg-slate-800/80 border-slate-700/60" : "bg-white/90 border-slate-200"
                                }`}>

                                    {/* Forwarding Address Box */}
                                    <div className="mb-6">
                                        <label className={`block text-xs font-extrabold uppercase tracking-widest mb-2 ${
                                            isDark ? "text-slate-400" : "text-slate-500"
                                        }`}>
                                            Your Unique Forwarding Address
                                        </label>
                                        <div className={`flex items-center gap-2 p-3 rounded-xl ${
                                            isDark ? "bg-slate-900 border border-slate-700" : "bg-slate-50 border border-slate-200"
                                        }`}>
                                            <FaEnvelope className={`w-3.5 h-3.5 flex-shrink-0 ${
                                                isDark ? "text-indigo-400" : "text-indigo-600"
                                            }`} />
                                            <code className={`text-xs md:text-sm font-mono font-bold flex-1 truncate ${
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
                                    </div>

                                    {/* Setup Stepper Header */}
                                    <div className="flex items-center justify-between border-b pb-4 mb-5 border-slate-700/40">
                                        <h3 className={`text-sm font-black uppercase tracking-wider ${
                                            isDark ? "text-white" : "text-slate-900"
                                        }`}>
                                            Setup Progress
                                        </h3>
                                        <span className={`text-xs font-bold px-2.5 py-1 rounded-full ${
                                            isDark ? "bg-indigo-900/50 text-indigo-300" : "bg-indigo-100 text-indigo-700"
                                        }`}>
                                            {provider === "GMAIL" ? "Gmail Workflow" : "Standard Email Workflow"}
                                        </span>
                                    </div>

                                    {/* STEP 1: Forwarding Address Verification */}
                                    <div className="mb-6">
                                        <div className="flex items-start gap-3">
                                            <div className={`w-7 h-7 rounded-full flex items-center justify-center font-bold text-xs flex-shrink-0 ${
                                                currentPhase !== "FORWARDING_VERIFICATION"
                                                    ? "bg-emerald-500 text-white"
                                                    : "bg-indigo-600 text-white ring-4 ring-indigo-500/20"
                                            }`}>
                                                {currentPhase !== "FORWARDING_VERIFICATION" ? <FaCheck className="w-3 h-3" /> : "1"}
                                            </div>
                                            <div className="flex-1">
                                                <h4 className={`text-sm font-bold ${
                                                    currentPhase !== "FORWARDING_VERIFICATION"
                                                        ? isDark ? "text-slate-300" : "text-slate-700"
                                                        : isDark ? "text-white" : "text-slate-900"
                                                }`}>
                                                    Phase 1: Verify Forwarding Address
                                                </h4>
                                                <p className={`text-xs mt-1 ${isDark ? "text-slate-400" : "text-slate-600"}`}>
                                                    {provider === "GMAIL" 
                                                        ? "Gmail requires adding and confirming the forwarding address in Gmail Settings -> Forwarding."
                                                        : "Non-Gmail providers do not require address verification. You can proceed to Phase 2."}
                                                </p>

                                                {/* Gmail Verification Banner */}
                                                {provider === "GMAIL" && currentPhase === "FORWARDING_VERIFICATION" && (
                                                    <div className="mt-3">
                                                        {settings.verificationLink ? (
                                                            <div className={`p-4 rounded-2xl border ${
                                                                isDark ? "bg-amber-950/40 border-amber-500/50 text-amber-200" : "bg-amber-50 border-amber-300 text-amber-900"
                                                            }`}>
                                                                <p className="text-xs font-bold mb-2">
                                                                    📩 Gmail Confirmation Link Received!
                                                                </p>
                                                                <p className="text-xs opacity-90 mb-3">
                                                                    Click the link below to confirm the forwarding address with Google:
                                                                </p>
                                                                <a
                                                                    href={settings.verificationLink}
                                                                    target="_blank"
                                                                    rel="noopener noreferrer"
                                                                    onClick={handleVerifyGmail}
                                                                    className="inline-flex items-center gap-1.5 px-4 py-2 bg-gradient-to-r from-amber-500 to-orange-500 text-slate-950 font-extrabold text-xs rounded-xl hover:brightness-110 transition-all shadow"
                                                                >
                                                                    <span>Confirm Forwarding in Gmail</span>
                                                                    <FaExternalLinkAlt className="w-2.5 h-2.5" />
                                                                </a>
                                                            </div>
                                                        ) : (
                                                            <div className={`flex items-center gap-2 p-3 rounded-xl border ${
                                                                isDark ? "bg-slate-900/60 border-slate-700 text-slate-400" : "bg-slate-50 border-slate-200 text-slate-600"
                                                            }`}>
                                                                <FaClock className="w-3.5 h-3.5 animate-spin text-amber-400" />
                                                                <span className="text-xs">
                                                                    Add the forwarding address in Gmail settings. Waiting for Google's confirmation email...
                                                                </span>
                                                            </div>
                                                        )}
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </div>

                                    {/* STEP 2: Filter & Rule Setup */}
                                    <div className="mb-6">
                                        <div className="flex items-start gap-3">
                                            <div className={`w-7 h-7 rounded-full flex items-center justify-center font-bold text-xs flex-shrink-0 ${
                                                currentPhase === "FILTER_SETUP"
                                                    ? "bg-indigo-600 text-white ring-4 ring-indigo-500/20"
                                                    : "bg-slate-700 text-slate-400"
                                            }`}>
                                                "2"
                                            </div>
                                            <div className="flex-1">
                                                <h4 className={`text-sm font-bold ${
                                                    currentPhase === "FILTER_SETUP"
                                                        ? isDark ? "text-white" : "text-slate-900"
                                                        : isDark ? "text-slate-400" : "text-slate-500"
                                                }`}>
                                                    Phase 2: Set Up Forwarding Filter
                                                </h4>
                                                <p className={`text-xs mt-1 ${isDark ? "text-slate-400" : "text-slate-600"}`}>
                                                    Create an email filter / rule in your inbox to forward all Venmo notification emails.
                                                </p>

                                                {/* Filter Instructions Card (Active when in FILTER_SETUP phase) */}
                                                {currentPhase === "FILTER_SETUP" && (
                                                    <div className={`mt-3 p-4 rounded-2xl border ${
                                                        isDark ? "bg-slate-900/80 border-slate-700" : "bg-slate-50 border-slate-200"
                                                    }`}>
                                                        <div className="text-xs space-y-2 mb-4">
                                                            <div className="flex items-center gap-2">
                                                                <span className="font-bold w-16">1. From:</span>
                                                                <code className={`px-2 py-0.5 rounded font-mono ${
                                                                    isDark ? "bg-slate-800 text-indigo-300" : "bg-indigo-50 text-indigo-700"
                                                                }`}>venmo@venmo.com</code>
                                                            </div>
                                                            <div className="flex items-center gap-2">
                                                                <span className="font-bold w-16">2. Subject:</span>
                                                                <code className={`px-2 py-0.5 rounded font-mono ${
                                                                    isDark ? "bg-slate-800 text-indigo-300" : "bg-indigo-50 text-indigo-700"
                                                                }`}>You paid</code>
                                                            </div>
                                                            <div className="flex items-center gap-2">
                                                                <span className="font-bold w-16">3. Action:</span>
                                                                <span>Forward to unique address above</span>
                                                            </div>
                                                        </div>

                                                        {/* Complete Filter Setup Action Button */}
                                                        <button
                                                            onClick={handleCompleteFilterSetup}
                                                            disabled={completingPhase2}
                                                            className="w-full flex items-center justify-center gap-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl py-3 px-4 text-xs font-black transition-all shadow-lg shadow-emerald-600/20 disabled:opacity-60"
                                                        >
                                                            {completingPhase2 ? (
                                                                <span>Saving...</span>
                                                            ) : (
                                                                <>
                                                                    <FaCheckCircle className="w-4 h-4" />
                                                                    <span>I Have Created the Forwarding Filter</span>
                                                                </>
                                                            )}
                                                        </button>
                                                    </div>
                                                )}
                                            </div>
                                        </div>
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

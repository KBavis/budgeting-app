import React, { useContext } from 'react';
import Modal from './Modal';
import { ThemeContext } from '../../context/theme/ThemeContext';
import { FaExclamationTriangle } from 'react-icons/fa';

/**
 * Standardized Confirmation Modal component with dynamic Light/Dark mode support
 */
const ConfirmationModal = ({
    onClose,
    onConfirm,
    question,
    accountName,
    confirmText = "Confirm",
    cancelText = "Cancel",
}) => {
    const { theme } = useContext(ThemeContext);
    const isDark = theme === "dark";

    return (
        <Modal isOpen={true} onClose={onClose} title="Confirm Action" size="sm">
            <div className="flex flex-col items-center gap-4 text-center">
                <div className={`p-3.5 rounded-full border ${
                    isDark ? "bg-red-500/10 border-red-500/20 text-red-400" : "bg-red-50 border-red-200 text-red-600"
                }`}>
                    <FaExclamationTriangle className="w-6 h-6" />
                </div>

                <p className={`text-base font-bold leading-snug ${isDark ? "text-slate-200" : "text-slate-800"}`}>
                    {question}
                </p>

                {accountName && (
                    <p className={`text-lg font-black ${isDark ? "text-brand-400" : "text-brand-600"}`}>
                        {accountName}
                    </p>
                )}

                <div className={`flex justify-end gap-3 pt-4 border-t w-full mt-2 ${isDark ? "border-slate-800" : "border-slate-200"}`}>
                    <button
                        type="button"
                        onClick={onClose}
                        className={`px-4 py-2 text-sm font-semibold rounded-lg transition-colors ${
                            isDark
                                ? "text-slate-300 hover:text-white bg-slate-800 hover:bg-slate-700"
                                : "text-slate-700 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 border border-slate-200"
                        }`}
                    >
                        {cancelText}
                    </button>
                    <button
                        type="button"
                        onClick={onConfirm}
                        className="px-5 py-2 text-sm font-bold text-white bg-red-600 hover:bg-red-500 rounded-lg transition-colors shadow-md"
                    >
                        {confirmText}
                    </button>
                </div>
            </div>
        </Modal>
    );
};

export default ConfirmationModal;

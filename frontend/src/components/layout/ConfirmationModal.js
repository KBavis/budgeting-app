import React from 'react';

const ConfirmationModal = ({
                               onClose,
                               onConfirm,
                               question,
                               accountName,
                               confirmText = "Confirm",
                               cancelText = "Cancel",
                           }) => {
    return (
        <div className="fixed inset-0 flex items-center justify-center z-[500] backdrop-blur-sm overflow-y-auto">
            <div className="bg-white p-8 rounded-lg shadow-lg w-11/12 md:w-2/3 lg:w-1/3 flex flex-col justify-between max-h-[85%] xs:p-4">
                <div>
                    <div className="flex flex-col items-center mb-4 xs:mb-2">
                        <h2 className="text-2xl font-bold text-indigo-500 text-center mt-2 mb-4 xs:text-xl xs:mb-2">
                            {question}
                        </h2>
                        <h3 className="text-3xl font-extrabold text-indigo-600 text-center mb-2 xs:text-2xl">
                            {accountName}
                        </h3>
                    </div>
                </div>
                <div className="flex justify-between mt-4 xs:mt-2">
                    <button
                        onClick={onClose}
                        className="modal-button-cancel px-4 py-2 mr-2 text-white bg-red-500 rounded hover:bg-red-600 xs:px-3 xs:py-1 xs:text-sm"
                    >
                        {cancelText}
                    </button>
                    <button
                        onClick={onConfirm}
                        className="modal-button-confirm px-4 py-2 text-white bg-green-500 rounded hover:bg-green-600 xs:px-3 xs:py-1 xs:text-sm"
                    >
                        {confirmText}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmationModal;



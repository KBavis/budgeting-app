

const Merchant = ({ merchant }) => {

    return (
        <div className="bg-gradient-to-br from-gray-50 to-gray-100 rounded-xl border border-gray-200 shadow-sm p-2">
            {/* Merchant Header */}
            <div className="flex items-center mb-3 pb-2 border-b border-gray-200">
                {merchant.merchantLogoUrl ? (
                    <img
                        src={merchant.merchantLogoUrl}
                        alt="Merchant Logo"
                        className="w-8 h-8 rounded-full flex-shrink-0 border border-gray-300"
                    />
                ) : (
                    <img
                        src="https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
                        alt="Default Logo"
                        className="w-8 h-8 rounded-full flex-shrink-0 border border-gray-300"
                    />
                )}
                <p className="font-bold text-base text-gray-800 ml-3">{merchant.merchantName}</p>
            </div>
            
            {/* Merchant Stats */}
            <div className="space-y-1.5">
                <div className="flex justify-between text-gray-700 font-medium text-sm">
                    <span>Total Spend:</span>
                    <span className="text-indigo-600 font-bold">${merchant.totalSpent.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-gray-700 font-medium text-sm">
                    <span>Avg Transaction:</span>
                    <span className="text-indigo-600 font-bold">${merchant.avgTransactionAmount.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-gray-700 font-medium text-sm">
                    <span>Transactions:</span>
                    <span className="text-indigo-600 font-bold">{merchant.transactionCount}</span>
                </div>
            </div>
        </div>
    )
}

export default Merchant
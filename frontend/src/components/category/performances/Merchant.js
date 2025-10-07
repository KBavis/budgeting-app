

const Merchant = ({ merchant }) => {

    return (
        <div className="text-black mt-2">
            <div className="flex flex-col border border-black bg-gray-100 justify-center items-center text-center py-2 px-2">
                <div className="flex flex-row px-2 py-2 w-full items-center justify-center">
                    {merchant.merchantLogoUrl ? (
                        <img
                            src={merchant.merchantLogoUrl}
                            alt="Transaction Logo"
                            className="w-6 h-6 rounded-full flex-shrink-0"
                        />
                    ) : (
                        <img
                            src="https://bavis-budget-app-bucket.s3.amazonaws.com/default-avatar-icon-of-social-media-user-vector.jpg"
                            className="w-6 h-6 rounded-full flex-shrink-0"
                        />
                    )}
                    <p className="font-bold text-sm ml-2">{merchant.merchantName}</p>
                </div>
                <div className="flex mt-2">
                    <ul>
                        <li className="text-xs font-bold">Total Spend: <span className="text-indigo-600">${merchant.totalSpent.toFixed(2)}</span></li>
                        <li className="text-xs font-bold">Average Transaction Amount: <span className="text-indigo-600">${merchant.avgTransactionAmount.toFixed(2)}</span></li>
                        <li className="text-xs font-bold">Number of Transactions: <span className="text-indigo-600">{merchant.transactionCount}</span></li>
                    </ul>
                </div>
            </div>
        </div>
    )
}

export default Merchant
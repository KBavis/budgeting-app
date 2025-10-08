import { useEffect, useState } from "react"

const Breakdown = ({ performance }) => {
    const [percentUtilized, setPercentUtilized] = useState(0.0)

    useEffect(() => {
        setPercentUtilized(performance.categoryPercentUtilization * 100)
    }, [performance])

    const getTextColor = () => {
        const ratio = performance.categoryPercentUtilization
        if (ratio <= 0.5) return "text-green-500"
        if (ratio <= 0.7) return "text-yellow-500"
        if (ratio <= 0.9) return "text-orange-400"
        if (ratio <= 0.99) return "text-orange-600"
        return "text-red-600"
    }

    return (
        <div className="bg-white border border-gray-300 rounded-2xl shadow-md flex flex-col p-4 h-full">
            {/* Title */}
            <h1 className="text-xl font-extrabold text-indigo-700 underline mb-4 text-center">
                Spending Highlights
            </h1>

            {/* Stats */}
            <ul className="flex-1 flex flex-col justify-between">
                <li className="flex justify-between text-gray-700 font-medium mb-2">
                    <span>Total Spend:</span>
                    <span className="text-indigo-600 font-bold">${performance.totalSpend.toFixed(2)}</span>
                </li>
                <li className="flex justify-between text-gray-700 font-medium mb-2">
                    <span>Amount Budgeted:</span>
                    <span className="text-indigo-600 font-bold">${performance.totalAmountAllocated.toFixed(2)}</span>
                </li>
                <li className="flex justify-between text-gray-700 font-medium mb-2">
                    <span>Transaction Count:</span>
                    <span className="text-indigo-600 font-bold">{performance.transactionCount}</span>
                </li>
                <li className="flex justify-between text-gray-700 font-medium">
                    <span>Utilization:</span>
                    <span className={`font-bold ${getTextColor()}`}>{percentUtilized.toFixed(2)}%</span>
                </li>
            </ul>
        </div>
    )
}

export default Breakdown

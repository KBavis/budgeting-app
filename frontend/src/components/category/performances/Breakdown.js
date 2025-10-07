import { useEffect, useState } from "react"

const Breakdown = ({ performance }) => {

    const [percentUtilized, setPercentUtilized] = useState(0.00)

    // set the percent utilized to be a valid percentage instead of ratio
    useEffect(() => {
        let currUtil = (performance.categoryPercentUtilization * 100)
        setPercentUtilized(currUtil)
    }, [performance])


    /**
     * Function to get the correct color of the text based on the percent utilzied 
     *  
     * @returns 
     *      - tailwindcss classname for relevant text color
     * 
     */
    const getTextColor = () => {
        let ratio = performance.categoryPercentUtilization
        if (ratio <= .50) {
            return "text-green-500";
        } else if (ratio <= .70) {
            return "text-yellow-500";
        } else if (ratio <= .90) {
            return "text-orange-400";
        } else if (ratio <= .99) {
            return "text-orange-600"
        } else {
            return "text-red-600";
        }
    };




    return (
        <div className="border border-black text-center px-5 py-3">
            <h1 className="text-black font-bold text-xl underline">Spending Highlights</h1>
            <div className="mt-2 border border-black py-3 px-1 text-left">
                <ul>
                    <li className="text-black text-sm px-2 font-bold mb-2">
                        <span className="">Total Spend:</span> <span className="text-slate-500 ml-1">${performance.totalSpend.toFixed(2)}</span>
                    </li>
                    <li className="text-black text-sm px-2 font-bold mb-2">
                        <span className="">Total Amount Budgeted:</span> <span className="text-slate-500 ml-1">${performance.totalAmountAllocated.toFixed(2)}</span>
                    </li>
                    <li className="text-sm px-2 font-bold mb-2 text-black">
                        <span className="">Utilization:</span> <span className={`ml-1 ${getTextColor()}`}>{percentUtilized.toFixed(2)}%</span>
                    </li>
                    <li className="text-sm px-2 font-bold mb-2 text-black">
                        <span className="">Number of Transactions:</span> <span className="text-slate-500 ml-1">{performance.transactionCount}</span>
                    </li>
                </ul>
            </div>
        </div>
    )
}

export default Breakdown
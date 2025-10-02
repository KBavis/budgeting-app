import { useEffect } from "react"

const Breakdown = ({ performance }) => {



    return (
        <div className="border border-black text-center px-5 py-3">
            <h1 className="text-black font-bold text-base">Spending Highlights</h1>
            <div className="mt-2 border border-black py-3 px-1 text-left">
                <ul>
                    <li className="text-black text-sm px-2">
                        <span className="font-bold">Total Spend:</span> <span>{performance.totalSpend}</span>
                    </li>
                </ul>
            </div>
        </div>
    )
}

export default Breakdown
import { useEffect, useState } from "react"
import Merchant from "./Merchant"

const Top3Merchants = ({ performance }) => {

    const [merchants, setMerchants] = useState([])

    useEffect(() => {
        let top3Merchants = performance.topMerchants
        setMerchants(top3Merchants)
    }, [performance])

    return (
        <div className="bg-white border border-gray-300 rounded-2xl shadow-md flex flex-col px-2 py-3 h-full md:p-4">
            <h1 className="text-xl font-extrabold text-indigo-700 underline mb-4 text-center">
                Where Your Money Went
            </h1>
            <div className="flex-1 space-y-3">
                {merchants.map((merchant) => (
                    <Merchant
                        key={merchant.merchantRank}
                        merchant={merchant}
                    />
                ))}
            </div>
        </div>
    )
}

export default Top3Merchants
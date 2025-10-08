import { useEffect, useState } from "react"
import Merchant from "./Merchant"

const Top3Merchants = ({ performance }) => {

    const [merchants, setMerchants] = useState([])

    useEffect(() => {
        let top3Merchants = performance.topMerchants
        setMerchants(top3Merchants)
    }, [performance])

    return (
        <div className="text-center px-2 py-3 h-full bg-white border-gray-300 rounded-2xl shadow-md">
            <h1 className="font-extrabold text-indigo-700 underline text-xl">Where Your Money Went?</h1>
            <div className="w-full space-y-2">
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
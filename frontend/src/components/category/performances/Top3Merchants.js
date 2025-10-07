import { useEffect, useState } from "react"
import Merchant from "./Merchant"

const Top3Merchants = ({ performance }) => {

    const [merchants, setMerchants] = useState([])

    useEffect(() => {
        let top3Merchants = performance.topMerchants
        setMerchants(top3Merchants)
    }, [performance])

    return (
        <div className="border-black border text-center px-2 py-3 h-full bg-white">
            <h1 className="text-black font-bold underline text-xl">Where Your <span className="text-green-500 font-extrabold">Money</span> Went?</h1>
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
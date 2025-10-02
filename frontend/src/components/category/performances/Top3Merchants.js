import { useEffect } from "react"

const Top3Merchants = ({ performance }) => {

    useEffect(() => {

    }, [performance])

    return (
        <div className="border-black border text-center px-5 py-3">
            <h1 className="text-black font-bold">Where Your <span className="text-green-500 font-extrabold">Money</span> Went?</h1>
        </div>
    )
}

export default Top3Merchants 
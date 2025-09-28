import { useNavigate, useParams } from "react-router-dom"
import categoryTypeContext from "../context/category/types/categoryTypeContext"
import { useContext, useEffect, useState } from "react"

const SpendingAnalysisPage = () => {

    const { categoryTypes } = useContext(categoryTypeContext)

    const [currentType, setCurrentType] = useState(null)
    const navigate = useNavigate()
    const { type, month, year } = useParams()

    useEffect(() => {

        // find selected category type 
        if (categoryTypes) {
            const curr = categoryTypes?.filter(cat => cat.name === type)
        }

    }, [categoryTypes])

    return (
        // gradient background on page
        <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 relative">
            {/* back button for navigation back to Budget Summary page  */}
            <button
                onClick={() => navigate(-1)}
                className="fixed top-4 left-4 z-50 bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-2 px-3 rounded shadow-lg hover:cursor-pointer"
            >
                ← Back
            </button>
            {/* main page content*/}
            <div className="flex flex-col items-center justify-center flex-1 text-white">
                <h1 className="text-3xl font-bold">{type.charAt(0).toUpperCase() + type.slice(1)} Spending Analysis </h1>
            </div>
        </div>
    )
}

export default SpendingAnalysisPage
import { useNavigate, useParams } from "react-router-dom"
import categoryTypeContext from "../context/category/types/categoryTypeContext"
import CategoryPerformanceContext from "../context/category/performances/categoryPerformanceContext"
import { useContext, useEffect, useState } from "react"

const SpendingAnalysisPage = () => {

    const { categoryTypes, fetchCategoryTypes } = useContext(categoryTypeContext)
    const { category_performances, fetchCategoryPerformances } = useContext(CategoryPerformanceContext)

    const [currentType, setCurrentType] = useState(null)
    const navigate = useNavigate()
    const { type, month, year } = useParams()

    // determine the relevant Category Type the user selected 
    useEffect(() => {
        if (categoryTypes && categoryTypes.length >= 3) {

            // set currentType equal to CategoryType in state based on name in URL 
            for (let i = 0; i < categoryTypes.length; i++) {
                if (categoryTypes[i].name.toLowerCase() == type) {
                    setCurrentType(categoryTypes[i])
                }
            }

        }

    }, [categoryTypes])

    // fetch category types if page is refreshed 
    useEffect(() => {
        const fetch = async () => {
            fetchCategoryTypes()
        }

        if (!categoryTypes || categoryTypes.length == 0) {
            fetch()
        }

    }, [categoryTypes])

    // fetch category performances if page refreshed 
    useEffect(() => {

        const fetch = async () => {
            let monthYear = { "month": month.toUpperCase(), "year": parseInt(year) }
            fetchCategoryPerformances([currentType.categoryTypeId], monthYear)
        }

        if (!category_performances && currentType) {
            fetch()
        }

    }, [category_performances, currentType])

    return (
        // gradient background on page
        <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 relative">
            {/* back button for navigation back to Budget Summary page 
                    TODO:  Create state variable to track previously selected month year so when we navigate back
                    to home page, we can "remember" which budget summary we have open (like a prevSummary initially set to null)
            */}
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
import { useNavigate, useParams } from "react-router-dom"
import categoryTypeContext from "../context/category/types/categoryTypeContext"
import CategoryPerformanceContext from "../context/category/performances/categoryPerformanceContext"
import { useContext, useEffect, useState } from "react"
import Loading from "../components/util/Loading"
import CategoryPerformance from "../components/category/performances/CategoryPerformance"
import categoryContext from "../context/category/categoryContext"

const SpendingAnalysisPage = () => {

    const { categoryTypes, fetchCategoryTypes } = useContext(categoryTypeContext)
    const { category_performances, fetchCategoryPerformances, loading } = useContext(CategoryPerformanceContext)
    const { categories, fetchCategories } = useContext(categoryContext)

    const [currentType, setCurrentType] = useState(null)
    const [filteredPerformances, setFilteredPerformances] = useState([])
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

    // fetch user categories if page is refreshed 
    useEffect(() => {
        const fetch = async () => {
            fetchCategories()
        }

        if (!categories || categories.length == 0) {
            console.log('Fetching Categories!')
            fetch()
        }

    }, [categories])

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

    // filter category performances based on current category type, sort based on highest spend, filter out Categories with 0 spend
    useEffect(() => {
        if (currentType && category_performances) {
            let currPerformances = category_performances
                .filter((curr) => curr.categoryTypeId === currentType.categoryTypeId)
                .filter((curr) => curr.totalSpend != 0)
                .sort((a, b) => b.totalSpend - a.totalSpend);
            setFilteredPerformances(currPerformances)
        }

    }, [currentType, category_performances])


    /**
     * Function to capitlaize first letter of word
     * 
     * @param word 
     *          - word to capitalize first letter for 
     * @returns 
     *          - capitalizedd word
     */
    const capitalizeFirstLetter = (word) => {
        return word.charAt(0).toUpperCase() + word.slice(1)
    }

    return (
        // gradient background on page
        <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800 relative">
            {/* back button for navigation back to Budget Summary page*/}
            <button
                onClick={() => navigate(-1)}
                className="fixed top-4 left-4 z-50 bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-1 px-2 md:py-2 md:px-3 rounded shadow-lg hover:cursor-pointer"
            >
                ← Back
            </button>
            {/* main page content*/}
            <div className="flex flex-col items-center justify-center flex-1 text-white mt-10 pt-2">
                {
                    filteredPerformances.length > 0 ? (
                        <h1 className="text-3xl font-bold flex flex-col text-center mb-2 pt-5">
                            <span className="text-5xl font-extrabold mb-3">{capitalizeFirstLetter(month)} {year}</span>
                            <span className="text-indigo-600 font-extrabold mt-3 b text-4xl">{capitalizeFirstLetter(type)}</span> Spending Analysis
                        </h1>
                    ) : (
                        <h1 className="text-5xl font-extrabold">No spending analysis found for <span className="text-indigo-400">{capitalizeFirstLetter(month)} {year}</span></h1>
                    )
                }
                <div className="flex flex-col w-11/12 my-5 justify-center items-center mx-auto md:w-3/5 mt-3 bg-white">
                    {
                        !loading ? (
                            filteredPerformances.map((performance) => (
                                <CategoryPerformance
                                    key={performance.categoryPerformanceId}
                                    performance={performance}
                                />
                            ))
                        ) : (
                            <Loading />
                        )
                    }
                </div>
            </div>
        </div>
    );
}

export default SpendingAnalysisPage
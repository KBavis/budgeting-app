import Breakdown from "./Breakdown"
import Top3Merchants from "./Top3Merchants"
import { useContext, useEffect, useState } from "react"
import categoryContext from "../../../context/category/categoryContext"

const CategoryPerformance = ({ performance }) => {

    const [categoryName, setCategoryName] = useState("")
    const { categories } = useContext(categoryContext)


    // update the category name based on current CategoryPerformance
    useEffect(() => {
        if (categories) {
            let category = categories.find((cat) => cat.categoryId == performance.categoryId)
            if (category) {
                setCategoryName(category.name)
            }
        }

    }, [performance, categories])


    return (
        <div className="flex flex-col items-center w-full xs:w-11/12 mt-4 mb-4 border border-black px-2 py-2 bg-gray-100">
            <h1 className="font-extrabold text-3xl text-indigo-600 text-center mb-2 underline">
                {categoryName}
            </h1>
            <div className="flex flex-col md-xl:flex-row p-0 xs:px-4 xs:py-3 w-full items-stretch ">
                <div className="flex-1 p-2 flex flex-col xs:min-win-0">
                    <Breakdown performance={performance} />
                </div>

                <div className="flex-1 p-2 flex flex-col min-win-0">
                    <Top3Merchants performance={performance} />
                </div>
            </div>
        </div>
    )
}

export default CategoryPerformance
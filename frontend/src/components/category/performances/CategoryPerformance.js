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
        <div className="flex flex-col items-center w-3/4 mt-4 mb-4 border border-black px-2 py-2">
            <h1 className="font-extrabold text-3xl text-indigo-600 text-center mb-4 underline">
                {categoryName}
            </h1>

            {/* TODO: If this is mobile, lets make these stack veritcally instead of horizontally */}
            <div className="flex flex-row gap-6 px-4 py-3 w-full border border-black">
                <div className="flex-1 min-w-0 p-2">
                    <Breakdown performance={performance} />
                </div>

                <div className="flex-1 min-w-0 p-2">
                    <Top3Merchants performance={performance} />
                </div>
            </div>
        </div>
    )
}

export default CategoryPerformance
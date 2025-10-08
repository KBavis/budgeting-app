import React, { useContext, useEffect, useRef, useState } from 'react';
import SummaryContext from "../context/summary/summaryContext";
import BudgetOverview from "../components/summary/BudgetOverview";
import { FaArrowLeft, FaArrowRight } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import CategoryPerformanceContext from '../context/category/performances/categoryPerformanceContext';
import categoryTypeContext from '../context/category/types/categoryTypeContext';

const BudgetSummaryPage = () => {
    const { summaries, fetchBudgetSummaries, setLoading, prev, setPrev } = useContext(SummaryContext);
    const { fetchCategoryPerformances } = useContext(CategoryPerformanceContext)
    const { categoryTypes, fetchCategoryTypes } = useContext(categoryTypeContext)
    const [selectedSummary, setSelectedSummary] = useState(null);
    const initalFetchRef = useRef(false);
    const navigate = useNavigate();
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 4; // 2 columns x 2 rows
    const [categoryTypeIds, setCategoryTypeIds] = useState([])
    const [sortedSummaries, setSortedSummaries] = useState([])
    const [filterQuery, setFilterQuery] = useState("");

    const handleMonthYearClick = (summary) => {
        if (selectedSummary === summary) {
            setSelectedSummary(null);
            setPrev(null);
        } else {
            setSelectedSummary(summary);
            setPrev(summary)
        }
    }

    // Map months to numbers for easier comparison
    const monthOrder = {
        JANUARY: 1,
        FEBRUARY: 2,
        MARCH: 3,
        APRIL: 4,
        MAY: 5,
        JUNE: 6,
        JULY: 7,
        AUGUST: 8,
        SEPTEMBER: 9,
        OCTOBER: 10,
        NOVEMBER: 11,
        DECEMBER: 12,
    };


    const handleBackClick = () => {
        navigate("/home");
    }

    const nextPage = () => {
        if (currentPage < Math.ceil(summaries.length / itemsPerPage)) {
            setCurrentPage(currentPage + 1);
        }
    }

    const prevPage = () => {
        if (currentPage > 1) {
            setCurrentPage(currentPage - 1);
        }
    }

    // Fetch All Budget Summaries
    const getBudgetSummaries = async () => {
        if (!summaries || summaries.length === 0) {
            console.log("Fetching budget summaries...");
            setLoading();
            await fetchBudgetSummaries();
        }
    }

    // fetch category types if page is refreshed 
    useEffect(() => {
        const fetch = async () => {
            fetchCategoryTypes()
        }

        if (!categoryTypes || categoryTypes.length == 0) {
            fetch()
        }

    }, [categoryTypes])

    // Fetch All Entities from Backend initial Component Mounting
    useEffect(() => {
        console.log(`Component Mounted! Initial Fetch Value : ${initalFetchRef.current}`);
        if (!initalFetchRef.current) {
            getBudgetSummaries();
            initalFetchRef.current = true;
        }
    }, []);


    // fetch category type performances for all relevant CategoryTypeIds & selected Month Year
    useEffect(() => {
        const fetch = async () => {
            let month = selectedSummary.id.monthYear.month
            let year = selectedSummary.id.monthYear.year
            let monthYear = { "month": month.toUpperCase(), "year": parseInt(year) }

            await fetchCategoryPerformances(categoryTypeIds, monthYear)
        }

        if (selectedSummary && categoryTypeIds) {
            fetch()
        }


    }, [selectedSummary, categoryTypeIds])

    // extract list of relevant category type ids for user 
    useEffect(() => {
        if (categoryTypes) {
            let ids = categoryTypes.map((type) => type.categoryTypeId)
            setCategoryTypeIds(ids)
        }

    }, [categoryTypes])


    // set currently selected summary to previous (to preserve history when navigating to spending analysis and back)
    useEffect(() => {
        if (prev && prev != null && selectedSummary == null) {
            handleMonthYearClick(prev)
        }
    }, [prev, selectedSummary])

    // sort summaries based on latest month & year 
    useEffect(() => {
        if (!summaries) {
            return;
        }

        let sorted = summaries.sort((a, b) => {

            // if years are different, sort by descending 
            if (b.id.monthYear.year != a.id.monthYear.year) {
                return b.id.monthYear.year - a.id.monthYear.year;
            }

            // if years are same, sort by descending month 
            return monthOrder[b.id.monthYear.month] - monthOrder[a.id.monthYear.month];
        })
        setSortedSummaries(sorted);

    }, [summaries])

    /**
     * Functionality to convert to normal case
     *
     * @param str
     *          - String to convert
     * @returns {string}
     */
    const convertToNormalCase = (str) => {
        // Split the string by spaces to handle multi-word strings
        return str
            .toLowerCase()
            .split(' ')
            .map(word => word.charAt(0).toUpperCase() + word.slice(1))
            .join(' ');
    }

    // Calculate the items for the current page
    const indexOfLastItem = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentSummaries = sortedSummaries.slice(indexOfFirstItem, indexOfLastItem);

    // variable to store filtered summaries based on user input
    const filteredSummaries = sortedSummaries.filter(summary => {
        let month = summary.id.monthYear.month.toLowerCase()
        let year = summary.id.monthYear.year.toString()
        let monthAndYear = month + " " + year;
        console.log(monthAndYear)

        return monthAndYear.startsWith(filterQuery.toLowerCase());

    });

    return (
        <div className="flex flex-col min-h-screen bg-gradient-to-br from-gray-900 to-indigo-800">
            <FaArrowLeft
                className="text-3xl text-white ml-5 mt-5 hover:scale-105 hover:text-gray-200 cursor-pointer z-[500] xs:text-2xl xs:ml-3 xs:mt-3"
                onClick={handleBackClick}
            />
            <div className="flex flex-col items-center px-8 md:px-12 h-full">
                {!selectedSummary && (
                    <div className="max-w-md text-center mb-4 mt-5">
                        <h2 className="text-3xl md:text-4xl font-bold text-white">
                            Choose Month to View Budget Performance
                        </h2>
                    </div>
                )}
                <div className="flex justify-center items-center w-full h-1/5 mt-5 mb-8">
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 w-full sm:w-2/5 xs:w-4/5">
                        {filterQuery.length == 0 ? (
                            // current page summaries if no user input provided
                            currentSummaries.map((summary, index) => (
                                <button
                                    key={index}
                                    className={`cursor-pointer text-white font-bold bg-indigo-900 rounded-lg shadow-md p-2 flex items-center justify-center space-x-2 w-full h-12 hover:bg-transparent hover:border hover:border-white hover:duration-500
                                    ${selectedSummary === summary ? 'border-2 border-white' : ''} xs:text-sm xs:h-10`}
                                    onClick={() => handleMonthYearClick(summary)}
                                >
                                    {summary.id.monthYear.month} {summary.id.monthYear.year}
                                </button>
                            ))) : (
                            // filtered budget performances if user input added 
                            filteredSummaries.map((summary, index) => (
                                <button
                                    key={index}
                                    className={`cursor-pointer text-white font-bold bg-indigo-900 rounded-lg shadow-md p-2 flex items-center justify-center space-x-2 w-full h-12 hover:bg-transparent hover:border hover:border-white hover:duration-500
                                    ${selectedSummary === summary ? 'border-2 border-white' : ''} xs:text-sm xs:h-10`}
                                    onClick={() => handleMonthYearClick(summary)}
                                >
                                    {summary.id.monthYear.month} {summary.id.monthYear.year}
                                </button>
                            )))}
                    </div>
                </div>
                <input
                    type="text"
                    placeholder="Filter budget performances by month..."
                    value={filterQuery}
                    onChange={(e) => setFilterQuery(e.target.value)}
                    className="mt-4 p-2 w-1/4 mb-5 rounded-md text-black xs:mt-2 xs:p-1 xs:text-sm"
                />
                <div className="flex justify-between w-2/5 mb-8 xs:w-4/5">
                    <div className="flex">
                        {currentPage > 1 && (
                            <FaArrowLeft
                                className="text-2xl text-white cursor-pointer hover:scale-105 hover:text-gray-200 xs:text-xl"
                                onClick={prevPage}
                            />
                        )}
                    </div>
                    <div className="flex">
                        {currentPage < Math.ceil(summaries.length / itemsPerPage) && (
                            <FaArrowRight
                                className="text-2xl text-white cursor-pointer hover:scale-105 hover:text-gray-200 xs:text-xl"
                                onClick={nextPage}
                            />
                        )}
                    </div>
                </div>
                <div className="flex flex-col items-center w-full h-4/5 overflow-y-auto scrollbar-hide">
                    {selectedSummary && (
                        <>
                            <h2 className="text-3xl md:text-4xl font-bold text-white mb-4">
                                Budget Performance for <span className="text-indigo-500 text-4xl font-extrabold">{convertToNormalCase(selectedSummary.id.monthYear.month)} {selectedSummary.id.monthYear.year}</span>
                            </h2>
                            <BudgetOverview overview={selectedSummary.generalOverview} month={selectedSummary.id.monthYear.month} year={selectedSummary.id.monthYear.year} />
                            <BudgetOverview overview={selectedSummary.needsOverview} month={selectedSummary.id.monthYear.month} year={selectedSummary.id.monthYear.year} />
                            <BudgetOverview overview={selectedSummary.wantsOverview} month={selectedSummary.id.monthYear.month} year={selectedSummary.id.monthYear.year} />
                            <BudgetOverview overview={selectedSummary.investmentOverview} month={selectedSummary.id.monthYear.month} year={selectedSummary.id.monthYear.year} />
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}

export default BudgetSummaryPage;

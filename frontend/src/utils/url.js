const apiUrl = 
    process.env.NODE_ENV === "production" ? "http://66.228.45.138:8080" : 
    "http://localhost:8080"



export default apiUrl;

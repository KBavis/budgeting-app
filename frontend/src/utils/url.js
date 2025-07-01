const apiUrl = 
    process.env.NODE_ENV === "production" ? "https://bavisbudgeting.com" : 
    "http://localhost:8080"



export default apiUrl;

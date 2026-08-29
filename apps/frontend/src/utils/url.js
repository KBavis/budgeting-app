// Resolves the backend API URL dynamically from environment variables
// - In development: defaults to http://localhost:8080
// - In production: uses REACT_APP_API_URL or relative path "" (routed via reverse-proxy)
const apiUrl =
  process.env.REACT_APP_API_URL ||
  (process.env.NODE_ENV === "production" ? "" : "http://localhost:8080");

export default apiUrl;

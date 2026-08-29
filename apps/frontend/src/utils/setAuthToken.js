import axios from "axios";

/**
 * Add JWT Token To 'Authorization' Header
 *
 * @param token
 *       - JWT Token to apply to our Authorization Header
 */
const setAuthToken = (token) => {
   try {
      if (token) {
         axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
      } else {
         delete axios.defaults.headers.common["Authorization"];
      }
   } catch (error) {
      console.error(`Error Setting Auth Token: ${token}`);
   }
};

export default setAuthToken;

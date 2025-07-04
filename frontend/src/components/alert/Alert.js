import React, { useContext } from "react";
import AlertContext from "../../context/alert/alertContext";

/**
 *  Alert Component for displaying relevant messages to our User
 */
const Alerts = () => {
   const { alerts } = useContext(AlertContext);

   return (
      <>
         {alerts.length > 0 &&
            alerts.map((alert) => (
               <div
                  key={alert.id}
                  className={`fixed top-4 z-[100] right-4 rounded-lg shadow-lg py-4 px-6 text-lg font-semibold flex items-center ${
                     alert.type === "danger"
                        ? "bg-red-500 text-white"
                        : "bg-green-500 text-white"
                  } xs:top-2 xs:right-2 xs:py-2 xs:px-4 xs:text-sm`}
               >
                  <i
                     className={`fas fa-info-circle mr-4 ${
                        alert.type === "danger"
                           ? "text-red-200"
                           : "text-green-200"
                     }`}
                  ></i>
                  <span>{alert.msg}</span>
               </div>
            ))}
      </>
   );
};

export default Alerts;

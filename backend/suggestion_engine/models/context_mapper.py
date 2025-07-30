

class ContextMapper():
    """
    Mapper to utilize for generating Category suggestions when users have under 50 transactiosn 
    """

    def __init__(self, user_id):
        self.user_id = user_id
    

    def map_transaction(self, transaction):
        """
        Map a transaction to a corresponding Category using users small historical data 

        Args:
            transaction (_type_): _description_
        """
        
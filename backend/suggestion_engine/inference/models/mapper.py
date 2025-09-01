from pydantic import BaseModel
from suggestion_engine.inference.outcomes.uncategorized_suggestion import UncategorizedSuggestion
from suggestion_engine.inference.outcomes.category_suggestion import CategorySuggestion

class ContextMapper():
    """
    Mapper to utilize for generating Category suggestions when users have under 50 transactiosn 
    """

    def __init__(self, user_id):
        self.user_id = user_id
    

    def map_transaction(self, transaction: BaseModel):
        """
        Map a transaction to a corresponding Category using users small historical data 

        Args:
            transaction (BaseModel): meta data for a users transaction
        """

        category = self.exact_plaid_category_match(transaction)
        if category:
            return category

        category = self.similar_transactions_match(transaction)
        if category:
            return category

        return UncategorizedSuggestion()
    

    def exact_plaid_category_match(self, transaction: BaseModel):
        """
        Determine if user has previously assigned transactions with exact Plaid Primary Category & Detailed Category

        Args:
            transaction (BaseModel): metadata for transaction
        """

        #TODO: Query database to find Category IDs that has transactions with these same plaid categories. Aggregate total counts and return most frequent along with confidence 
        #NOTE: The confidence can be determined by using total number of transactions user has assigned to speciic category with exact matches


    def similar_transactions_match(self, transaction: BaseModel):
        """
        Determine if user has previously assigned transactions with similar meta data (merchant_name, time, etc)

        Args:
            transaction (BaseModel): transaction meta data
        """

        #TODO: Query database for similar user tanasactions, aggregate totals grouped by category ID, determine confidence via total count of transactions aggregated
        
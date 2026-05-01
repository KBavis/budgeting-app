from pydantic import BaseModel
from typing import Optional
from datetime import datetime


class UncategorizedSuggestion(BaseModel):
    """
    Model to represetn an UncategorizedSuggestion for a particular Transaction
    """

    reasons: list = [] 
    type: str = "uncategorized"
    suggested_actions: list = []


class CategorySuggestion(BaseModel):
    """
    Model to represent a CategorySuggestion for a particular Transaction
    """

    category_id: int
    confidence: float
    source: str
    reasoning: str = "" 
    

class TransactionMetadata(BaseModel):
    """
    Model to represent relevant Transaction meta data that can be leveraged to make a prediction as to what Category this Transaction belongs 
    to based on the user's historical Transaction categorization behavior
    """
    merchant: Optional[str] = None
    amount: float 
    date_time: Optional[datetime] = None
    plaid_detailed_category: Optional[str] = None
    plaid_primary_category: Optional[str] = None


class CategorySuggestionRequest(BaseModel):
    """
    Model to represent the Request Body sent to Inference Service 
    """
    user_id: int
    transaction: TransactionMetadata
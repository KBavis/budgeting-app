from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime
from suggestion_engine.inference.models.manager import ModelManager
from suggestion_engine.inference import predict
from suggestion_engine.inference.outcomes.uncategorized_suggestion import UncategorizedSuggestion
from typing import Optional


app = FastAPI()

manager = ModelManager()


class TransactionMetadata(BaseModel):
    merchant: Optional[str] = None
    amount: float 
    date_time: Optional[datetime] = None
    plaid_detailed_category: Optional[str] = None
    plaid_primary_category: Optional[str] = None

class CategorySuggestionRequest(BaseModel):
    user_id: int
    transaction: TransactionMetadata


@app.post("/suggestion")
def category_suggestion(request: CategorySuggestionRequest):

    user_id = request.user_id

    #TODO: Add ability to retrieve Venmo descriptions and backfill exisitng transactions with additional meta data
    if request.transaction.merchant == 'Venmo': #TODO: Fix issue with merchant not being named 'Venmo' or 'Charles Schwab'
        return UncategorizedSuggestion(reasons=['Unable to currently make accurate predictions for Venmo Transactions'])

    # retrieve model
    onnx_model = manager.get_model(user_id)
    if not onnx_model:
        # TODO: Invoke Context Mapper logic 
        return UncategorizedSuggestion(reasons=[f'No existing model exists for user ID {user_id}'])
    
    # make suggestion
    return predict.predict_category(user_id, request.transaction, onnx_model, manager.model_accuracy[user_id])
    
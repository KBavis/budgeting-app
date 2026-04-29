from fastapi import FastAPI
from backend.suggestion_engine.inference.manager import ModelManager
from suggestion_engine.inference import predict
from suggestion_engine.inference.schema import UncategorizedSuggestion
from suggestion_engine.inference.schema import CategorySuggestionRequest
import logging


app = FastAPI()

manager = ModelManager()

logger = logging.getLogger(__name__)



@app.post("/suggestion")
def category_suggestion(request: CategorySuggestionRequest):

    logger.info(f"Received category suggestion request for user ID {request.user_id} with Transaction metadata: {request.transaction}")

    user_id = request.user_id

    #TODO: Add ability to retrieve Venmo descriptions and backfill exisitng transactions with additional meta data
    if request.transaction.merchant == 'Venmo': #TODO: Fix issue with merchant not being named 'Venmo' or 'Charles Schwab'
        logger.debug(f"Skipping prediction for Venmo transaction")
        return UncategorizedSuggestion(reasons=['Unable to currently make accurate predictions for Venmo Transactions'])

    # retrieve model
    onnx_model, accuracy, _,  = manager.get_model(user_id)
    if not onnx_model:
        logger.warning(f"No model could be retrieved for user ID {user_id}, unable to make category suggestion")
        return UncategorizedSuggestion(reasons=[f'No existing model exists for user ID {user_id}'])
    
    # make suggestion
    try:
        suggestion = predict.predict_category(user_id, request.transaction, onnx_model, accuracy)
        return suggestion
    except Exception as e:
        logger.error(f"An unexpected error occurred while making a category suggestion for user ID {user_id} with error: {e}")
        return UncategorizedSuggestion(reasons=[f'An unexpected error occurred while making a category suggestion with error: {e}'])
    
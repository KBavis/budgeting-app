from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime
from pathlib import Path
import torch
from suggestion_engine.models.classifer import CategoryPredictor
from suggestion_engine.models.context_mapper import ContextMapper
from suggestion_engine import predict
import json


app = FastAPI()


class TransactionMetadata(BaseModel):
    merchant: str
    amount: float 
    date_time: datetime
    plaid_detailed_category: str 
    plaid_primary_category: str

class CategorySuggestionRequest(BaseModel):
    user_id: int
    transaction: TransactionMetadata


@app.post("/suggestion")
def category_suggestion(request: CategorySuggestionRequest):

    user_id = request.user_id
    file_path_str = f"artifacts/{user_id}/model_weights.pth"
    meta_data_path_str = f"artifacts/{user_id}/metadata.json"
    path = Path(file_path_str)

    if path.is_file():
        print(f"User {user_id} has a corresponding Personal model; utilizing model for prediction")

        # load meta data 
        with open(meta_data_path_str, "r") as metadata:
            data = json.load(metadata)

        nn = CategoryPredictor(data['input_dim'], data['num_classes'])
        nn.load_state_dict(torch.load(file_path_str, weights_only=True))
        suggestion = predict.predict_category(request.transaction)
    else:
        print(f"User {user_id} has no corresponding Personal model; utilizing Context Mapper for prediction")
        mapper = ContextMapper(user_id)
        suggestion = mapper.map_transaction(request.transaction) 

    
    return suggestion
from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime
from pathlib import Path
import torch
from classifer import CategoryPredictor
from context_mapper import ContextMapper
import predict


app = FastAPI()


class TransactionMetadata(BaseModel):
    merchant_name: str
    amount: float 
    date_time: datetime
    detailed: str 
    primary: str

class CategorySuggestionRequest(BaseModel):
    user_id: int
    transaction: TransactionMetadata


@app.post("/suggestion")
def category_suggestion(request: CategorySuggestionRequest):

    user_id = request.user_id
    file_path_str = f"../artifacts/{user_id}/model_weights.pth"
    path = Path(file_path_str)

    if path.is_file():
        print(f"User {user_id} has a corresponding Personal model; utilizing model for prediction")
        nn = CategoryPredictor()
        nn.load_state_dict(torch.load(file_path_str, weights_only=True))
        suggestion = predict.predict_category(request.transaction)
    else:
        print(f"User {user_id} has no corresponding Personal model; utilizing Context Mapper for prediction")
        mapper = ContextMapper(user_id)
        suggestion = mapper.map_transaction(request.transaction) 

    
    return suggestion
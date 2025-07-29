from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime

app = FastAPI()

class TransactionMetadata(BaseModel):
    merchant_name: str
    amount: float 
    date_time: datetime
    detailed: str 
    primary: str


@app.post("/suggestion")
def category_suggestion(metadata: TransactionMetadata):
    return {
        "merchant_name": metadata.merchant_name,
        "amount": metadata.amount, 
        "date_time": metadata.date_time, 
        "plaid_detailed_category": metadata.detailed,
        "plaid_primary_category": metadata.primary 
    }
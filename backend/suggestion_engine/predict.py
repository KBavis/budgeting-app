import torch
from suggestion_engine.outcomes.category_suggestion import CategorySuggestion
from suggestion_engine.outcomes.uncategorized_suggestion import UncategorizedSuggestion
from suggestion_engine import data
import joblib
import pandas as pd
from suggestion_engine.data import extract_text


def predict_category(user_id, transaction, nn):
    print(f"Attempting to predict the Category for the following Transaction metadata: {transaction}")

    # prepare input data 
    txs = [transaction.dict()]
    features, _ = data.prepare_input(txs)
    df = pd.DataFrame(features, columns=['amount', 'hour', 'day', 'merchant', 'primary_category', 'detailed_category'])

    # load preprocessor & invoke
    preprocesor = joblib.load(f'artifacts/{user_id}/preprocessor.joblib')
    Xs = preprocesor.transform(df)

    # convert to tensor 
    tensor = torch.from_numpy(Xs).float()

    nn.eval()
    with torch.no_grad():
        pred = nn(tensor)

    print(tensor)
    print(pred)

    

    # TODO: Determine confidence level and return UncategorizedSuggestion if confidence is too low after making prediction via neural network
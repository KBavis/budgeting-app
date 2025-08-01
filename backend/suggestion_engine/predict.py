import torch
from suggestion_engine.outcomes.category_suggestion import CategorySuggestion
from suggestion_engine.outcomes.uncategorized_suggestion import UncategorizedSuggestion
from suggestion_engine.scripts import train_model
from suggestion_engine import data

def predict_category(transaction, nn):
    print(f"Attempting to predict the Category for the following Transaction metadata: {transaction}")

    nn.eval()

    # preprocess data 
    # TODO: move preprocessing function to seperate module 
    txs = [transaction.dict()]
    Xs, _, _ = data.preprocess(txs)

    print(type(Xs))

    # convert to tensor 
    tensor = torch.from_numpy(Xs.to_numpy()).float()

    with torch.no_grad():
        pred = nn(tensor)

    print(tensor)
    print(pred)

    

    # TODO: Determine confidence level and return UncategorizedSuggestion if confidence is too low after making prediction via neural network
import torch
from outcomes.category_suggestion import CategorySuggestion
from outcomes.uncategorized_suggestion import UncategorizedSuggestion
from suggestion_engine.scripts import train_model

def predict_category(transaction, nn):
    print(f"Attempting to predict the Category for the following Transaction metadata: {transaction}")

    nn.eval()

    # preprocess data 
    # TODO: move preprocessing function to seperate module 
    txs = [transaction.dict()]
    Xs, _, _ = train_model.preprocess(txs)

    # convert to tensor 
    tensor = torch.from_numpy(Xs.to_numpy()).float()

    

    # TODO: Determine confidence level and return UncategorizedSuggestion if confidence is too low after making prediction via neural network
import torch
from outcomes.category_suggestion import CategorySuggestion
from outcomes.uncategorized_suggestion import UncategorizedSuggestion

def predict_category(transaction, nn):
    print(f"Attempting to predict the Category for the following Transaction metadata: {transaction}")
    

    # TODO: Determine confidence level and return UncategorizedSuggestion if confidence is too low after making prediction via neural network
import torch
from suggestion_engine.outcomes.category_suggestion import CategorySuggestion
from suggestion_engine.outcomes.uncategorized_suggestion import UncategorizedSuggestion
from suggestion_engine import data
import joblib
import pandas as pd

def predict_category(user_id, transaction, nn):
    print(f"Attempting to predict the Category for the following Transaction metadata: {transaction}")

    # prepare input data 
    txs = [transaction.dict()]
    features, _ = data.prepare_input(txs)
    df = pd.DataFrame(features, columns=['amount', 'hour', 'day', 'merchant', 'primary_category', 'detailed_category'])

    # load preprocessor & invoke
    preprocesor = joblib.load(f'suggestion_engine/artifacts/{user_id}/preprocessor.joblib')
    Xs = preprocesor.transform(df)

    # convert to tensor 
    tensor = torch.from_numpy(Xs).float()

    # make prediction
    nn.eval()
    with torch.no_grad():
        pred = nn(tensor)

    # retrieve label encoder & create de-coding mapper
    label_encoder = joblib.load(f"suggestion_engine/artifacts/{user_id}/label_encoder.joblib")
    original_labels = list(label_encoder.classes_)

    mapper = {}
    for i in range(len(original_labels)):
        curr_label = original_labels[i]
        mapper[i] = curr_label
    

    predicted_classes = torch.argmax(pred, dim=1)
    encoded_category_id = predicted_classes.item()
    print(predicted_classes)

    print(f"The predicted category ID is {mapper[encoded_category_id]}")
    

    # TODO: Determine confidence level and return UncategorizedSuggestion if confidence is too low after making prediction via neural network
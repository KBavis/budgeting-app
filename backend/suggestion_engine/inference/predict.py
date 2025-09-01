import torch
from suggestion_engine.inference.outcomes.category_suggestion import CategorySuggestion
from suggestion_engine.inference.outcomes.uncategorized_suggestion import UncategorizedSuggestion
from suggestion_engine.training.preprocess import data
import joblib
import pandas as pd


def predict_category(user_id, transaction, nn, confidence):
    print(f"Attempting to predict the Category for the following Transaction metadata: {transaction}")

    # load joblibs from training
    preprocesor = joblib.load(f'suggestion_engine/artifacts/{user_id}/preprocessor.joblib')
    label_encoder = joblib.load(f"suggestion_engine/artifacts/{user_id}/label_encoder.joblib")

    # prepare input data 
    txs = [transaction.dict()]
    features, _ = data.prepare_input(txs)
    df = pd.DataFrame(features, columns=['amount', 'hour', 'day', 'merchant', 'primary_category', 'detailed_category'])
    Xs = preprocesor.transform(df)
    tensor = torch.from_numpy(Xs).float()

    # make prediction
    nn.eval()
    with torch.no_grad():
        pred = nn(tensor)

    # create encoded value mapping
    original_labels = list(label_encoder.classes_)
    encoded_map = {}
    for i in range(len(original_labels)):
        curr_label = original_labels[i]
        encoded_map[i] = curr_label
    

    predicted_classes = torch.argmax(pred, dim=1)
    category_id = encoded_map[predicted_classes.item()]
    
    return CategorySuggestion(category_id=category_id, confidence=confidence, source="PERSONAL_MODE")
    

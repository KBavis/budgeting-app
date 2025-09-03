from suggestion_engine.inference.outcomes.category_suggestion import CategorySuggestion
from suggestion_engine.inference.outcomes.uncategorized_suggestion import UncategorizedSuggestion
from suggestion_engine import data
import joblib
import pandas as pd
import numpy as np


def predict_category(user_id, transaction, onnx_model, confidence):
    print(f"Attempting to predict the Category for the following Transaction metadata: {transaction}")

    # load joblibs from training
    preprocesor = joblib.load(f'suggestion_engine/artifacts/{user_id}/preprocessor.joblib')
    label_encoder = joblib.load(f"suggestion_engine/artifacts/{user_id}/label_encoder.joblib")

    # prepare input data 
    txs = [transaction.dict()]
    features, _ = data.prepare_input(txs)
    df = pd.DataFrame(features, columns=['amount', 'hour', 'day', 'merchant', 'primary_category', 'detailed_category'])
    Xs = preprocesor.transform(df)

    # create final np array used to make prediction using onnx 
    input_data = Xs.astype(np.float32) 

    # fetch input name 
    input_name = onnx_model.get_inputs()[0].name 

    # run inference 
    inference = onnx_model.run(None, {input_name: input_data})
    if not inference:
        raise Exception('An unexpected failure occured while making inference using ONNX model')

    # extract suggested categoyr
    logits = inference[0]
    predicted_class = np.argmax(logits, axis=1)[0]

    # utilize encoded label mapping to decode user specific category 
    encoded_map = get_encoded_labels_mapping(label_encoder)
    category_id = encoded_map[predicted_class]

    return CategorySuggestion(category_id=category_id, confidence=confidence, source="PERSONAL_MODE")



def get_encoded_labels_mapping(label_encoder):
    original_labels = list(label_encoder.classes_)
    encoded_map = {}
    for i in range(len(original_labels)):
        curr_label = original_labels[i]
        encoded_map[i] = curr_label
    
    return encoded_map
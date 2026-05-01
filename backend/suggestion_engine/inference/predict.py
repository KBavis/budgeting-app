import logging

import numpy as np
from scipy.special import softmax

from suggestion_engine.inference.schema import CategorySuggestion
from suggestion_engine.training.data import prepare_input
import joblib
import pandas as pd

logger = logging.getLogger(__name__)


def predict_category(user_id, transaction, onnx_model):
    """
    Run ONNX inference for a single transaction and return the predicted category
    along with a per-prediction confidence score.

    The confidence is the softmax probability of the top-1 predicted class —
    a value in [0, 1] that reflects how certain the model is about *this*
    specific transaction, as opposed to the global training accuracy.

    Args:
        user_id (int): used to locate the user's preprocessing artifacts
        transaction (TransactionMetadata): the transaction to predict for
        onnx_model (ort.InferenceSession): the loaded ONNX model

    Returns:
        CategorySuggestion
    """

    # load preprocessing artifacts saved during training
    try:
        preprocessor = joblib.load(f'suggestion_engine/artifacts/{user_id}/preprocessor.joblib')
        label_encoder = joblib.load(f"suggestion_engine/artifacts/{user_id}/label_encoder.joblib")
    except Exception as e:
        logger.error(f"An unexpected error occurred while loading the preprocessor and label encoder for user {user_id} with error: {e}")
        raise Exception(f"An unexpected error occurred while loading the preprocessor and label encoder for user {user_id} with error: {e}")

    # prepare input — must match the column layout used during training
    txs = [transaction.dict()]
    features, _ = prepare_input(txs)
    df = pd.DataFrame(features, columns=['amount', 'hour', 'day', 'merchant', 'primary_category', 'detailed_category'])
    Xs = preprocessor.transform(df)

    # cast to float32 as required by ONNX runtime
    input_data = Xs.astype(np.float32)

    # run inference
    input_name = onnx_model.get_inputs()[0].name
    inference = onnx_model.run(None, {input_name: input_data})
    if not inference:
        raise Exception('An unexpected failure occured while making inference using ONNX model')

    
    """
    TODO: Now that we have the actual confidence level of a particular prediction, we should enhance our 
    current logic to account for this confidence score. For example, if we have a 95% confidence level 
    for a prediction, we should automatically assign the transaction to that category, instead of 
    having the user confirm the category. If it's lower than X percentage, we should have them confirm it 
    """


    logits = inference[0] # extract raw model output (logits)
    probs = softmax(logits, axis=1) # convert logits to probabilities
    predicted_class = int(np.argmax(probs, axis=1)[0]) # find index of the highest probability value (the model's prediction)
    per_prediction_confidence = float(probs[0][predicted_class]) # extract the probability of the predicted class 

    # decode integer class index back to the user's category_id
    encoded_map = {i: cls for i, cls in enumerate(label_encoder.classes_)}
    category_id = encoded_map[predicted_class]

    return CategorySuggestion(category_id=category_id, confidence=per_prediction_confidence, source="PERSONAL_MODE")

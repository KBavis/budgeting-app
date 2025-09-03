from sklearn.compose import ColumnTransformer
from sklearn.feature_extraction.text import HashingVectorizer
from sklearn.preprocessing import StandardScaler, OneHotEncoder, FunctionTransformer
from sklearn.pipeline import Pipeline
import pandas as pd
import numpy as np
from suggestion_engine.training.preprocess.utils import get_day_of_week, get_hour, extract_text


def prepare_input(transactions: list):


    features = []
    labels = []
    for tx in transactions:
        features.append([
            tx.get('amount', 0.0),
            get_hour(tx.get('date_time', None)), 
            get_day_of_week(tx.get('date_time', None)),
            tx['merchant'] if 'merchant' in tx and tx['merchant'] else 'UNKNOWN',
            tx['plaid_primary_category'] if 'plaid_primary_category' in tx and tx['plaid_primary_category'] else 'UNKNOWN',
            tx['plaid_detailed_category'] if 'plaid_detailed_category' in tx and tx['plaid_detailed_category'] else 'UNKNOWN',
        ])
        labels.append(tx.get('category_id', None))
    

    return features, labels



def preprocess(transactions: list):
    """
    Preprocess user transactions prior to training

    Args:
        transactions (list): list of transactions to pre-process
    """
    


    # extract features and labels
    features, labels = prepare_input(transactions)


    # create column specific processor 
    preprocessor = ColumnTransformer([
        ('amount_scaler', StandardScaler(), ['amount']), # scale only the transaction amount 
        ('time_features', 'passthrough', ['hour', 'day']), # hour/day normalized already 


        ('merchant_hasher', 
            Pipeline([
                ('extractor', FunctionTransformer(extract_text, kw_args={'column': 'merchant'}, validate=False)), # extract text as a single 1D array prior to passing to hasher
                ('hasher', HashingVectorizer(n_features=100)) # transform to vectors 
            ]),
            ['merchant'] 
        ),

        ('plaid_encoder', OneHotEncoder(handle_unknown='ignore'), ['primary_category', 'detailed_category']), # binary encoding for each unique plaid primary category / detailed category  
    ], remainder='drop', sparse_threshold=0)

    X = pd.DataFrame(features, columns=['amount', 'hour', 'day', 'merchant', 'primary_category', 'detailed_category'])
    y = np.array(labels)


    return preprocessor.fit_transform(X), y, preprocessor
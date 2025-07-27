from dotenv import load_dotenv
import argparse
import db
from sklearn.compose import ColumnTransformer
from sklearn.feature_extraction.text import HashingVectorizer
from sklearn.preprocessing import StandardScaler, OneHotEncoder, FunctionTransformer
from sklearn.pipeline import Pipeline
import numpy as np
import pandas as pd

def main(user_id):
    
    # fetch user transactions
    transactions = fetch_user_transactions(user_id) 

    # preprocess users transactions
    X, y, preprocessor = preprocess(transactions)

    # create data loaders 


    # train/test model 

    # save artifacts 


def create_data_loaders(X, y, test_size=0.2, batch_size=32):
    return None


def preprocess(transactions: list):

    def get_hour(timestamp):
        if timestamp is None:
            return 0.0  # default midnight
        return timestamp.hour / 24.0


    def get_day_of_week(timestamp):
        if timestamp is None:
            return 0.0  # default to Monday
        return timestamp.weekday() / 6.0  
    
    def extract_text(X, column):
        return X[column].values


    # extract features and labels
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
        labels.append(tx['category_id']) # NOTE: This should never be missing given our query 

    

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
    ], remainder='drop')

    X = pd.DataFrame(features, columns=['amount', 'hour', 'day', 'merchant', 'primary_category', 'detailed_category'])
    y = np.array(labels)


    return preprocessor.fit_transform(X), y, preprocessor


def save_artifacts(model, preprocessor, ):
    return None


def fetch_user_transactions(user_id: int):
    connection = db.connect_db()
    return db.fetch_transactions(user_id, connection)


    
def parse_args():
    """
    Parse command line args

    Returns:
        parsed args
    """

    parser = argparse.ArgumentParser()

    parser.add_argument(
        "--user",
        type=int,
        required=True,
        help='User ID to train personal model for'
    )
    
    return parser.parse_args()



if __name__ == "__main__":
    load_dotenv()
    cl_args = parse_args()
    main(cl_args.user)

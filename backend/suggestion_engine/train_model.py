from dotenv import load_dotenv
import argparse
import db
from sklearn.compose import ColumnTransformer
from sklearn.feature_extraction.text import HashingVectorizer
from sklearn.preprocessing import StandardScaler, OneHotEncoder

def main(user_id):
    
    # fetch user transactions
    transactions = fetch_user_transactions(user_id) 

    # preprocess users transactions
    X, y = preprocess(transactions)

    # create data loaders 

    # train/test model 

    # save artifacts 

def preprocess(transactions: list):

    def get_hour(timestamp):
        if timestamp is None:
            return 0.0  # default midnight
        return timestamp.hour / 24.0


    def get_day_of_week(timestamp):
        if timestamp is None:
            return 0.0  # default to Monday
        return timestamp.weekday() / 6.0  


    # extract features and labels
    features = []
    labels = []
    for tx in transactions:
        features.append([
            tx.get('amount', 0.0),
            get_hour(tx.get('date_time', None)), 
            get_day_of_week(tx.get('date_time'), None),
            tx.get('merchant_name', 'UNKNOWN'),
            tx.get('plaid_primary_category', 'MISSING'),
            tx.get('plaid_detailed_category', 'MISSING'),
        ])
        labels.append(tx['category_id']) # NOTE: This should never be missing given our query 

    

    # create column specific processor 
    preprocessor = ColumnTransformer([
        ('amount_scaler', StandardScaler(), [0]), # scale only the transaction amount 
        ('time_features', 'passthrough', [1, 2]), # hour/day normalized already 
        ('merchant_hasher', HashingVectorizer(n_features=100), [3]) # transform to vectors 
        ('plaid_encoder', OneHotEncoder(handle_unknown='ignore'), [4, 5]), # binary encoding for each unique plaid primary category / detailed category  
    ], remainder='drop')

    return None, None


def save_artifacts():
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

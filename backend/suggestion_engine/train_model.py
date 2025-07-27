from dotenv import load_dotenv
import argparse
import db
from sklearn.compose import ColumnTransformer
from sklearn.feature_extraction.text import HashingVectorizer
from sklearn.preprocessing import StandardScaler, OneHotEncoder, FunctionTransformer
from sklearn.model_selection import train_test_split
from classifer import CategoryPredictor
from torch.utils.data import DataLoader, TensorDataset
import torch
from torch import nn
from sklearn.pipeline import Pipeline
import numpy as np
import pandas as pd

def main(user_id):
    
    # fetch user transactions
    transactions = fetch_user_transactions(user_id) 

    # preprocess users transactions
    X, y, preprocessor = preprocess(transactions)
    num_classes = len(np.unique(y))

    # create data loaders 
    train_dataloader, test_dataloader = create_data_loaders(X, y)

    # create model 
    model = CategoryPredictor(X.shape[1], num_categories=num_classes)

    # train/test model 
    optimization_loop(train_dataloader, test_dataloader, model)

    # save artifacts 

def optimization_loop(train_data_loader, teset_data_loader, model):

    loss_fn = nn.CrossEntropyLoss()
    optimizer = torch.optim.Adam(model.parameters(), lr=0.001)

    epochs = 250
    best_test_loss = float('inf')
    counter = 0
    patience = 5


    def train_loop():
        #TODO: Implement me 
        return


    def test_loop():
        #TODO: Implement me 
        return 

    for training_iteration in range(epochs):
        print(f"Starting Epoch {training_iteration + 1}\n--------------------------")
        train_loop()
        test_loss = test_loop()


        if test_loss < best_test_loss:
            best_test_loss = test_loss
            counter = 0
        else:
            counter += 1
            if counter >= patience:
                print(f"Test loss plateaued; best loss acheived was {best_test_loss}")
                break
    
    return model
        
    


def create_data_loaders(X, y, test_size=0.2, batch_size=32):

    # split data
    X_train, X_val, y_train, y_val = train_test_split(
        X, y, test_size=test_size, random_state=42
    )

    train_dataset = TensorDataset(
        torch.from_numpy(X_train).float(),
        torch.from_numpy(y_train).long()
    )

    test_dataset = TensorDataset(
        torch.from_numpy(X_val).float(),
        torch.from_numpy(y_val).long()
    )

    train_loader = DataLoader(train_dataset, batch_size=batch_size, shuffle=True)
    test_loader = DataLoader(test_dataset, batch_size=batch_size)

    return train_loader, test_loader


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
    ], remainder='drop', sparse_threshold=0)

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

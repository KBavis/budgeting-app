from dotenv import load_dotenv
import argparse
import db
from sklearn.compose import ColumnTransformer
from sklearn.feature_extraction.text import HashingVectorizer
from sklearn.preprocessing import StandardScaler, OneHotEncoder, FunctionTransformer, LabelEncoder
from sklearn.model_selection import train_test_split
from classifer import CategoryPredictor
from torch.utils.data import DataLoader, TensorDataset
import torch
from torch import nn
from sklearn.pipeline import Pipeline
import numpy as np
import pandas as pd
import os
import joblib
import json
from datetime import datetime

def main(user_id):
    """
    Retrieve user transactions, preprocess data, and train/evalute users personal neural network 

    Args:
        user_id (long): user ID 
    """
    
    # fetch user transactions
    transactions = fetch_user_transactions(user_id) 

    # preprocess users transactions
    X, y, preprocessor = preprocess(transactions)

    # create data loaders 
    train_dataloader, test_dataloader, label_encoder = create_data_loaders(X, y)
    num_classes = len(label_encoder.classes_)

    # create model 
    model = CategoryPredictor(X.shape[1], num_categories=num_classes)

    # train/test model 
    best_model = optimization_loop(train_dataloader, test_dataloader, model)

    # save artifacts 
    save_artifacts(best_model, preprocessor, label_encoder, user_id)



def optimization_loop(train_data_loader, test_data_loader, model):
    """
    Loop through training and testing of our model

    Args:
        train_data_loader (): 
        test_data_loader (_type_): _description_
        model (_type_): _description_

    Returns:
        _type_: _description_
    """

    loss_fn = nn.CrossEntropyLoss()
    optimizer = torch.optim.Adam(model.parameters(), lr=0.001)

    epochs = 250
    best_test_loss = float('inf')
    counter = 0
    patience = 5


    def train_loop():
        """
        Training loop for our neural network 
        """

        model.train()

        size = len(train_data_loader)
        batch_size = train_data_loader.batch_size or 32

        for batch, (X,y) in enumerate(train_data_loader):
            
            inputs = X.float()
            labels = y.long()

            pred = model(inputs)
            loss = loss_fn(pred, labels)

            # back propagation 
            loss.backward()
            optimizer.step()
            optimizer.zero_grad()

            if batch % 10 == 0:
                current = batch * batch_size
                print(f"[Train] Batch {batch:03d} - Loss: {loss.item():.4f}  ({current}/{size} samples)")




    def test_loop():
        """
        Evaluation loop of our model 
        """

        model.eval()
        size = len(test_data_loader.dataset)
        correct = 0 
        test_loss = 0

        with torch.no_grad():
            for X, y in test_data_loader:
                inputs = X.float()
                labels = y.long()
                
                pred = model(inputs)
                test_loss += loss_fn(pred, labels).item()

                predicted_classes = torch.argmax(pred, dim=1)
                correct += (predicted_classes == labels).sum().item()
        
        avg_loss = test_loss / len(test_data_loader)
        accuracy = 100 * correct / size

        print("\n[Test Results]")
        print(f" Accuracy  : {accuracy:.2f}%")
        print(f" Avg Loss  : {avg_loss:.4f}\n")

        return avg_loss


    
    best_model = model

    for training_iteration in range(epochs):
        print(f"Starting Epoch {training_iteration + 1}\n--------------------------")
        train_loop()
        test_loss = test_loop()


        if test_loss < best_test_loss:
            best_model = model
            best_test_loss = test_loss
            counter = 0
        else:
            counter += 1
            if counter >= patience:
                print(f"Test loss plateaued; best loss acheived was {best_test_loss}")
                break
    
    return best_model
        
    


def create_data_loaders(X, y, test_size=0.2, batch_size=32):
    """
    Generate relevant training and testing data loaders and encoded labels

    Args:
        X (pd.Dataframe): inputs
        y (np.array): outputs
        test_size (float, optional): testing size. Defaults to 0.2.
        batch_size (int, optional): batch size. Defaults to 32.

    Returns:
        _type_: _description_
    """

    le = LabelEncoder()
    y_encoded = le.fit_transform(y)

    # split data
    X_train, X_val, y_train, y_val = train_test_split(
        X, y_encoded, test_size=test_size, random_state=42
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

    return train_loader, test_loader, le


def extract_text(X, column):
    return X[column].values

def preprocess(transactions: list):
    """
    Preprocess user transactions prior to training

    Args:
        transactions (list): list of transactions to pre-process
    """

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





def save_artifacts(model, preprocessor, label_encoder, user_id):
    """
    Save relevant training and testing artifacts on server 

    Args:
        model (nn.Module): neural network model 
        preprocessor (ColumnTransformer): preprocessor of our columns
        label_encoder (LabelEncoder): label encoder 
        user_id (long): user ID
    """
    
    def get_input_dim(model):
        for layer in model.modules():
            if isinstance(layer, nn.Linear):
                return layer.in_features
        raise AttributeError("No nn.Linear layer found in the model")
    

    dir = f"../models/{user_id}"
    os.makedirs(dir, exist_ok=True)
    os.chdir(dir)

    # save model 
    torch.save(model.state_dict(), "model_weights.pth")

    # save preprocesing pipeline 
    joblib.dump(preprocessor, "preprocessor.joblib")

    # save label encoder 
    joblib.dump(label_encoder, "label_encoder.joblib")

    # save meta data 
    metadata = {
        'trained_at': datetime.now().isoformat(),
        'num_classes': len(label_encoder.classes_),
        'input_dim': get_input_dim(model)
    }
    with open("metadata.json", "w") as f:
        json.dump(metadata, f)


def fetch_user_transactions(user_id: int):
    """
    Retrieve relevant users transactions from DB

    Args:
        user_id (int):  user to retrieve transactions for 

    Returns:
        list: user trnasactions
    """
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

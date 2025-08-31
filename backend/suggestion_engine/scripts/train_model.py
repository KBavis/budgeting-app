from dotenv import load_dotenv
import argparse
from suggestion_engine import db
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from suggestion_engine.models.classifer import CategoryPredictor
from torch.utils.data import DataLoader, TensorDataset
import torch
from torch import nn
import os
import joblib
import json
from datetime import datetime
from suggestion_engine.data import preprocess

def main(user_id):
    """
    Retrieve user transactions, preprocess data, and train/evalute users personal neural network 

    Args:
        user_id (int): user ID 
    """
    
    # fetch user transactions
    transactions = fetch_user_transactions(user_id) #TODO: Skip training model on users if < 50 transactions categorized
    print(f"Successfully retrieved {len(transactions)} user transactions to train model on")

    # preprocess users transactions
    X, y, preprocessor = preprocess(transactions)

    # create data loaders 
    train_dataloader, test_dataloader, label_encoder = create_data_loaders(X, y)
    num_classes = len(label_encoder.classes_)

    # create model 
    model = CategoryPredictor(X.shape[1], num_categories=num_classes)

    # train/test model 
    best_model, best_accuracy = optimization_loop(train_dataloader, test_dataloader, model)

    # save artifacts 
    save_artifacts(best_model, preprocessor, label_encoder, best_accuracy, user_id)



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

        total_samples = len(train_data_loader.dataset)
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
                print(f"[Train] Batch {batch:03d} - Loss: {loss.item():.4f}  ({current}/{total_samples} samples)")




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

        return avg_loss, accuracy


    
    best_model = model
    best_accuracy = None

    for training_iteration in range(epochs):
        print(f"Starting Epoch {training_iteration + 1}\n--------------------------")
        train_loop()
        test_loss, test_accuracy = test_loop()


        if test_loss < best_test_loss:
            best_model = model
            best_test_loss = test_loss
            best_accuracy = test_accuracy
            counter = 0
        else:
            counter += 1
            if counter >= patience:
                print(f"Test loss plateaued; best loss acheived was {best_test_loss}")
                break
    
    return best_model, best_accuracy
        
    


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


def save_artifacts(model, preprocessor, label_encoder, best_accuracy, user_id):
    """
    Save relevant training and testing artifacts on server 

    Args:
        model (nn.Module): neural network model 
        preprocessor (ColumnTransformer): preprocessor of our columns
        label_encoder (LabelEncoder): label encoder 
        best_accuracy (float): testing accuracy achieved with the model with the best loss
        user_id (long): user ID
    """
    
    def get_input_dim(model):
        for layer in model.modules():
            if isinstance(layer, nn.Linear):
                return layer.in_features
        raise AttributeError("No nn.Linear layer found in the model")
    

    dir = f"suggestion_engine/artifacts/{user_id}"
    os.makedirs(dir, exist_ok=True)
    os.chdir(dir)

    print(f"Attempting to save relevant training/testing artifacts in {os.getcwd()}")

    # save model weights
    torch.save(model.state_dict(), "model_weights.pth")

    # save preprocesing pipeline 
    joblib.dump(preprocessor, "preprocessor.joblib")

    # save label encoder 
    joblib.dump(label_encoder, "label_encoder.joblib")

    # save meta data 
    model_input_dim = get_input_dim(model)
    metadata = {
        'trained_at': datetime.now().isoformat(),
        'num_classes': len(label_encoder.classes_),
        'input_dim': model_input_dim,
        'accuracy': round(best_accuracy, 2)
    }
    with open("metadata.json", "w") as f:
        json.dump(metadata, f)
    

    # save model using ONNX
    dummy_input = (torch.randn(1, model_input_dim),)  # Changed variable name
    torch.onnx.export(
        model, 
        dummy_input, 
        "model.onnx", 
        export_params=True,
        opset_version=11, 
        do_constant_folding=True,
        input_names=['input'],
        output_names=['output']
    )


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

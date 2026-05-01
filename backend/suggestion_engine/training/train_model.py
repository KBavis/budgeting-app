from dotenv import load_dotenv
import argparse
from suggestion_engine.training import db
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.utils.class_weight import compute_class_weight
from suggestion_engine.training.classifier import CategoryPredictor
from torch.utils.data import DataLoader, TensorDataset
import torch
from torch import nn
import numpy as np
import os
import joblib
import json
import logging 
from datetime import datetime
from suggestion_engine.training.data import preprocess

logger = logging.getLogger(__name__)

def main(user_id):
    """
    Retrieve user transactions, preprocess data, and train/evalute users personal neural network 

    Args:
        user_id (int): user ID 
    """

    now = datetime.now().strftime("%m/%d/%Y %H:%M:%S")
    logger.info("\n\n----------------------------\nTraining started for user_id=%s at %s\n----------------------------", user_id, now)
    
    # fetch user transactions
    transactions = fetch_user_transactions(user_id) #TODO: Skip training model on users if < 50 transactions categorized
    logger.info("Retrieved %d transactions to train model on for user_id=%s", len(transactions), user_id)

    # preprocess users transactions
    X, y, preprocessor = preprocess(transactions)

    # create data loaders 
    train_dataloader, test_dataloader, label_encoder, class_weights = create_data_loaders(X, y)
    num_classes = len(label_encoder.classes_)

    # create model 
    model = CategoryPredictor(X.shape[1], num_categories=num_classes)
    logger.info("Created PyTorch Model for user_id=%s with %d features and %d classes", user_id, X.shape[1], num_classes)

    # train/test model 
    logger.info("Starting Optimization Loop for user_id=%s", user_id)
    best_model, best_accuracy = optimization_loop(train_dataloader, test_dataloader, model, class_weights)

    # save artifacts 
    logger.info("Saving Model artifacts for user_id=%s", user_id)
    save_artifacts(best_model, preprocessor, label_encoder, best_accuracy, user_id)



def optimization_loop(train_data_loader, test_data_loader, model, class_weights):
    """
    Loop through training and testing of our model.

    Args:
        train_data_loader (DataLoader): training batches
        test_data_loader (DataLoader): validation batches
        model (nn.Module): the CategoryPredictor to train
        class_weights (torch.Tensor): per-class weights to counteract label imbalance

    Returns:
        best_model (nn.Module): model snapshot with the lowest validation loss
        best_accuracy (float): validation accuracy (%) at that checkpoint
    """
    

    loss_fn = nn.CrossEntropyLoss(weight=class_weights) # rare categories are weighted higher (model forced to learn about minority categories)
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
                logger.info("[Train] Batch %03d - Loss: %.4f  (%d/%d samples)", batch, loss.item(), current, total_samples)




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

        logger.info("\n[Test Results]\n Accuracy  : %.2f%%\n Avg Loss  : %.4f\n", accuracy, avg_loss)

        return avg_loss, accuracy


    
    best_model = model
    best_accuracy = None

    for training_iteration in range(epochs):
        logger.info("Starting Epoch %d\n--------------------------", training_iteration + 1)
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
                logger.info("Test loss plateaued; best loss achieved was %.4f", best_test_loss)
                break
    
    return best_model, best_accuracy
        
    


def create_data_loaders(X, y, test_size=0.2, batch_size=32):
    """
    Generate training/validation data loaders, a fitted LabelEncoder, and
    class weights for imbalanced category distributions.

    Class weights are computed to account for the fact that we expect 
    imbalanced Category distributions (i.e 400+ transactions for Food & Dining, 
    but only 5 transactions for a niche category). This causes the 
    loss function (CrossEntropyLoss) to discover shortcut which is 
    always predicting frequently used Categories.


    Args:
        X (np.ndarray): preprocessed feature matrix
        y (np.ndarray): raw category_id labels
        test_size (float): fraction of data reserved for validation (default 0.2)
        batch_size (int): mini-batch size (default 32)

    Returns:
        train_loader (DataLoader)
        test_loader (DataLoader)
        le (LabelEncoder): fitted encoder for decoding predicted class indices
        class_weights (torch.Tensor): per-class weights tensor for CrossEntropyLoss
    """

    le = LabelEncoder()
    y_encoded = le.fit_transform(y)

    # compute class weights on all user data to ensure the weight tensor 
    classes = np.unique(y_encoded)
    weights = compute_class_weight('balanced', classes=classes, y=y_encoded)
    class_weights = torch.tensor(weights, dtype=torch.float)

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

    return train_loader, test_loader, le, class_weights


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

    logger.info("Attempting to save relevant training/testing artifacts in %s", os.getcwd())

    try:
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

        logger.info("Successfully saved model artifacts for user_id=%s", user_id)

    except Exception as e:
        logger.error("Error saving model artifacts for user_id=%s", user_id)
        logger.error("Error message: %s", e)
        raise


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
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    )
    cl_args = parse_args()
    main(cl_args.user)

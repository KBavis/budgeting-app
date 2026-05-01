from sklearn.compose import ColumnTransformer
from sklearn.feature_extraction.text import HashingVectorizer
from sklearn.preprocessing import StandardScaler, OneHotEncoder, FunctionTransformer
from sklearn.pipeline import Pipeline
import pandas as pd
import numpy as np


# ---------------------------------------------------------------------------
# Utility helpers (previously in preprocess/utils.py)
# ---------------------------------------------------------------------------

def extract_text(X, column):
    """Extract a single column from a DataFrame as a 1D array for HashingVectorizer."""
    return X[column].values


def get_hour(timestamp) -> float:
    """Normalize hour of day to [0, 1]. Returns 0.0 if timestamp is None."""
    if timestamp is None:
        return 0.0
    return timestamp.hour / 24.0


def get_day_of_week(timestamp) -> float:
    """Normalize day of week to [0, 1]. Returns 0.0 (Monday) if timestamp is None."""
    if timestamp is None:
        return 0.0
    return timestamp.weekday() / 6.0


# ---------------------------------------------------------------------------
# Feature extraction
# ---------------------------------------------------------------------------

def prepare_input(transactions: list):
    """
    Convert a list of transaction dicts into a feature matrix and label array.

    Each transaction dict is expected to have the following keys:
        - amount (float)
        - date_time (datetime | None)
        - merchant (str | None)
        - plaid_primary_category (str | None)
        - plaid_detailed_category (str | None)
        - category_id (int | None)  — only present during training

    Returns:
        features (list[list]): raw feature rows
        labels (list): category_id per row (None values during inference)
    """
    features = []
    labels = []

    for tx in transactions:
        features.append([
            tx.get('amount', 0.0),
            get_hour(tx.get('date_time', None)), 
            get_day_of_week(tx.get('date_time', None)),
            tx.get('merchant') or 'UNKNOWN',
            tx.get('plaid_primary_category') or 'UNKNOWN',
            tx.get('plaid_detailed_category') or 'UNKNOWN',
        ])
        labels.append(tx.get('category_id', None))

    return features, labels


# ---------------------------------------------------------------------------
# Preprocessing pipeline
# ---------------------------------------------------------------------------

def preprocess(transactions: list):
    """
    Build and fit the full preprocessing pipeline on training transactions.

    Feature engineering:
        - amount         → StandardScaler (zero-mean, unit-variance)
        - hour / day     → passthrough (already normalized to [0, 1])
        - merchant       → HashingVectorizer (50 buckets, L2-normalized)
                           50 buckets is appropriate for a personal dataset;
                           L2 norm prevents merchant features from dominating
                           amount/time features due to vector magnitude.
        - primary_category / detailed_category → OneHotEncoder

    Args:
        transactions (list): raw transaction dicts from db.fetch_transactions()

    Returns:
        X_transformed (np.ndarray): preprocessed feature matrix
        y (np.ndarray): label array (category_id per row)
        preprocessor (ColumnTransformer): fitted transformer for later inference
    """

    features, labels = prepare_input(transactions)

    preprocessor = ColumnTransformer([
        ('amount_scaler', StandardScaler(), ['amount']),
        ('time_features', 'passthrough', ['hour', 'day']),

        ('merchant_hasher', 
            Pipeline([
                # extract the column as a 1D array before passing to HashingVectorizer
                ('extractor', FunctionTransformer(extract_text, kw_args={'column': 'merchant'}, validate=False)),
                # 50 features is sufficient for a personal dataset;
                # norm='l2' ensures consistent vector magnitudes across merchants
                ('hasher', HashingVectorizer(n_features=50, norm='l2')),
            ]),
            ['merchant'],
        ),

        ('plaid_encoder', OneHotEncoder(handle_unknown='ignore'), ['primary_category', 'detailed_category']),
    ], remainder='drop', sparse_threshold=0)

    X = pd.DataFrame(features, columns=['amount', 'hour', 'day', 'merchant', 'primary_category', 'detailed_category'])
    y = np.array(labels)

    return preprocessor.fit_transform(X), y, preprocessor

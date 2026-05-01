import torch.nn as nn 
import torch


class CategoryPredictor(nn.Module): 
    """
    Neural network for predicting a transaction's budget category.
    """

    def __init__(self, input_size: int, num_categories: int):
        super().__init__()
        self.layers = nn.Sequential(
            nn.Linear(input_size, 128),
            nn.ReLU(),
            nn.Dropout(0.3),
            nn.Linear(128, 64),
            nn.ReLU(),
            nn.Dropout(0.3),
            nn.Linear(64, num_categories)
        )

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        return self.layers(x)

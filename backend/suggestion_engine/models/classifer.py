import torch.nn as nn 
import torch

class CategoryPredictor(nn.Module): 
    def __init__(self, input_size, num_categories):
        super().__init__()
        self.layers = nn.Sequential(
            nn.Linear(input_size, 32),
            nn.Dropout(),
            nn.ReLU(),
            nn.Linear(32, 64),
            nn.Dropout(),
            nn.ReLU(),
            nn.Linear(64, num_categories)
        )
    

    def forward(self, x: torch.Tensor):
        return self.layers(x)
    
import gc
from backend.suggestion_engine.training.models.classifer import CategoryPredictor
from pathlib import Path
import json
import torch

class ModelManager:

    def __init__(self) -> None:
        self._user_models = {} 
        self._max_capacity = 2
        self.model_accuracy = {}
    

    def get_model(self, user_id):

        # return model if already in memory 
        if user_id in self._user_models:
            return self._user_models[user_id]

        # clear in-memory data strucutre if at capacity
        if len(self._user_models) > self._max_capacity:
            self._user_models.clear()
            self.model_accuracy.clear()
            gc.collect()

        
        # validate model persisted corresponding to user 
        model_weights_path = Path(f"suggestion_engine/artifacts/{user_id}/model_weights.pth")
        if not model_weights_path.is_file():
            return None 

        # fetch model meta data 
        meta_data = self.load_model_meta_data(user_id)

        # create model in memory & store in cache
        nn = CategoryPredictor(meta_data["input_dim"], meta_data["num_classes"])
        nn.load_state_dict(torch.load(str(model_weights_path), weights_only=True))
        nn.eval()

        self._user_models[user_id] = nn
        self.model_accuracy[user_id] = meta_data['accuracy']

        gc.collect() # force garabge collection

        return nn
    

    def load_model_meta_data(self, user_id):
        meta_data_path_str = f"suggestion_engine/artifacts/{user_id}/metadata.json"

        with open(meta_data_path_str, "r") as metadata:
            return json.load(metadata)




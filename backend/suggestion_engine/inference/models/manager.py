import gc
from pathlib import Path
import json
import onnxruntime as ort
from datetime import datetime, time

class ModelManager:

    def __init__(self) -> None:
        self._user_models = {} 
        self._max_capacity = 2
        self.model_accuracy = {}
        self._cache_date = {}
    

    def get_model(self, user_id):

        # clear cache if necessary 
        self.clear_cache(user_id)

        # return model if already in memory 
        if user_id in self._user_models:
            return self._user_models[user_id]

        # clear in-memory data strucutre if at capacity
        if len(self._user_models) > self._max_capacity:
            self._user_models.clear()
            self.model_accuracy.clear()
            self._cache_date.clear()
            gc.collect()

        
        # validate model persisted corresponding to user 
        onnx_model_path = Path(f"suggestion_engine/artifacts/{user_id}/model.onnx")
        if not onnx_model_path.is_file():
            return None 
        
        # load onnx model 
        onnx_model = ort.InferenceSession(str(onnx_model_path))

        # fetch model meta data 
        meta_data = self.load_model_meta_data(user_id)

        self._user_models[user_id] = onnx_model
        self.model_accuracy[user_id] = meta_data['accuracy']
        self._cache_date[user_id] = datetime.now().date()

        gc.collect() # force garabge collection

        return onnx_model
    

    def is_cache_stale(self, user_id):
        """
        Helper function to determine if the cache is stale corresponding to a user 
        """
        if user_id not in self._cache_date:
            return True 

        now = datetime.now() 
        cache_date = self._cache_date[user_id]
        today = now.date()

        # if cached on differnet day, its stale 
        if cache_date != today:
            return True

        # cached today implies cache is fresh (nightly training at 2AM)
        return False 



    def clear_cache(self, user_id):
        """
        Clear cache for user specific data stored in memory 
        """

        if self.is_cache_stale(user_id):
            print(f"Cache is stale for user {user_id}, clearing user model from cache")

            if user_id in self._user_models:
                del self._user_models[user_id]
            if user_id in self.model_accuracy:
                del self.model_accuracy[user_id]
            if user_id in self._cache_date:
                del self._cache_date[user_id]

            gc.collect()

    

    def load_model_meta_data(self, user_id):
        meta_data_path_str = f"suggestion_engine/artifacts/{user_id}/metadata.json"

        with open(meta_data_path_str, "r") as metadata:
            return json.load(metadata)




from pathlib import Path
import json
import onnxruntime as ort
from datetime import datetime
import gc
import logging
import threading 

logger = logging.getLogger(__name__)

class ModelManager:

    def __init__(self) -> None:
        self._user_models = {} # dictionary mapping user_id to (onnx_model, model_accuracy, cache_date)
        self._max_capacity = 2
        self._lock = threading.Lock() 

    
    def get_model(self, user_id): 
        """
        Retrieve the ONNX Model corresponding to a particular User ID, as these models are trained on a per user basis.
            - account for cache eviction if cache is stale / at capacity 
            - utilized cached model if exists in memory 
        
            
        Args:
            user_id (int): User ID corresponding to the model being retrieved
        """

        today = datetime.now().date()

        # evict cached model if applicable 
        self._evict_stale_cache(user_id, today)

        # extract model from cache if available 
        with self._lock: 
            if user_id in self._user_models:
                logger.info(f"Model for user {user_id} retrieved from cache")
                return self._user_models[user_id] # return onnx model from cached tuple of (onnx_model, model_accuracy, cache_date)


            # check if model will exceed cache capacity (if so, clear entirety of cache)
            if len(self._user_models) >= self._max_capacity:
                logger.info(f"Cache is at capacity, clearing cache of all {len(self._user_models)} models")
                self._user_models.clear() 
                gc.collect() # force garbage collection to free up memory


        # validate ONNX model exists for User ID 
        onnx_model_path = Path(f"suggestion_engine/artifacts/{user_id}/model.onnx")
        if not onnx_model_path.is_file():
            logger.error(f"No model exists for user {user_id}; unable to retrieve model")
            return None 
        
        # load ONNX model
        try:
            onnx_model = ort.InferenceSession(str(onnx_model_path))
        except Exception as e:
            logger.error(f"An unexpected error occurred while loading the ONNX model for user {user_id} with error: {e}")
            return None
        
        
        # load meta data corresponding to user's model 
        meta_data = self._load_model_meta_data(user_id)
        if not meta_data:
            logger.error(f"An unexpected error occurred while loading the meta data for user {user_id}")
            return None

        # cache model & relevant meta data in memory (thread safe)
        with self._lock:
            self._user_models[user_id] = (onnx_model, meta_data['accuracy'], today)

        return onnx_model, meta_data['accuracy'], today # return tuple of (onnx_model, model_accuracy, cache_date)


    def _evict_stale_cache(self, user_id, today): 
        """
        Evict stale cache for a User if the cache is deemed stale based on when the Model was previously cached for this user 

        Args:
            user_id (int): User ID corresponding to the model being retrieved
            today (date): Current date used to compare against cache date to determine staleness of cache
        """

        with self._lock: # ensure thread safety when evicting cache

            # skip evicting if no model cached corresponding to user 
            if user_id not in self._user_models:
                logger.info(f"No cached model exists for user {user_id}, skipping eviction")
                return 

            # determine when the model corresponding to this user was last cached 
            _, _, cache_date = self._user_models[user_id]
            if today > cache_date:
                logger.info(f"Cache is stale for user {user_id}, as it was last cached on {cache_date}, evicting model from cache")
                del self._user_models[user_id]
                gc.collect() # force garbage collection to free up memory

            logger.info(f"Cache is fresh for user {user_id}, as it was last cached on {cache_date}, skipping eviction")
    

    def _load_model_meta_data(self, user_id):
        """
        Functionality to retreive the meta data corresponding to a particular user's model
            - this is necessary to determine the number of inputs the model expects (for inference) and the accuracy of the model (for contextualizing suggestions)

        Args:
            user_id (int): User ID corresponding to the model meta data being retrieved
        """

        try:
            meta_data_path_str = f"suggestion_engine/artifacts/{user_id}/metadata.json"
            with open(meta_data_path_str, "r") as metadata:
                return json.load(metadata)
        except Exception as e:
            logger.error(f"An error occurred while loading model meta data for user {user_id} with error: {e}")
            return None




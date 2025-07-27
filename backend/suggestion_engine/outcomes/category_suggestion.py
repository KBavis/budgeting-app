class CategorySuggestion:

    def __init__(self, category_id: int, confidence: float, source: str, reason: str = ""):
        self.category_id = category_id
        self.confidence = confidence
        self.source = source
        self.reasoning = reason
    

    def to_dict(self):
        return {
            "type": "categorized",
            "category_id": self.category_id,
            "confidence": round(self.confidence, 2),
            "source": self.source,
            "reasoning": self.reasoning
        }
    
    def is_high_confidence(self, threshold=0.7):
        return self.confidence >= threshold
    

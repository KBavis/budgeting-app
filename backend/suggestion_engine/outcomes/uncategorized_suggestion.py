from pydantic import BaseModel

class UncategorizedSuggestion(BaseModel):

    reasons: list = [] 
    type: str = "uncategorized"
    suggested_actions: list = []

    def to_dict(self):
        return {
            "type": "uncategorized",
            "reasons": self.reasons,
            "suggested_actions": self.suggested_actions
        }


    def add_reason(self, reason: str):
        self.reasons.append(reason)
        
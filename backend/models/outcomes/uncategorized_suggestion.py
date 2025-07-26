
class UncategorizedSuggestion:

    def __init__(self, reasons: list):
        self.reasons = reasons # list of reasons as to why the transaction is uncategorized 

    def to_dict(self):
        return {
            "type": "uncategorized",
            "reasons": self.reasons,
            "suggested_actions": self.suggested_actions
        }


    def add_reason(self, reason: str):
        self.reasons.append(reason)
        
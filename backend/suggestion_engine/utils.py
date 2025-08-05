
def extract_text(X, column):
    return X[column].values



def get_hour(timestamp):
    if timestamp is None:
        return 0.0  # default midnight
    return timestamp.hour / 24.0


def get_day_of_week(timestamp):
    if timestamp is None:
        return 0.0  # default to Monday
    return timestamp.weekday() / 6.0  
    
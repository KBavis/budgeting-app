import os
from psycopg2 import connect

def fetch_transactions(user_id, connection): 

    #TODO: Remove null check on plaid primary category once able to retrieve description for venmo transactions 
    query = """
        SELECT category_id, amount, date_time, merchant_name, plaid_detailed_category, plaid_primary_category
        FROM transaction 
        WHERE account_id IN (SELECT account_id FROM account WHERE user_id = %s)
        AND category_id IS NOT NULL AND plaid_primary_category IS NOT NULL
    """

    try:
        
        transactions = []

        with connection.cursor() as cur:
            cur.execute(query, (user_id,))
            rows = cur.fetchall()

            if not rows:
                print(f'No user transactions found for user_id {user_id}')
                return []

            for row in rows:
                transactions.append({"category_id": row[0], "amount": row[1], "date_time": row[2], "merchant_name": row[3], "plaid_detailed_category": row[4], "plaid_primary_category": row[5]})
        
        return transactions

    except Exception as e:
        print(f"An error occurred while retrieving transactions for user ID {user_id}")
        raise e 
    

def connect_db():
    """
    Connect our database

    Raises:
        ValueError: ensure all env variables present
        e: failure to establish connection

    Returns:
        connection (pyscopg.Connection): pg DB connection
    """

    try:
        db_name = os.getenv("DB_NAME")
        password = os.getenv("DB_PASS")
        host = os.getenv("DB_HOST")
        port = os.getenv("DB_PORT")
        user = os.getenv("DB_USER")

        if not all([db_name, password, host, port, user]):
            raise ValueError(
                "One or more required environment variables for establishing a db connection are missing."
            )

        return connect(
            database=db_name, user=user, password=password, host=host, port=port
        )

    except Exception as e:
        print(f"Failed to connect DB: {e}")
        raise e
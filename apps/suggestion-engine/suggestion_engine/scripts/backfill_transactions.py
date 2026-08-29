import argparse
import dotenv
import psycopg2
import os
import requests
from datetime import datetime

def backfill():
    """
    Main function to invoke necessary functionality to backfill 
    user's persisted transactiosn with relevant meta data 
    """
    
    dotenv.load_dotenv()
    args = parse_args()
    user_ids = args.users

    connection = connect_db()
    access_tokens = retrieve_user_access_tokens(user_ids, connection)

    for user_id, tokens in access_tokens.items():
        print(f"Beginning Backfill of Transactions for User {user_id}")


        # iterate through each user account
        for token in tokens:
            
            has_more = True 
            cursor = None 

            # continue syncing transactiosn for account until Plaid indicates o/w
            while has_more:

                # make request
                response_json = make_plaid_request(token, cursor)

                # update transactions meta data in DB
                added_transactions = response_json.get("added", [])
                modified_transactions = response_json.get("modified", [])
                update_transaction_meta_data(added_transactions + modified_transactions, connection)

                # update has more and cursor 
                has_more = response_json.get('has_more', False)
                cursor = response_json.get('next_cursor', None)


    print('Successfully backfilled transactions with relevant meta data')



def update_transaction_meta_data(transactions: list, connection):
    """
    Iterate through transactions in response and 

    Args:
        transactions (list): list of transactions to persist updates for 
        connection (pyscopg2.connection): pg DB connection
    """

    if not transactions:
        print("No added/modified transactions in response; skipping updates")
        return 
    
    for transaction in transactions:

        transaction_id = transaction['transaction_id']

        datetime = transaction['datetime']
        location = transaction['location']
        personal_finance_category = transaction['personal_finance_category']
        merchant_name = transaction['merchant_name']

        update_transaction(transaction_id, datetime, location, merchant_name, personal_finance_category, connection)


def update_transaction(transaction_id: str, datetime: datetime, location: dict, merchant_name: str, personal_finance_category: dict, connection):
    """
    Update transaction in our database 

    Args:
        transaction_id (str): relevant transaction to update
        datetime (datetime): datetime to correspond transaciton with 
        location (dict): location where transaction occured
        merchant_name (str): name of the merchant
        personal_finance_category (dict): plaid's determined personal finance category 
        connection (pyscopg2.connection): pg DB connection
    """

    query = """
        UPDATE transaction 
        SET 
            date_time = %s,
            address = %s,
            city = %s, 
            country = %s, 
            lat = %s,
            lon = %s, 
            postal_code = %s,
            region = %s,
            merchant_name = %s,
            plaid_detailed_category = %s,
            plaid_primary_category = %s,
            plaid_confidence_level = %s
        WHERE
            transaction_id = %s
    """

    try:
        address = location['address']
        city = location['city']
        country = location['country']
        lat = location['lat']
        lon = location['lon']
        postal_code = location['postal_code']
        region = location['region']
        plaid_detailed_category = personal_finance_category['detailed']
        plaid_primary_category = personal_finance_category['primary']
        plaid_confidence_level = personal_finance_category['confidence_level']

        with connection.cursor() as cur:
            cur.execute(query, (datetime, address, city, country, lat, lon, postal_code, region, merchant_name, plaid_detailed_category, plaid_primary_category, plaid_confidence_level, transaction_id))

            connection.commit()
            print(f"Successfully updated transaction {transaction_id} with relevant meta data")

    except Exception as e:
        print(f"An exception occurred while attempting to update transaction {transaction_id} with relevant meta data: {e}")
        raise e




def make_plaid_request(access_token: str, cursor: str = None):
    """
    Make request to Plaid API to Sync Transaction data for an Account

    Args:
        access_token (str): relevant access token corresponding to Plaid item
        cursor (str, optional): previous cursor to continue syncing item transactiosn from. Defaults to None.

    Returns:
        dict : json from response 
    """

    secret = os.getenv("PLAID_SECRET_KEY")
    client_id = os.getenv("PLAID_CLIENT_ID")

    data = {
        "client_id": client_id,
        "secret": secret,
        "access_token": access_token,
        "count": 100, 
        "cursor": cursor
    }
    url = "https://production.plaid.com/transactions/sync"
    headers = {
        "Content-Type": "application/json"
    }

    response = requests.post(url=url, json=data, headers=headers)

    if response.status_code != 200:
        print(f"Received {response.status_code} response code: {response.text}")
        raise Exception('Received non-200 response from Plaid API')
    
    return response.json() if response else None


def retrieve_user_access_tokens(user_ids: list, connection):
    """
    Retrieve access tokens for specified users with active accounts 

    Args:
        user_ids (list): specified user ids to update transactiosn for 
        connection (pyscopg.Connection): pg DB connection
    """

    placeholders = ','.join(['%s'] * len(user_ids))

    query = f"""
        SELECT access_token, user_id 
        FROM connection c 
        JOIN account a ON a.connection_id = c.connection_id 
        WHERE is_deleted = false AND user_id IN ({placeholders})
    """ 
    
    access_tokens = {}

    try:

        with connection.cursor() as cur:
            cur.execute(query, user_ids)
            rows = cur.fetchall()

            # mapping of user IDs to access tokens 
            for row in rows:
                user_id = row[1]
                token = row[0]

                if user_id not in access_tokens:
                    access_tokens[user_id] = []

                access_tokens[user_id].append(token)
            
            return access_tokens


    except Exception as e:
        print(f"An error occurred while retrieving user's access tokens: {e}")
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

        return psycopg2.connect(
            database=db_name, user=user, password=password, host=host, port=port
        )

    except Exception as e:
        print(f"Failed to connect DB: {e}")
        raise e



def parse_args():
    """
    Parse command line args

    Returns:
        parsed args
    """

    parser = argparse.ArgumentParser(prog="Bavis Budgeting")

    parser.add_argument(
        "--users",
        nargs='+',
        required=True,
        help='List of User IDs to backfill transaction meta data for'
    )
    
    return parser.parse_args()


if __name__ == "__main__":
    backfill()

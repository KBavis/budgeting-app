import argparse
from dotenv import load_dotenv
from psycopg2 import connect
import os

def backfill():

    args = parse_args()
    user_ids = args.users

    connection = connect_db()
    access_tokens = retrieve_user_access_tokens(user_ids, connection)



def retrieve_user_access_tokens(user_ids, connection):
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
    
    load_dotenv()

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
        logging.error(
            f"An exception occured while attempting to establish DB connection: {e}"
        )
        raise e



def parse_args():

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

import argparse
from dotenv import load_dotenv
from psycopg2 import connect
import logging
import os

def backfill():

    args = parse_args()
    connection = connect_db()




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
        help='List of User IDs to backfill transaction meta data for'
    )
    
    return parser.parse_args()

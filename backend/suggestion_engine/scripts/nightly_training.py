import os
import psycopg2
import dotenv
import asyncio

def nightly_training():
    """
    Script to re-generate users ONNX models based on any additional transactions that they have categorized 
    throughout the day
    """

    connection = connect_db()
    user_ids = get_user_ids(connection)
    asyncio.run(run_train_models(user_ids))


async def run_train_models(user_ids: list):
    """
    Async execution of 'train_model.py' for each relevant user in parallel

    Args:
        user_ids (list): _description_
    """
    





def get_user_ids(connection):
    """
    Fetch user ids pertainign to users with > 50 transactions categorized

    Args:
        connection (pyscopg.Connection): pg DB connection
    """

    query = f"""
        SELECT b.user_id 
        FROM budget_user b
        JOIN account a ON a.user_id = b.user_id 
        JOIN transaction t ON t.account_id = a.account_id 
        WHERE t.category_id IS NOT NULL
        GROUP BY b.user_id 
        HAVING COUNT(*) >= 50
    """ 

    try:

        with connection.cursor() as cur:
            cur.execute(query,)
            rows = cur.fetchall()

            user_ids = [row[0] for row in rows]
            return user_ids 


    except Exception as e:
        print(f"An error occurred while retrieving user IDs with > 50 transactions categorized: {e}")
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





if __name__ == "__main__":
    dotenv.load_dotenv()
    nightly_training()
import os
import psycopg2
import dotenv   
import asyncio
import logging
import sys
from datetime import datetime, timedelta
from pathlib import Path


# set up logging
timestamp = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler(f'suggestion_engine/logs/nightly_training_{timestamp}.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

def nightly_training():
    """
    Script to re-generate users ONNX models based on any additional transactions that they have categorized 
    throughout the day
    """

    try:
        cleanup_logs() 

        connection = connect_db()

        user_ids = get_user_ids(connection) 
        if not user_ids:
            logger.info("No users found with >= 50 categorized transactions")
            return 

        asyncio.run(run_train_models(user_ids))
    except Exception as e:
        logger.error(f"Nightly training script failure: {e}")
        raise 
    finally:
        if connection:
            connection.close()


async def run_train_models(user_ids: list):
    """
    Async execution of 'train_model.py' for each relevant user in parallel

    Args:
        user_ids (list): _description_
    """
    logger.info(f"Starting parallel nightly training for {len(user_ids)} users")

    # create relevant tasks to run later (work order) 
    tasks = [ asyncio.create_task(train_single_user_model(user_id)) for user_id in user_ids] 

    try:
        # run tasks concurrently (*tasks unpacks list of tasks to execute)
        results = await asyncio.gather(*tasks, return_exceptions=True)

        # process results 
        successful = 0
        failed = 0

        for i, result in enumerate(results):
            user_id = user_ids[i]

            if isinstance(result, Exception):
                logger.error(f"Nightly training has failed for user {user_id}: {result}")
                failed += 1
            else:
                logger.info(f"Nightly training was successful for user {user_id}")
                successful += 1 
        
        logger.info(f"Nightly training summary: {successful} successful, {failed} failures")

    except Exception as e:
        logger.error(f"Error during parallel training execution: {e}")
        raise
    

async def train_single_user_model(user: str):
    """
    Train model for a single user by calling train_model.py subprocess

    Args:
        user (str): user to invoke training for 
    """
    logger.info(f"Starting nightly training for user ID {user}")

    try:
        # create subprocess to run train_model.py
        process = await asyncio.create_subprocess_exec(
            sys.executable, # use same python interpreter 
            '-m', 'suggestion_engine.training.train_model',
            '--user', str(user), # user ID to pass as arg
            # redirrect stdout & stderr
            stdout=asyncio.subprocess.PIPE, 
            stderr=asyncio.subprocess.PIPE,
            cwd=os.path.join(os.getcwd()),  # project root, i.e. backend
        )

        stdout, stderr = await process.communicate()  # wait for child process to complete 

        # determine if process completed successfully & add relevant logs 
        if process.returncode == 0:
            logger.info(f"User {user} training completed successfully")
            if stdout:
                logger.info(f"--- Subprocess Output (User {user}) ---\n{stdout.decode().strip()}")
            if stderr:
                logger.info(f"--- Subprocess Stderr (User {user}) ---\n{stderr.decode().strip()}")
            return True
        else:
            stdout_msg = stdout.decode().strip() if stdout else ""
            stderr_msg = stderr.decode().strip() if stderr else f"Process exited with code {process.returncode}"
            logger.error(f"Training failed for user {user}")
            if stdout_msg:
                logger.error(f"Last Stdout:\n{stdout_msg}")
            if stderr_msg:
                logger.error(f"Last Stderr:\n{stderr_msg}")
            raise RuntimeError(f"Training script failed for user {user}")

    except asyncio.TimeoutError:
        logger.error(f"Training timeout for user {user}")
        raise
    except Exception as e:
        logger.error(f"Unexpected error training user {user}: {e}")
        raise


def cleanup_logs(days=15):
    """
    Remove log files older than `days` days in the specified log_dir.
    """
    log_dir = Path("suggestion_engine/logs")
    if not log_dir.exists():
        logger.info(f"Log directory {log_dir} does not exist, skipping cleanup.")
        return

    log_pattern = "nightly_training_*.log"
    cutoff = datetime.now() - timedelta(days=days)

    for file_path in log_dir.glob(log_pattern):
        try:
            file_time = datetime.fromtimestamp(file_path.stat().st_mtime)
            if file_time < cutoff:
                file_path.unlink()
                logger.info(f"Deleted old log: {file_path}")
        except Exception as e:
            logger.warning(f"Skipping {file_path}, error during cleanup: {e}")

    

def get_user_ids(connection):
    """
    Fetch user ids pertainings to users with > 50 transactions categorized

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
# build stage
FROM python:3.11-slim AS builder

# avoid bytecode generation, enforce stdout/stderr to be unbuffered, no cache dir when installing pckgs, disbale automatic version checks
ENV PYTHONDONTWRITEBYTECODE=1 \ 
    PYTHONUNBUFFERED=1 \
    PIP_NO_CACHE_DIR=1 \
    PIP_DISABLE_PIP_VERSION_CHECK=1

# change to build dir
WORKDIR /build

# copy & install necessary deps
COPY suggestion_engine/requirements.txt .
RUN pip install --user --no-warn-script-location -r requirements.txt


# production stage 
FROM python:3.11-slim AS production

# avoid bytecode generation, enforce stdout/stdder to be unbuffered, update PATH variable, indicate to Python interpreter where to look for moudle
ENV PYTHONDONTWRITEBYTECODE=1 \ 
    PYTHONUNBUFFERED=1 \
    PATH="/root/.local/bin:$PATH" \
    PYTHONPATH="/app"


# update working directory 
WORKDIR /app

# copy Python packages from builder stage
COPY --from=builder /root/.local /root/.local

# copy code
COPY . /app

# expose relevant port 
EXPOSE 8000

# run service
CMD ["uvicorn", "suggestion_engine.service:app", "--host", "0.0.0.0", "--port", "8000"]

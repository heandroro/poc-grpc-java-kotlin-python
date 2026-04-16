FROM python:3.12-slim AS base

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY ../proto /proto

RUN python -m grpc_tools.protoc \
      -I /proto \
      -I "$(python -c 'import grpc_tools, os; print(os.path.dirname(grpc_tools.__file__))')/_proto" \
      --python_out=src/generated \
      --grpc_python_out=src/generated \
      /proto/notification/v1/notification.proto \
      /proto/subscription/v1/subscription.proto \
      /proto/analytics/v1/analytics.proto \
    && touch src/generated/__init__.py \
           src/generated/notification/__init__.py \
           src/generated/notification/v1/__init__.py \
           src/generated/subscription/__init__.py \
           src/generated/subscription/v1/__init__.py \
           src/generated/analytics/__init__.py \
           src/generated/analytics/v1/__init__.py

COPY src ./src

EXPOSE 50053 9091

CMD ["python", "src/main.py"]

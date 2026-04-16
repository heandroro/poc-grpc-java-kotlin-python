#!/usr/bin/env bash
# Generates Python gRPC stubs from .proto files.
# Run from the analytics-service directory.
set -euo pipefail

PROTO_DIR="$(cd "$(dirname "$0")/../../proto" && pwd)"
OUT_DIR="$(cd "$(dirname "$0")/.." && pwd)/src/generated"

mkdir -p "${OUT_DIR}"
touch "${OUT_DIR}/__init__.py"

python -m grpc_tools.protoc \
  -I "${PROTO_DIR}" \
  -I "$(python -c 'import grpc_tools; import os; print(os.path.dirname(grpc_tools.__file__))')//_proto" \
  --python_out="${OUT_DIR}" \
  --grpc_python_out="${OUT_DIR}" \
  "${PROTO_DIR}/notification/v1/notification.proto" \
  "${PROTO_DIR}/subscription/v1/subscription.proto" \
  "${PROTO_DIR}/analytics/v1/analytics.proto"

echo "Proto stubs generated in ${OUT_DIR}"

#!/usr/bin/env bash
# Generates Python gRPC stubs from shared proto/ directory.
# Run from integration-tests/ or let conftest.py call it automatically.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROTO_DIR="${SCRIPT_DIR}/../proto"
OUT_DIR="${SCRIPT_DIR}/generated"

mkdir -p "${OUT_DIR}"

GRPC_TOOLS_PROTO="$(python -c 'import grpc_tools, os; print(os.path.dirname(grpc_tools.__file__))')/_proto"

python -m grpc_tools.protoc \
  -I "${PROTO_DIR}" \
  -I "${GRPC_TOOLS_PROTO}" \
  --python_out="${OUT_DIR}" \
  --grpc_python_out="${OUT_DIR}" \
  "${PROTO_DIR}/notification/v1/notification.proto" \
  "${PROTO_DIR}/subscription/v1/subscription.proto" \
  "${PROTO_DIR}/analytics/v1/analytics.proto"

touch "${OUT_DIR}/__init__.py" \
      "${OUT_DIR}/notification/__init__.py" \
      "${OUT_DIR}/notification/v1/__init__.py" \
      "${OUT_DIR}/subscription/__init__.py" \
      "${OUT_DIR}/subscription/v1/__init__.py" \
      "${OUT_DIR}/analytics/__init__.py" \
      "${OUT_DIR}/analytics/v1/__init__.py"

echo "Stubs generated in ${OUT_DIR}"

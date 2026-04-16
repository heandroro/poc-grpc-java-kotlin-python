.PHONY: test-java test-kotlin test-python test-all \
        lint-java lint-kotlin lint-python lint \
        test-integration

# ──────────────────────────────────────────────────────────
#  Java — notification-service
# ──────────────────────────────────────────────────────────
test-java:
	cd notification-service && mvn verify -q

lint-java:
	cd notification-service && mvn checkstyle:check -q

# ──────────────────────────────────────────────────────────
#  Kotlin — subscription-service
# ──────────────────────────────────────────────────────────
test-kotlin:
	cd subscription-service && ./gradlew koverVerify --info

lint-kotlin:
	cd subscription-service && ./gradlew ktlintCheck

# ──────────────────────────────────────────────────────────
#  Python — analytics-service
# ──────────────────────────────────────────────────────────
test-python:
	cd analytics-service && pip install -r requirements.txt -q && \
	pytest tests/ --cov=src --cov-report=term-missing --cov-fail-under=90

lint-python:
	cd analytics-service && ruff check src/ tests/

# ──────────────────────────────────────────────────────────
#  Aggregated
# ──────────────────────────────────────────────────────────
test-all: test-java test-kotlin test-python

lint: lint-java lint-kotlin lint-python

# ────────────────────────────────────────────────────────
#  Integration tests (Python + pytest + Testcontainers)
# ────────────────────────────────────────────────────────
test-integration:
	cd integration-tests && pip install -r requirements.txt -q && \
	bash generate_stubs.sh && \
	pytest test_e2e.py -v --timeout=120

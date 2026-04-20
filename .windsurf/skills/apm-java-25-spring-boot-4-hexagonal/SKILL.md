---
name: poc-apm
description: APM package for Java 25 and Spring Boot 4 with instructions, prompts, agents, and specialized sub-skills for quality, testing, performance, observability, and containerization.
---

# POC APM Package

Package-level guide for using this APM package in Java 25 + Spring Boot 4 projects.

## Overview

This package provides reusable guidance and workflows for modern Java applications built with Spring Boot 4 and hexagonal architecture.

It combines:

- Project-wide Java and Spring instructions
- Reusable review prompts
- Specialized agents for quality, testing, performance, and DevOps concerns
- Sub-skills under `.apm/skills/` that are promoted independently on install

## Included Capabilities

- `best-practices` for Java 25 idioms, SOLID, Clean Code, and rich domain modeling
- `code-quality` for static analysis and quality gates
- `unit-test`, `integration-test`, and `arch-test` for testing strategy
- `tuning` and `stress-test` for performance work
- `observability` for metrics, tracing, and logging
- `container` for Docker, Buildpacks, and deployment concerns
- `local-test` for local environment and developer workflow support

## How To Use

1. Install the package with `apm install heandroro/apm-java-25-spring-boot-4-hexagonal` in a consumer project.
2. Let APM deploy the primitives to the tool-native directories such as `.github/`, `.claude/`, `.cursor/`, or `.opencode/`.
3. Use the package-level instructions for general Java and Spring work, and rely on the promoted sub-skills for focused tasks.
4. Use `apm compile` only when you need generated outputs such as `AGENTS.md` or `CLAUDE.md` for tools that depend on compiled files.

## Bundled Resources

- `.apm/instructions/` contains the Java 25 + Spring Boot 4 guidance
- `.apm/prompts/` contains reusable prompt workflows
- `.apm/agents/` contains specialized personas for common engineering domains
- `.apm/skills/` contains focused sub-skills with references, examples, and scripts

## Notes

- This repository is a source package, so `.apm/` is the source of truth.
- `AGENTS.md` is generated output and should be regenerated rather than edited manually.
- The package itself does not contain Java application sources, so compilation may warn that `**/*.java` matches no files in this repository.
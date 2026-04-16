# APM — Agent Programming Model for Java 25 + Spring Boot 4

Modular documentation and skill system for AI coding assistants working with Java 25 and Spring Boot 4 projects following hexagonal architecture.

## Overview

This repository contains a structured collection of **skills**, **agents**, **instructions**, and **prompts** that guide AI assistants in generating high-quality, idiomatic code for modern Java applications.

## Structure

```
.apm/
├── agents/           # Specialized AI agents (quality, test, performance, devops)
├── instructions/     # Project-wide coding instructions
├── prompts/          # Reusable prompt templates, including the review workflow
└── skills/           # Modular skill packages
    ├── arch-test/        # ArchUnit architecture testing
    ├── best-practices/   # Java 25 idioms, SOLID, Clean Code, MapStruct
    ├── code-quality/     # Checkstyle, SpotBugs, PMD, Error Prone, NullAway
    ├── container/        # Dockerfile, Jib, Buildpacks, GraalVM Native
    ├── integration-test/ # Testcontainers, WireMock, Spring Boot Test
    ├── local-test/       # Docker Compose, Testcontainers Desktop
    ├── observability/    # Actuator, Micrometer, Prometheus, OpenTelemetry
    ├── stress-test/      # Gatling, JMH, k6
    ├── tuning/           # JVM flags, Virtual Threads, caching, pools
    └── unit-test/        # JUnit 6, Mockito 5, AssertJ, Instancio, DataFaker
```

## Skill Format

Each skill follows a consistent structure:

- **`SKILL.md`** — Meta-guide with bundled resources table
- **`examples/`** — Complete, runnable code examples
- **`scripts/`** — Maven/Gradle configuration snippets
- **`references/`** — Concise technical documentation

The code review workflow for this package is defined in `.apm/prompts/review.prompt.md` and uses the detailed checklist in `.apm/skills/code-quality/references/quality-rules.md`.

## Tech Stack

- **Java 25** (with preview features)
- **Spring Boot 4.x** / Spring Framework 7.x
- **Hexagonal Architecture** (Ports & Adapters)
- **Virtual Threads** + Structured Concurrency
- **GraalVM Native Image** support

## APM CLI Setup

Before installing this package, you need the APM CLI.

### Requirements

- macOS, Linux, or Windows (x86_64 or ARM64)
- [git](https://git-scm.com/) for dependency management
- Python 3.10+ (only for pip or from-source installs)

### Quick Install (recommended)

**macOS / Linux:**

```bash
curl -sSL https://aka.ms/apm-unix | sh
```

**Windows (PowerShell):**

```powershell
irm https://aka.ms/apm-windows | iex
```

### Package Managers

**Homebrew (macOS/Linux):**

```bash
brew install microsoft/apm/apm
```

**Scoop (Windows):**

```powershell
scoop bucket add apm https://github.com/microsoft/scoop-apm
scoop install apm
```

**pip:**

```bash
pip install apm-cli   # requires Python 3.10+
```

### Verify Installation

```bash
apm --version
```

For more details, see the [official APM documentation](https://microsoft.github.io/apm/).

---

## Installation

Install in any project using APM CLI:

```bash
apm install heandroro/apm-java-25-spring-boot-4-hexagonal
```

APM automatically:
- Downloads the package to `apm_modules/`
- Resolves dependencies and updates `apm.yml` with the package reference
- Deploys supported primitives from the package to the native directories your AI tools already watch, such as `.github/`, `.claude/`, `.cursor/`, and `.opencode/`
- Creates or refreshes the lockfile so the installed agent configuration stays reproducible across machines

## Compile for Other Tools

If you use tools beyond **GitHub Copilot**, **Claude**, **Cursor**, and **OpenCode** (which read deployed primitives natively), generate compiled instruction files:

```bash
apm compile
```

This produces:
- **`AGENTS.md`** — for Codex, Gemini
- **`CLAUDE.md`** — for tools that need a single instructions file

> **Note:** Copilot, Claude, Cursor, and OpenCode primarily consume deployed primitives from their native directories after `apm install`. Use `apm compile` when you need generated files for tools that rely on compiled outputs such as `AGENTS.md` or `CLAUDE.md`.

### Understanding `Context efficiency`

When `apm compile` prints `Context efficiency`, it is reporting how much of the inherited instruction context is actually relevant to the files covered by the generated output.

Conceptually:

```text
Context efficiency = relevant instructions / total inherited instructions
```

Higher values mean less context pollution. In other words, the agent sees a higher proportion of instructions that are actually useful for the files in that part of the project.

Practical interpretation:

- `80-100%` — excellent locality
- `60-80%` — good optimization
- `40-60%` — acceptable
- `20-40%` — poor locality
- `0-20%` — critical or heavily root-biased placement

Important: a low value is not automatically a problem. The compiler always prioritizes complete coverage over efficiency, so some cross-cutting instructions must remain at the root.

In this repository specifically, low or even `0.0%` efficiency is expected during local compilation because this is a source package, not a Java application. The main instruction pattern is `**/*.java`, but the repository itself does not contain Java source files to benefit from localized placement. As a result, the compiler falls back to root placement and the efficiency metric is not representative here.

To evaluate the metric in a meaningful way, install this package into a real Java project and run:

```bash
apm install
apm compile --verbose
```

That gives a realistic view of how well the generated context matches the actual project structure.

Example output in this repository:

```text
Generated 1 AGENTS.md file
+- Context efficiency:    0.0%
+- Generation time:       4ms

[!] Warning: Pattern '**/*.java' matches no files - placing at project root
```

In this case, `0.0%` does not indicate a broken package. It reflects that the package defines Java-specific instructions, but this repository contains package sources rather than Java application files.

## Using With Different LLM Tools

The package can be consumed by several tools, but the setup model is not identical across them.

The safe rule is:

1. Create the target tool directory in the consumer project.
2. Run `apm install`.
3. Run `apm compile` only when the tool depends on compiled instruction files such as `AGENTS.md` or `CLAUDE.md`.

### GitHub Copilot in VS Code

Recommended when you want native support for prompts, agents, instructions, and skills.

```bash
mkdir -p .github
apm install heandroro/apm-java-25-spring-boot-4-hexagonal
```

APM deploys package content into the native VS Code structure:

- `.github/instructions/` for `.instructions.md`
- `.github/prompts/` for `.prompt.md`
- `.github/agents/` for `.agent.md`
- `.github/skills/` for package skills and promoted sub-skills

Use `apm compile` only if you also want merged instruction output in `AGENTS.md`.

### GitHub Copilot in Other IDEs

For JetBrains, Visual Studio, and other IDEs that rely on GitHub Copilot file-level discovery, the practical setup is the same as VS Code because APM deploys to `.github/`.

```bash
mkdir -p .github
apm install heandroro/apm-java-25-spring-boot-4-hexagonal
```

If your Copilot environment does not pick up granular primitives reliably, add:

```bash
apm compile --target copilot
```

### Claude Code

Claude Code reads deployed primitives natively, so `apm compile` is optional.

```bash
mkdir -p .claude
apm install heandroro/apm-java-25-spring-boot-4-hexagonal
```

APM maps the package into Claude-native paths such as:

- `.claude/rules/` for instructions
- `.claude/commands/` for prompts
- `.claude/agents/` for agents
- `.claude/skills/` for skills

If you want a single compiled instruction file for Claude-oriented tools, generate it with:

```bash
apm compile --target claude
```

### Claude Desktop

Use the same `.claude/` deployment model as Claude Code, but prefer compiling a `CLAUDE.md` if you want a single project instruction file.

```bash
mkdir -p .claude
apm install heandroro/apm-java-25-spring-boot-4-hexagonal
apm compile --target claude
```

### Cursor

Cursor supports native deployed primitives, with `AGENTS.md` as a useful fallback.

```bash
mkdir -p .cursor
apm install heandroro/apm-java-25-spring-boot-4-hexagonal
```

APM deploys into paths such as:

- `.cursor/rules/` for instructions converted to Cursor rule format
- `.cursor/agents/` for agents
- `.cursor/skills/` for skills

If you want merged project-level instructions as a fallback, run:

```bash
apm compile --target copilot
```

### OpenCode

OpenCode consumes native deployed agents, commands, and skills, but uses `AGENTS.md` for instructions.

```bash
mkdir -p .opencode
apm install heandroro/apm-java-25-spring-boot-4-hexagonal
apm compile --target copilot
```

Expected output:

- `.opencode/agents/` for agents
- `.opencode/commands/` for prompts converted to command format
- `.opencode/skills/` for skills
- `AGENTS.md` for instructions

### Codex CLI

Codex needs native deployment for skills and agents plus compiled instructions via `AGENTS.md`.

```bash
mkdir -p .codex
apm install heandroro/apm-java-25-spring-boot-4-hexagonal
apm compile --target codex
```

Expected output:

- `.agents/skills/` for skills
- `.codex/agents/` for converted agent files
- `.codex/hooks.json` when hook packages are present
- `AGENTS.md` for compiled instructions

### Devin

Devin is not documented by APM as having a dedicated native target like `.github/`, `.claude/`, `.cursor/`, or `.opencode/`. The safest integration pattern is therefore to expose project guidance through compiled repository-level files.

Recommended setup in a consumer project:

```bash
apm install heandroro/apm-java-25-spring-boot-4-hexagonal
apm compile
```

Recommended artifacts to keep in the repository for Devin sessions:

- `AGENTS.md` as the main compiled instruction file
- `README.md` for package and workflow documentation
- `.github/` if the same repository is also used with GitHub Copilot or other tools that understand native deployed primitives

If Devin is running in an environment where you want prebuilt context without requiring APM installation at session time, prepare a bundle in advance:

```bash
apm install
apm pack --archive
```

This lets you distribute the resolved context as an artifact and unpack it before or during the Devin workflow.

### Windsurf

APM documentation currently mentions Windsurf as planned integration, not as a first-class native target like `.github/`, `.claude/`, `.cursor/`, `.opencode/`, or `.codex/`. Because of that, the safest recommendation is to use compiled instruction output.

Recommended setup in a consumer project:

```bash
apm install heandroro/apm-java-25-spring-boot-4-hexagonal
apm compile
```

This ensures the project exposes an `AGENTS.md` file with the merged instructions.

Recommended practical guidance:

- In consumer repositories, consider keeping `AGENTS.md` committed when Windsurf should immediately see project instructions after clone
- Prefer `apm compile --verbose` while validating adoption, so you can inspect placement and context-efficiency output
- If the same repository is also used with GitHub Copilot, keeping `.github/` alongside `AGENTS.md` is reasonable, but the Windsurf-safe fallback remains the compiled root file

If APM later adds official Windsurf target support, this section should be updated to reflect the native integration path instead of the compile-based fallback.

### Gemini And Other AGENTS.md Consumers

For tools that mainly understand a compiled `AGENTS.md`, install the package and compile the instructions.

```bash
apm install heandroro/apm-java-25-spring-boot-4-hexagonal
apm compile
```

In this mode, `AGENTS.md` is the main integration artifact.

### APM Runtime Setup

If you also want APM to manage the local runtime used to execute prompts and scripts, you can provision supported runtimes explicitly.

```bash
apm runtime setup copilot
apm runtime setup codex
apm runtime setup llm
```

Use cases:

- `copilot` for GitHub Copilot CLI workflows
- `codex` for Codex CLI workflows
- `llm` for the generic `llm` CLI with provider-specific API keys

### Recommended Matrix

For most teams, use the following combinations:

- GitHub Copilot or VS Code: `.github/` + `apm install`
- Claude Code: `.claude/` + `apm install`
- Cursor: `.cursor/` + `apm install`
- OpenCode: `.opencode/` + `apm install` + `apm compile`
- Codex CLI: `.codex/` + `apm install` + `apm compile --target codex`
- Gemini or single-file instruction consumers: `apm install` + `apm compile`

## Day-to-Day Workflow

For a project that consumes this package:

1. Commit `apm.yml` and `apm.lock.yaml` so every contributor gets the same resolved agent configuration.
2. Commit deployed directories such as `.github/`, `.claude/`, `.cursor/`, and `.opencode/` when your team wants agent context to be available immediately after clone.
3. If your workflow depends on compiled root files such as `AGENTS.md` or `CLAUDE.md`, you may also commit them in consumer repositories.
4. Keep `apm_modules/` out of version control because it is rebuilt from the lockfile during `apm install`.
5. Re-run `apm install` after updating dependencies so deployed primitives and the lockfile stay in sync.

This repository includes a minimal `.gitignore` that follows this workflow by excluding `apm_modules/`, generated compile artifacts, and common local pack outputs such as `build/`, `dist/`, `release-artifacts/`, and archived bundles.

## Package Authoring Notes

This repository is the **source package**, so its primitives live under `.apm/` and are meant to be installed into another project with `apm install`.

- Use `.apm/` as the source of truth for agents, instructions, prompts, and skills.
- Treat `AGENTS.md` and `CLAUDE.md` as generated output from `apm compile`, not hand-edited source files.
- Do not version compiled artifacts in this source repository; regenerate them on demand from `.apm/` sources.
- Expect `apm compile` in this repository to warn that `**/*.java` matches no files, because this package distributes Java guidance but does not contain a Java application itself.

## Distribution Options

If you need to distribute resolved output without requiring APM on the consumer side, use the pack workflow described in the official docs:

```bash
apm install
apm pack --archive
```

This produces a portable bundle of the deployed primitives that can be unpacked or attached to CI and release workflows.

## Versioning And Releases

This package uses the version declared in `apm.yml` as its package version:

```yml
name: poc-apm
version: 1.0.0
```

Recommended release workflow:

1. Update the `version` field in `apm.yml` using semantic versioning.
2. Commit the version change together with the package changes that belong to that release.
3. Create a Git tag that matches the package version, for example `v1.0.0`.
4. Push the branch and the tag to the remote repository.

Example:

```bash
git add apm.yml README.md .apm/ SKILL.md
git commit -m "release: v1.0.0"
git tag v1.0.0
git push origin main
git push origin v1.0.0
```

For consumers of the package, the recommended form is to install a tagged version rather than relying on a moving branch:

```bash
apm install heandroro/apm-java-25-spring-boot-4-hexagonal#v1.0.0
```

This makes dependency resolution reproducible and aligns the installed package ref with the package metadata declared in `apm.yml`.

## License

MIT License

Copyright (c) 2026

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

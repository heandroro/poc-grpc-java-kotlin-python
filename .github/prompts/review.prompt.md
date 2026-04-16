---
description: Run a comprehensive code review for Java 25 + Spring Boot 4
---

Review this code applying the following checklist for Java 25 + Spring Boot 4.

Present findings first, ordered by severity. For each finding, include file path, line number, severity (`critical`, `warning`, or `info`), why it matters, and a concrete suggested fix. If there are no findings, say so explicitly and then list residual risks or testing gaps.

Use this review baseline:

- Quality rules reference: `.apm/skills/code-quality/references/quality-rules.md`
- Project-wide Java and Spring guidance: `.apm/instructions/java-spring-boot.instructions.md`
- Relevant domain-specific sub-skills in `.apm/skills/`

Minimum checks to always apply:

- Quality and style violations
- Architecture boundary violations
- Missing or weak tests
- Security issues and unsafe input handling
- Performance and concurrency risks
- Operational and deployability gaps when relevant

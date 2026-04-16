---
name: code-quality
description: Qualidade de código com Checkstyle, SpotBugs, PMD, Error Prone, NullAway, SonarQube e Google Java Format
---

# Code Quality

Skill para garantir qualidade de código em projetos Java 25 + Spring Boot 4 usando análise estática e formatação automatizada.

## Recursos Bundled

### `scripts/` — Configurações de Build
| Arquivo | Conteúdo |
|---------|----------|
| `maven-quality.xml` | Checkstyle, SpotBugs, PMD, Error Prone + NullAway para Maven |
| `gradle-quality.gradle` | Checkstyle, SpotBugs, PMD, Error Prone + NullAway para Gradle |

### `references/` — Documentação Técnica
| Arquivo | Conteúdo |
|---------|----------|
| `quality-rules.md` | Quality gates, regras de complexidade, configuração de ferramentas |

## Ferramentas obrigatórias

- **Checkstyle** — enforce Google Java Style ou custom ruleset via `checkstyle.xml`
- **SpotBugs** — detecção de bugs com plugin `sb-contrib` e `findsecbugs`
- **PMD** — regras de complexidade ciclomática, código morto, e design
- **Error Prone + NullAway** — plugin do compilador para capturar erros e null safety em tempo de compilação
- **JSpecify** — anotações padrão de indústria para null safety (Google, JetBrains, Spring, Oracle)
- **SonarQube / SonarCloud** — quality gate com métricas de cobertura, duplicação, e dívida técnica
- **Google Java Format** — formatação automática e consistente

## Regras e padrões

- Quality gate: zero bugs críticos, zero vulnerabilidades, cobertura ≥ 80%, duplicação ≤ 3%
- Complexidade ciclomática máxima por método: 10
- Tamanho máximo de método: 30 linhas
- Tamanho máximo de classe: 300 linhas
- Todos os warnings do compilador devem ser tratados (`-Werror` no build)
- Utilizar `@SuppressWarnings` apenas com justificativa documentada
- Error Prone deve rodar como annotation processor no `maven-compiler-plugin` ou Gradle

## Configuração

Ver `scripts/maven-quality.xml` e `scripts/gradle-quality.gradle` para configuração completa de todas as ferramentas.

## Convenções

- Executar análise estática em cada PR via CI
- Bloquear merge se quality gate falhar
- Revisar regras customizadas trimestralmente
- Manter baseline de issues para projetos legados migrando para Java 25

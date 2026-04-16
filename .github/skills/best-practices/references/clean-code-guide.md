# Clean Code — Guia Técnico

Princípios e práticas para código limpo em Java 25.

## Nomenclatura

| Elemento | Convenção | Exemplo |
|----------|-----------|---------|
| Classes | Substantivos, PascalCase | `OrderService`, `CustomerRepository` |
| Interfaces | Adjetivos/Papéis, PascalCase | `OrderRepository`, `PaymentProcessor` |
| Métodos | Verbos, camelCase | `processOrder()`, `isValid()` |
| Variáveis | camelCase, significativas | `orderTotal`, `customerEmail` |
| Constantes | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| Pacotes | lowercase, singular | `com.example.order` |
| Records | PascalCase, substantivos | `OrderRequest`, `CustomerDTO` |
| Enums | PascalCase (tipo), UPPER_SNAKE (valores) | `OrderStatus.PENDING` |

**Regra**: se um comentário é necessário para explicar *o quê* o código faz, o nome está errado.

## Funções

- **Máximo 20 linhas** por método.
- **Máximo 3 parâmetros** — use record para agrupar se precisar de mais.
- **Uma única responsabilidade** — se o método precisa de comentários para separar etapas, extraia métodos.
- Se há comentários tipo `// validar`, `// calcular`, `// salvar` dentro de um método, cada bloco é um método candidato à extração.

## Comentários

| ✅ Quando Usar | ❌ Evitar |
|---------------|----------|
| Documentação de API pública (Javadoc) | Comentários explicando código óbvio |
| Intenção ou decisão de design | Comentários redundantes com o nome |
| TODOs com tickets de rastreamento | Comentários desatualizados |
| Avisos sobre consequências | Comentários em português (código em inglês) |


## Formatação

- **Indentação**: 4 espaços
- **Chaves**: abertura na mesma linha (estilo K&R)
- **Linhas em branco**: separar grupos lógicos de código
- **Comprimento máximo**: 120 caracteres por linha
- **Imports**: organizados, sem wildcards (`*`), removidos os não utilizados

## Tratamento de Erros

- Exceções específicas de domínio com `errorCode` — nunca `new RuntimeException("error")`.
- `ProblemDetail` (RFC 9457) para todas as respostas de erro HTTP.
- Tratamento de erros separado da lógica principal.
- Ver `references/exceptions-guide.md` para hierarquia completa.

## Testabilidade

- Dependências sempre injetadas via construtor — sem `new` dentro de services.
- Sem estado global, sem `static` mutável.
- Toda lógica de negócio em classes sem framework (testável sem Spring context).

## Checklist Clean Code

- [ ] Nomes revelam intenção sem comentários explicativos
- [ ] Funções fazem uma única coisa (≤ 20 linhas, ≤ 3 parâmetros)
- [ ] Sem duplicação de código (DRY)
- [ ] Sem código morto (comentado ou não utilizado)
- [ ] Tratamento de erros separado da lógica de negócio
- [ ] Dependências injetadas via construtor
- [ ] Testes cobrem comportamentos, não implementação interna
- [ ] Código segue convenções do projeto (Checkstyle)

## Exemplos

Ver `examples/CleanCode.java` — antes/depois de refatorações com extração de métodos e nomenclatura.

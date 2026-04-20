# Guia de Testes Unitários

## Nomenclatura de Testes

Padrão: `should_[resultado]_when_[condição]`

```java
// ✅ Bom
void should_calculateDiscount_when_customerIsPremium() { }
void should_throwException_when_orderIsEmpty() { }
void should_returnEmptyOptional_when_customerNotFound() { }

// ❌ Ruim
void test1() { }
void discountTest() { }
void testPremiumCustomer() { }
```

## Estrutura BDD (given-when-then)

```java
@Test
void should_createOrder_when_validRequest() {
    // given - preparação
    var request = new CreateOrderRequest("item-1", 2);
    given(repository.save(any())).willReturn(new Order("id-1"));

    // when - ação
    var result = service.create(request);

    // then - verificação
    assertThat(result.id()).isEqualTo("id-1");
    then(repository).should().save(any());
}
```

## Cobertura JaCoCo ≥ 90%

Configuração obrigatória para LINE e BRANCH:

```xml
<limit>
    <counter>LINE</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.90</minimum>
</limit>
<limit>
    <counter>BRANCH</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.90</minimum>
</limit>
```

## Geração de Dados de Teste

### Instancio

```java
// Cria objetos automaticamente populados
Order order = Instancio.create(Order.class);

// Customização
Order order = Instancio.of(Order.class)
    .set(field("status"), OrderStatus.PENDING)
    .create();
```

### DataFaker

```java
Faker faker = new Faker();

String email = faker.internet().emailAddress();
String name = faker.name().fullName();
String address = faker.address().fullAddress();
```

## Anti-patterns

- ❌ Não testar implementação (testar comportamento)
- ❌ Múltiplos asserts testando conceitos diferentes
- ❌ Testes que dependem de estado/ordem
- ❌ Sleep para sincronização
- ❌ Mocks excessivamente específicos (coupling)

## Checklist

- [ ] Nome descreve comportamento, não implementação
- [ ] Um conceito lógico por teste
- [ ] Isolamento completo (sem DB, rede, FS)
- [ ] Execução < 10s por módulo
- [ ] Cobertura ≥ 90% linhas e branches

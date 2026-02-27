# Code Quality Principles

## 1. Single Responsibility Principle (SRP)
Each class must be cohesive — it should represent one concept and have one reason to change.
A class that handles multiple concerns (e.g., parsing + persistence + business logic) must be split.

## 2. Weighted Methods Per Class (WMC ≤ 5)
A class must not have more than 5 public methods.
If a class exceeds this, extract responsibilities into collaborating classes.
Private helpers do not count toward the limit but should still be minimal.

## 3. Domain-Driven Design — Aggregates
Group related classes into aggregates around a single Aggregate Root.
- Only the Aggregate Root is accessible from outside the aggregate boundary.
- Internal entities and value objects are owned by the root and not shared across aggregates.
- Identify aggregates by asking: "What is the consistency boundary here?"

## 4. Loose Coupling Between Aggregates
Aggregates must not hold direct references to other aggregates' internals.
- Reference other aggregates by ID only, not by object reference.
- Use domain events or application services to coordinate cross-aggregate behavior.
- This ensures each aggregate can be built, run, and tested in isolation.

## 5. Aggregate as the Unit of Testing
Each aggregate is a self-contained unit.
Tests for an aggregate must not require instantiating classes from another aggregate.
Use fakes or stubs at aggregate boundaries, not mocks of internal collaborators.

## 6. Function Length ≤ 10 Lines
No function body should exceed 10 lines (excluding blank lines and comments).
If a function is longer, extract logical steps into well-named private functions.
A long function is a signal that it is doing too much or mixing abstraction levels.

## 7. Single Level of Abstraction Per Function (SLAP)
Every line inside a function must be at the same level of abstraction.
- High-level orchestration functions should only call other named functions — no inline logic.
- Low-level functions do the actual work — no orchestration.

✅ Good:
```kotlin
fun processOrder(order: Order) {
    validateOrder(order)
    applyDiscounts(order)
    persistOrder(order)
    notifyCustomer(order)
}
```

❌ Bad:
```kotlin
fun processOrder(order: Order) {
    if (order.items.isEmpty()) throw IllegalArgumentException("No items")
    order.items.forEach { it.price *= 0.9 }
    db.save(order)
    emailService.send(order.customer.email, "Your order is confirmed")
}
```

## 8. Maximum Nesting Depth ≤ 3
Code must not nest deeper than 3 levels (function body = level 1).
Reduce depth using early returns (guard clauses), extracted functions, or flatMap/filter pipelines.

❌ Bad (depth 4):
```kotlin
fun process() {
    if (a) {           // depth 2
        for (x in b) { // depth 3
            if (c) {   // depth 4 ❌
            }
        }
    }
}
```

## 9. No Magic Numbers or Strings
All literals with business meaning must be named constants or enum values.
Place constants in a clearly named object/companion scoped to the relevant domain concept.

❌ Bad: `if (status == 3)`
✅ Good: `if (status == OrderStatus.CONFIRMED)`

## 10. No Primitive Obsession
Domain concepts must be represented as typed classes, not raw primitives.
Never pass raw `String`, `Int`, or `Double` for values that have domain meaning.

✅ Good:
```kotlin
@JvmInline value class CustomerId(val value: String)
@JvmInline value class Money(val amount: BigDecimal)
@JvmInline value class Email(val value: String)
```

❌ Bad:
```kotlin
fun transferFunds(fromAccount: String, toAccount: String, amount: Double)
```

Benefits:
- Compile-time correctness — prevents passing `Email` where `CustomerId` is expected.
- Encapsulates validation at construction time.
- Aligns directly with DDD value objects.

## 11. Immutability by Default
Value objects and entities must prefer immutable state.
Mutation is only permitted at the Aggregate Root boundary, and must be intentional and explicit.
```kotlin
// Value object — always immutable
data class Money(val amount: BigDecimal, val currency: Currency) {
    operator fun plus(other: Money): Money {
        require(currency == other.currency)
        return Money(amount + other.amount, currency)
    }
}

// Entity — state change only through explicit root method
class Order private constructor(...) {
    private var status: OrderStatus = OrderStatus.PENDING

    fun confirm(): Result {
        if (status != OrderStatus.PENDING) return Err(OrderError.InvalidTransition)
        status = OrderStatus.CONFIRMED
        return Ok(this)
    }
}
```

## 12. Dependency Inversion
Aggregates must depend on abstractions, not concrete infrastructure.
Define interfaces inside the domain; let infrastructure implement them.
```kotlin
// Domain layer — owns the interface
interface OrderRepository {
    fun save(order: Order): Result
    fun findById(id: OrderId): Result
}

// Infrastructure layer — provides the implementation
class PostgresOrderRepository(private val db: Database) : OrderRepository { ... }
```

Rules:
- The domain layer must never import infrastructure packages.
- Infrastructure depends on the domain — never the reverse.
- This enables replacing persistence, messaging, or external services without touching domain logic.

## 13. Factory Methods Over Constructors
Complex object creation must use named factory methods or dedicated factory classes.
Constructors must remain simple — no branching, no validation, no side effects.
```kotlin
class Order private constructor(
    val id: OrderId,
    val customerId: CustomerId,
    val items: List
) {
    companion object {
        fun create(
            customerId: CustomerId,
            items: List
        ): Result {
            if (items.isEmpty()) return Err(OrderError.EmptyOrder)
            if (items.size > Order.MAX_ITEMS) return Err(OrderError.TooManyItems)
            return Ok(Order(OrderId.generate(), customerId, items))
        }
    }
}
```

Benefits:
- Factory method name expresses intent (`create`, `reconstitute`, `fromSnapshot`).
- Returns `Result` instead of throwing — consistent with the error handling strategy.
- Private constructor enforces that all creation goes through the factory.

## 14. Error Handling Strategy
Exceptions are reserved for truly unexpected failures (bugs, infrastructure collapse).
Business rule violations must be represented as typed values using a Result/Either type.

Define a sealed class for each aggregate's error domain:
```kotlin
sealed class OrderError {
    object EmptyOrder : OrderError()
    object InsufficientStock : OrderError()
    object InvalidTransition : OrderError()
    data class CustomerNotFound(val id: CustomerId) : OrderError()
}
```

Use a `Result` type consistently across Control and Entity layers:
```kotlin
// Entity enforces invariant, returns typed error
fun confirm(): Result {
    if (status != OrderStatus.PENDING) return Err(OrderError.InvalidTransition)
    status = OrderStatus.CONFIRMED
    return Ok(this)
}

// Control orchestrates and propagates
fun confirmOrder(orderId: OrderId): Result {
    val order = repository.findById(orderId).getOrElse { return Err(it) }
    return order.confirm().map { repository.save(it) }
}
```

Rules:
- Never use exceptions for expected business failures.
- Never return `null` to signal failure — use `Result`.
- All `Result` values must be handled at the call site — never silently ignored.

## 15. Fail Fast at Boundaries
All input validation must occur in the Boundary layer before reaching Control or Entity.
Nothing invalid may cross inward from the boundary.
```kotlin
// Boundary — validates and translates
class OrderBoundary(private val control: OrderControl) {
    fun placeOrder(request: PlaceOrderRequest): Response {
        val customerId = CustomerId.parse(request.customerId)
            .getOrElse { return Response.badRequest("Invalid customer ID") }
        val items = request.items.map { OrderItemMapper.toDomain(it) }
            .getOrElse { return Response.badRequest("Invalid order items") }

        return control.placeOrder(customerId, items)
            .fold(
                onSuccess = { Response.ok(it) },
                onFailure = { Response.unprocessable(it) }
            )
    }
}
```

## 16. Naming Rules
Names must communicate intent without needing a comment to explain them.

| Kind            | Convention                        | Example                          |
|-----------------|-----------------------------------|----------------------------------|
| Functions       | Verb or verb phrase               | `calculateDiscount`, `findOrder` |
| Booleans        | Question form                     | `isEligible`, `hasExpired`       |
| Classes         | Noun representing a concept       | `Invoice`, `ShippingPolicy`      |
| Constants       | Screaming snake or named object   | `MAX_ITEMS`, `OrderStatus.CONFIRMED` |
| Value objects   | Domain noun, not technical suffix | `Money`, not `MoneyDTO`          |

Rules:
- No abbreviations unless universally understood (e.g., `id`, `url`).
- No filler words: `Manager`, `Helper`, `Util`, `Handler` signal a missing concept.
- If you cannot name something clearly, the abstraction is probably wrong.

## 17. Boundary-Control-Entity (BCE) Pattern
Organize classes within each aggregate into three roles:

| Role         | Responsibility                                                               |
|--------------|------------------------------------------------------------------------------|
| **Boundary** | Handles entry/exit — input validation, DTOs, API adapters, response mapping  |
| **Control**  | Orchestrates the use case flow — coordinates entities, applies rules         |
| **Entity**   | Holds domain state and enforces invariants — pure domain logic only          |

Dependency direction is strict and one-way:
```
Boundary → Control → Entity
```

Rules:
- Boundaries talk to Controls only — never directly to Entities.
- Controls talk to Entities — they never contain domain logic themselves.
- Entities never talk to Boundaries or Controls — they are called, not callers.
- Infrastructure (repositories, event publishers) is injected into Control via interfaces.

## 18. Scenario-Based Unit Tests
Write one test per scenario — not one test per method.
Total number of tests = total number of meaningful usage scenarios for the aggregate.

Each test must follow **Given / When / Then**:
```kotlin
@Test
fun `should reject order when inventory is insufficient`() {
    // Given
    val inventory = Inventory(availableStock = 2)
    val order = Order(requestedQuantity = 5)

    // When
    val result = inventory.reserve(order)

    // Then
    assertThat(result).isEqualTo(Err(InventoryError.InsufficientStock))
}
```

Negative scenarios are mandatory — every aggregate must have at least one:
- `should reject ... when ...`
- `should fail ... when ...`
- `should not allow ... when ...`

## 19. Tests as Documentation
Test names must read as plain English sentences describing behavior, not implementation.
A developer unfamiliar with the codebase must be able to read only test names and understand
what the system does.

✅ Good: `` `should apply loyalty discount when customer has over 1000 points` ``
❌ Bad: `testDiscount()`, `discountMethodTest()`, `test1()`

## 20. No Logic in Tests
Tests must not contain `if`, `else`, `when`, loops, or computed assertions.
If branching is needed, write two separate tests — one per path.
A bug inside a test is worse than a missing test.

❌ Bad:
```kotlin
@Test
fun `discount test`() {
    val customers = listOf(goldCustomer, silverCustomer)
    customers.forEach { customer ->
        if (customer.tier == Tier.GOLD) {
            assertThat(discount(customer)).isEqualTo(Discount.TWENTY_PERCENT)
        } else {
            assertThat(discount(customer)).isEqualTo(Discount.TEN_PERCENT)
        }
    }
}
```

✅ Good:
```kotlin
@Test
fun `should apply 20 percent discount for gold tier customers`() {
    val result = discount(goldCustomer)
    assertThat(result).isEqualTo(Discount.TWENTY_PERCENT)
}

@Test
fun `should apply 10 percent discount for silver tier customers`() {
    val result = discount(silverCustomer)
    assertThat(result).isEqualTo(Discount.TEN_PERCENT)
}
```

## 21. Test Data Builders
All test object creation must use builders or object mothers — never inline construction.
This keeps tests readable and insulates them from constructor changes.
```kotlin
// Object Mother — canonical valid instances
object OrderMother {
    fun pendingOrder(
        customerId: CustomerId = CustomerId("cust-001"),
        items: List = listOf(OrderItemMother.standardItem())
    ) = Order.create(customerId, items).getOrThrow()

    fun confirmedOrder() = pendingOrder().also { it.confirm() }
}

// Usage in test
@Test
fun `should cancel a confirmed order`() {
    val order = OrderMother.confirmedOrder()
    val result = order.cancel()
    assertThat(result).isOk()
}
```

Rules:
- Builders expose only the parameters relevant to the scenario being tested.
- Default values represent the canonical valid state.
- Never share mutable test objects across tests.

## 22. Definition of Done
A feature is not complete until all of the following are true:

- [ ] Each class has ≤ 5 public methods and a single responsibility
- [ ] No function exceeds 10 lines or depth 3
- [ ] No magic numbers, strings, or primitives for domain concepts
- [ ] All aggregates are loosely coupled — referenced by ID only
- [ ] BCE roles are clearly assigned and dependency direction is respected
- [ ] All business errors are typed — no exceptions for control flow
- [ ] Every aggregate has scenario-based tests including at least one negative case
- [ ] Test names read as plain English behavior descriptions
- [ ] Test data uses builders or object mothers
- [ ] No logic inside tests
- [ ] Domain layer has zero imports from infrastructure packages

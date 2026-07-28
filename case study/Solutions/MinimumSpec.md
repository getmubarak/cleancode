# Code Quality Principles

## 1. Single Responsibility Principle (SRP)
Each class must be cohesive — it should represent one concept and have one reason to change.
A class that handles multiple concerns (e.g., parsing + persistence + business logic) must be split.

## 2. Weighted Methods Per Class (WMC ≤ 5)
A class must not have more than 5 public methods.
If a class exceeds this, extract responsibilities into collaborating classes.
Private helpers do not count toward the limit but should still be minimal.

## 3. Function Length ≤ 10 Lines
No function body should exceed 10 lines (excluding blank lines and comments).
If a function is longer, extract logical steps into well-named private functions.
A long function is a signal that it is doing too much or mixing abstraction levels.

## 4. Single Level of Abstraction Per Function (SLAP)
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

## 5. Maximum Nesting Depth ≤ 3
Code must not nest deeper than 3 levels (function body = level 1).
Reduce depth using early returns (guard clauses), extracted functions, or flatMap/filter pipelines.

## 6. No Magic Numbers or Strings
All literals with business meaning must be named constants or enum values.
Place constants in a clearly named object/companion scoped to the relevant domain concept.

❌ Bad: `if (status == 3)`
✅ Good: `if (status == OrderStatus.CONFIRMED)`

## 7. Scenario-Based Unit Tests
Write one test per scenario — not one test per method.
Total number of tests = total number of meaningful usage scenarios for the aggregate.

## 8. Tests as Documentation
Test names must read as plain English sentences describing behavior, not implementation.
A developer unfamiliar with the codebase must be able to read only test names and understand
what the system does.

✅ Good: `` `should apply loyalty discount when customer has over 1000 points` ``
❌ Bad: `testDiscount()`, `discountMethodTest()`, `test1()`

## 9. No Logic in Tests
Tests must not contain `if`, `else`, `when`, loops, or computed assertions.
If branching is needed, write two separate tests — one per path.
A bug inside a test is worse than a missing test.

## 10. No static methods


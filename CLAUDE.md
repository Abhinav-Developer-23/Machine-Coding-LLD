# Repository Context

This is a **Low-Level Design (LLD) machine-coding practice repository**. Each
top-level directory is an independent LLD problem I use to revise design
patterns and OO modeling. Treat every sub-project as a standalone exercise.

# Coding Standards

- Apply **SOLID principles** rigorously — especially SRP and DIP.
- Model the domain with appropriate **GoF design patterns** (Strategy, Factory,
  Observer, State, Singleton, etc.). Prefer the simplest pattern that fits;
  do not over-engineer.
- Use **clean OO code**: encapsulated fields, meaningful names, constructor
  injection for dependencies, interfaces at seams.
- Use **Lombok `@Getter`** on model/domain classes instead of hand-written
  getters. Prefer `@Getter` at the class level; avoid `@Data`/`@Setter` unless
  mutability is genuinely required.
- Write **imperative Java** (plain `for` loops, `if/else`) — avoid Streams and
  functional chains unless explicitly asked.
- No speculative abstractions, no unused hooks, no premature generalization.

# Architecture — 3-Layer

Every project must follow a three-layer structure:

1. **Demo / Runner** — `main()` entry point that wires the object graph and
   demonstrates the flow.
2. **Service** — orchestrates business logic and coordinates repositories,
   strategies, and domain objects.
3. **Repository** — in-memory data store (usually `ConcurrentHashMap`-backed)
   exposing CRUD-style access to domain entities.

**Default to a single `Service` class and a single `Repository` class per
project.** Only split them when the problem statement explicitly requires
multiple bounded contexts (e.g., separate `UserService` + `BookingService`).

# Folder Structure

Each project directory should follow this layout:

```
<ProblemName>/
├── src/main/java/com/lld/<problemname>/
│   ├── model/          # Domain entities (POJOs)
│   ├── enums/          # Enum types (Status, Type, etc.)
│   ├── strategy/       # Strategy-pattern implementations + interfaces
│   ├── repository/     # Single Repository class (unless stated otherwise)
│   ├── service/        # Single Service class (unless stated otherwise)
│   ├── exception/      # Custom exceptions (if needed)
│   └── DemoRunner.java # main() — wires dependencies and runs the demo
```

Keep packages flat within each project; do not nest deeper than needed.

# Conventions

- All maps/collections live inside the single `Repository` — never scatter
  state across service classes.
- Strategies (pricing, matching, eviction, etc.) go under `strategy/` behind
  a common interface; the service picks the implementation.
- `DemoRunner` must show the **happy path plus at least one edge case** for
  the problem.
- No Spring, no frameworks — plain Java only.

# Build & Formatting

- Code **must be compilable at all times**. After any edit, ensure the project
  still builds (`mvn compile` in the affected sub-project). Never leave the
  repo in a broken state between steps.
- After any **large refactor** (renames, package moves, multi-file structural
  changes), run `mvn spotless:apply` in the affected sub-project to normalize
  formatting before handing control back.

# Repository Context
Do not see other modules and try to copy , do this fresh

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

# Build & Formatting

- Code **must be compilable at all times**. After any edit, ensure the project
  still builds (`mvn compile` in the affected sub-project). Never leave the
  repo in a broken state between steps.
- After any **large refactor** (renames, package moves, multi-file structural
  changes), run `mvn spotless:apply` in the affected sub-project to normalize
  formatting before handing control back.

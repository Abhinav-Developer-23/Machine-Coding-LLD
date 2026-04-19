# LLD Quick Rules (Interview-Oriented)

## Architecture

Always follow **layered architecture**:

- Controller / Runner
- Service layer
- Repository layer
- Models (entities)
- Enums
- Strategies (if needed)

## Repository design

- Use a static `ConcurrentHashMap` in the repository.
  - Keep the repo simple — no DB-level complexity.
  - Focus on solving the problem, not infrastructure.

## Service layer

Keep it minimal:

- Max 1–2 service classes.
- Put all business logic here.
- Avoid unnecessary abstractions.

## Execution layer

- Have one Demo / Runner class.
- Use it to simulate flow and test logic.

## Aditya Pratap repo rule (important)

**Do not:**

- Try to replicate full Aditya Pratap repo-style design.
- Use an overly complex structure from that repo.
- Memorize or reproduce it wholesale.

**You may take:**

- Entities
- Enums
- Strategy ideas

**But the final solution must be:** layered architecture only (simple version).

## Interview reality (from experience)

You have given multiple interviews. Based on that:

**Companies generally do not expect:**

- Very hard / over-engineered solutions.
- Complex designs you cannot build in 40–45 minutes.

**What actually matters:**

- Clear logic
- Clean structure
- Ability to explain

## What to avoid

- Over-engineering like Aditya Pratap repo patterns.
- Too many classes / deep abstraction.
- Complex repo + service splitting.
- Fancy but impractical designs.

## Time strategy

Machine coding rounds are time-limited.

**Do not waste time on:**

- Unnecessary structure
- Over-design

**Do:**

- Solve first
- Keep it simple
- Make it readable

## Final rule

> **Layered + simple + explainable** beats **complex + fancy**.

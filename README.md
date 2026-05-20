# PokerGame

Lightweight JavaFX poker demo: single-player table, hand evaluation, and basic UI.

Features
- Simple Texas Hold'em hand evaluation and winner detection
- Clean, modular Java packages: `app`, `logic`, `model`, `ui`, `cards`
- JavaFX-based UI for dealing and inspecting hands

Requirements
- JDK 11 or later
- Maven (build tool)

Build & run
Build the project with Maven:

```bash
mvn clean package
```

Run in an IDE by launching the `app.Main` class, or use Maven Exec (if configured):

```bash
mvn exec:java -Dexec.mainClass=app.Main
```

Quick structure
- `src/app` — application entry (`Main.java`)
- `src/logic` — game controller and hand evaluator
- `src/model` — card, deck, and result models
- `src/ui` — JavaFX frames and view logic
- `src/cards` — card assets

Contribute
- Open issues or send pull requests. Keep changes small and focused.

License
- No license specified — add one if you plan to publish.

Enjoy exploring the code!


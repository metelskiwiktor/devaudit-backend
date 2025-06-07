# Agent Working Guidelines

## Repository Overview

This repository contains the **DevsMentoring - education platform** backend. It is a Java 21 project built with Spring
Boot 3.3.3. The code lives under `src/main/java` with tests under `src/test/java`.

## Building

Use the Maven wrapper scripts to build and test the project. A typical build that skips tests is:

```bash
./mvnw clean package -DskipTests
```

## Validation Steps

The project comes with a set of unit tests that should pass. Run them whenever you make changes and mention the outcome
in the PR description:

```bash
./mvnw test
```

## Contribution Guidelines

* Focus on the Java source files under `src/main/java` and `src/test/java`.
* Ignore `docker`, `docs`, `readme.md` and `target` unless a task mentions them specifically.
* Keep changes concise and well documented in commit messages.
* Include any relevant commands you ran (tests, PMD) in the PR description. Branch names should be descriptive of the
  changes made and written in lowercase with hyphens, e.g. `fix-mentor-survery-bug`.
* Any functional change **must be covered with tests**, as described in the `Test Guidelines` section.

## Test Guidelines

* Every new feature **must be covered by tests** – both unit and integration.
* Each test must follow the structure:

    * `// given` – setup of test data and mocks
    * `// when` – invocation of the logic under test
    * `// then` – assertions and verifications of expected outcomes
* Integration tests should use **WebTestClient**.
* Unit tests should rely on **Mockito** for mocking dependencies.
* **Adding comments to production or test code is strictly prohibited**. Code should be self-explanatory and readable by
  design.

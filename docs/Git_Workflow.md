# PropertyPilot Git Workflow

## Check Current Branch

```bash
git branch
```

Expected:

```text
* phase1-database-foundation
```

---

## Check Backend Changes Only

```bash
git status --short src
git status --short pom.xml
git status --short src/main/resources
```

---

## Stage Backend Changes

```bash
git add pom.xml

git add src/main/java/com/propertypilot/application
git add src/main/java/com/propertypilot/config
git add src/main/java/com/propertypilot/domain
git add src/main/java/com/propertypilot/infrastructure
git add src/main/java/com/propertypilot/security
git add src/main/java/com/propertypilot/web

git add src/main/resources/application.yml
git add src/main/resources/application-dev.yml
```

---

## Verify What Will Be Committed

```bash
git diff --cached --stat
```

---

## Commit

```bash
git commit -m "Phase X - Description"
```

Examples:

```bash
git commit -m "Phase 2 - Spring Boot foundation and security setup"
git commit -m "Phase 3 - JWT authentication implementation"
git commit -m "Phase 4 - User management APIs"
```

---

## Push

```bash
git push origin phase1-database-foundation
```

---

## Verify

```bash
git log --oneline -1
```
## Phase Completion Push

git status --short src
git status --short pom.xml
git status --short src/main/resources

git add pom.xml
git add src/main/java/com/propertypilot/application
git add src/main/java/com/propertypilot/config
git add src/main/java/com/propertypilot/domain
git add src/main/java/com/propertypilot/infrastructure
git add src/main/java/com/propertypilot/security
git add src/main/java/com/propertypilot/web
git add src/main/resources/application.yml
git add src/main/resources/application-dev.yml

git diff --cached --stat

git commit -m "Phase X - Description"

git push origin phase1-database-foundation

git log --oneline -1
# Final Repository Report – SIGATEC

**Project:** SIGATEC – Warranty Tracking and Control System  
**Repository:** https://github.com/JhonfczDev/SIGATEC-Project  
**Report date:** November 25, 2026  
**Final integration branch:** `develop`  
**Last merge:** PR #19 (Architecture documentation) → `develop` (November 25, 2026)

---

## 1. Executive Summary

This document presents the **final Git project management report**, including:

- Number of commits per team member
- Branches created and merged
- Pull Requests completed
- Final status of the repository

The work followed a **feature-branch strategy** (`feature/*` and `docs/*`), with continuous integration into the `develop` branch.

---

## 2. Development Team

| Member                | GitHub Username      | Role                    | Main Responsibilities                                      |
|-----------------------|----------------------|-------------------------|------------------------------------------------------------|
| **JhonfczDev**        | JhonfczDev           | Project Leader          | Console UI, Main, integration, documentation               |
| **sebastianflorezg**  | sebastianflorezg     | Developer (Dev1)        | Domain models + Repositories (in-memory and file) + architecture docs |
| **elkinjimenez7**     | elkinjimenez7        | Developer (Dev2)        | Services, JavaFX UI, JDBC persistence + HikariCP           |

> Author identities that appeared under different names have been unified:  
> `Jhon Fredy Chinchilla` → **JhonfczDev** · `sebasflorezgomez` → **sebastianflorezg**

---

## 3. Commits per Team Member

Count performed on the **`develop`** branch (branch that contains the final project code).

| Team Member (GitHub)   | Commits | Approximate Percentage |
|------------------------|---------|------------------------|
| **sebastianflorezg**   | 53      | ~48 %                  |
| **elkinjimenez7**      | 40      | ~36 %                  |
| **JhonfczDev**         | 18      | ~16 %                  |
| **Total**              | **111** | 100 %                  |

> **Note:** Merge commits from Pull Requests are attributed to the user who performed the merge. The count reflects the full history of `develop`.

---

## 4. Branches Created and Merged

### 4.1 Feature Branches

| Branch                          | Purpose                                              | Status   | Merged into                          |
|---------------------------------|------------------------------------------------------|----------|--------------------------------------|
| `feature/models-dev1`           | Domain models (Product, Customer, Warranty, Claim)   | Merged   | `develop` (PR #1)                    |
| `feature/repositories-dev1`     | Repository contracts and implementations (in-memory + file) | Merged | `develop` (PR #2 and #3)             |
| `feature/service-dev2`          | Service layer / business logic                       | Merged   | `develop` (PR #5, #11, #13)          |
| `feature/ui-leader`             | Console interface (CRUD menus and reports)           | Merged   | `develop` (PR #14)                   |
| `feature/main-leader`           | Main integration (Main / wiring)                     | Merged   | `develop` (PR #15)                   |
| `feature/javafx-ui-dev2`        | Graphical interface with JavaFX                      | Merged   | `develop` (PR #16)                   |
| `feature/jbdc-persistence`      | JDBC persistence + HikariCP connection pool          | Merged   | `develop` (PR #17 and #18)           |
| `feature/hierarchy-diagram`     | System hierarchy diagram                             | Merged   | `main` and `develop` (PR #9 and #10) |
| `feature/ui-exe`                | UI variant / experiment                              | Existing | —                                    |
| `feature/technic-documentation` | Technical documentation work                         | Existing | —                                    |
| `fix/main-integration`         | Intermediate integration                             | Existing | —                                    |

### 4.2 Documentation Branches

| Branch                           | Purpose                                                              | Status                         |
|----------------------------------|----------------------------------------------------------------------|--------------------------------|
| `docs/integration-documentation` | Phase 1 & 2 documentation + README                                   | Merged into `develop`          |
| `docs/complete-documentation`    | Full architecture docs (layers, packages, classes, hierarchy – Mermaid) | Merged into `develop` (PR #19) |

### 4.3 Main Branches

| Branch     | Description                                                              |
|------------|--------------------------------------------------------------------------|
| `develop`  | Integration branch. Contains the final project code and documentation.   |
| `main`     | Stable branch.                                                           |

---

## 5. Pull Requests Completed

A total of **19 Pull Requests** were created and merged:

| #  | Title (summary)                                        | Author             | Base ← Head                              | Merge Date   |
|----|--------------------------------------------------------|--------------------|------------------------------------------|--------------|
| 1  | Feat(model): core domain model                         | sebastianflorezg   | develop ← feature/models-dev1            | 2026-10-28  |
| 2  | Feat(repository): contracts + in-memory                | sebastianflorezg   | develop ← feature/repositories-dev1      | 2026-10-28  |
| 3  | Feat(repository): file persistence + seed data         | sebastianflorezg   | develop ← feature/repositories-dev1      | 2026-10-28  |
| 4  | docs: phase1 document                                  | JhonfczDev         | develop ← docs/integration-documentation | 2026-10-28  |
| 5  | Feature/service dev2                                    | elkinjimenez7      | develop ← feature/service-dev2           | 2026-10-28  |
| 6  | docs: Phase2 doc                                       | JhonfczDev         | develop ← docs/integration-documentation | 2026-10-28  |
| 7  | docs: add README                                       | JhonfczDev         | develop ← docs/integration-documentation | 2026-10-28  |
| 8  | Docs/integration documentation                         | elkinjimenez7      | develop ← docs/integration-documentation | 2026-10-28  |
| 9  | Docs: hierarchy diagram                                | sebastianflorezg   | main ← feature/hierarchy-diagram         | 2026-10-28  |
| 10 | Docs: hierarchy diagram                                | sebastianflorezg   | develop ← feature/hierarchy-diagram      | 2026-10-28  |
| 11 | Feature/service dev2                                    | elkinjimenez7      | develop ← feature/service-dev2           | 2026-11-11  |
| 12 | feat(ui): ConsoleUI full CRUD and reporting            | JhonfczDev         | feature/main-leader ← feature/ui-leader  | 2026-11-13  |
| 13 | fix: make delete method public                         | elkinjimenez7      | develop ← feature/service-dev2           | 2026-11-14  |
| 14 | Feature/UI leader                                       | JhonfczDev         | develop ← feature/ui-leader              | 2026-11-14  |
| 15 | Feature/main leader                                    | JhonfczDev         | develop ← feature/main-leader            | 2026-11-14  |
| 16 | Feature/javafx UI dev2                                  | elkinjimenez7      | develop ← feature/javafx-ui-dev2         | 2026-11-22  |
| 17 | Feature/jbdc persistence                                | elkinjimenez7      | develop ← feature/jbdc-persistence       | 2026-11-22  |
| 18 | perf: reuse database connections with HikariCP         | elkinjimenez7      | develop ← feature/jbdc-persistence       | 2026-11-25  |
| 19 | Docs: architecture documentation (layers, packages, classes, hierarchy) | sebastianflorezg | develop ← docs/complete-documentation | 2026-11-25  |

---

## 6. Git Workflow Used

```
main
 │
 └── develop  (final integration branch)
       ├── feature/models-dev1
       ├── feature/repositories-dev1
       ├── feature/service-dev2
       ├── feature/ui-leader / feature/main-leader
       ├── feature/javafx-ui-dev2
       ├── feature/jbdc-persistence          ← PR #17 & #18
       ├── docs/integration-documentation
       └── docs/complete-documentation       ← PR #19
```

**Strategy followed:**

1. Each team member worked on their own `feature/*` or `docs/*` branch.
2. Changes were integrated into `develop` through Pull Requests.
3. Frequent reviews and merges were performed.
4. The final project code and architecture documentation are on **`develop`**.

---

## 7. Final Repository Status

| Aspect                         | Status                                                              |
|--------------------------------|---------------------------------------------------------------------|
| Complete functional code       | On `develop`                                                        |
| Performance improvement        | HikariCP (PR #18) integrated                                        |
| Full architecture documentation| Added via PR #19 (Mermaid diagrams: layers, packages, classes, hierarchy) |
| Phase documentation            | Present in the repository                                           |
| Total Pull Requests merged     | **19**                                                              |
| Total commits on `develop`     | **111**                                                             |

---

## 8. Conclusion

The team correctly applied a **simplified Git Flow** strategy with feature and documentation branches, continuous integration on `develop`, and systematic use of Pull Requests.

The commit distribution shows balanced participation, with **sebastianflorezg** leading in volume (models, repositories and architecture documentation), **elkinjimenez7** contributing the service layer, JavaFX UI and JDBC/HikariCP work, and **JhonfczDev** driving integration, console UI and project leadership.

This report closes the Git project management activity for SIGATEC.

---

**Prepared for:** Project management with Git (closure)  
**Repository:** [JhonfczDev/SIGATEC-Project](https://github.com/JhonfczDev/SIGATEC-Project)

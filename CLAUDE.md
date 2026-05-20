# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository layout

This is a multi-project workspace for the "love-space" product. Each subdirectory is an independent project with its own VCS-less tree (the outer directory is the single git repo).

- `love-space-web/` — Admin dashboard **frontend** (React 19 + TypeScript + Vite 6 + Tailwind CSS v4). Bootstrapped from the TailAdmin free template.
- `love-space-admin/` — Admin dashboard **backend** (Spring Boot 4.0.6, Java 25, Maven). Package root: `com.loves.space`. Main class: `LoveSpaceAdminApplication`.
- `love-space-app/` — Mobile **app backend** (Spring Boot 4.0.6, Java 25, Maven). Package root: `com.space.app`. Main class: `LoveSpaceAppApplication`.
- `需求文档.pdf` — Product requirements document (Chinese). Consult this for feature/business-rule questions.

The two backends are siblings, not modules of one parent POM. They share the same dependency stack (Spring Data JPA, Spring Security, Spring Web MVC, PostgreSQL, Lombok) but serve different clients (admin web vs. mobile app) and likely point at different schemas/services.

## Common commands

All commands assume you are inside the relevant subproject directory.

### love-space-web

```bash
npm install        # first time
npm run dev        # Vite dev server
npm run build      # tsc -b && vite build (type-check then bundle)
npm run lint       # ESLint (flat config: eslint.config.js)
npm run preview    # preview production build
```

Node 18+ required; 20+ recommended.

### love-space-admin / love-space-app

Both use the Maven Wrapper.

```bash
./mvnw spring-boot:run                                  # run locally
./mvnw test                                             # run all tests
./mvnw -Dtest=ClassName test                            # single test class
./mvnw -Dtest=ClassName#methodName test                 # single test method
./mvnw package                                          # build jar (target/*.jar)
./mvnw spring-boot:build-image                          # OCI image (admin only — has it preconfigured)
```

Java 25 toolchain is required (set in `pom.xml` `<java.version>25</java.version>`).
PostgreSQL is the runtime DB; check `src/main/resources/application.properties` for connection config before running.

Lombok is enabled with annotation processing wired into the compiler plugin — IDEs need the Lombok plugin installed.

## Architecture notes

- **Three-tier split.** Frontend (`web`) talks to the admin backend (`admin`); the mobile app (not in this repo) talks to the app backend (`app`). Keep API surfaces for admin vs. app distinct — do not cross-wire controllers between the two backends.
- **Frontend is template-derived.** `love-space-web` still contains the upstream TailAdmin demo pages (Calendar, Charts, UI Elements, Forms, Tables, Auth) wired in `src/App.tsx`. Treat these as scaffolding to replace, not as product features. The structure is `pages/` (route components) + `layout/AppLayout.tsx` (sidebar+header shell) + `components/` + `context/` + `hooks/` + `icons/` (SVGR-imported).
- **Routing.** `react-router` v7 with `BrowserRouter`. Authenticated routes are nested inside `<AppLayout />`; `/signin` and `/signup` sit outside it. A catch-all 404 falls through at the bottom of `App.tsx`.
- **Styling.** Tailwind v4 via `@tailwindcss/postcss`. No `tailwind.config.js` — configuration lives in CSS (`src/index.css`) per Tailwind v4 conventions.
- **Backends are skeletons.** As of writing, `com.loves.space` and `com.space.app` contain only the `@SpringBootApplication` entry class and a context-loads test. When adding code, follow standard Spring Boot layering (controller / service / repository / entity) under the respective root package.

## Working across projects

- When a change touches both web and admin (e.g. a new admin API), update the Java controller/DTO first, then mirror types/clients in the React app. There is no shared schema/codegen yet.
- The outer directory is a single git repo, so commits can legitimately span multiple subprojects. Scope commit messages with a prefix (e.g. `web:`, `admin:`, `app:`) to keep history readable.

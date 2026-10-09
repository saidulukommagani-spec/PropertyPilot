# Frontend dependency recovery

## Evidence and confidence

The checked-in project files do not currently expose a recoverable dependency graph. `vite.config.ts`, `vitest.config.ts`, `playwright.config.ts`, `tsconfig.json`, and `tsconfig.node.json` contain directory-outline text instead of configuration. Most files under `src` do too; the remaining prose is a generic React roadmap, not executable imports. `rg` found no import statements in `src`.

The corrupted `package.json` does retain this command line:

```sh
npm install react-redux @reduxjs/toolkit react-query axios react-router-dom formik yup
```

The seven packages in that line are the only dependency evidence. React and React DOM are necessary for the visible `.tsx` app structure and Vite/TypeScript tooling is suggested by the named config files, but no surviving import/configuration proves their exact versions. Testing packages below are recommendations based on `vitest.config.ts`, `playwright.config.ts`, and `src/testing` filenames, not confirmed source imports.

## Reconstructed `package.json`

This is a practical recovery manifest based on the surviving install command and project shape. Scripts are conventional defaults because their original values are unrecoverable.

```json
{
  "name": "propertypilot-frontend",
  "private": true,
  "version": "0.1.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc -b && vite build",
    "preview": "vite preview",
    "test": "vitest",
    "test:watch": "vitest --watch",
    "test:coverage": "vitest --coverage",
    "test:e2e": "playwright test"
  },
  "dependencies": {
    "@reduxjs/toolkit": "^2.5.0",
    "axios": "^1.7.9",
    "formik": "^2.4.6",
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "react-query": "^3.39.3",
    "react-redux": "^9.2.0",
    "react-router-dom": "^6.28.1",
    "yup": "^1.6.1"
  },
  "devDependencies": {
    "@playwright/test": "^1.49.1",
    "@testing-library/jest-dom": "^6.6.3",
    "@testing-library/react": "^16.1.0",
    "@testing-library/user-event": "^14.5.2",
    "@types/react": "^18.3.18",
    "@types/react-dom": "^18.3.5",
    "@vitejs/plugin-react": "^4.3.4",
    "jsdom": "^25.0.1",
    "msw": "^2.7.0",
    "typescript": "~5.7.2",
    "vite": "^6.0.7",
    "vitest": "^2.1.8"
  }
}
```

### Dependencies

- **Explicitly present in the retained command:** `react-redux`, `@reduxjs/toolkit`, `react-query`, `axios`, `react-router-dom`, `formik`, `yup`.
- **Required by the React app shape:** `react`, `react-dom`.

### Dev dependencies

- **Build and TypeScript:** `vite`, `@vitejs/plugin-react`, `typescript`, `@types/react`, `@types/react-dom`.
- **Suggested by test config and test helper paths:** `vitest`, `jsdom`, `@testing-library/react`, `@testing-library/jest-dom`, `@testing-library/user-event`, `msw`, `@playwright/test`.

### Recommended versions

The manifest uses compatible, pinned major/minor ranges as a baseline for a React 18/Vite/TypeScript project. Exact historical versions cannot be established without the deleted configuration or a lockfile. The retained `react-query` package name implies React Query v3; if the source was actually written for TanStack Query v4/v5, the dependency would instead be `@tanstack/react-query` and its imports/configuration must be recovered from source history.

### Install command

From `propertypilot-frontend`, install runtime dependencies with:

```sh
npm install @reduxjs/toolkit axios formik react react-dom react-query react-redux react-router-dom yup
```

Install the build and test tooling with:

```sh
npm install -D @playwright/test @testing-library/jest-dom @testing-library/react @testing-library/user-event @types/react @types/react-dom @vitejs/plugin-react jsdom msw typescript vite vitest
```

To reproduce the versions in the manifest, save the JSON above as `package.json` first, then run `npm install` to generate a matching lockfile.

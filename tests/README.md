# QA Backend Challenge — Tests

This module contains basic regression and performance tests for the services:
- **svc-user**
- **svc-calendar**

Additional manual/outlined tests are documented in `tests/docs/TestCases.pdf`

**Technologies used:**
- Java 17
- RestAssured (API testing)
- JUnit 5 (test runner)
- Allure Report (reporting)
- Gatling (performance testing)
- GitHub Actions (CI/CD)
- Docker Compose (to run services and tests in containers)

## How to run tests locally

Prerequisite: Build service images and artifacts from the repository root so tests can run against built modules.

```bash
mvn clean install -pl !tests
```

Then run tests from the tests directory.

### Regression tests
```bash
cd tests
docker compose up -d
mvn -B test
docker compose down -v
```

### Performance tests (Gatling)
```bash
cd tests
docker compose up -d
mvn -B gatling:test
docker compose down -v
```

Notes:
- After running docker compose up -d, wait a few seconds for services to start.
- The initial mvn clean install -pl !tests at the repo root builds svc-user and svc-calendar, producing images/jars needed by the tests.

## CI/CD
- Any push to the remote repository triggers the GitHub Actions pipeline.
- Allure and Gatling reports are generated during CI. See workflow run summary for links/artifacts or published pages.

## Viewing reports locally
- Allure (after running regression tests):
```bash
mvn allure:report
mvn allure:serve
```
- Gatling: performance reports are generated automatically under tests/target/gatling after the run.

## Notes
- Regression tests are basic (mostly positive, with a few negative cases).
- Additional test cases: `tests/docs/TestCases.pdf`
- A few known issues may be present in the code.

## Possible improvements
- Add automated security testing (OWASP ZAP).
- Add static code analysis.
- Expand CI/CD pipeline.
- Add Java profiling to identify performance bottlenecks.
- Run more exhaustive performance testing.
- Provide Helm charts and Kubernetes manifests for deployment.
- Use Terraform to provision isolated environments for testing/performance in the cloud.
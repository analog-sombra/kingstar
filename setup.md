## Setup (Windows)

1. **Check Java**
   - Run: `java --version`
   - Required: Java 21+

2. **Check Maven**
   - Run: `mvn --version`
   - Ensure Maven uses Java 21+

3. **Configure MySQL connection**
   - Edit [db.properties](./src/main/resources/config/db.properties):
     - `db.url`
     - `db.username`
     - `db.password`

4. **Run migration (creates DB, tables, and seed data)**
   - Run: `mvn exec:java@migrate-db`
   - What it does:
     - creates database `library_management` if missing
     - creates core tables if missing
     - runs additional migration updates
     - inserts initial seed data if tables are empty

5. **Run application**
   - Run: `mvn javafx:run`
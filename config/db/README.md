# Config Database Folder

Store database backup and configuration files here.

## Usage:

### Backups
```bash
# To backup current database:
mysqldump -u root -p library_management > library_management_backup_YYYY-MM-DD.sql

# Place backup files in this folder:
config/db/library_management_backup_2026-10-23.sql
```

### Restore from backup:
```bash
mysql -u root -p library_management < config/db/library_management_backup_2026-10-23.sql
```

## File naming convention:
- `library_management_backup_YYYY-MM-DD.sql`
- `migration_vX.Y.Z.sql`
- `schema_update_YYYY-MM-DD.sql`

## Contents:
- Database backups
- Migration scripts
- Schema versions
- Restore scripts

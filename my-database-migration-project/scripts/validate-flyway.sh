#!/bin/bash

# Validate Flyway migrations

# Function to check SQL syntax
check_sql_syntax() {
    for file in migrations/*.sql; do
        if ! sqlcheck "$file"; then
            echo "SQL syntax error in $file"
            exit 1
        fi
    done
}

# Function to check migration numbering
check_migration_numbering() {
    previous_number=0
    for file in migrations/*.sql; do
        migration_number=$(echo "$file" | grep -oP 'V\K[0-9]+')
        if [[ $migration_number -le $previous_number ]]; then
            echo "Migration numbering error: $file has a number less than or equal to the previous migration."
            exit 1
        fi
        previous_number=$migration_number
    done
}

# Function to check for required migrations
check_required_migrations() {
    required_migrations=(1 2 3 4 5 6 7 8)
    for number in "${required_migrations[@]}"; do
        if [[ ! -f "migrations/V${number}__*.sql" ]]; then
            echo "Missing required migration: V${number}__*.sql"
            exit 1
        fi
    done
}

# Run checks
check_sql_syntax
check_migration_numbering
check_required_migrations

echo "All Flyway migrations are valid."
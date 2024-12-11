# PGCR Batch Processor

The **PGCRBatchProcessor** is a backend application that processes Post Game Carnage Reports (PGCRs) from Destiny 2, populate a database with historical player activity data.

## Features

- **Batch Processing**: Efficiently reads and processes large PGCR files using Spring Batch's processing architecture.
- **Data Storage**: Populates databases with player and activity data using both relational (PostgresQL) and non-relational (Redis) databases.
- **Modular Design**: Extensible architecture to support additional processing steps, including player-character linking.
- **Optimized Performance**: Built for handling high volumes of concurrent data processing tasks.

## Technology Stack

- **Languages**: Java
- **Frameworks**: Spring Boot, Spring Batch
- **Databases**: PostgresQL, Redis
- **Other**: Docker Compose, Grafana, Prometheus

## Current Development
This application was made to process original PGCRs and condense them down to only the useful nits-and-bits that will be utilized by my (rivenbot)[https://www.github.com/dvillavicencio/rivenbot] project,
because original PGCRs usually have many irrelevant and not useful data for Destiny 2 raids.

Huge thanks to both Cbro, Newo, and the Destiny 2 developer community!

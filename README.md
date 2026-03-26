# Email Reminders

A Spring Boot 3.x application for scheduled email reminders with magic link authentication, user groups, and recurring events.

## Version

1.0.0

## Features

- **Magic Link Authentication** - Passwordless login via one-time email links
- **User Groups** - Organize users into groups with roles (Owner, Admin, Member)
- **Events & Reminders** - Create events with configurable reminder times
- **Recurring Events** - Support for daily, weekly, monthly, and yearly recurrence
- **Email Notifications** - HTML reminder emails with action links
- **Reminder Actions** - Snooze (1 hour, 1 day, 1 week), turn off single, or cancel all

## Tech Stack

- Java 21
- Spring Boot 3.4.3
- PostgreSQL
- Flyway for migrations
- Spring Security with JWT
- Thymeleaf for web UI and email templates
- Spring Mail (Fastmail SMTP compatible)

## Quick Start

### Prerequisites

- Java 21+
- Docker and Docker Compose
- Maven 3.9+

### Running Locally

1. Start the infrastructure:
```bash
docker-compose up postgres mailhog -d
```

2. Run the application:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

3. Access the application:
   - Web UI: http://localhost:8080
   - MailHog (email testing): http://localhost:8025

### Running with Docker

```bash
docker-compose up --build
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/emailreminders` |
| `DATABASE_USERNAME` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `postgres` |
| `MAIL_HOST` | SMTP host | `smtp.fastmail.com` |
| `MAIL_PORT` | SMTP port | `465` |
| `MAIL_USERNAME` | SMTP username | - |
| `MAIL_PASSWORD` | SMTP password | - |
| `JWT_SECRET` | JWT signing secret (min 256 bits) | - |
| `APP_BASE_URL` | Application base URL | `http://localhost:8080` |
| `FROM_EMAIL` | Sender email address | - |

### Fastmail Configuration

For production with Fastmail:
1. Enable app passwords in Fastmail settings
2. Set `MAIL_HOST=smtp.fastmail.com`
3. Set `MAIL_PORT=465`
4. Set `MAIL_USERNAME` to your Fastmail email
5. Set `MAIL_PASSWORD` to your app password

## API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/magic-link` - Request magic link
- `GET /api/v1/auth/verify?token=` - Verify magic link

### Groups
- `GET /api/v1/groups` - List user's groups
- `POST /api/v1/groups` - Create group
- `GET /api/v1/groups/{id}` - Get group details
- `PUT /api/v1/groups/{id}` - Update group
- `DELETE /api/v1/groups/{id}` - Delete group
- `POST /api/v1/groups/{id}/members` - Add members

### Events
- `GET /api/v1/events` - List user's events
- `POST /api/v1/events` - Create event
- `GET /api/v1/events/{id}` - Get event details
- `PUT /api/v1/events/{id}` - Update event
- `DELETE /api/v1/events/{id}` - Delete event

## Project Structure

```
src/main/java/pl/piomin/services/emailreminders/
├── config/          # Configuration classes
├── controller/
│   ├── api/         # REST API controllers
│   └── web/         # Web UI controllers
├── dto/             # Request/Response DTOs
├── exception/       # Custom exceptions
├── model/           # JPA entities
├── repository/      # Spring Data repositories
├── security/        # JWT and security classes
├── service/         # Business logic
└── util/            # Utility classes
```

## Testing

```bash
# Run all tests
mvn test

# Run with coverage report
mvn verify
```

## License

Apache License 2.0

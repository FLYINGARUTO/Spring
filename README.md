**Backend Features and Technical Highlights**

- **User Management**: Provides essential features like user registration, login, and password reset.
  
- **MyBatis-Plus Integration**: Utilizes MyBatis-Plus for the DAO layer, simplifying database interactions.

- **Redis for Verification**: Stores registration and password reset verification codes in Redis with expiration time control.

- **Rate Limiting with Redis**: Implements IP address rate limiting using Redis to manage high-frequency access.

- **Asynchronous Task Processing with RabbitMQ**: Sends tasks via RabbitMQ, processed by listeners (e.g., sending verification codes to email for registration/password reset).

- **Spring Security & JWT**: Leverages Spring Security for authentication, manually integrated with JWT for token validation.

- **DTO and Entity Separation**: Separates Data Transfer Objects (DTOs) from data layer entities, using utility methods with reflection for easy conversion.

- **Unified JSON Error Handling**: Standardizes error and exception responses in JSON format for consistent front-end processing.

- **Custom CORS Handling**: Manages Cross-Origin Resource Sharing (CORS) using a custom filter implementation.

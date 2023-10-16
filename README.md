# FINT core resource server

## Project Overview
This project provides a resource server with a focus on security configurations, tailored for a Spring WebFlux environment. It includes custom JWT-based authentication mechanisms and role-based authorizations.

### Key Components:
- **ConsumerConfig**: Manages the configurations related to the consumer of the resource server.
- **FintSecurity**: Handles security settings and role types.
- **SecurityConfiguration**: Central configuration for Spring WebFlux security.
- **CorePrincipal**: Represents the authenticated user, containing details such as organization ID, scope, username, and roles.
- **CorePrincipalConverter**: Converts JWT claims into the CorePrincipal object.


### Configuration Setup
To set up the project, you'll need to configure the following properties:

#### In FintSecurity:
1. **fint.security.enabled**: Enables or disables the security. Default is true.
2. **fint.security.orgid**: Determines if an organization ID is required. Default is true.
3. **fint.security.component**: Indicates if a component is required. Default is true.
4. **fint.security.role-type**: Sets the role type. Default is Client, the only other available value is Adapter

#### In ConsumerConfig:
1. **fint.consumer.domain**: Specifies the domain of the consumer.
2. **fint.consumer.package**: Denotes the package name.
3. **fint.consumer.orgId**: Represents the organization ID.

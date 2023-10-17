# Fint Core Resource Server Security

This library provides a `SecurityConfiguration` designed for Fint projects. Its primary functions include:
1. Configuring security settings for Fint projects.
2. Converting any JWT (JSON Web Token) into a `CorePrincipal` object.

## 1. Configuration Files

### FintSecurity
- **Purpose**: Manages security-related configurations.
- **Properties**:
    - `fint.security.enabled`: Determines if the security is active.
    - `fint.security.orgid`: Validates if the user's org-id matches the consumer's org-id.
    - `fint.security.component`: Ensures the user has the appropriate credentials for the component, both in terms of domain and package.
    - `fint.security.scope`: Checks if the user possesses the correct scope.

### ConsumerConfig
- **Purpose**: Contains settings related to the consumer, including domain, package name, and organization ID.
- **Properties**:
    - `fint.consumer.domain`: Specifies the domain name.
    - `fint.consumer.package`: Sets the package name.
    - `fint.consumer.orgId`: Defines the organization ID.

**Example Configuration**:
For validating users for a utdanning vurdering consumer in mrfylke.no:

```yaml
fint:
  consumer:
    domain: utdanning
    package: vurdering
    org-id: mrfylke.no
```

### OAuthJwtConfig
- **Purpose**: Manages configurations related to OAuth JWT, including jwt-issuer URI.
- **Note**: Make sure to include a `jwt-config.properties` file in your classpath, specifically under the `oauth-jwt-settings` directory.

### SecurityConfiguration
- **Purpose**: Serves as the central configuration for security settings.
- **Behavior**: Automatically adapts to the configurations set in the above sections.

## 2. Principal and Converter

### CorePrincipal
- **Description**: Represents a user or system entity.
- **Attributes**: Includes `orgId`, `username`, `scopes`, and `roles`.

### CorePrincipalConverter
- **Purpose**: Transforms a `Jwt` token into a `CorePrincipal` object.

## 3. Integration Steps

1. **Include Library**: Add this library to your project's dependency list.

```groovy
dependencies {
    implementation 'no.fintlabs:core-resource-server-security:VERSION'
}
```

Please replace `VERSION` with the specific version number you intend to use.
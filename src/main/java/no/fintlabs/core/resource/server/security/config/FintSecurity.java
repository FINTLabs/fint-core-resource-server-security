package no.fintlabs.core.resource.server.security.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Data
@Configuration
public class FintSecurity {

    @Value("${fint.security.enabled:true}")
    private boolean enabled;

    @Value("${fint.security.orgid:true}")
    private boolean orgIdRequired;

    @Value("${fint.security.component:true}")
    private boolean componentRequired;

    @Value("${fint.security.scope:true}")
    private boolean scopeRequired;

    private RoleType roleType;

    @Value("${fint.security.role-type:Client}")
    public void setRoleType(String roleType) {
        this.roleType = RoleType.fromValue(roleType);
    }

    public String getRoleType() {
        return this.roleType.toString();
    }

    private enum RoleType {
        CLIENT("Client"),
        ADAPTER("Adapter");

        private final String value;

        RoleType(String value) {
            this.value = value;
        }

        public static RoleType fromValue(String value) {
            for (RoleType roleType : values()) {
                if (roleType.value.equalsIgnoreCase(value)) {
                    return roleType;
                }
            }
            log.error("Invalid role type: {} Defaulting to Client.", value);
            return CLIENT;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}

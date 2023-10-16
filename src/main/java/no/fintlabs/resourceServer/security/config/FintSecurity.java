package no.fintlabs.resourceServer.security.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class FintSecurity {

    @Value("${fint.security.enabled:true}")
    private boolean enabled;

    @Value("${fint.security.orgid:true}")
    private boolean orgIdRequired;

    @Value("${fint.security.component:true}")
    private boolean componentRequired;

    @Value("${fint.security.scope}")
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
            throw new IllegalArgumentException("Invalid role type: " + value);
        }

        @Override
        public String toString() {
            return value;
        }
    }
}

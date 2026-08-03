import { useEffect } from 'react';

export default function withAuditLog(Component, label) {
    const componentName = label || Component.displayName || Component.name || 'Component';

    function AuditLoggedComponent(props) {
        useEffect(() => {
            console.log(`[audit] <${componentName}> mounted`);
        }, []);

        console.log(`[audit] <${componentName}> render`);

        return <Component {...props} />;
    }

    AuditLoggedComponent.displayName = `withAuditLog(${componentName})`;

    return AuditLoggedComponent;
}
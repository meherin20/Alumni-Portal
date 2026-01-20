/**
 * Centralized Authentication Utility
 * Manages user authentication state and provides route protection
 */

const Auth = {
    // Storage keys
    STORAGE_EMAIL_KEY: 'amp-user-email',
    STORAGE_ROLE_KEY: 'amp-user-role',
    STORAGE_LOGIN_TIME_KEY: 'amp-login-time',

    /**
     * Get current authenticated user
     * @returns {Object|null} {email, role} or null if not authenticated
     */
    getCurrentUser() {
        try {
            const email = localStorage.getItem(this.STORAGE_EMAIL_KEY);
            const role = localStorage.getItem(this.STORAGE_ROLE_KEY);
            
            if (!email || !role) {
                return null;
            }
            
            return {
                email: email.trim(),
                role: role.trim().toLowerCase()
            };
        } catch (e) {
            console.error('Error getting current user:', e);
            return null;
        }
    },

    /**
     * Check if user is authenticated
     * @returns {boolean}
     */
    isAuthenticated() {
        const user = this.getCurrentUser();
        return user !== null && user.email && user.role;
    },

    /**
     * Set authentication data
     * @param {string} email - User email
     * @param {string} role - User role (admin, alumni, student)
     */
    setAuth(email, role) {
        try {
            if (!email || !role) {
                throw new Error('Email and role are required');
            }
            
            localStorage.setItem(this.STORAGE_EMAIL_KEY, email.trim());
            localStorage.setItem(this.STORAGE_ROLE_KEY, role.trim().toLowerCase());
            localStorage.setItem(this.STORAGE_LOGIN_TIME_KEY, new Date().toISOString());
            
            console.log('Auth set:', { email: email.trim(), role: role.trim().toLowerCase() });
        } catch (e) {
            console.error('Error setting auth:', e);
            throw e;
        }
    },

    /**
     * Clear authentication data
     */
    clearAuth() {
        try {
            localStorage.removeItem(this.STORAGE_EMAIL_KEY);
            localStorage.removeItem(this.STORAGE_ROLE_KEY);
            localStorage.removeItem(this.STORAGE_LOGIN_TIME_KEY);
            console.log('Auth cleared');
        } catch (e) {
            console.error('Error clearing auth:', e);
        }
    },

    /**
     * Require authentication and optionally specific role
     * Redirects to login if not authenticated or wrong role
     * @param {string|null} requiredRole - Required role ('admin', 'alumni', 'student') or null for any authenticated user
     * @param {string} redirectUrl - Optional redirect URL after login (default: current page)
     */
    requireAuth(requiredRole = null, redirectUrl = null) {
        const user = this.getCurrentUser();
        
        if (!user) {
            console.warn('Auth required but user not authenticated');
            const currentUrl = redirectUrl || window.location.pathname + window.location.search;
            window.location.href = `/login.html?redirect=${encodeURIComponent(currentUrl)}`;
            return false;
        }

        if (requiredRole && user.role !== requiredRole.toLowerCase()) {
            console.warn(`Auth required role '${requiredRole}' but user has role '${user.role}'`);
            // Redirect to appropriate dashboard based on user's actual role
            const roleDashboards = {
                'admin': '/admin-dashboard.html',
                'alumni': '/alumni-portal.html',
                'student': '/student-view.html'
            };
            const dashboard = roleDashboards[user.role] || '/login.html';
            window.location.href = dashboard;
            return false;
        }

        return true;
    },

    /**
     * Logout user and redirect to landing page
     */
    logout() {
        console.log('Logging out...');
        this.clearAuth();
        
        // Redirect to landing page
        window.location.href = '/index.html';
    },

    /**
     * Get user email (convenience method)
     * @returns {string|null}
     */
    getEmail() {
        const user = this.getCurrentUser();
        return user ? user.email : null;
    },

    /**
     * Get user role (convenience method)
     * @returns {string|null}
     */
    getRole() {
        const user = this.getCurrentUser();
        return user ? user.role : null;
    }
};

// Make Auth available globally
window.Auth = Auth;

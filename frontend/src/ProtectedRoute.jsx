import { Navigate, Outlet } from 'react-router-dom';

function ProtectedRoute() {
    const token = sessionStorage.getItem('access_token');

    // If no token exists, redirect to login page
    if (!token) {
        return <Navigate to="/login" replace />;
    }

    // If token exists, render the child page (e.g., Dashboard)
    return <Outlet />;
}

export default ProtectedRoute;
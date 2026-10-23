import { Navigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export default function RequireRole({ role, children }) {
    const { auth } = useAuth()
    if (!auth) return <Navigate to="/login" replace />
    if (auth.role !== role) return <Navigate to="/" replace />
    return children
}
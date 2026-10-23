import { NavLink, Navigate, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

const NAV = {
    OWNER: [
        ['/owner/students', 'Students'],
        ['/owner/teachers', 'Teachers'],
        ['/owner/batches', 'Batches'],
        ['/owner/enrollments', 'Enrollments'],
        ['/owner/attendance', 'Attendance'],
        ['/owner/fees', 'Fees'],
        ['/owner/reports', 'Reports'],
    ],
    TEACHER: [['/teacher/attendance', 'Attendance']],
    STUDENT: [
        ['/student/fees', 'My fees'],
        ['/student/batches', 'My batches'],
    ],
}

export default function Layout() {
    const { auth, logout } = useAuth()
    const navigate = useNavigate()

    if (!auth) return <Navigate to="/login" replace />

    function handleLogout() {
        logout()
        navigate('/login')
    }

    return (
        <div className="app">
            <header className="topbar">
                <strong className="brand">Coaching Manager</strong>
                <nav>
                    {(NAV[auth.role] || []).map(([to, label]) => (
                        <NavLink key={to} to={to}>
                            {label}
                        </NavLink>
                    ))}
                </nav>
                <div className="user">
          <span>
            {auth.email} ({auth.role})
          </span>
                    <button className="secondary small" onClick={handleLogout}>
                        Logout
                    </button>
                </div>
            </header>
            <main className="content">
                <Outlet />
            </main>
        </div>
    )
}
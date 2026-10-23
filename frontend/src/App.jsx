import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './auth/AuthContext'
import Layout from './components/Layout'
import RequireRole from './components/RequireRole'
import Login from './pages/Login'
import Attendance from './pages/Attendance'
import Students from './pages/owner/Students'
import Teachers from './pages/owner/Teachers'
import Batches from './pages/owner/Batches'
import Enrollments from './pages/owner/Enrollments'
import Fees from './pages/owner/Fees'
import Reports from './pages/owner/Reports'
import MyFees from './pages/student/MyFees'
import MyBatches from './pages/student/MyBatches'

const HOME = {
  OWNER: '/owner/students',
  TEACHER: '/teacher/attendance',
  STUDENT: '/student/fees',
}

function Home() {
  const { auth } = useAuth()
  if (!auth) return <Navigate to="/login" replace />
  if (!HOME[auth.role]) return <p>Unknown role: {auth.role}</p>
  return <Navigate to={HOME[auth.role]} replace />
}

export default function App() {
  const owner = (el) => <RequireRole role="OWNER">{el}</RequireRole>
  const teacher = (el) => <RequireRole role="TEACHER">{el}</RequireRole>
  const student = (el) => <RequireRole role="STUDENT">{el}</RequireRole>

  return (
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<Home />} />

        <Route element={<Layout />}>
          <Route path="/owner/students" element={owner(<Students />)} />
          <Route path="/owner/teachers" element={owner(<Teachers />)} />
          <Route path="/owner/batches" element={owner(<Batches />)} />
          <Route path="/owner/enrollments" element={owner(<Enrollments />)} />
          <Route path="/owner/attendance" element={owner(<Attendance mode="owner" />)} />
          <Route path="/owner/fees" element={owner(<Fees />)} />
          <Route path="/owner/reports" element={owner(<Reports />)} />

          <Route path="/teacher/attendance" element={teacher(<Attendance mode="teacher" />)} />

          <Route path="/student/fees" element={student(<MyFees />)} />
          <Route path="/student/batches" element={student(<MyBatches />)} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
  )
}
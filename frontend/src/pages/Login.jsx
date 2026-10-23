import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import Alert from '../components/Alert'

export default function Login() {
    const { auth, login } = useAuth()
    const navigate = useNavigate()
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [busy, setBusy] = useState(false)

    if (auth) return <Navigate to="/" replace />

    async function submit(e) {
        e.preventDefault()
        setError('')
        setBusy(true)
        try {
            await login(email, password)
            navigate('/')
        } catch (err) {
            setError(err.message)
        } finally {
            setBusy(false)
        }
    }

    return (
        <div className="login-wrap">
            <form className="card form" onSubmit={submit}>
                <h2>Coaching Manager</h2>
                <p className="muted">Login as owner, teacher or student</p>
                <Alert>{error}</Alert>
                <input
                    type="text"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button type="submit" disabled={busy}>
                    {busy ? 'Logging in...' : 'Login'}
                </button>
            </form>
        </div>
    )
}
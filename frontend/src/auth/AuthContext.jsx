import { createContext, useContext, useState } from 'react'
import { api, getAuth, saveAuth, clearAuth } from '../api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
    const [auth, setAuth] = useState(getAuth())

    async function login(email, password) {
        const data = await api.post('/api/auth/login', { email, password })
        saveAuth(data)
        setAuth(data)
        return data
    }

    function logout() {
        clearAuth()
        setAuth(null)
    }

    return (
        <AuthContext.Provider value={{ auth, login, logout }}>
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    return useContext(AuthContext)
}
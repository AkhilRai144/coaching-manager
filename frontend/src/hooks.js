import { useCallback, useEffect, useRef, useState } from 'react'
import { api } from './api'

// GET karke data, loading aur error deta hai. path null ho toh kuch nahi karta.
export function useFetch(path) {
    const [data, setData] = useState(null)
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(Boolean(path))
    const latest = useRef(0)

    const reload = useCallback(async () => {
        const id = ++latest.current
        if (!path) {
            setData(null)
            setError('')
            setLoading(false)
            return
        }
        setLoading(true)
        setError('')
        try {
            const result = await api.get(path)
            if (id === latest.current) setData(result)
        } catch (err) {
            if (id === latest.current) {
                setData(null)
                setError(err.message)
            }
        } finally {
            if (id === latest.current) setLoading(false)
        }
    }, [path])

    useEffect(() => {
        reload()
    }, [reload])

    return { data, error, loading, reload }
}
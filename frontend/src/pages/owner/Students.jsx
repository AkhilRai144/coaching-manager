import { useState } from 'react'
import { api } from '../../api'
import { useFetch } from '../../hooks'
import Alert from '../../components/Alert'

const EMPTY = { name: '', phone: '', email: '', password: '' }

export default function Students() {
    const students = useFetch('/api/students')
    const [form, setForm] = useState(EMPTY)
    const [editingId, setEditingId] = useState(null)
    const [msg, setMsg] = useState(null)

    const change = (e) => setForm({ ...form, [e.target.name]: e.target.value })

    function cancel() {
        setForm(EMPTY)
        setEditingId(null)
    }

    async function submit(e) {
        e.preventDefault()
        setMsg(null)
        try {
            if (editingId) {
                await api.put(`/api/students/${editingId}`, { name: form.name, phone: form.phone })
                setMsg({ type: 'success', text: 'Student updated' })
            } else {
                await api.post('/api/students', form)
                setMsg({ type: 'success', text: 'Student added' })
            }
            cancel()
            students.reload()
        } catch (err) {
            setMsg({ type: 'error', text: err.message })
        }
    }

    function startEdit(s) {
        setEditingId(s.id)
        setForm({ name: s.name, phone: s.phone, email: s.email, password: '' })
    }

    async function remove(s) {
        if (!window.confirm(`Delete ${s.name}?`)) return
        setMsg(null)
        try {
            await api.del(`/api/students/${s.id}`)
            students.reload()
        } catch (err) {
            setMsg({ type: 'error', text: err.message })
        }
    }

    return (
        <div>
            <h2>Students</h2>
            {msg && <Alert type={msg.type}>{msg.text}</Alert>}

            <form className="card form" onSubmit={submit}>
                <h3>{editingId ? 'Edit student' : 'Add student'}</h3>
                <input name="name" placeholder="Name" value={form.name} onChange={change} />
                <input name="phone" placeholder="Phone (10 digits)" value={form.phone} onChange={change} />
                {!editingId && (
                    <>
                        <input name="email" placeholder="Email" value={form.email} onChange={change} />
                        <input
                            name="password"
                            type="password"
                            placeholder="Password (min 6 characters)"
                            value={form.password}
                            onChange={change}
                        />
                    </>
                )}
                <div className="row">
                    <button type="submit">{editingId ? 'Update' : 'Add'}</button>
                    {editingId && (
                        <button type="button" className="secondary" onClick={cancel}>
                            Cancel
                        </button>
                    )}
                </div>
            </form>

            {students.loading && <p>Loading...</p>}
            <Alert>{students.error}</Alert>
            {students.data && students.data.length === 0 && <p className="muted">No students yet.</p>}
            {students.data && students.data.length > 0 && (
                <div className="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Phone</th>
                            <th>Email</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        {students.data.map((s) => (
                            <tr key={s.id}>
                                <td>{s.id}</td>
                                <td>{s.name}</td>
                                <td>{s.phone}</td>
                                <td>{s.email}</td>
                                <td className="row">
                                    <button className="small" onClick={() => startEdit(s)}>
                                        Edit
                                    </button>
                                    <button className="small danger" onClick={() => remove(s)}>
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    )
}
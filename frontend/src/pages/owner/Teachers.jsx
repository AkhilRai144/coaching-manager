import { useState } from 'react'
import { api } from '../../api'
import { useFetch } from '../../hooks'
import Alert from '../../components/Alert'

const EMPTY = { name: '', subject: '', email: '', password: '' }

export default function Teachers() {
    const teachers = useFetch('/api/teachers')
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
                await api.put(`/api/teachers/${editingId}`, { name: form.name, subject: form.subject })
                setMsg({ type: 'success', text: 'Teacher updated' })
            } else {
                await api.post('/api/teachers', form)
                setMsg({ type: 'success', text: 'Teacher added' })
            }
            cancel()
            teachers.reload()
        } catch (err) {
            setMsg({ type: 'error', text: err.message })
        }
    }

    function startEdit(t) {
        setEditingId(t.id)
        setForm({ name: t.name, subject: t.subject, email: t.email, password: '' })
    }

    async function remove(t) {
        if (!window.confirm(`Delete ${t.name}?`)) return
        setMsg(null)
        try {
            await api.del(`/api/teachers/${t.id}`)
            teachers.reload()
        } catch (err) {
            setMsg({ type: 'error', text: err.message })
        }
    }

    return (
        <div>
            <h2>Teachers</h2>
            {msg && <Alert type={msg.type}>{msg.text}</Alert>}

            <form className="card form" onSubmit={submit}>
                <h3>{editingId ? 'Edit teacher' : 'Add teacher'}</h3>
                <input name="name" placeholder="Name" value={form.name} onChange={change} />
                <input name="subject" placeholder="Subject" value={form.subject} onChange={change} />
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

            {teachers.loading && <p>Loading...</p>}
            <Alert>{teachers.error}</Alert>
            {teachers.data && teachers.data.length === 0 && <p className="muted">No teachers yet.</p>}
            {teachers.data && teachers.data.length > 0 && (
                <div className="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Subject</th>
                            <th>Email</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        {teachers.data.map((t) => (
                            <tr key={t.id}>
                                <td>{t.id}</td>
                                <td>{t.name}</td>
                                <td>{t.subject}</td>
                                <td>{t.email}</td>
                                <td className="row">
                                    <button className="small" onClick={() => startEdit(t)}>
                                        Edit
                                    </button>
                                    <button className="small danger" onClick={() => remove(t)}>
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
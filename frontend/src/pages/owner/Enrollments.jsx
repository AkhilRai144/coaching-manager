import { useState } from 'react'
import { api } from '../../api'
import { useFetch } from '../../hooks'
import Alert from '../../components/Alert'

export default function Enrollments() {
    const students = useFetch('/api/students')
    const batches = useFetch('/api/batches')
    const [form, setForm] = useState({ studentId: '', batchId: '' })
    const [viewBatch, setViewBatch] = useState('')
    const list = useFetch(viewBatch ? `/api/enrollments/batch/${viewBatch}` : null)
    const [msg, setMsg] = useState(null)

    async function submit(e) {
        e.preventDefault()
        setMsg(null)
        try {
            await api.post('/api/enrollments', {
                studentId: Number(form.studentId),
                batchId: Number(form.batchId),
            })
            setMsg({ type: 'success', text: 'Student enrolled' })
            if (String(form.batchId) === String(viewBatch)) list.reload()
        } catch (err) {
            setMsg({ type: 'error', text: err.message })
        }
    }

    return (
        <div>
            <h2>Enrollments</h2>
            {msg && <Alert type={msg.type}>{msg.text}</Alert>}

            <form className="card form" onSubmit={submit}>
                <h3>Enroll a student</h3>
                <select
                    value={form.studentId}
                    onChange={(e) => setForm({ ...form, studentId: e.target.value })}
                    required
                >
                    <option value="">Select student</option>
                    {students.data?.map((s) => (
                        <option key={s.id} value={s.id}>
                            {s.name} (#{s.id})
                        </option>
                    ))}
                </select>
                <select
                    value={form.batchId}
                    onChange={(e) => setForm({ ...form, batchId: e.target.value })}
                    required
                >
                    <option value="">Select batch</option>
                    {batches.data?.map((b) => (
                        <option key={b.id} value={b.id}>
                            {b.name}
                        </option>
                    ))}
                </select>
                <button type="submit">Enroll</button>
            </form>

            <div className="card">
                <h3>Students in a batch</h3>
                <select value={viewBatch} onChange={(e) => setViewBatch(e.target.value)}>
                    <option value="">Select batch</option>
                    {batches.data?.map((b) => (
                        <option key={b.id} value={b.id}>
                            {b.name}
                        </option>
                    ))}
                </select>
                <Alert>{list.error}</Alert>
                {list.data && list.data.length === 0 && <p className="muted">No students in this batch.</p>}
                {list.data && list.data.length > 0 && (
                    <table style={{ marginTop: 12 }}>
                        <thead>
                        <tr>
                            <th>Student</th>
                            <th>Joined</th>
                        </tr>
                        </thead>
                        <tbody>
                        {list.data.map((en) => (
                            <tr key={en.id}>
                                <td>{en.studentName}</td>
                                <td>{en.joinDate}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    )
}
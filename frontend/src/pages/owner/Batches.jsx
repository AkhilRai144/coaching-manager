import { useState } from 'react'
import { api } from '../../api'
import { useFetch } from '../../hooks'
import { money } from '../../format'
import Alert from '../../components/Alert'

const EMPTY = { name: '', startTime: '', endTime: '', monthlyFee: '', teacherId: '' }

export default function Batches() {
    const batches = useFetch('/api/batches')
    const teachers = useFetch('/api/teachers')
    const [form, setForm] = useState(EMPTY)
    const [msg, setMsg] = useState(null)

    const change = (e) => setForm({ ...form, [e.target.name]: e.target.value })

    async function submit(e) {
        e.preventDefault()
        setMsg(null)
        try {
            await api.post('/api/batches', {
                name: form.name,
                startTime: form.startTime,
                endTime: form.endTime,
                monthlyFee: Number(form.monthlyFee),
                teacherId: Number(form.teacherId),
            })
            setMsg({ type: 'success', text: 'Batch created' })
            setForm(EMPTY)
            batches.reload()
        } catch (err) {
            setMsg({ type: 'error', text: err.message })
        }
    }

    return (
        <div>
            <h2>Batches</h2>
            {msg && <Alert type={msg.type}>{msg.text}</Alert>}

            <form className="card form" onSubmit={submit}>
                <h3>Create batch</h3>
                <input name="name" placeholder="Batch name" value={form.name} onChange={change} />
                <div className="row">
                    <label>
                        Start <input name="startTime" type="time" value={form.startTime} onChange={change} />
                    </label>
                    <label>
                        End <input name="endTime" type="time" value={form.endTime} onChange={change} />
                    </label>
                </div>
                <input
                    name="monthlyFee"
                    type="number"
                    step="any"
                    placeholder="Monthly fee"
                    value={form.monthlyFee}
                    onChange={change}
                />
                <select name="teacherId" value={form.teacherId} onChange={change} required>
                    <option value="">Select teacher</option>
                    {teachers.data?.map((t) => (
                        <option key={t.id} value={t.id}>
                            {t.name} ({t.subject})
                        </option>
                    ))}
                </select>
                <button type="submit">Create</button>
            </form>

            {batches.loading && <p>Loading...</p>}
            <Alert>{batches.error}</Alert>
            {batches.data && batches.data.length === 0 && <p className="muted">No batches yet.</p>}
            {batches.data && batches.data.length > 0 && (
                <div className="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Time</th>
                            <th>Fee / month</th>
                            <th>Teacher</th>
                        </tr>
                        </thead>
                        <tbody>
                        {batches.data.map((b) => (
                            <tr key={b.id}>
                                <td>{b.id}</td>
                                <td>{b.name}</td>
                                <td>
                                    {b.startTime.slice(0, 5)} - {b.endTime.slice(0, 5)}
                                </td>
                                <td>{money(b.monthlyFee)}</td>
                                <td>{b.teacherName}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    )
}
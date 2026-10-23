import { useState } from 'react'
import { api } from '../api'
import { useFetch } from '../hooks'
import Alert from '../components/Alert'

export default function Attendance({ mode }) {
    const owner = mode === 'owner'
    const today = new Date().toLocaleDateString('en-CA') // YYYY-MM-DD, local time

    const batches = useFetch(owner ? '/api/batches' : '/api/me/batches')
    const [batchId, setBatchId] = useState('')
    const [date, setDate] = useState(today)
    const [status, setStatus] = useState({})
    const [saved, setSaved] = useState(null)
    const [msg, setMsg] = useState(null)
    const [busy, setBusy] = useState(false)

    const studentsPath = batchId
        ? owner
            ? `/api/enrollments/batch/${batchId}`
            : `/api/me/batches/${batchId}/students`
        : null
    const students = useFetch(studentsPath)

    const statusOf = (id) => status[id] || 'PRESENT'

    function pickBatch(value) {
        setBatchId(value)
        setStatus({})
        setSaved(null)
        setMsg(null)
    }

    async function save() {
        setMsg(null)
        setBusy(true)
        try {
            const entries = students.data.map((s) => ({
                studentId: s.studentId,
                status: statusOf(s.studentId),
            }))
            await api.post('/api/attendance', {
                batchId: Number(batchId),
                attendanceDate: date,
                entries,
            })
            setMsg({ type: 'success', text: `Attendance saved for ${entries.length} students` })
            setSaved(null)
        } catch (err) {
            setMsg({ type: 'error', text: err.message })
        } finally {
            setBusy(false)
        }
    }

    async function viewSaved() {
        setMsg(null)
        try {
            setSaved(await api.get(`/api/attendance/batch/${batchId}?date=${date}`))
        } catch (err) {
            setSaved(null)
            setMsg({ type: 'error', text: err.message })
        }
    }

    return (
        <div>
            <h2>Attendance</h2>
            {msg && <Alert type={msg.type}>{msg.text}</Alert>}
            <Alert>{batches.error}</Alert>
            {batches.data && batches.data.length === 0 && <p className="muted">No batches found.</p>}

            <div className="card row">
                <select value={batchId} onChange={(e) => pickBatch(e.target.value)}>
                    <option value="">Select batch</option>
                    {batches.data?.map((b) => (
                        <option key={b.id} value={b.id}>
                            {b.name}
                        </option>
                    ))}
                </select>
                <input
                    type="date"
                    value={date}
                    max={today}
                    onChange={(e) => {
                        setDate(e.target.value)
                        setSaved(null)
                    }}
                />
            </div>

            {batchId && (
                <div className="card">
                    <Alert>{students.error}</Alert>
                    {students.loading && <p>Loading students...</p>}
                    {students.data && students.data.length === 0 && (
                        <p className="muted">No students in this batch.</p>
                    )}
                    {students.data && students.data.length > 0 && (
                        <>
                            <table>
                                <thead>
                                <tr>
                                    <th>Student</th>
                                    <th>Status</th>
                                </tr>
                                </thead>
                                <tbody>
                                {students.data.map((s) => (
                                    <tr key={s.studentId}>
                                        <td>{s.studentName}</td>
                                        <td>
                                            {['PRESENT', 'ABSENT'].map((st) => (
                                                <label key={st} style={{ marginRight: 14 }}>
                                                    <input
                                                        type="radio"
                                                        name={`att-${s.studentId}`}
                                                        checked={statusOf(s.studentId) === st}
                                                        onChange={() => setStatus({ ...status, [s.studentId]: st })}
                                                    />{' '}
                                                    {st}
                                                </label>
                                            ))}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                            <div className="row" style={{ marginTop: 12 }}>
                                <button onClick={save} disabled={busy}>
                                    {busy ? 'Saving...' : 'Save attendance'}
                                </button>
                                <button className="secondary" onClick={viewSaved}>
                                    View saved for this date
                                </button>
                            </div>
                        </>
                    )}
                </div>
            )}

            {saved && (
                <div className="card">
                    <h3>Saved attendance ({date})</h3>
                    {saved.length === 0 && <p className="muted">Nothing saved for this date.</p>}
                    {saved.length > 0 && (
                        <table>
                            <thead>
                            <tr>
                                <th>Student</th>
                                <th>Status</th>
                            </tr>
                            </thead>
                            <tbody>
                            {saved.map((a) => (
                                <tr key={a.id}>
                                    <td>{a.studentName}</td>
                                    <td>
                                        <span className={`badge ${a.status}`}>{a.status}</span>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    )}
                </div>
            )}
        </div>
    )
}
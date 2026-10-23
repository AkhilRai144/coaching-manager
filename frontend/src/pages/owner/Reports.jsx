import { useState } from 'react'
import { useFetch } from '../../hooks'
import { money } from '../../format'
import Alert from '../../components/Alert'

export default function Reports() {
    const pending = useFetch('/api/reports/pending-fees')
    const batches = useFetch('/api/batches')
    const [batchId, setBatchId] = useState('')
    const attendance = useFetch(batchId ? `/api/reports/attendance/batch/${batchId}` : null)

    return (
        <div>
            <h2>Reports</h2>

            <h3>Pending fees by batch</h3>
            <Alert>{pending.error}</Alert>
            {pending.data && pending.data.length === 0 && <p className="muted">Nothing pending.</p>}
            {pending.data && pending.data.length > 0 && (
                <div className="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Batch</th>
                            <th>Pending dues</th>
                            <th>Pending amount</th>
                        </tr>
                        </thead>
                        <tbody>
                        {pending.data.map((r) => (
                            <tr key={r.batchId}>
                                <td>{r.batchName}</td>
                                <td>{r.pendingDues}</td>
                                <td>{money(r.pendingAmount)}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            <h3 style={{ marginTop: 28 }}>Attendance percentage</h3>
            <select value={batchId} onChange={(e) => setBatchId(e.target.value)}>
                <option value="">Select batch</option>
                {batches.data?.map((b) => (
                    <option key={b.id} value={b.id}>
                        {b.name}
                    </option>
                ))}
            </select>
            <Alert>{attendance.error}</Alert>
            {attendance.data && attendance.data.length === 0 && (
                <p className="muted">No students in this batch.</p>
            )}
            {attendance.data && attendance.data.length > 0 && (
                <div className="table-wrap" style={{ marginTop: 12 }}>
                    <table>
                        <thead>
                        <tr>
                            <th>Student</th>
                            <th>Classes</th>
                            <th>Present</th>
                            <th>Attendance %</th>
                        </tr>
                        </thead>
                        <tbody>
                        {attendance.data.map((r) => (
                            <tr key={r.studentId}>
                                <td>{r.studentName}</td>
                                <td>{r.totalClasses}</td>
                                <td>{r.presentCount}</td>
                                <td>{r.percentage}%</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    )
}
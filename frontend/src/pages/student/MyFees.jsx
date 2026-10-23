import { useFetch } from '../../hooks'
import { money, period } from '../../format'
import Alert from '../../components/Alert'

export default function MyFees() {
    const fees = useFetch('/api/me/fees')

    return (
        <div>
            <h2>My fees</h2>
            {fees.loading && <p>Loading...</p>}
            <Alert>{fees.error}</Alert>
            {fees.data && fees.data.length === 0 && <p className="muted">No fees yet.</p>}
            {fees.data && fees.data.length > 0 && (
                <div className="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Batch</th>
                            <th>Period</th>
                            <th>Fee</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                        <tbody>
                        {fees.data.map((f) => (
                            <tr key={f.id}>
                                <td>{f.batchName}</td>
                                <td>{period(f.periodStart, f.periodEnd)}</td>
                                <td>{money(f.amount)}</td>
                                <td>
                                    <span className={`badge ${f.status}`}>{f.status}</span>
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
import { useFetch } from '../../hooks'
import Alert from '../../components/Alert'

export default function MyBatches() {
    const list = useFetch('/api/me/enrollments')

    return (
        <div>
            <h2>My batches</h2>
            {list.loading && <p>Loading...</p>}
            <Alert>{list.error}</Alert>
            {list.data && list.data.length === 0 && <p className="muted">You are not enrolled yet.</p>}
            {list.data && list.data.length > 0 && (
                <div className="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Batch</th>
                            <th>Joined on</th>
                        </tr>
                        </thead>
                        <tbody>
                        {list.data.map((en) => (
                            <tr key={en.id}>
                                <td>{en.batchName}</td>
                                <td>{en.joinDate}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    )
}
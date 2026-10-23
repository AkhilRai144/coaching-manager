import { useState } from 'react'
import { api } from '../../api'
import { useFetch } from '../../hooks'
import { money, period } from '../../format'
import Alert from '../../components/Alert'

function DueTable({ dues, onOpen, buttonLabel }) {
    return (
        <div className="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>Student</th>
                    <th>Batch</th>
                    <th>Period</th>
                    <th>Fee</th>
                    <th>Status</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                {dues.map((d) => (
                    <tr key={d.id}>
                        <td>{d.studentName}</td>
                        <td>{d.batchName}</td>
                        <td>{period(d.periodStart, d.periodEnd)}</td>
                        <td>{money(d.amount)}</td>
                        <td>
                            <span className={`badge ${d.status}`}>{d.status}</span>
                        </td>
                        <td>
                            <button className="small" onClick={() => onOpen(d)}>
                                {d.status === 'PENDING' ? buttonLabel : 'Details'}
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    )
}

export default function Fees() {
    const batches = useFetch('/api/batches')
    const pending = useFetch('/api/fees/pending')
    const [genBatch, setGenBatch] = useState('')
    const [viewBatch, setViewBatch] = useState('')
    const batchDues = useFetch(viewBatch ? `/api/fees/batch/${viewBatch}` : null)
    const [panel, setPanel] = useState(null)
    const [pay, setPay] = useState({ amountPaid: '', mode: 'CASH' })
    const [msg, setMsg] = useState(null)

    function reloadLists() {
        pending.reload()
        batchDues.reload()
    }

    async function generate(e) {
        e.preventDefault()
        setMsg(null)
        try {
            const r = await api.post('/api/fees/generate', { batchId: Number(genBatch) })
            setMsg({
                type: 'success',
                text: `Dues created: ${r.created}, already existed: ${r.alreadyExisted}`,
            })
            reloadLists()
        } catch (err) {
            setMsg({ type: 'error', text: err.message })
        }
    }

    async function openDue(due) {
        setMsg(null)
        try {
            const history = await api.get(`/api/payments/due/${due.id}`)
            const remaining = history.length ? history[0].remainingAmount : due.amount
            setPanel({ ...due, remaining, history })
            setPay({ amountPaid: String(remaining), mode: 'CASH' })
        } catch (err) {
            setMsg({ type: 'error', text: err.message })
        }
    }

    async function recordPayment(e) {
        e.preventDefault()
        setMsg(null)
        try {
            const r = await api.post('/api/payments', {
                feeDueId: panel.id,
                amountPaid: Number(pay.amountPaid),
                mode: pay.mode,
            })
            setMsg({
                type: 'success',
                text: `Payment recorded for ${period(r.periodStart, r.periodEnd)}. Remaining: ${money(
                    r.remainingAmount,
                )} (${r.feeStatus})`,
            })
            setPanel(null)
            reloadLists()
        } catch (err) {
            setMsg({ type: 'error', text: err.message })
        }
    }

    return (
        <div>
            <h2>Fees</h2>
            {msg && <Alert type={msg.type}>{msg.text}</Alert>}

            <form className="card form" onSubmit={generate}>
                <h3>Generate dues</h3>
                <p className="muted">
                    Creates dues for every cycle that has started (cycles count from each student's join
                    date). Safe to click again.
                </p>
                <select value={genBatch} onChange={(e) => setGenBatch(e.target.value)} required>
                    <option value="">Select batch</option>
                    {batches.data?.map((b) => (
                        <option key={b.id} value={b.id}>
                            {b.name} ({money(b.monthlyFee)})
                        </option>
                    ))}
                </select>
                <button type="submit">Generate</button>
            </form>

            {panel && (
                <div className="card">
                    <h3>
                        {panel.studentName} - {panel.batchName}
                    </h3>
                    <p>
                        Period: <strong>{period(panel.periodStart, panel.periodEnd)}</strong>
                    </p>
                    <p className="muted">
                        Total {money(panel.amount)} | Remaining {money(panel.remaining)}
                    </p>
                    {panel.history.length > 0 && (
                        <ul>
                            {panel.history.map((p) => (
                                <li key={p.id}>
                                    {money(p.amountPaid)} paid on {p.paidOn} ({p.mode})
                                </li>
                            ))}
                        </ul>
                    )}
                    {panel.status === 'PENDING' && (
                        <form className="form" onSubmit={recordPayment}>
                            <input
                                type="number"
                                step="any"
                                placeholder="Amount"
                                value={pay.amountPaid}
                                onChange={(e) => setPay({ ...pay, amountPaid: e.target.value })}
                            />
                            <select value={pay.mode} onChange={(e) => setPay({ ...pay, mode: e.target.value })}>
                                <option value="CASH">CASH</option>
                                <option value="UPI">UPI</option>
                                <option value="CARD">CARD</option>
                            </select>
                            <div className="row">
                                <button type="submit">Record payment</button>
                                <button type="button" className="secondary" onClick={() => setPanel(null)}>
                                    Close
                                </button>
                            </div>
                        </form>
                    )}
                    {panel.status !== 'PENDING' && (
                        <button className="secondary" onClick={() => setPanel(null)}>
                            Close
                        </button>
                    )}
                </div>
            )}

            <h3>Pending dues</h3>
            {pending.loading && <p>Loading...</p>}
            <Alert>{pending.error}</Alert>
            {pending.data && pending.data.length === 0 && <p className="muted">No pending dues.</p>}
            {pending.data && pending.data.length > 0 && (
                <DueTable dues={pending.data} onOpen={openDue} buttonLabel="Pay" />
            )}

            <h3 style={{ marginTop: 28 }}>All dues of a batch</h3>
            <select value={viewBatch} onChange={(e) => setViewBatch(e.target.value)}>
                <option value="">Select batch</option>
                {batches.data?.map((b) => (
                    <option key={b.id} value={b.id}>
                        {b.name}
                    </option>
                ))}
            </select>
            <Alert>{batchDues.error}</Alert>
            {batchDues.data && batchDues.data.length === 0 && (
                <p className="muted">No dues in this batch yet.</p>
            )}
            {batchDues.data && batchDues.data.length > 0 && (
                <div style={{ marginTop: 12 }}>
                    <DueTable dues={batchDues.data} onOpen={openDue} buttonLabel="Pay" />
                </div>
            )}
        </div>
    )
}
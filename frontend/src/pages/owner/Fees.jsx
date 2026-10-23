import { useState } from 'react'
import { api } from '../../api'
import { useFetch } from '../../hooks'
import { money, monthLabel } from '../../format'
import Alert from '../../components/Alert'

export default function Fees() {
    const batches = useFetch('/api/batches')
    const pending = useFetch('/api/fees/pending')
    const [gen, setGen] = useState({ batchId: '', month: '' })
    const [payFor, setPayFor] = useState(null)
    const [pay, setPay] = useState({ amountPaid: '', mode: 'CASH' })
    const [msg, setMsg] = useState(null)

    async function generate(e) {
        e.preventDefault()
        setMsg(null)
        try {
            // <input type="month"> deta hai "2026-10", backend ko poori date chahiye
            const r = await api.post('/api/fees/generate', {
                batchId: Number(gen.batchId),
                feeMonth: `${gen.month}-01`,
            })
            setMsg({
                type: 'success',
                text: `Dues created: ${r.created}, already existed: ${r.alreadyExisted}`,
            })
            pending.reload()
        } catch (err) {
            setMsg({ type: 'error', text: err.message })
        }
    }

    async function openPay(due) {
        setMsg(null)
        try {
            const history = await api.get(`/api/payments/due/${due.id}`)
            const remaining = history.length ? history[0].remainingAmount : due.amount
            setPayFor({ ...due, remaining, history })
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
                feeDueId: payFor.id,
                amountPaid: Number(pay.amountPaid),
                mode: pay.mode,
            })
            setMsg({
                type: 'success',
                text: `Payment recorded. Remaining: ${money(r.remainingAmount)} (${r.feeStatus})`,
            })
            setPayFor(null)
            pending.reload()
        } catch (err) {
            setMsg({ type: 'error', text: err.message })
        }
    }

    return (
        <div>
            <h2>Fees</h2>
            {msg && <Alert type={msg.type}>{msg.text}</Alert>}

            <form className="card form" onSubmit={generate}>
                <h3>Generate monthly dues</h3>
                <select
                    value={gen.batchId}
                    onChange={(e) => setGen({ ...gen, batchId: e.target.value })}
                    required
                >
                    <option value="">Select batch</option>
                    {batches.data?.map((b) => (
                        <option key={b.id} value={b.id}>
                            {b.name} ({money(b.monthlyFee)})
                        </option>
                    ))}
                </select>
                <input
                    type="month"
                    value={gen.month}
                    onChange={(e) => setGen({ ...gen, month: e.target.value })}
                    required
                />
                <button type="submit">Generate</button>
            </form>

            {payFor && (
                <form className="card form" onSubmit={recordPayment}>
                    <h3>
                        Payment: {payFor.studentName} - {payFor.batchName} ({monthLabel(payFor.feeMonth)})
                    </h3>
                    <p className="muted">
                        Total {money(payFor.amount)} | Remaining {money(payFor.remaining)}
                    </p>
                    {payFor.history.length > 0 && (
                        <ul>
                            {payFor.history.map((p) => (
                                <li key={p.id}>
                                    {money(p.amountPaid)} on {p.paidOn} ({p.mode})
                                </li>
                            ))}
                        </ul>
                    )}
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
                        <button type="button" className="secondary" onClick={() => setPayFor(null)}>
                            Close
                        </button>
                    </div>
                </form>
            )}

            <h3>Pending dues</h3>
            {pending.loading && <p>Loading...</p>}
            <Alert>{pending.error}</Alert>
            {pending.data && pending.data.length === 0 && <p className="muted">No pending dues.</p>}
            {pending.data && pending.data.length > 0 && (
                <div className="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Student</th>
                            <th>Batch</th>
                            <th>Month</th>
                            <th>Fee</th>
                            <th>Status</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        {pending.data.map((d) => (
                            <tr key={d.id}>
                                <td>{d.studentName}</td>
                                <td>{d.batchName}</td>
                                <td>{monthLabel(d.feeMonth)}</td>
                                <td>{money(d.amount)}</td>
                                <td>
                                    <span className={`badge ${d.status}`}>{d.status}</span>
                                </td>
                                <td>
                                    <button className="small" onClick={() => openPay(d)}>
                                        Pay
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
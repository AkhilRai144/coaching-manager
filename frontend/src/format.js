export const money = (v) => `₹${Number(v).toFixed(2)}`

const fmt = (d) =>
    new Date(`${d}T00:00:00`).toLocaleDateString('en-IN', {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
    })

// "23 Oct 2026 - 22 Nov 2026"
export const period = (start, end) => `${fmt(start)} - ${fmt(end)}`
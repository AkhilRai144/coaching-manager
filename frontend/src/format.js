export const money = (v) => `₹${Number(v).toFixed(2)}`
export const monthLabel = (d) => (d ? d.slice(0, 7) : '')
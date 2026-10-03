import { format, parseISO } from 'date-fns'

// Date object → '2026-10-24' (safe to store in Redux and in the URL)
export const toISODate = (date) => format(date, 'yyyy-MM-dd')

// '2026-10-24' → Date object (for the calendar); empty → undefined
export const fromISODate = (iso) => (iso ? parseISO(iso) : undefined)

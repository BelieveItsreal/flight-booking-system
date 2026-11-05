import { format, parseISO } from 'date-fns'

// Date object → '2026-10-24' (safe to store in Redux and in the URL)
export const toISODate = (date: Date): string => format(date, 'yyyy-MM-dd')

// '2026-10-24' → Date object (for the calendar); empty → undefined
export const fromISODate = (iso: string | null | undefined): Date | undefined => (iso ? parseISO(iso) : undefined)

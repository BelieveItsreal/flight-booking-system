export function addMinutes(time: string, minutes: number): string{
    const [hours, mins] = time.split(':').map(Number)
    const total = (hours * 60 + mins + minutes) % (24*60)
    const hh = String(Math.floor(total / 60)).padStart(2, '0')
    const mm = String(total % 60).padStart(2, '0')
    return `${hh}:${mm}`
}

export function formatDuration(minutes: number): string{
    const hours = Math.floor(minutes / 60)
    const mins = String(minutes % 60).padStart(2, '0')
    return `${hours}h ${mins}m`
}

export function formatStops(stops: number): string{
    if (stops === 0) return `Non-stop`
    return `${stops} Stop${stops > 1 ? 's' : ''}`
}